package com.android.flypigeon.service;

import hichang.test.DswLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.String;

import com.android.flypigeon.util.ByteAndInt;
import com.android.flypigeon.util.Constant;
import com.android.flypigeon.util.Message;
import com.android.flypigeon.util.Person;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class MainService extends Service {
	private ServiceBinder sBinder = new ServiceBinder();//服务绑定器
	private static ArrayList<Map<Integer,Person>> children = new ArrayList<Map<Integer,Person>>();//保存所有组中的用户，每个map对象保存一个组的全部用户
	private static Map<Integer,Person> childrenMap = new HashMap<Integer,Person>();//当前在线用户
	private static ArrayList<Integer> personKeys = new ArrayList<Integer>();//当前在线用户id
	private static Map<Integer,List<Message>> msgContainer = new HashMap<Integer,List<Message>>();//所有用户信息容器
	private SharedPreferences pre = null;
	private SharedPreferences.Editor editor = null;
	private WifiManager wifiManager = null;
	private ServiceBroadcastReceiver receiver = null;
	public InetAddress localInetAddress = null;
	private String localIp = null;
	private byte[] localIpBytes = null; 
	private byte[] regBuffer = new byte[Constant.bufferSize];//本机网络注册交互指令
	private byte[] msgSendBuffer = new byte[Constant.bufferSize];//信息发送交互
	private byte[] talkCmdBuffer = new byte[Constant.bufferSize];//通话指令
	private byte[] songCmdBuffer = new byte[Constant.bufferSize];//通话指令
	private static Person me = null;//用来保存自身的相关信息
	private CommunicationBridge comBridge = null;//通讯与协议解析模块		
	private boolean isServiceAlive = false;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return sBinder;
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}
	@Override
	public void onRebind(Intent intent) {
		
	}
	@Override
	public void onCreate() {
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onStart(Intent intent, int startId) {	
		String actionString = intent.getAction();
		if(startId != 1) {
			if(actionString == null)
				return;
			else if(actionString.equals(Constant.acceptTalkRequestAction)) {	
				final Person psn = (Person)intent.getExtras().get("person");
				if(psn == null) return;
				new AcceptTalkThread(psn.personId).start();
			}
			else if(actionString.equals(Constant.stopSongAction)) {
				int personId = intent.getIntExtra("personid", -1);
				new StopTalkThread(personId).start();
			}
			else if(actionString.equals(Constant.returnCurrentModeAction)) {	
				final Person psn = (Person)intent.getExtras().get("person");
				int mode = intent.getIntExtra("mode", -1);
				if(psn == null) return;
				new SendCurrentModeThread(psn.personId,mode).start();
			}
			else if(actionString.equals(Constant.orderedSongAction)) {
				int personId = intent.getIntExtra("personid", -1);
				int songId = intent.getIntExtra("songid", -1);
				new SendOrderedSongThread(personId,songId).start();
			}
			else if(actionString.equals(Constant.orderedSongListAction)) {
				final Person psn = (Person)intent.getExtras().get("person");
				if(psn == null) return;
				ArrayList<Integer> songList = intent.getExtras().getIntegerArrayList("songlist");				
				new SendOrderedSongListThread(psn.personId,songList).start();
			}
			else if(actionString.equals(Constant.nextActivityAction)) {
				Integer activityID = (Integer)intent.getExtras().get("activityid");
				int personId = intent.getIntExtra("personid",-1);
				new NextActivityThread(personId,activityID.intValue()).start();
			}
			else if(actionString.equals(Constant.startSongAction)) {
				Integer personId = (Integer)intent.getExtras().get("personid");
				new StartSongThread(personId.intValue()).start();
			}
			else if(actionString.equals(Constant.refuseOrderSongAction)) {
				new RemoteSongThread(Constant.REFUSEORDERSONG).start();
			}
			else if(actionString.equals(Constant.acceptOrderSongAction)) {	
				new RemoteSongThread(Constant.ACCEPTORDERSONG).start();
			}
			else if(actionString.equals(Constant.remindOrderSongAction)) {	
				final Person psn = (Person)intent.getExtras().get("person");
				if(psn == null) return;
				new RemoteSongThread(Constant.REMINDORDERSONG,psn.personId).start();
			}
			else if(actionString.equals(Constant.currentSongFinishedAction)) {	
				final Person psn = (Person)intent.getExtras().get("person");
				if(psn == null) return;
				new RemoteSongThread(Constant.CURRENTSONGFINISH,psn.personId).start();
			}
			return;
		}
		
		isServiceAlive = true;
		
		initCmdBuffer();//初始化指令缓存
		wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		new CheckNetConnectivity().start();//侦测网络状态，获取IP地址
		
		comBridge = new CommunicationBridge();//启动socket连接
		comBridge.start();
		
		pre = PreferenceManager.getDefaultSharedPreferences(this);
		editor = pre.edit();
		
		regBroadcastReceiver();//注册广播接收器
		getMyInfomation();//获得自身信息
		new UpdateMe().start();//向网络发送心跳包，并注册
		new CheckUserOnline().start();//检查用户列表是否有超时用户
		sendPersonHasChangedBroadcast();//通知有新用户加入或退出
		System.out.println("Service started...");
	}
	
	private class SendOrderedSongThread extends Thread {
		int personId = -1;
		int songId = -1;
		
		public SendOrderedSongThread(int personId, int songId) {
			this.personId = personId;			
			this.songId = songId;
		}
		
		public void run() {
			SendOrderedSong(personId,songId);
		};
	}
	
	private class SendOrderedSongListThread extends Thread {
		int personId = -1;
		ArrayList<Integer> songList = new ArrayList<Integer>();
		
		public SendOrderedSongListThread(int personId, ArrayList<Integer> songList) {
			this.personId = personId;
			this.songList = songList;
		}
		
		public void run() {
			SendOrderedSongList(personId,songList);
		};
	}
	
	//发送接收语音命令
	private class AcceptTalkThread extends Thread {
		int personId = -1;
		
		public AcceptTalkThread(int personId) {
			this.personId = personId;			
		}
		
		public void run() {
			acceptTalk(personId);
		};
	};
	
	//
	private class StopTalkThread extends Thread {
		int personId = -1;
		
		public StopTalkThread(int personId) {
			this.personId = personId;
		}
		
		public void run() {
			stopTalk(personId);
		}
	}
	
	private class SendCurrentModeThread extends Thread {
		int personId = -1;
		int mode = -1;
		
		public SendCurrentModeThread(int personId, int mode) {
			this.personId = personId;			
			this.mode = mode;
		}
		
		public void run() {
			SendCurrentMode(personId, mode);
		};
	}
	
	private class StartSongThread extends Thread {
		int personId = -1;
		
		public StartSongThread(int personId) {
			this.personId = personId;			
		}
		
		public void run() {
			startSong(personId);
		};
	}
	
	private class NextActivityThread extends Thread {
		int activityID = -1;
		int personId = -1;
		
		public NextActivityThread(int personId,int activityID) {
			this.activityID = activityID;
			this.personId = personId;
		}
		
		public void run() {
			sendNextActivityID(personId,activityID);
		};
	}
	
	//接收语音线程
	private class RemoteSongThread extends Thread {
		int personId = -1;
		int commandID = -1;
		
		public RemoteSongThread(int commandID) {	
			this.commandID = commandID;
		}
		
		public RemoteSongThread(int commandID, int personId) {
			this.commandID = commandID;
			this.personId = personId;			
		}
		
		public void run() {
			sendRemoteSongMsg(commandID,personId);
		};
	};
	
	//服务绑定
	public class ServiceBinder extends Binder{
		public MainService getService(){
			return MainService.this;
		}
	}
	
    //获得自已的相关信息
    private void getMyInfomation(){
    	SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
//    	int iconId = pre.getInt("headIconId", R.drawable.black_bird);
    	String nickeName = pre.getString("nickeName", android.os.Build.MODEL);
    	//int myId = pre.getInt("myId", Constant.getMyId());
    	int myId = 1000001;
		editor.putInt("myId", myId);
		editor.commit();
		
    	if(null==me)me = new Person();
//    	me.personHeadIconId = iconId;
    	me.personNickeName = nickeName;
    	me.personId = myId;    	
    	//Toast.makeText(getApplicationContext(),localIp, Toast.LENGTH_SHORT).show();
    	me.ipAddress = localIp;
    	
    	//更新注册命令用户数据
    	System.arraycopy(ByteAndInt.int2ByteArray(myId), 0, regBuffer, 6, 4);
//    	System.arraycopy(ByteAndInt.int2ByteArray(iconId), 0, regBuffer, 10, 4);
    	for(int i=14;i<44;i++)regBuffer[i] = 0;//把原来的昵称内容清空
    	byte[] nickeNameBytes = nickeName.getBytes();
    	System.arraycopy(nickeNameBytes, 0, regBuffer, 14, nickeNameBytes.length);    	
    	
    	//更新通话命令用户数据
    	System.arraycopy(ByteAndInt.int2ByteArray(myId), 0, talkCmdBuffer, 6, 4);
//    	System.arraycopy(ByteAndInt.int2ByteArray(iconId), 0, talkCmdBuffer, 10, 4);
    	for(int i=14;i<44;i++)talkCmdBuffer[i] = 0;//把原来的昵称内容清空
    	System.arraycopy(nickeNameBytes, 0, talkCmdBuffer, 14, nickeNameBytes.length);
    	
    	//更新通话命令用户数据
    	System.arraycopy(ByteAndInt.int2ByteArray(myId), 0, songCmdBuffer, 6, 4);
//    	System.arraycopy(ByteAndInt.int2ByteArray(iconId), 0, talkCmdBuffer, 10, 4);
    	for(int i=14;i<44;i++)songCmdBuffer[i] = 0;//把原来的昵称内容清空
    	System.arraycopy(nickeNameBytes, 0, songCmdBuffer, 14, nickeNameBytes.length);
    }
	
	private String getCurrentTime(){
		Date date = new Date();
		return date.toLocaleString();
	}

    //检测网络连接状态,获得本机IP地址
	private class CheckNetConnectivity extends Thread {
		public void run() {			
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
			WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
			if (null != info) {
				int temp = info.getIpAddress();
				localIp = ( (temp) & 0xFF) +"."+((temp >> 8 ) & 0xFF)+"."+((temp >> 16 ) & 0xFF)+"."+(temp >> 24 & 0xFF );
				try {
					localInetAddress = InetAddress.getByName(localIp);
					localIpBytes = localInetAddress.getAddress();
					System.arraycopy(localIpBytes,0,regBuffer,44,4);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}			
			}
		};
	};
	
	//初始化指令缓存
	private void initCmdBuffer(){
		//初始化用户注册指令缓存
		for(int i=0;i<Constant.bufferSize;i++)regBuffer[i]=0;
		System.arraycopy(Constant.pkgHead, 0, regBuffer, 0, 3);
		regBuffer[3] = Constant.CMD80;
		regBuffer[4] = Constant.CMD_TYPE1;
		regBuffer[5] = Constant.OPR_CMD1;
		
		//初始化信息发送指令缓存
		for(int i=0;i<Constant.bufferSize;i++)msgSendBuffer[i]=0;
		System.arraycopy(Constant.pkgHead, 0, msgSendBuffer, 0, 3);
		msgSendBuffer[3] = Constant.CMD81;
		msgSendBuffer[4] = Constant.CMD_TYPE1;
		msgSendBuffer[5] = Constant.OPR_CMD1;
				
		//初始化通话指令
		for(int i=0;i<Constant.bufferSize;i++)talkCmdBuffer[i]=0;
		System.arraycopy(Constant.pkgHead, 0, talkCmdBuffer, 0, 3);
		talkCmdBuffer[3] = Constant.CMD83;
		talkCmdBuffer[4] = Constant.CMD_TYPE1;
		talkCmdBuffer[5] = Constant.OPR_CMD1;
		
		//初始化通话指令
		for (int i = 0; i < Constant.bufferSize; i++)
			songCmdBuffer[i] = 0;
		System.arraycopy(Constant.pkgHead, 0, songCmdBuffer, 0, 3);
		songCmdBuffer[3] = Constant.KEYPRESSED;
		songCmdBuffer[4] = Constant.KEYUP;
		songCmdBuffer[5] = Constant.OPR_CMD1;
	}
	//获得所有用户对象
	public ArrayList<Map<Integer,Person>> getChildren(){
		return children;
	}
	//获得所有用户id
	public ArrayList<Integer> getPersonKeys(){
		return personKeys;
	}
	//根据用户id获得该用户的消息
	public List<Message> getMessagesById(int personId){
		return msgContainer.get(personId);
	}
	//根据用户id获得该用户的消息数量
	public int getMessagesCountById(int personId){
		List<Message> msgs = msgContainer.get(personId);
		if(null!=msgs){
			return msgs.size();
		}else {
			return 0;
		}
	}
	
	//每隔10秒发送一个心跳包
	boolean isStopUpdateMe = false;
	private class UpdateMe extends Thread{
		@Override
		public void run() {
			while(!isStopUpdateMe){
				try{
					comBridge.joinOrganization();
					sleep(10000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	//检测用户是否在线，如果超过15秒说明用户已离线，则从列表中清除该用户
	private class CheckUserOnline extends Thread{
		@Override
		public void run() {
			super.run();
			boolean hasChanged = false;
			while(!isStopUpdateMe){
				if(childrenMap.size()>0){
					Set<Integer> keys = childrenMap.keySet();
					for (Integer key : keys) {
						if(System.currentTimeMillis()-childrenMap.get(key).timeStamp>15000){
							childrenMap.remove(key);
							personKeys.remove(Integer.valueOf(key));
							hasChanged = true;
						}
					}
				}
				if(hasChanged)sendPersonHasChangedBroadcast();
				try {sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
	
	//发送用户更新广播
	private void sendPersonHasChangedBroadcast(){
		Intent intent = new Intent();
		intent.setAction(Constant.personHasChangedAction);
		sendBroadcast(intent);
	}
	
	//注册广播接收器
	private void regBroadcastReceiver(){
		receiver = new ServiceBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.WIFIACTION);
		filter.addAction(Constant.ETHACTION);
		filter.addAction(Constant.updateMyInformationAction);
		filter.addAction(Constant.imAliveNow);
		registerReceiver(receiver, filter);
	}
	
	//广播接收器处理类
	private class ServiceBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Constant.WIFIACTION) || intent.getAction().equals(Constant.ETHACTION)){
				new CheckNetConnectivity().start();
			}else if(intent.getAction().equals(Constant.updateMyInformationAction)){
				getMyInfomation();
				comBridge.joinOrganization();
			}else if(intent.getAction().equals(Constant.imAliveNow)){
				
			}
		}
	}
	
	//开始语音呼叫
	public void startTalk(int personId){
		comBridge.startTalk(personId);
	}
	//结束语音呼叫
	public void stopTalk(int personId){
		comBridge.stopTalk(personId);
	}
	
	public void SendOrderedSong(int personId, int songId) {
		comBridge.SendOrderedSong(personId, songId);
	}
	
	public void SendOrderedSongList(int personId,ArrayList<Integer> songList) {
		comBridge.SendOrderedSongList(personId, songList);
	}
	
	//接受远程语音呼叫
	public void acceptTalk(int personId){
		comBridge.acceptTalk(personId);
	}
	//开始唱歌
	public void startSong(int personId){
		comBridge.startSong(personId);
	}
	//发送点歌相关消息
	public void sendRemoteSongMsg(int commandID, int personId){
		comBridge.sendRemoteSongMsg(commandID,personId);
	}
	
	public void SendCurrentMode(int personId, int mode) {
		comBridge.sendCurrentMode(personId,mode);
	}
	
	public void sendNextActivityID(int personId,int activityID)
	{
		comBridge.sendNextActivityID(personId,activityID);
	}
	
	@Override
	public void onDestroy() {
		comBridge.release();
		unregisterReceiver(receiver);
		isStopUpdateMe = true;
		children.clear();
		System.out.println("Service on destory...");
	}
	
	//========================协议分析与通讯模块=======================================================
	private class CommunicationBridge extends Thread{
		private MulticastSocket multicastSocket = null;
		private byte[] recvBuffer = new byte[Constant.bufferSize];
		private boolean isStopTalk = false;//通话结束标志
		
		private AudioHandler audioHandler = null;//音频处理模块，用来收发音频数据
		
		public CommunicationBridge(){			
			audioHandler = new AudioHandler();
		}

		//打开组播端口，准备组播通讯
		@Override
		public void run() {
			super.run();
			try {
				multicastSocket = new MulticastSocket(Constant.PORT);
				multicastSocket.joinGroup(InetAddress.getByName(Constant.MULTICAST_IP));
				System.out.println("Socket started...");
				while (!multicastSocket.isClosed() && null!=multicastSocket) {
					for (int i=0;i<Constant.bufferSize;i++){recvBuffer[i]=0;}
		        	DatagramPacket rdp = new DatagramPacket(recvBuffer, recvBuffer.length);
		        	multicastSocket.receive(rdp);
		        	parsePackage(recvBuffer);
		        }
			} catch (Exception e) {
				try {
					if(null!=multicastSocket && !multicastSocket.isClosed()){
						multicastSocket.leaveGroup(InetAddress.getByName(Constant.MULTICAST_IP));
						multicastSocket.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} 
		}

		//解析接收到的数据包
		private void parsePackage(byte[] pkg) {
			int CMD = pkg[3];//命令字
			int cmdType = pkg[4];//命令类型
			int oprCmd = pkg[5];//操作命令

			//获得用户ID号
			byte[] uId = new byte[4];
			System.arraycopy(pkg, 6, uId, 0, 4);
			int userId = ByteAndInt.byteArray2Int(uId);
			if(userId == 1000001) return;
			
			switch (CMD) {
			case Constant.CMD80:
				switch (cmdType) {
				case Constant.CMD_TYPE1:
					//如果该信息不是自己发出则给对方发送回应包,并把对方加入用户列表
					if(userId != me.personId){
						updatePerson(userId,pkg);
						//发送应答包
						byte[] ipBytes = new byte[4];//获得请求方的ip地址
						System.arraycopy(pkg, 44, ipBytes, 0, 4);
						try {
							InetAddress targetIp = InetAddress.getByAddress(ipBytes);
							regBuffer[4] = Constant.CMD_TYPE2;//把自己的注册信息修改成应答信息标志，把自己的信息发送给请求方
							DatagramPacket dp = new DatagramPacket(regBuffer,Constant.bufferSize,targetIp,Constant.PORT);
							multicastSocket.send(dp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
				case Constant.CMD_TYPE2:
					if(userId != me.personId) {
						updatePerson(userId,pkg);
					}
					break;
				case Constant.CMD_TYPE3:
					childrenMap.remove(userId);
					personKeys.remove(Integer.valueOf(userId));
					sendPersonHasChangedBroadcast();
					break;
				}
				break;
			case Constant.CMD81:// 收到信息
				switch (cmdType) {
				case Constant.CMD_TYPE1:
					List<Message> messages = null;
					if(msgContainer.containsKey(userId)){
						messages = msgContainer.get(userId);
					}else{
						messages = new ArrayList<Message>();
					}
					byte[] msgBytes = new byte[Constant.msgLength];
					System.arraycopy(pkg, 10, msgBytes, 0, Constant.msgLength);
					String msgStr = new String(msgBytes).trim();
					Message msg = new Message();
					msg.msg = msgStr;
					msg.receivedTime = getCurrentTime();
					messages.add(msg);
					msgContainer.put(userId, messages);
					
					Intent intent = new Intent();
					intent.setAction(Constant.hasMsgUpdatedAction);
					intent.putExtra("userId", userId);
					intent.putExtra("msgCount", messages.size());
					sendBroadcast(intent);
					break;
				case Constant.CMD_TYPE2:
					break;
				}
				break;
			case Constant.CMD83://83命令，语音通讯相关
				switch(cmdType){
				case Constant.CMD_TYPE1:
					switch(oprCmd){
					case Constant.OPR_CMD1://接收到远程语音通话请求
						System.out.println("Received a talk request ... ");
						isStopTalk = false;
						Person person = childrenMap.get(Integer.valueOf(userId));
						Intent intent = new Intent();
						intent.putExtra("person", person);
						intent.setAction(Constant.receivedTalkRequestAction);
						sendBroadcast(intent);
						break;
					case Constant.OPR_CMD2:
						//收到关闭指令，关闭语音通话
						System.out.println("Received remote user stop talk cmd ... ");
						isStopTalk = true;
						Intent i = new Intent();
						i.setAction(Constant.remoteUserClosedTalkAction);
						sendBroadcast(i);
						audioHandler.stop();
						break;
					case Constant.OPR_CMD3:
						//被叫应答，开始语音通话
						if(!isStopTalk){
							System.out.println("Begin to talk with remote user ... ");
							final Person p = childrenMap.get(Integer.valueOf(userId));
							audioHandler.audioSend(p);
						}
						break;
					}
					break;
				}
				break;
			case Constant.KEYPRESSED://84命令, 按键消息
				if(userId != me.personId){
					Person person = childrenMap.get(Integer.valueOf(userId));
					if(cmdType >= Constant.KEYUP && cmdType <= Constant.KEYORIGINAL) {					
						Intent i = new Intent();
						i.setAction(Constant.receiveKeyPressedAction);
						i.putExtra("keycode", cmdType);
						i.putExtra("person", person);
						sendBroadcast(i);
					}
				}				
				break;
			case Constant.ORDERSONG://点歌命令
				if(userId != me.personId){
					switch (cmdType) {
					case Constant.ORDEREDSONG:
						//获得歌曲ID号
						byte[] songId = new byte[4];
						System.arraycopy(pkg, 10, songId, 0, 4);
						int intSongID = ByteAndInt.byteArray2Int(songId);
						
						Person person = childrenMap.get(Integer.valueOf(userId));
						Intent i = new Intent();
						i.setAction(Constant.receiveRemotedSongAction);
						i.putExtra("songid", intSongID);
						i.putExtra("person", person);
						sendBroadcast(i);
						break;
					case Constant.REQUESTORDEREDSONGLIST:
						Person psn = childrenMap.get(Integer.valueOf(userId));
						Intent i1 = new Intent();
						i1.setAction(Constant.requestOrderedSongAction);
						i1.putExtra("person", psn);
						sendBroadcast(i1);
						break;
					case Constant.GETMODE:
						Person psn1 = childrenMap.get(Integer.valueOf(userId));
						Intent i2 = new Intent();
						i2.setAction(Constant.getCurrentModeAction);
						i2.putExtra("person", psn1);
						sendBroadcast(i2);
						break;
					default:
						break;
					}
				}
				break;
			case Constant.VOLUME:
				if(userId != me.personId){
					Person person = childrenMap.get(Integer.valueOf(userId));
					if(cmdType >= 0 && cmdType <= 100) {					
						Intent i = new Intent();
						i.setAction(Constant.volumeChangedAction);
						i.putExtra("volume", cmdType);
						i.putExtra("person", person);
						sendBroadcast(i);
					}
				}		
				break;
			}
		}
		
		//更新或加用户信息到用户列表中
		private void updatePerson(int userId,byte[] pkg){
			Person person = new Person();
			getPerson(pkg,person);
			childrenMap.put(userId, person);
			if(!personKeys.contains(Integer.valueOf(userId))) {
				personKeys.add(Integer.valueOf(userId));				
			}
			if(!children.contains(childrenMap)){
				children.add(childrenMap);
			}
			sendPersonHasChangedBroadcast();
		}
		
		//关闭Socket连接
		private void release(){
			try {
				regBuffer[4] = Constant.CMD_TYPE3;//把命令类型修改成注消标志，并广播发送，从所有用户中退出
				DatagramPacket dp = new DatagramPacket(regBuffer,Constant.bufferSize,InetAddress.getByName(Constant.MULTICAST_IP),Constant.PORT);
				multicastSocket.send(dp);
				System.out.println("Send logout cmd ...");
				
				multicastSocket.leaveGroup(InetAddress.getByName(Constant.MULTICAST_IP));
				multicastSocket.close();
				
				System.out.println("Socket has closed ...");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				audioHandler.release();
			}
		}
		
		//分析数据包并获取一个用户信息
		private void getPerson(byte[] pkg,Person person){
			
			byte[] personIdBytes = new byte[4];
			byte[] iconIdBytes = new byte[4];
			byte[] nickeNameBytes = new byte[30];
			byte[] personIpBytes = new byte[4];
			
			System.arraycopy(pkg, 6, personIdBytes, 0, 4);
			System.arraycopy(pkg, 10, iconIdBytes, 0, 4);
			System.arraycopy(pkg, 14, nickeNameBytes, 0, 30);
			System.arraycopy(pkg, 44, personIpBytes, 0, 4);
			
			person.personId = ByteAndInt.byteArray2Int(personIdBytes);
			person.personHeadIconId = ByteAndInt.byteArray2Int(iconIdBytes);
			person.personNickeName = (new String(nickeNameBytes)).trim();
			person.ipAddress = Constant.intToIp(ByteAndInt.byteArray2Int(personIpBytes));
			person.timeStamp = System.currentTimeMillis();
		}
		
		//注册自己到网络中
		public void joinOrganization(){
			try {
				if(null!=multicastSocket && !multicastSocket.isClosed()){
					regBuffer[4] = Constant.CMD_TYPE1;//恢复成注册请求标志，向网络中注册自己
					DatagramPacket dp = new DatagramPacket(regBuffer,Constant.bufferSize,InetAddress.getByName(Constant.MULTICAST_IP),Constant.PORT);
					multicastSocket.send(dp);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	    //开始语音呼叫，向远方发送语音呼叫请求
	    public void startTalk(int personId){
			try {
				isStopTalk = false;
				talkCmdBuffer[3] = Constant.CMD83;
				talkCmdBuffer[4] = Constant.CMD_TYPE1;
		    	talkCmdBuffer[5] = Constant.OPR_CMD1;
				System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);
				Person person = childrenMap.get(Integer.valueOf(personId));
				if(person == null) return;
				DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
				multicastSocket.send(dp);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    //结束语音呼叫
	    public void stopTalk(int personId){
	    	isStopTalk = true;
	    	talkCmdBuffer[3] = Constant.CMD83;
	    	talkCmdBuffer[4] = Constant.CMD_TYPE1;
	    	talkCmdBuffer[5] = Constant.OPR_CMD2;
	    	Person person = childrenMap.get(Integer.valueOf(personId));
	    	if(person == null) return;
	    	try {
	    		System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);
	    		DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
				multicastSocket.send(dp);
				audioHandler.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    //接受远程语音呼叫请求，并向远程发送语音数据
	    public void acceptTalk(int personId){
	    	isStopTalk = false;
			talkCmdBuffer[3] = Constant.CMD83;
			talkCmdBuffer[4] = Constant.CMD_TYPE1;
			talkCmdBuffer[5] = Constant.OPR_CMD3;
			Person person = childrenMap.get(Integer.valueOf(personId));
			if(person == null) return;
			try {
				//发送接受语音指令
				System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);				
				DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
				multicastSocket.send(dp);
				audioHandler.audioPlay(person);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    //开始唱歌
	    public void startSong(int personId){	    	
	    	songCmdBuffer[3] = Constant.ORDERSONG;
	    	songCmdBuffer[4] = Constant.STARTSONG;
	    	
			if(personId != -1) {
				Person person = childrenMap.get(Integer.valueOf(personId));
				if(person == null) return;
				DswLog.v("Service", "Start song: " + person.ipAddress);
				try {
					//发送单播控制消息
					System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, songCmdBuffer, 44, 4);				
					DatagramPacket dp = new DatagramPacket(songCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
					multicastSocket.send(dp);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	    
	    //
	    public void sendNextActivityID(int personId, int activityID){
	    	talkCmdBuffer[3] = Constant.ORDERSONG;
			talkCmdBuffer[4] = Constant.NEXTACTIVITY;
			talkCmdBuffer[5] = (byte) activityID;
			DswLog.v("Service", "activityID: " + activityID);
			if(personId == -2) {
				try {
					//发送多播控制消息
					System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);				
					DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(Constant.MULTICAST_IP),Constant.PORT);
					multicastSocket.send(dp);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(personId != -1) {
				Person person = childrenMap.get(Integer.valueOf(personId));
				if(person == null) return;
				
				try {
					//发送单播控制消息
					System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);				
					DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
					multicastSocket.send(dp);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	    
	    public void sendCurrentMode(int personId, int mode) {
	    	talkCmdBuffer[3] = Constant.ORDERSONG;
			talkCmdBuffer[4] = Constant.MODE;
			talkCmdBuffer[5] = (byte) mode;
			
			if(personId != -1) {
				Person person = childrenMap.get(Integer.valueOf(personId));
				if(person == null) return;				
				try {
					//发送单播控制消息
					System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);				
					DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
					multicastSocket.send(dp);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	    
	    public void SendOrderedSong(int personId, int songId) {
	    	talkCmdBuffer[3] = Constant.ORDERSONG;
			talkCmdBuffer[4] = Constant.ORDEREDSONG;
			DswLog.v("Service", "Ordered Song: " + songId + " " + personId);
			System.arraycopy(ByteAndInt.int2ByteArray(songId), 0, talkCmdBuffer, 10, 4);
			if(personId != -1) {
				Person person = childrenMap.get(Integer.valueOf(personId));
				if(person == null) return;				
				try {
					//发送单播控制消息
					System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);				
					DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
					multicastSocket.send(dp);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	    
	    public void SendOrderedSongList(int personId,ArrayList<Integer> songList) {
	    	talkCmdBuffer[3] = Constant.ORDERSONG;
			talkCmdBuffer[4] = Constant.ORDEREDSONGLIST;			
						
			if(personId != -1) {
				Person person = childrenMap.get(Integer.valueOf(personId));
				if(person == null) return;
				try {
					//发送单播控制消息
					Iterator<Integer> it1 = songList.iterator();
					int songCount = 0;
					while(it1.hasNext()){
						Integer tempSongId = it1.next();				
						System.arraycopy(ByteAndInt.int2ByteArray(tempSongId.intValue()), 0, talkCmdBuffer, songCount*4+48, 4);
						songCount++;
					}
					System.arraycopy(ByteAndInt.int2ByteArray(songCount), 0, talkCmdBuffer, 10, 4);
					
					System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);				
					DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
					multicastSocket.send(dp);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	    
	    //发送点歌控制消息
	    public void sendRemoteSongMsg(int commandID, int personId) {
	    	talkCmdBuffer[3] = Constant.ORDERSONG;
			talkCmdBuffer[4] = (byte) commandID;
			if(personId != -1) {
				Person person = childrenMap.get(Integer.valueOf(personId));
				if(person == null) return;
				try {
					//发送单播控制消息
					System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);				
					DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(person.ipAddress),Constant.PORT);
					multicastSocket.send(dp);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					//发送多播控制消息
					System.arraycopy(InetAddress.getByName(me.ipAddress).getAddress(), 0, talkCmdBuffer, 44, 4);				
					DatagramPacket dp = new DatagramPacket(talkCmdBuffer,Constant.bufferSize,InetAddress.getByName(Constant.MULTICAST_IP),Constant.PORT);
					multicastSocket.send(dp);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	    
	    //=========================RTP语音传输模块==================================================================    
		//基于rtp语音传输模块
		private class AudioHandler{
			private AudioSend     audioSend = null;
			private AudioPlay     audioPlay = null;
			private SoundSender   sender    = null;
			private SoundReceiver receiver  = null;
			
			//用来启动音频发送子线程
			public void audioSend(Person person){
				if(audioSend != null) stop();
				audioSend = new AudioSend(person);
				audioSend.start();
			}
			
			public void audioPlay(Person person){
				if(audioPlay != null) stop();
				audioPlay = new AudioPlay(person);
				audioPlay.start();
			}
			
			//音频播线程
			public class AudioPlay extends Thread{
				Person person = null;
				public AudioPlay(Person person){
					this.person = person;	
					android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO); 
				}
				
				@Override
				public void run() {
					receiver = new SoundReceiver(person.ipAddress);
				}
			}
			
			//音频发送线程
			public class AudioSend extends Thread{
				Person person = null;				
				
				public AudioSend(Person person){
					this.person = person;			
					android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO); 
				}
				@Override
				public void run() {
					super.run();	
					
					sender = new SoundSender(person.ipAddress);
					sender.run();					
				}
			}
			
			public void stop() {
				if(sender != null) {
					sender.stop();
					sender = null;
				}
				if(receiver != null) {
					receiver.stop();
					receiver = null;
				}
			}
			
			public void release() {
				if(sender != null) {
					sender.stop();
					sender = null;
				}
				if(receiver != null) {
					receiver.stop();
					receiver = null;
				}
			}
		}
		//=========================TCP语音传输模块结束================================================================== 
	}
	//========================协议分析与通讯模块结束=======================================================
}

