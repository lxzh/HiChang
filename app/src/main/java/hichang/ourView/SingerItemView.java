package hichang.ourView;

import hichang.Song.Singer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class SingerItemView extends View {

	private Bitmap imageBm;
	private Singer singer; 
//	private Bitmap numBm;
	private Paint textPaint;
	private final int borderWidth=11;
	private final int singerX=160;
	private final int singerY=30;
	private final int maxSingerWidth=150;//height=43
	public SingerItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		textPaint=new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(StringUtil.getRawSize(context, TypedValue.COMPLEX_UNIT_SP, 12));
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (singer != null) {
			canvas.translate(borderWidth, borderWidth);
			String singerName = singer.getName().trim();
			RectF rectF = new RectF(0, 0, 148, 148);
			canvas.drawBitmap(imageBm, null, rectF, textPaint);
			StringUtil.drawText(singerName, singerX, 0, maxSingerWidth, canvas, textPaint);
			canvas.translate(-borderWidth, -borderWidth);
		}
	}
	public Bitmap getImageBm() {
		return imageBm;
	}
	public void setImageBm(Bitmap imageBm) {
		this.imageBm = imageBm;
	}
	public Singer getSinger() {
		return singer;
	}
	public void setSinger(Singer singer) {
		this.singer = singer;
	}

	public Paint getTextPaint() {
		return textPaint;
	}
	public void setTextPaint(Paint textPaint) {
		this.textPaint = textPaint;
	}

}
