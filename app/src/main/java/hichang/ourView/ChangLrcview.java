package hichang.ourView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import hichang.Song.*;

public class ChangLrcview extends SurfaceView implements Callback {
	/*
	 * 唱歌界面的歌词显示 调用mydrawtext方法自动填充歌词 drawtex方法只画出当前歌词 部填充
	 */

	SurfaceHolder sfh;
	float time = 0;

	public ChangLrcview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		sfh = this.getHolder();
		sfh.addCallback(this);
		this.setZOrderOnTop(true);
		sfh.setFormat(PixelFormat.TRANSPARENT);
	}

	public ChangLrcview(Context context, AttributeSet attrs) {
		super(context, attrs);
		sfh = this.getHolder();
		sfh.addCallback(this);
		this.setZOrderOnTop(true);
		sfh.setFormat(PixelFormat.TRANSPARENT);
	}

	public ChangLrcview(Context context) {
		super(context);
		sfh = this.getHolder();
		sfh.addCallback(this);
		this.setZOrderOnTop(true);
		sfh.setFormat(PixelFormat.TRANSPARENT);
	}

	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@SuppressWarnings("unused")
	public void drawText(Sentence sentence1, Sentence sentence2, int interval) {
		int size = 30;
		Paint paint = new Paint();
		paint.setTextSize(size);// 设置画笔大小，默认为30
		Paint newpaint = new Paint();
		newpaint.setTextSize(size);
		newpaint.setColor(Color.WHITE);
		int wordWidth = (int) paint.measureText(sentence1.text);
		while (wordWidth >= getWidth())// 如果字体长度大于整个控件长度，减小画笔大小
		{
			size -= 5;
			paint.setTextSize(size);
			wordWidth = (int) paint.measureText(sentence1.text);
		}

		setPaintShader(paint, sentence1, interval, wordWidth);// 设置画笔的阴影
		int x = 0;// 设置字体靠左边
		int y = getHeight() / 2;// 尽量靠上画
		int x1 = getWidth() - (int) newpaint.measureText(sentence2.text);// 第二句话的起始位置
		if (sentence2 != null) {
			Canvas canvas = sfh.lockCanvas();
			canvas.drawText(sentence1.text, x, y, paint);
			canvas.drawText(sentence2.text, x1, y + 40, newpaint);// 把第二个字换在第一个字下方40像素的位置
			sfh.unlockCanvasAndPost(canvas);
		} else {
			Canvas canvas = sfh.lockCanvas();
			canvas.drawText(sentence1.text, x, y, paint);
			sfh.unlockCanvasAndPost(canvas);
		}
	}

	private void setPaintShader(Paint paint, Sentence sentence, float interval, float wordwidth) {
		float density;// 每毫秒应该前进的像素
		density = wordwidth / sentence.LastTimeofThis;

		LinearGradient shader = new LinearGradient(0, // 起始位置=字体的居中起始位置
				getHeight() / 20, time + 1, getHeight() / 20,
				// 设置画笔的终点阴影位置
				new int[] { Color.BLUE, Color.WHITE }, new float[] { 0.99f, 1 }, TileMode.CLAMP);
		time = time + interval * density;// 增加长度
		paint.setShader(shader);
	}

	class MyThread implements Runnable {
		// 开启多线程来自动画图
		private Sentence sentence1;
		private Sentence sentence2;
		private int interval;
		private int runtime = 0;

		public MyThread(Sentence sentence1, Sentence sentence2, int interval) {
			this.sentence1 = sentence1;
			this.sentence2 = sentence2;
			this.interval = interval;
		}

		public void run() {
			Clear();
			while (runtime <= sentence1.LastTimeofThis) {
				drawText(sentence1, sentence2, interval);

				try {
					Thread.sleep(interval);
					runtime += interval;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void myDrawText(Sentence sentence1, Sentence sentence2, int interval) {
		Clear();
		Clear();// 调用两次清屏，清除双缓冲区
		MyThread thread = new MyThread(sentence1, sentence2, interval);
		Thread td = new Thread(thread);
		td.start();
	}

	public void Clear() {
		Canvas canvas = sfh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
		sfh.unlockCanvasAndPost(canvas);
		time = 0;
	}
}
