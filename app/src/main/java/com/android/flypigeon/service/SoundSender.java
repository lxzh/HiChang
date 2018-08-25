/* This file is based on 
 * http://www.anyexample.com/programming/java/java_play_wav_sound_file.xml
 * Please see the site for license information.
 */
	 
package com.android.flypigeon.service;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.lang.String;
import java.net.DatagramSocket;

import jlibrtp.*;

/**
 * @author anfeng.yuan
 */
public class SoundSender implements RTPAppIntf  {
	
	private boolean isStopTalk = false;
	private int rtpPort  = 16386;
	private int rtcpPort = 16387;
	private int length = 0;
	
	public  RTPSession  rtpSession = null;
	private AudioRecord recorder   = null;
	
	public SoundSender(String ipAddress)  {
		DatagramSocket rtpSocket = null;
		DatagramSocket rtcpSocket = null;
		
		try {
			rtpSocket = new DatagramSocket(rtpPort);
			rtcpSocket = new DatagramSocket(rtcpPort);
		} catch (Exception e) {
			//System.out.println("RTPSession failed to obtain port");
		}		
		
		rtpSession = new RTPSession(rtpSocket, rtcpSocket);
		rtpSession.RTPSessionRegister(this,null, null);
		
		Participant p = new Participant(ipAddress,16384,16385);
		rtpSession.addParticipant(p);
	}	
	
	public void receiveData(DataFrame dummy1, Participant dummy2) {
		// We don't expect any data.
	}
	
	public void userEvent(int type, Participant[] participant) {
		//Do nothing
	}
	
	public int frameSize(int payloadType) {
		return 1;
	}
	
	public void run() {		
		
		//获得录音缓冲区大小
		int bufferSize = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		
		//获得录音机对象
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				8000,AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				bufferSize*10);
		
		recorder.startRecording();//开始录音
		byte[] readBuffer = new byte[320];//录音缓冲区	
		
		while (!isStopTalk) {									
			length = recorder.read(readBuffer, 0, readBuffer.length);// 从mic读取音频数据

			if (length > 0 && length % 2 == 0) {				
				rtpSession.sendData(readBuffer);
			}
			//System.gc();			
		}
		
		try { Thread.sleep(200);} catch(Exception e) {}
		
		this.rtpSession.endSession();
		recorder.stop();
		recorder = null;
		
		try { Thread.sleep(2000);} catch(Exception e) {}
	}
	
	public void stop() {
		isStopTalk = true;
	}

}
