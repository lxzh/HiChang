package hichang.ourView;

import hichang.Song.Song;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class RankMusicItemView extends View{
	private Bitmap numBm;
	private Bitmap heatBm;
	private Song song; 
	private Paint textPaint;
	private float spPerPx;
	private final int songX=78;
	private final int heatX=413;
	private final int singerX=462;
	private final int borderWidth=5;
	private final int maxSongWidth=323;
	private final int maxSingerWidth=184;
	public RankMusicItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		textPaint=new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setAntiAlias(true);
		Log.d("RankMusicItemView", StringUtil.getRawSize(context, TypedValue.COMPLEX_UNIT_SP, 15)+"");
		textPaint.setTextSize(StringUtil.getRawSize(context, TypedValue.COMPLEX_UNIT_SP, 15));
		textPaint.setTextAlign(Align.LEFT);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (song != null) {
			String songName = song.getName();
			String singerName = song.getSinger1().trim();
			canvas.translate(borderWidth, borderWidth);
			RectF rectF = new RectF(0, 0, 70, 70);
			canvas.drawBitmap(numBm, null, rectF, textPaint);
			StringUtil.drawText(song.getName(), songX, 0, maxSongWidth, canvas, textPaint);
			rectF = new RectF(heatX, 19, heatX + 38, 19 + 32);
			canvas.drawBitmap(heatBm, null, rectF, textPaint);
			StringUtil.drawText(song.getSinger1(), singerX, 0, maxSingerWidth, canvas, textPaint);
			canvas.translate(-borderWidth, -borderWidth);
		}
	}

	public Bitmap getNumBm() {
		return numBm;
	}
	public void setNumBm(Bitmap numBm) {
		this.numBm = numBm;
	}
	public Bitmap getHeatBm() {
		return heatBm;
	}
	public void setHeatBm(Bitmap heatBm) {
		this.heatBm = heatBm;
	}
	public Song getSong() {
		return song;
	}
	public void setSong(Song song) {
		this.song = song;
		if(song!=null){
			if(song.getIsAvailable()==1){
				textPaint.setColor(Color.WHITE);
			}else{
				textPaint.setColor(Color.GRAY);
			}
		}
	}
	public Paint getTextPaint() {
		return textPaint;
	}
	public void setTextPaint(Paint textPaint) {
		this.textPaint = textPaint;
	}
}
