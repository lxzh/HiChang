/* This file is based on 
 * http://www.anyexample.com/programming/java/java_play_wav_sound_file.xml
 * Please see the site for license information.
 */
package com.android.flypigeon.service;

import hichang.audio.AudRec;
import hichang.audio.Encoder;
import hichang.test.DswLog;

import java.net.DatagramSocket;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import jlibrtp.*;

/**
 * @author anfeng.yuan
 */
public class SoundReceiver implements RTPAppIntf {
	RTPSession rtpSession = null;
	private int rtpPort  = 16384;
	private int rtcpPort = 16385;
	AudioTrack player;
	private float pitch = 0.0f;
	private boolean isStopTalk = false;
	private Encoder encoder = null;
	private short[] tempData = new short[160];

	public void receiveData(DataFrame frame, Participant p) {
		byte[] data = frame.getConcatenatedData();
			
		pitch = data.length;
		if(!isStopTalk && player != null) {
			if (data.length > 0 && data.length % 2 == 0) {
				player.write(data, 0, data.length);// 播放音频数据
				
				/*if(encoder == null) {
					encoder = AudRec.getEncoder();
				}
								
				if(encoder != null && encoder.isIdle()){
					long start = System.currentTimeMillis();
					for(int i = 0; i < 160 && i < data.length/2; i++) {
						tempData[i] = (short) ((data[2*i] << 8) + (data[2*i+1] & 0xFF));
					}
					encoder.putData(0,System.currentTimeMillis(), tempData,tempData.length);
					long stop = System.currentTimeMillis();
					DswLog.v("SoundReceiver cost time: ",(stop - start) + "ms");
				}*/
			}
		}		
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void userEvent(int type, Participant[] participant) {
		//Do nothing
	}
	public int frameSize(int payloadType) {
		return 1;
	}
	
	public SoundReceiver(String ipAddress)  {
		DatagramSocket rtpSocket = null;
		DatagramSocket rtcpSocket = null;
		
		try {
			rtpSocket = new DatagramSocket(rtpPort);
			rtcpSocket = new DatagramSocket(rtcpPort);
		} catch (Exception e) {
			System.out.println("RTPSession failed to obtain port");
		}
				
		rtpSession = new RTPSession(rtpSocket, rtcpSocket);
		rtpSession.naivePktReception(true);
		rtpSession.RTPSessionRegister(this,null, null);
		
		//获得音频缓冲区大小
		int bufferSize = android.media.AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		
		//获得音轨对象
		player = new AudioTrack(AudioManager.STREAM_MUSIC, 
				8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				bufferSize,
				AudioTrack.MODE_STREAM);

		//设置喇叭音量
		player.setStereoVolume(1.0f, 1.0f);			
		
		//开始播放声音
		player.play();			
	}
	
	public void stop(){
		isStopTalk = true;
		player.stop();
		player = null;
		rtpSession.endSession();
	}
}
