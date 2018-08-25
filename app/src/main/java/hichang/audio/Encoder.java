package hichang.audio;

import hichang.Song.CMediaPlayer;
import hichang.Song.Sentence;

import java.io.FileOutputStream;
import java.util.ArrayList;

import android.os.Handler;

public class Encoder implements Runnable {

	private volatile int leftSize = 0;
	private final Object mutex = new Object(); // 互斥量
	private long startTime;
	private long sentenceStartTime = 0;   // 句子起始时间, 用于练歌时前进或后退几句
	private long ts = 0; //
	private short[] rawdata = new short[1024]; // 存录音数据的的数组
	private volatile boolean isRecording; // 是否在录制
	private float pitch; // 目前音高
	private int[] note;  // 音符数组
	private float[] pitchArray;  // 音高数组
	private int tempBufferSize = 160; // 临时buffer大小, 等于YIN算法的输入数据个数
	private long preludeTime = 0;     // 前奏时间
	private float mark[];               // 句子评分数组
	private int results[];
	private int markPointCount[];       // 当前句子评分点个数
	private ArrayList<Sentence> sentences = new ArrayList<Sentence>();
	private int previousRecordIndex = 0;
	private int currentRealNote = 0;
	private int currentReadIndex = 0;
	private int currentRecordIndex = 0;
	private int currentSingNote = 0;
	private int previousReadIndex = 0;
	private Handler handler;
	private CMediaPlayer mediaPlayer;
	private boolean bPlayerStart = false;
	private boolean bHangOn = false;
	private int currentPos = 0;
	private float currentMark = 0.0f;
	private long processTime = 0;
	private int falseCount = 0;
	private static final int MAXFALSECOUNT = 3;
	
	private static final float[] frequency = {27.5f,29.14f,30.87f,32.7f,34.65f,36.71f,38.89f,41.2f,43.65f,46.25f,49f,51.91f,55f,58.27f,61.74f,65.41f,69.3f,73.42f,77.78f,82.41f,87.31f,92.5f,98f,103.8f,
		110f,116.5f,123.5f,130.8f,138.6f,146.8f,155.6f,164.8f,174.6f,185f,196f,207.6f,220f,233.1f,246.9f,261.6f,277.2f,293.7f,311.1f,329.6f,349.2f,370f,392f,415.3f,
        440f,466.2f,493.9f,523.2f,554.4f,587.3f,622.2f,659.3f,698.5f,740f,784f,830.6f,880f,932.3f,987.8f,1046f,1109f,1175f,1245f,1319f,1397f,1480f,1568f,1661f,
        1760f,1865f,1976f,2093f,2217f,2349f,2489f,2637f,2794f,2960f,3136f,3322f,3520f,3729f,3951f,4186f};

	public int getCurrentRecordIndex() {
		return currentRecordIndex;
	}
	
	public int getCurrentReadindex() {
		return currentReadIndex;
	}

	public int getCurrentRealNote() {
		return currentRealNote;
	}
	
	public float getCurrentMark() {
		return currentMark;
	}
	
	public Encoder(ArrayList<Sentence> sentences, Handler handler, CMediaPlayer nowMediaPlayer) {
		super();
		this.sentences = sentences;
		this.handler = handler;
		this.mediaPlayer = nowMediaPlayer;
		Sentence tempSen = sentences.get(sentences.size() - 1);
		note = new int[(tempSen.StartTimeofThis + tempSen.LastTimeofThis) / 40 + 1];
		results = new int[(tempSen.StartTimeofThis + tempSen.LastTimeofThis) / 40 + 1];
		pitchArray = new float[(tempSen.StartTimeofThis + tempSen.LastTimeofThis) / 40 + 1];
		for (int i = 0; i < (tempSen.StartTimeofThis + tempSen.LastTimeofThis) / 40 + 1; i++) {
			note[i] = 0;
			results[i] = -10;
			pitchArray[i] = 0.0f;
		}
		Sentence firstSen = sentences.get(0);
		this.preludeTime = firstSen.StartTimeofThis;
		mark = new float[sentences.size()];
		markPointCount = new int[sentences.size()];
		for (int i = 0; i < sentences.size(); i++) {
			mark[i] = 0;
			markPointCount[i] = 0;
		}
	}

	public void run() {

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);		

		while (this.isRecording() || bHangOn) {
			if (isIdle() || bHangOn) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			synchronized (mutex) {
							    
				currentPos = mediaPlayer.CGetCurrentPosition();
				currentPos -= 400;
				if (currentPos < preludeTime) {
					continue;
				}
				
				int currentNote = 0;
				float[] m_data = new float[tempBufferSize];
				for (int i = 0; i < tempBufferSize; i++) {
					m_data[i] = rawdata[i] / 32767.0f;
				}

				pitch = new YIN(m_data).getPitch();

				currentNote = getNote();
				currentSingNote = currentNote;
				
				int index = currentPos / 40;
				currentRecordIndex = index;
				
				int i = 0;
				if (index < note.length) {
					int playTime = currentPos;
					for (int k = previousRecordIndex + 1; k <= index; k++) {
						
						note[k] = currentNote;
						pitchArray[k] = pitch;

						playTime -= 40 * (index - k);
						int realNote = 0;
						for (i = 0; i < sentences.size(); i++) {
							Sentence tempSen = sentences.get(i);
							if (playTime <= (tempSen.StartTimeofThis + tempSen.LastTimeofThis)
									&& playTime >= tempSen.StartTimeofThis) {
								if(playTime - 40 < tempSen.StartTimeofThis) {
									markPointCount[i] = 0;
									mark[i] = 0.0f;
								}
								for (int m = 0; m < tempSen.getChaptetCount(); m++) {
									if (playTime - tempSen.StartTimeofThis <= (tempSen.getChapterStart(m) + tempSen.getChapterLast(m))
											&& playTime	- tempSen.StartTimeofThis >= tempSen.getChapterStart(m)) {
										realNote = tempSen.getChapterHigh(m);
										break;
									}
								}
								break;
							}
						}

						currentRealNote = realNote;

						if (realNote != 0) {
							if(currentNote == 0) {
								if(note[previousRecordIndex] != 0 && results[previousRecordIndex] == 0) {
									results[k] = 0;
									markPointCount[i]++;
									mark[i] += 5;
								}
								else if(note[previousRecordIndex] != 0 && results[previousRecordIndex] != 0) {
									results[k] = results[previousRecordIndex];
									markPointCount[i]++;
									if(Math.abs(results[k]) > 5) {
										mark[i] += 0;
									}
									else {
										mark[i] += (5 - Math.abs(results[k]));
									}
								}
								else {
									results[k] = 10;
									markPointCount[i]++;
									mark[i] += 0;
								}
							}
							else {
								int temp =  (currentNote%12) + realNote/12*12;
								if(Math.abs(temp - realNote) < 2) {
									results[k] = 0;
								}
								else {
									if(temp - realNote > 0) {
										results[k] = temp - realNote - 2;
									}
									else {
										results[k] = temp - realNote + 2;
									}
								}								

								markPointCount[i]++;
								if(Math.abs(results[k]) > 5) {
									mark[i] += 0;
								}
								else {
									mark[i] += (5 - Math.abs(results[k]));
								}
							}														
							
						} else {
							results[k] = 0;
						}
					}
				}
				processTime = System.currentTimeMillis() - ts;
				previousRecordIndex = index;

				setIdle();
			}
		}
	}

	public long getProcessTime() {
		return processTime;
	}

	/**
	 * 获取声音频率
	 * 
	 * @return
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * 获取音符, 仅在Encoder类内部使用
	 * 
	 * @return
	 */
	private int getNote() {
		if (this.pitch - 0.0f < 1e-4) {
			return 0;
		}
		return (int) Math.round(69 + 12 * Math.log(this.pitch / 440)/ Math.log(2));
	}
	
	private int getRealNote(long time) {
		int realNote = 0;
		for (int i = 0; i < sentences.size(); i++) {
			Sentence tempSen = sentences.get(i);
			if (time <= (tempSen.StartTimeofThis + tempSen.LastTimeofThis)
					&& time >= tempSen.StartTimeofThis) {
				for (int m = 0; m < tempSen.getChaptetCount(); m++) {
					if (time - tempSen.StartTimeofThis <= (tempSen.getChapterStart(m) + tempSen.getChapterLast(m))
							&& time	- tempSen.StartTimeofThis >= tempSen.getChapterStart(m)) {
						realNote = tempSen.getChapterHigh(m);
						break;
					}
				}
				break;
			}
		}
		return realNote;
	}
	
	public int[] getNote(long time) {		
		int retNote[];
		
		int index = (int) (time / 40);
		currentReadIndex = index;
		if(!isRecording() || (index >= previousRecordIndex) || (time < preludeTime) || (index <= previousReadIndex) || (index < 0) || (index >= note.length))
		{
			return (new int[0]);
		}
				
		retNote = new int[index - previousReadIndex];
		
		for(int k = 0; k < index - previousReadIndex ; k++) {
			retNote[k] = results[previousReadIndex+k+1];				
		}
		falseCount = 0;
		for (int k = 0; k < retNote.length; k++) {
			if(0 == getRealNote((previousReadIndex+k+1)*40)) {
				retNote[k] = 10;
				continue;
			}
			if(retNote[k] != 0) {
				if(k == 0) {
					falseCount = 1;
				}
				else {
					if(retNote[k-1] == retNote[k]) {
						falseCount++;
					}
					else {
						for(int i = 1; i <= falseCount; i++) {
							if(k-i >= 0) retNote[k-i] = 0;
						}
						falseCount = 0;
					}
				}
			}
			if(falseCount == MAXFALSECOUNT) {
				falseCount = 0;
			}
		}
		if(falseCount > 0 && falseCount < MAXFALSECOUNT && falseCount < (index - previousReadIndex)) {
			int retNote2[] = new int[index - previousReadIndex - falseCount];
			for (int i = 0; i < retNote2.length; i++) {
				retNote2[i] = retNote[i];
			}
			falseCount = 0;
			previousReadIndex = index - previousReadIndex - falseCount;
			return retNote2;
		}
		
		previousReadIndex = index;
		return retNote;
	}
	
	public void writeFileSdcard(String fileName,String message){ 
	      try{ 
	    	  FileOutputStream fout = new FileOutputStream(fileName);
	    	  byte [] bytes = message.getBytes();  	
	    	  fout.write(bytes); 
	    	  fout.close(); 
    	  } 
	      catch(Exception e) { 
	    	  e.printStackTrace(); 
    	  } 
    }
	
	/**
	 * 评分
	 * @param realNote
	 * @param singNote
	 * @param singPitch
	 * @return
	 */
	private int calculateMark(int realNote, int singNote, float singPitch) {		
		float leftPitch;
		float rightPitch;
		float realPitch = frequency[realNote-21];
		int mark;
		if(realNote > 21) {
			leftPitch = frequency[realNote-22];
			rightPitch = frequency[realNote-20];
		}
		else{
			leftPitch = frequency[realNote-21];
			rightPitch = frequency[realNote-20];
		}
		
		int tempIndex = singNote / 12 * 12 - 21;
		float scale = (singPitch - frequency[tempIndex]) / (frequency[tempIndex+12] - frequency[tempIndex]);
		tempIndex = realNote / 12 * 12 - 21;
		float comparedPitch = frequency[tempIndex] + scale*(frequency[tempIndex+12] - frequency[tempIndex]);
		if(comparedPitch > realPitch*0.93 && comparedPitch < realPitch*1.07) {
			mark = 100;
		} 
		else if(comparedPitch < leftPitch*0.93 || comparedPitch > rightPitch*1.07) {
			mark = 0;
		}
		else if(comparedPitch > realPitch*1.07) {
			mark = (int) ((comparedPitch - realPitch*1.07f)/(rightPitch*1.07f - realPitch*1.07f));
		}
		else {
			mark = -1 * (int) ((realPitch*0.93f - comparedPitch)/(realPitch*0.93f - leftPitch*0.93f));
		}
		return mark;
	}
	
	/**
	 * 获取某一个句子的评分
	 * @param indexOfSentence 句子的索引
	 * @return
	 */
	public int getMark(int indexOfSentence)	{
		if (Math.round(20 * mark[indexOfSentence]
				/ (float) markPointCount[indexOfSentence]) >= 100) {
			return 99;
		} else {
			return Math.round(20 * mark[indexOfSentence]
					/ (float) markPointCount[indexOfSentence]);
		}
	}

	/**
	 * 向Encoder填入从麦克风得到的数据
	 * 
	 * @param ts
	 * @param data
	 * @param size
	 */
	public void putData(int playTime, long ts, short[] data, int size) {
		synchronized (mutex) {			
			if(!isRecording) {
				return;
			}
			if (this.ts == 0) {
				if(!bPlayerStart) {
					handler.sendEmptyMessage(200);
					bPlayerStart = true;
				}
				startTime = ts;
			}
			this.ts = ts;
			this.currentPos = playTime;
			System.arraycopy(data, 0, rawdata, 0, size);
			this.leftSize = size;
		}
	}

	/**
	 * 设定起始时间，用于练歌时前进或后退几句
	 * 
	 * @param startTime
	 */
	public void setStartTime(int startTime) {
		sentenceStartTime = startTime + 80;
		this.ts = 0;
		previousReadIndex = (startTime - 40)/40;
	}

	/**
	 * 判断Encoder是否空闲
	 * 
	 * @return
	 */
	public boolean isIdle() {
		synchronized (mutex) {
			return leftSize == 0 ? true : false;
		}
	}

	/**
	 * 设定Encoder为空闲
	 */
	public void setIdle() {
		leftSize = 0;
	}

	/**
	 * 设定录音状态
	 * 
	 * @param isRecording
	 */
	public void setHang(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			bHangOn = !isRecording;
		}
	}
	
	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
		}
	}
	
	/**
	 * 获取录音状态
	 * 
	 * @return
	 */
	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}

	public int getTotalMark() {
		float totalMark = 0.0f;
		for (int i = 0; i < sentences.size(); i++) {
			totalMark += getMark(i);			
		}
		if(Math.round(totalMark/sentences.size()) >= 100)
			return 99;
		return Math.round(totalMark/sentences.size());
	}
}
