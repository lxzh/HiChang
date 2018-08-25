package com.android.flypigeon.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Constant {
	
	public static Map<String,Integer> exts = new HashMap<String,Integer>();
	
	//自定义Action
	public static final String tvFindMeAction = "com.android.flypigeon.tvFindMe";
	public static final String startTalkAction = "com.android.flypigeon.startTalkAction";
	public static final String keyPressedAction = "com.android.flypigeon.keyPressedAction";
	public static final String receiveKeyPressedAction = "com.android.flypigeon.receiveKeyPressedAction";
	public static final String volumeChangedAction = "com.android.flypigeon.volumeChangedAction";
	public static final String receiveRemotedSongAction = "com.android.flypigeon.receiveRemotedSongAction";
	public static final String sentRemoteSongAction = "com.android.flypigeon.sentRemoteSongAction";
	public static final String nextActivityAction = "com.android.flypigeon.nextActivityAction";
	public static final String startSongAction = "com.android.flypigeon.startSongAction";
	public static final String refuseOrderSongAction = "com.android.flypigeon.refuseOrderSongAction";
	public static final String acceptOrderSongAction = "com.android.flypigeon.acceptOrderSongAction";
	public static final String remindOrderSongAction = "com.android.flypigeon.remindOrderSongAction";
	public static final String orderedSongAction = "com.android.flypigeon.orderedSongAction";
	public static final String orderedSongListAction = "com.android.flypigeon.orderedSongListAction";
	public static final String requestOrderedSongAction = "com.android.flypigeon.requestOrderedSongAction";
	public static final String currentSongFinishedAction = "com.android.flypigeon.currentSongFinishedAction";
	public static final String getCurrentModeAction = "com.android.flypigeon.getCurrentModeAction";
	public static final String returnCurrentModeAction = "com.android.flypigeon.returnCurrentModeAction";
	public static final String stopSongAction = "com.android.flypigeon.stopSongAction";
	
	public static final String updateMyInformationAction = "com.android.flypigeon.updateMyInformation";
	public static final String personHasChangedAction = "com.android.flypigeon.personHasChanged";
	public static final String hasMsgUpdatedAction = "com.android.flypigeon.hasMsgUpdated";
	public static final String receivedSendFileRequestAction = "com.android.flypigeon.receivedSendFileRequest";
	public static final String refuseReceiveFileAction = "com.android.flypigeon.refuseReceiveFile";
	public static final String remoteUserRefuseReceiveFileAction = "com.android.flypigeon.remoteUserRefuseReceiveFile";
	public static final String dataReceiveErrorAction = "com.android.flypigeon.dataReceiveError";
	public static final String dataSendErrorAction = "com.android.flypigeon.dataSendError";
	public static final String whoIsAliveAction = "com.android.flypigeon.whoIsAlive";//询问当前那个Activity是激活状态
	public static final String imAliveNow = "com.android.flypigeon.imAliveNow";
	public static final String remoteUserUnAliveAction = "com.android.flypigeon.remoteUserUnAlive";
	public static final String fileSendStateUpdateAction = "com.android.flypigeon.fileSendStateUpdate";
	public static final String fileReceiveStateUpdateAction = "com.android.flypigeon.fileReceiveStateUpdate";
	public static final String receivedTalkRequestAction = "com.android.flypigeon.receivedTalkRequest";
	public static final String acceptTalkRequestAction = "com.android.flypigeon.acceptTalkRequest";
	public static final String remoteUserClosedTalkAction = "com.android.flypigeon.remoteUserClosedTalk";
	
	//系统Action
	//System Action declare
	public static final String bootCompleted = "android.intent.action.BOOT_COMPLETED";
	public static final String WIFIACTION="android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ETHACTION = "android.intent.action.ETH_STATE";
	
	//生成唯一ID码
	public static int getMyId(){
		int id = (int)(Math.random()*1000000);
		return id;
	}
	
	//other 其它定义，另外消息长度为60个汉字，utf-8中定义一个汉字占3个字节，所以消息长度为180bytes
	//文件长度为30个汉字，所以总长度为90个字节
	public static final int bufferSize = 256;
	public static final int msgLength = 180;
	public static final int fileNameLength = 90;
	public static final int readBufferSize = 4096;//文件读写缓存
	public static final byte[] pkgHead = "AND".getBytes();
	public static final int CMD80 = 80;
	public static final int CMD81 = 81;
	public static final int CMD82 = 82;
	public static final int CMD83 = 83;
	public static final int KEYPRESSED = 84;
	public static final int ORDERSONG = 85;
	public static final int VOLUME = 86;
	public static final int CMD_TYPE1 = 1;
	public static final int CMD_TYPE2 = 2;
	public static final int CMD_TYPE3 = 3;
	public static final int OPR_CMD1 = 1;
	public static final int OPR_CMD2 = 2;
	public static final int OPR_CMD3 = 3;
	public static final int OPR_CMD4 = 4;
	public static final int OPR_CMD5 = 5;
	public static final int OPR_CMD6 = 6;
	public static final int OPR_CMD10 = 10;
	
	public static final int KEYUP = 1;
	public static final int KEYDOWN = 2;
	public static final int KEYLEFT = 3;
	public static final int KEYRIGHT = 4;
	public static final int KEYOK = 5;
	public static final int KEY1 = 6;
	public static final int KEY2 = 7;
	public static final int KEY3 = 8;
	public static final int KEY4 = 9;
	public static final int KEY5 = 10;
	public static final int KEY6 = 11;
	public static final int KEY7 = 12;
	public static final int KEY8 = 13;
	public static final int KEY9 = 14;
	public static final int KEY0 = 15;
	public static final int KEYTURN = 16;
	public static final int KEYSCREENDISPLAY = 17;
	public static final int KEYBACK = 18;
	public static final int KEYMODE = 19;
	public static final int KEYORIGINAL = 20;
	
	public static final int ORDEREDSONG = 1;
	public static final int REFUSEORDERSONG = 2;
	public static final int ACCEPTORDERSONG = 3;
	public static final int REMINDORDERSONG = 4;
	public static final int CURRENTSONGFINISH = 5;
	public static final int STARTSONG = 6;
	public static final int NEXTACTIVITY = 7;
	public static final int REQUESTORDEREDSONGLIST = 8;
	public static final int ORDEREDSONGLIST = 9;
	public static final int GETMODE = 10;
	public static final int MODE = 11;
	
	public static final int SING = 1;
	public static final int PRACTICE = 2;
	public static final int PARTY = 3;
	public static final int HELP = 4;
	public static final int UPDATE = 5;
	public static final int REMOTESONG = 6;
	public static final int SCORE = 7;
	public static final int MAIN = 7;
	
	public static final String MULTICAST_IP = "239.9.9.1";
	public static final int PORT = 5760;
	public static final int AUDIO_PORT = 5761;
	
	//int to ip转换
	public static String intToIp(int i) {   
		String ip = ( (i >> 24) & 0xFF) +"."+((i >> 16 ) & 0xFF)+"."+((i >> 8 ) & 0xFF)+"."+(i & 0xFF );
		
		return ip;
	}
	
	//其它定义
	public static final int FILE_RESULT_CODE = 1;
	public static final int SELECT_FILES = 1;//是否要在文件选择器中显示文件
	public static final int SELECT_FILE_PATH = 2;//文件选择器只显示文件夹
	//文件选择状态保存
	public static TreeMap<Integer,Boolean> fileSelectedState = new TreeMap<Integer,Boolean>();
	
	
	//转换文件大小  
 	  public static String formatFileSize(long fileS) {
	      DecimalFormat df = new DecimalFormat("#.00");
	      String fileSizeString = "";
	      if (fileS < 1024) {
	    	  fileSizeString = fileS+"B";
	       //   fileSizeString = df.format((double) fileS) + "B";
	      } else if (fileS < 1048576) {
	          fileSizeString = df.format((double) fileS / 1024) + "K";
	      } else if (fileS < 1073741824) {
	          fileSizeString = df.format((double) fileS / 1048576) + "M";
	      } else {
	          fileSizeString = df.format((double) fileS / 1073741824) + "G";
	      }
	      return fileSizeString;
	  }
 	  
}
