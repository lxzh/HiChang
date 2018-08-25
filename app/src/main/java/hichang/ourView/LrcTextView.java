package hichang.ourView;

import hichang.Song.Chapter;
import hichang.Song.Sentence;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LrcTextView extends SurfaceView implements Callback {

	// 注释：两个外部函数
	// drawText用来画歌词的，第一帧画出整个歌词，接下来的几帧，每一帧渐变。
	// clear 清屏将整个控件清空，在播放完这一句之后调用

	SurfaceHolder sfh;
	/**
	 * 第一句话和第二句话
	 */
	public Sentence firstSentence, secSentence;
	/**
	 * 字体大小
	 */
	int size = 50;
	/**
	 * 第一句的画笔
	 */
	Paint paint;
	/**
	 * 第二句的画笔
	 */
	Paint newPaint;
	/**
	 * 阴影
	 */
	LinearGradient shader;
	/**
	 * 画布
	 */
	Canvas canvas;
	/**
	 * 控件高度
	 */
	public int height, width;
	/**
	 * 当前字的标识
	 */
	public int chapterFlag;
	/**
	 * 当前字和下一个字
	 */
	Chapter nowChapter, nextChapter, lastChapter;
	/**
	 * 这句话中的字的数目
	 */
	int chapterSize;
	/**
	 * 阴影的结束点
	 */
	public float nowEndPos, preEndPos;
	/**
	 * 当前字的宽度
	 */
	float nowChapterLength;
	/**
	 * 开始坐标
	 */
	float firstStartX, secStartX, firstStartY, secStartY;
	boolean isKTV;
	/**
	 * 是否是第一句
	 */
	boolean isFirst = false;
	public int songLength;

	public LrcTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sfh = this.getHolder();
		sfh.addCallback(this);
		this.setZOrderOnTop(true);
		sfh.setFormat(PixelFormat.TRANSPARENT);

		size = 50;
		// 第一句的画笔
		paint = new Paint();
		// 第二句的画笔
		newPaint = new Paint();
		paint.setTextSize(size);
		paint.setColor(Color.WHITE);
		newPaint.setColor(Color.WHITE);
		newPaint.setTextSize(size);

		firstSentence = new Sentence();
		secSentence = new Sentence();
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		getWH();
		isFirst = false;
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		getWH();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
	}

	public void init(int songlength) {
		this.songLength = songlength;
		isFirst = false;
	}

	// 画出对应时刻的句子
	public void DrawSentence(float nowtime) {
		setPaintShader(nowtime);
		paint.setShader(shader);
		if (!isKTV) {
			canvas = sfh.lockCanvas(new Rect(0, 0, width, height / 2));
		} else {
			if (isFirst) {
				canvas = sfh.lockCanvas(new Rect(0, 0, width, height / 2));
			} else {
				canvas = sfh.lockCanvas(new Rect(0, height / 2, width, height));
			}
		}
		canvas.drawText(firstSentence.text, firstStartX, firstStartY, paint);
		sfh.unlockCanvasAndPost(canvas);
	}

	// 设置画笔
	public void setPaintShader(float nowtime) {

		if (nowtime - firstSentence.StartTimeofThis >= nextChapter.ChpofStart) {
			nowChapter = nextChapter;
			nowChapterLength = paint.measureText(nowChapter.text);

			preEndPos = paint.measureText(firstSentence.text.substring(0, chapterFlag));
			nowEndPos = preEndPos;

			shader = new LinearGradient(firstStartX, 0, firstStartX + nowEndPos, 0, new int[] {
					Color.YELLOW, Color.WHITE }, new float[] { 0.999f, 1 }, TileMode.CLAMP);

			chapterFlag++;
			if (chapterFlag < chapterSize) {
				nextChapter = firstSentence.anotherChapters.get(chapterFlag);
			} else {
				if (secSentence != null) {
					nextChapter = new Chapter(secSentence.StartTimeofThis, 0, 0, "");
				} else {
					nextChapter = new Chapter(songLength, 0, 0, "");
				}
			}
		}
		if (nowtime <= firstSentence.StartTimeofThis + nowChapter.ChpofStart + nowChapter.ChpofLast) {
			if (nowtime <= firstSentence.StartTimeofThis + lastChapter.ChpofStart
					+ lastChapter.ChpofLast) {
				nowEndPos = (nowtime - firstSentence.StartTimeofThis - nowChapter.ChpofStart)
						/ nowChapter.ChpofLast * nowChapterLength + preEndPos;
			}
			shader = new LinearGradient(firstStartX, 0, firstStartX + nowEndPos, 0, new int[] {
					Color.YELLOW, Color.WHITE }, new float[] { 0.99f, 1 }, TileMode.CLAMP);
		}
	}

	public void myDrawText(Sentence sentence1, Sentence sentence2, boolean isktv) {

		firstSentence = sentence1;
		secSentence = sentence2;

		chapterFlag = 0;
		chapterSize = firstSentence.anotherChapters.size();
		nowChapter = null;
		nextChapter = firstSentence.anotherChapters.get(0);
		lastChapter = firstSentence.anotherChapters.get(chapterSize - 1);
		nowEndPos = 0;
		preEndPos = 0;
		shader = null;
		paint.setShader(shader);

		isKTV = isktv;
		// 专业模式
		if (!isKTV) {
			size = 50;
			paint.setTextSize(size);
			newPaint.setTextSize(size);

			Clear();
			Clear();
			firstStartX = (width - newPaint.measureText(firstSentence.text)) / 2;
			firstStartY = height / 2 - 5;
			// 上来先画两句话
			canvas = sfh.lockCanvas();
			canvas.drawText(firstSentence.text, firstStartX, firstStartY, newPaint);
			if (secSentence != null) {
				secStartX = (width - newPaint.measureText(secSentence.text)) / 2;
				secStartY = height - 5;
				canvas.drawText(secSentence.text, secStartX, secStartY, newPaint);
			}
			sfh.unlockCanvasAndPost(canvas);
		}
		// KTV模式
		else if (isKTV) {
			size = 65;
			paint.setTextSize(size);
			newPaint.setTextSize(size);

			isFirst = !isFirst;
			if (isFirst) {
				ClearSec();
				ClearSec();
				// 如果句子字长小于8，就右居中
				if (firstSentence.text.length() >= 8) {
					firstStartX = 0;
				} else {
					firstStartX = width / 2 - newPaint.measureText(firstSentence.text);
				}

				firstStartY = height / 2 - 8;
				// 上来先画两句话
				canvas = sfh.lockCanvas();
				canvas.drawText(firstSentence.text, firstStartX, firstStartY, newPaint);
				if (secSentence != null) {
					// 如果句子字长小于10，就左居中
					if (secSentence.text.length() >= 8) {
						secStartX = width - newPaint.measureText(secSentence.text);
					} else {
						secStartX = width / 2;
					}
					secStartY = height / 2 + size + 8;
					canvas.drawText(secSentence.text, secStartX, secStartY, newPaint);
				}
				sfh.unlockCanvasAndPost(canvas);
			} else {
				ClearFirst();
				ClearFirst();

				// 上来先画两句话
				canvas = sfh.lockCanvas();
				if (secSentence != null) {
					// 如果句子字长小于10，就右居中
					if (secSentence.text.length() >= 8) {
						secStartX = 0;
					} else {
						secStartX = width / 2 - newPaint.measureText(secSentence.text);
					}
					secStartY = height / 2 - 8;
					canvas.drawText(secSentence.text, secStartX, secStartY, newPaint);
				}
				// 如果句子字长小于10，就左居中
				if (firstSentence.text.length() >= 8) {
					firstStartX = width - newPaint.measureText(firstSentence.text);
				} else {
					firstStartX = width / 2;
				}
				firstStartY = height / 2 + size + 8;
				canvas.drawText(firstSentence.text, firstStartX, firstStartY, newPaint);
				sfh.unlockCanvasAndPost(canvas);
			}
		}
	}

	// 清屏函数
	//
	public void Clear() {
		Canvas canvas = sfh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
		sfh.unlockCanvasAndPost(canvas);
	}

	public void ClearFirst() {
		Canvas canvas = sfh.lockCanvas(new Rect(0, 0, width, height / 2));
		canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
		sfh.unlockCanvasAndPost(canvas);
	}

	public void ClearSec() {
		Canvas canvas = sfh.lockCanvas(new Rect(0, height / 2, width, height));
		canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
		sfh.unlockCanvasAndPost(canvas);
	}

	public void getWH() {
		height = getHeight();
		width = getWidth();
	}
}
