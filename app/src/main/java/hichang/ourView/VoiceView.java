package hichang.ourView;

import hichang.Song.Chapter;
import hichang.Song.Sentence;

import java.util.ArrayList;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

/*
 * 在调用DrawLrc（Sentence sentcet）之前
 * 一定要调用init方法初始化最大音高和最小音高
 * 否则会出错
 * 
 * 
 * 在声音传入事件被触发之后，调用drawChange函数
 */
public class VoiceView extends SurfaceView implements
		android.view.SurfaceHolder.Callback {
	SurfaceHolder sfh;
	public float density = 0; // 记录每句话的图像密度 一个字的相对起始位置
	private Sentence sentence;// 当前所要绘的句子；
	private int max;// 这首歌的最大音高
	private int min;// 这首歌的最小音高

	Canvas canvas;
	Paint paint, textPaint, pointPaint, wrongPaint, linePaint;
	public float width;
	public float height;
	float[] screenlocation;
	public float rectHeight;
    public float pitchHeight;
	
	int nowPointColors[];
	float nowPointPos[];

	int nowChapterFlag;
	float nowHigh;
	int rectLeft, rectRight;
	LinearGradient shader;
	Chapter nowChapter, nextChapter, lastChapter;
	/**
	 * nowPointNum:现在要画的字的点数
	 */
	int nowPoint;
	/**
	 * nowPoint:现在画到的点数
	 */
	int nowPointNum;
	/**
	 * 这个字的点的位置和初始颜色准备好了没有
	 */
	boolean isPrepared = false;
	public boolean isKTV = false;
	/**
	 * 当前字唱错的点的点集
	 */
    ArrayList<Float> wrongPointX=new ArrayList<Float>(); 
    ArrayList<Integer> wrongResult=new ArrayList<Integer>();
    
    int preLineX=0;
	public VoiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sfh = this.getHolder();
		sfh.addCallback(this);
		this.setZOrderOnTop(true);
		sfh.setFormat(PixelFormat.TRANSLUCENT);

		paint = new Paint();
		paint.setColor(Color.YELLOW);// 设置每句话的颜色
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2.5f);
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);// 设置字体的颜色
		textPaint.setTextSize(40);// 设置字体的大小
		pointPaint = new Paint();
		wrongPaint = new Paint();
		wrongPaint.setColor(Color.rgb(255, 69, 0));
		linePaint=new Paint();
		linePaint.setColor(Color.DKGRAY);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		getWH();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		getWH();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	// 画每句话的曲线
	// sentence是当前要画的句子
	public void DrawLrc(Sentence sentence, boolean isktv) {

		getWH();
		isKTV = isktv;
		getWH();
		
		Clear(isktv);
		Clear(isktv);

		// 调用两次clear 清除双缓冲
		this.sentence = sentence;
		isPrepared = false;
		nowChapterFlag = 0;
		nextChapter = sentence.mychapter.get(0);
		lastChapter = sentence.mychapter.get(sentence.mychapter.size() - 1);
		canvas = sfh.lockCanvas();
		screenlocation = new float[sentence.getChaptetCount()];
		screenLocation(screenlocation, sentence);

		if (isKTV) {
			DrawWhiteLines(canvas);

			RectF rectF;
			float high;
			for (int i = 0; i < sentence.getChaptetCount(); i++) {
				high = chapterHigh(sentence.getChapterHigh(i));
				rectF = new RectF((int) screenlocation[i], (int)high,
						(int) (screenlocation[i] + sentence.getChapterLast(i)
								* density), (int)high + rectHeight);
				// 记录所要画的圆角矩形
				canvas.drawRoundRect(rectF, 8, 8, paint);
			} // 设置字体在音乐下方的位置
			sfh.unlockCanvasAndPost(canvas);
			// 画出一句完整的歌词

		} else {
			RectF rectF;
			float high;
			for (int i = 0; i < sentence.getChaptetCount(); i++) {
				high = chapterHigh(sentence.getChapterHigh(i));
				rectF = new RectF((int) screenlocation[i], (int)high,
						(int) (screenlocation[i] + sentence.getChapterLast(i)
								* density), (int)high + rectHeight);

				// 记录所要画的圆角矩形
				canvas.drawRoundRect(rectF, 8, 8, paint);
				canvas.drawText(sentence.getChapterText(i),
						(int) screenlocation[i] + sentence.getChapterLast(i)
								* density / 4,// 设置字体在应高下面的位置
						high + 100, textPaint);
			} // 设置字体在音乐下方的位置
			sfh.unlockCanvasAndPost(canvas);
			// 画出一句完整的歌词
		}
	}

	private void screenLocation(float[] location, Sentence sentence) {

		density = (width -20)/ sentence.LastTimeofThis;// 图像的像素/时间（每毫秒走多少像素）
		for (int k = 0; k < sentence.getChaptetCount(); k++) {
			location[k] = density * sentence.getChapterStart(k)+10;
		}
	}// 计算每个字在图像上的起始位置

	private float chapterHigh(int thishigh) {

		return height - pitchHeight * (thishigh - min + 1 + 4);// 音高乘以每份的高度
	}// 计算每个字的在屏幕上的正确高度

	// 初始化这首歌的最大音高和最小音高
	public void init(int max, int min) {
		this.max = max;
		this.min = min;
		// this.handImage=hand;
	}

	public void Clear(boolean isktv) {
		canvas = sfh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT,
				android.graphics.PorterDuff.Mode.CLEAR);
		if (isktv) {
			DrawWhiteLines(canvas);
		}
		sfh.unlockCanvasAndPost(canvas);
	}// 清屏
	
	public void ClearTotal() {
		canvas = sfh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT,
				android.graphics.PorterDuff.Mode.CLEAR);
	
		sfh.unlockCanvasAndPost(canvas);
	}// 清屏
	
	// 画白色底线
	public void DrawWhiteLines(Canvas canvas) {
		float[] pts = new float[10];
		pts[0] = height / 11;
		canvas.drawLine(0, pts[0], width, pts[0], linePaint);
		for (int i = 1; i < 10; i++) {
			pts[i] = pts[i - 1] + height / 11;
			canvas.drawLine(0, pts[i], width, pts[i], linePaint);
		}
	}

	public int drawPoints(int nowTime, int result) {
		if (nowTime < sentence.StartTimeofThis + lastChapter.ChpofStart + lastChapter.ChpofLast + 40)
        {
			if (isPrepared) 
			{
				if ((nowTime >= (nowChapter.ChpofStart + sentence.StartTimeofThis))&&(nowTime < (nowChapter.ChpofStart + nowChapter.ChpofLast + sentence.StartTimeofThis + 40))) 
				{
					Log.i("3", nowTime - nowChapter.ChpofStart
							- sentence.StartTimeofThis + "");

					if (nowTime > nowChapter.ChpofStart + nowChapter.ChpofLast + sentence.StartTimeofThis) 
					{
						isPrepared = false;
					}
					setColor(result);

					shader = new LinearGradient(rectLeft, 0, rectRight, 0,nowPointColors, nowPointPos, TileMode.CLAMP);
					pointPaint.setShader(shader);
					canvas = sfh.lockCanvas(new Rect(rectLeft, 0, rectRight,(int) height));
					if (!isKTV)
					{
						canvas.drawText(nowChapter.text,rectLeft + sentence.getChapterLast(nowChapterFlag - 1)* density / 4,nowHigh + 100, textPaint);
					}
					else 
					{
						DrawWhiteLines(canvas);
					}
					
					// 避免闪烁，重画当前的圆角矩形和当前字
					canvas.drawRoundRect(new RectF(rectLeft, (int) nowHigh,rectRight, (int) (nowHigh + rectHeight)), 8,8, paint);
					

					// 填充圆角矩形
					canvas.drawRoundRect(new RectF(rectLeft, (int) nowHigh,	rectRight, (int) (nowHigh + rectHeight)), 8, 8,pointPaint);

					for (int i = 0; i < wrongPointX.size(); i++)
					{
						canvas.drawRect(
								new Rect(
										(int) (rectLeft + (rectRight - rectLeft)
												* (wrongPointX.get(i) - 1.0f / nowChapter.ChpofLast * 40)),
										(int) (nowHigh + wrongResult.get(i)
												* pitchHeight),
										(int) (rectLeft + (rectRight - rectLeft)
												* wrongPointX.get(i)),
										(int) (nowHigh + pitchHeight + wrongResult
												.get(i) * pitchHeight)),
								wrongPaint);
					}
					sfh.unlockCanvasAndPost(canvas);
				}
			}
			if (nowTime >= nextChapter.ChpofStart + sentence.StartTimeofThis)
			{
				nowChapter = nextChapter;
				rectLeft = (int) screenlocation[nowChapterFlag];
				rectRight = (int) (rectLeft + nowChapter.ChpofLast * density);
				nowHigh = chapterHigh(nowChapter.ChpofHigh);
				wrongPointX.clear();
				wrongResult.clear();
				initPosAndColor(nowChapter.ChpofLast, nowTime - sentence.StartTimeofThis - nowChapter.ChpofStart,nowTime);
				nowChapterFlag++;
				if (nowChapterFlag == sentence.mychapter.size()) 
				{
					nextChapter = new Chapter(500000, 0, 0, "");
				}
				else
				{
					nextChapter = sentence.mychapter.get(nowChapterFlag);
				}
				setColor(result);

				shader = new LinearGradient(rectLeft, 0, rectRight, 0,
						nowPointColors, nowPointPos, TileMode.CLAMP);
				pointPaint.setShader(shader);

				canvas = sfh.lockCanvas(new Rect(rectLeft, 0, rectRight,(int) height));

				if (!isKTV) 
				{
					canvas.drawText(
							nowChapter.text,
							rectLeft
									+ sentence
											.getChapterLast(nowChapterFlag - 1)
									* density / 4,// 设置字体在应高下面的位置
							nowHigh + 100, textPaint);
				}
				else
				{
					DrawWhiteLines(canvas);
				}
				// 避免闪烁，重画当前的圆角矩形和当前字
				canvas.drawRoundRect(new RectF(rectLeft, (int) nowHigh,
						rectRight, (int) (nowHigh + rectHeight)), 8, 8,
						paint);

				// 填充圆角矩形
				canvas.drawRoundRect(new RectF(rectLeft, (int) nowHigh,
						rectRight, (int) (nowHigh + rectHeight)), 8, 8,
						pointPaint);

				for (int i = 0; i < wrongPointX.size(); i++) 
				{
					canvas.drawRect(
							new Rect(
									(int) (rectLeft + (rectRight - rectLeft)
											* (wrongPointX.get(i) - 1.0f / nowChapter.ChpofLast * 40)),
									(int) (nowHigh + wrongResult.get(i)
											* pitchHeight),
									(int) (rectLeft + (rectRight - rectLeft)
											* wrongPointX.get(i)),
									(int) (nowHigh + pitchHeight + wrongResult
											.get(i) * pitchHeight)),
							wrongPaint);
				}
				sfh.unlockCanvasAndPost(canvas);
				isPrepared = true;
			}
		}
        preLineX=(int)((float)(nowTime-sentence.StartTimeofThis)/sentence.LastTimeofThis*width);
        return preLineX;
	}
	/**
	 * 初始化每个字点的位置信息和颜色
	 * @param nowChapterLastTime 当前字的持续时间
	 * @param firstPosTime   目前字的开始时间
	 * @param nowTime  目前播放的时间
	 */
	public void initPosAndColor(int nowChapterLastTime, int firstPosTime,
			int nowTime) {
		Log.i("1", firstPosTime + "");
		Log.i("2", nowChapterLastTime + "");

		nowPointPos = null;
		nowPointColors = null;
		nowPoint = 0;
		if (nowChapterLastTime % 40 == 0) {
			nowPointNum = nowChapterLastTime / 40 + 1;
		} else {
			nowPointNum = nowChapterLastTime / 40 + 2;
		}
		nowPointPos = new float[2 * nowPointNum];
		nowPointPos[0] = 1.0f / nowChapterLastTime * firstPosTime;
		nowPointPos[1] = nowPointPos[0];
		for (int i = 2; i < nowPointPos.length; i += 2) {
			nowPointPos[i] = nowPointPos[i - 1] + 1.0f / nowChapterLastTime
					* 40;
			nowPointPos[i + 1] = nowPointPos[i];
		}
		if (nowPointPos[nowPointPos.length - 1] > 1) {
			nowPointPos[nowPointPos.length - 2] = 1;
			nowPointPos[nowPointPos.length - 1] = 1;
		}
		nowPointColors = new int[2 * nowPointNum];
		for (int i = 0; i < nowPointColors.length; i++) {
			nowPointColors[i] = Color.TRANSPARENT;
		}
	}

	public void setColor(int result) {
		nowPoint++;
		if (result == 0)
		{
			if (nowPoint == 1)
			{
				nowPointColors[0] = Color.rgb(34, 139, 34);
			}
			else if (nowPoint != 0)
			{
				nowPointColors[(nowPoint - 1) * 2] = Color.rgb(34, 139, 34);
				nowPointColors[(nowPoint - 1) * 2 - 1] = Color.rgb(34, 139, 34);
		    } 
		} 
		else 
		{
			wrongPointX.add((Float)nowPointPos[2*nowPoint-1]);
			wrongResult.add((Integer)result);
		}
	}

	public void getWH() {
		width = getWidth();
		height = getHeight();
        
		pitchHeight = height / (max - min + 9);
	    if(isKTV){
	    	rectHeight=3;
	    	paint.setStyle(Style.FILL);
	    }
	    else{
	    	rectHeight=30;
	    	paint.setStyle(Style.STROKE);
	    }
	}
}
