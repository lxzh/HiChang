package hichang.ourView;

import hichang.Song.Song;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class MusicItemView  extends View{
	private Bitmap numBm;
	private Song song; 
	private String nowSinger;
	private Paint textPaint;
	private final int songX=75;
	private final int singerX=315;
	private final int borderWidth=5;
	private final int maxSongWidth=240;
	private final int maxSingerWidth=200;
	public MusicItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		textPaint=new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(StringUtil.getRawSize(context, TypedValue.COMPLEX_UNIT_SP, 12));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(song!=null){
			String songName=song.getName();
			String singerName=song.getSinger1().trim();
			canvas.translate(borderWidth, borderWidth);
			RectF rectF=new RectF(0, 0, 70, 70);
			canvas.drawBitmap(numBm, null, rectF, textPaint);
			StringUtil.drawText(song.getName(),songX,5,maxSongWidth,canvas,textPaint);
			StringUtil.drawText(song.getSinger1(),singerX,5,maxSingerWidth,canvas,textPaint);
			canvas.translate(-borderWidth, -borderWidth);
		}
	}

	public Bitmap getNumBm() {
		return numBm;
	}
	public void setNumBm(Bitmap numBm) {
		this.numBm = numBm;
	}
	public Song getSong() {
		return song;
	}
	public void setSong(Song song) {
		this.song = song;
		if(song!=null){
			nowSinger=song.getSinger1();
			if(song.getIsAvailable()==1){
				textPaint.setColor(Color.WHITE);
			}else{
				textPaint.setColor(Color.GRAY);
			}
		}
	}
	public String getNowSinger() {
		return nowSinger;
	}
	public void setNowSinger(String nowSinger) {
		this.nowSinger = nowSinger;
	}

	public Paint getTextPaint() {
		return textPaint;
	}
	public void setTextPaint(Paint textPaint) {
		this.textPaint = textPaint;
	}
}
