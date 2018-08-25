package hichang.audio;

import hichang.Song.CMediaPlayer;
import hichang.Song.Sentence;

import java.util.ArrayList;

import android.util.Log;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.AudioRecord;
import android.os.Handler;

public class AudRec extends Thread {
	protected AudioRecord m_in_rec = null;
	protected int m_in_buf_size;
	protected boolean m_keep_running;

	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	
	private double dVolume = 0.0D;

	private ArrayList<Sentence> sentences = new ArrayList<Sentence>();
	private static Encoder encoder;
	private Handler handler;
	private CMediaPlayer mediaPlayer;

	public AudRec(ArrayList<Sentence> sentences, Handler handler, CMediaPlayer nowMediaPlayer) {
		this.sentences = sentences;
		this.handler = handler;
		this.mediaPlayer = nowMediaPlayer;
		m_keep_running = true;
	}
	
	public AudRec(ArrayList<Sentence> nowSongSentence) {
		this.sentences = sentences;
		m_keep_running = true;
	}
	
	public static Encoder getEncoder() {
		return encoder;
	}

	/**
	 * 获取对应时间的音符, 供外部调用的接口
	 * 
	 * @param time
	 * @return
	 */
	public int[] getNote(long time) {
		if(encoder == null)
			return new int[0];
		return encoder.getNote(time);
	}
	
	/**
	 * 获取某一个句子的评分
	 * @param indexOfSentence 句子的索引
	 * @return
	 */
	public int getMark(int indexOfSentence) {
		if(encoder == null)
			return 0;
		return encoder.getMark(indexOfSentence);
	}
	
	/**
	 * 获取整首歌的评分
	 * @return
	 */
	public int getTotalMark() {
		if(encoder == null)
			return 0;
		return encoder.getTotalMark();
	}
	
	/**
	 * 获取当前评分点评分
	 * @return
	 */
	public float getCurrentMark() {
		if(encoder == null)
			return 0;
		return encoder.getCurrentMark();
	}
	
	/**
	 * 设定起始时间，用于练歌时前进或后退几句
	 * @param startTime
	 */
	public void setStartTime(int startTime) {
		encoder.setStartTime(startTime);
	}

	/**
	 * AudioRecorder初始化
	 */
	public void init() {
		m_in_buf_size = android.media.AudioRecord.getMinBufferSize(frequency,
				AudioFormat.CHANNEL_IN_MONO, audioEncoding);
        if(m_in_rec == null) {
        	m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
        			AudioFormat.CHANNEL_IN_MONO, audioEncoding, m_in_buf_size);
        }

		m_keep_running = true;
	}

	/**
	 * 停止AudioRecorder
	 */
	public void free() {
		m_keep_running = false;
		try {
			sleep(1000);
		} catch (Exception e) {
			logd("sleep exceptions...\n");
		}
	}

	public void run() {
		int bufferRead = 0;		

		// 启动编码线程
		encoder = new Encoder(this.sentences,handler, mediaPlayer);
		Thread encodeThread = new Thread(encoder);
		encoder.setRecording(true);
		encodeThread.start();

		short[] tempBuffer = new short[m_in_buf_size];

		m_in_rec.startRecording();		
		
		while (m_keep_running) {			
			int currentPos = mediaPlayer.CGetCurrentPosition();
			bufferRead = m_in_rec.read(tempBuffer, 0, m_in_buf_size);
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
						"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException(
						"read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
						"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}
			
			/*double sqsum = 0.0D;
	        dVolume = 0.0D;
			for (int i = 0; i < bufferRead; i++) {
				final long v = tempBuffer[i];
	            sqsum += v * v;
			}
			
			// 超过185.0D即为有声音
			dVolume = (20.0D * Math.log10(Math.sqrt(sqsum / bufferRead) / 2.0E-006D));*/

			if (/*dVolume - 185.0D > 1.0E-006D &&*/ encoder.isIdle()) {
				encoder.putData(currentPos,System.currentTimeMillis(), tempBuffer,
						bufferRead);
			}
			
			/*try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}

		m_in_rec.stop();
		m_in_rec.release();
		m_in_rec = null;
		encoder.setRecording(false);
	}
	
	public int getCurrentReadindex() {
		if(encoder == null)
			return 0;
		return encoder.getCurrentReadindex();
	}
	
	public int getCurrentRecordIndex() {
		if(encoder == null)
			return 0;
		return encoder.getCurrentRecordIndex();
	}
	
	public int getCurrentRealNote() {
		if(encoder == null)
			return 0;
		return encoder.getCurrentRealNote();
	}
	
	public long getProcessTime() {
		if(encoder == null)
			return 0;
		return encoder.getProcessTime();
	}
	
	/**
	 * 设定录音状态
	 * 
	 * @param isRecording
	 */
	public void setRecording(boolean isRecording) {
		if(encoder != null) {
			encoder.setHang(isRecording);
		}
	}

	/**
	 * 获取录音状态
	 * 
	 * @return
	 */
	public boolean isRecording() {
		if(encoder != null) {
			return encoder.isRecording();
		}
		return false;
	}

	/**
	 * 写日志小函数
	 * @param s
	 */
	public void logd(String s) {
		Log.d("AudRec", s);
	}
}
