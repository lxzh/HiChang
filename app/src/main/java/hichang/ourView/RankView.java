package hichang.ourView;

import java.util.ArrayList;

import hichang.Song.Song;
import hichang.activity.R;
import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.nfc.tech.IsoDep;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class RankView extends View{
	
	private ArrayList<Song> songs =new ArrayList<Song>();
	private Paint textPaint;
	private int page,maxPage;
	Bitmap numBm[],heatBm[];
	public RankView(Context context, AttributeSet attrs) {
		super(context, attrs);
		textPaint=new Paint();
		textPaint.setColor(Color.GRAY);
		textPaint.setTextSize(45);
		
		numBm=new Bitmap[10];
		heatBm=new Bitmap[4];
		for(int i =0;i<10;i++)
		{
			numBm[i]=BitmapFactory.decodeResource(getResources(), R.drawable.button0+i);
			songs.add(new Song());
			songs.get(i).setName("");
		}
		for(int i =0;i<4;i++){
			heatBm[i]=BitmapFactory.decodeResource(getResources(), R.drawable.heat0+i);
		}
		
		page = 1;
		maxPage = 0;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Log.v("draw", "1");
		
		int firstLeftX= 150;
		int firstLeftY= 200;
		int firstRightX= 700;
		int firstRightY= 200;
		textPaint.setTextSize(65);
		canvas.drawText("首页>排行榜点歌", 90, 23, textPaint);
		textPaint.setTextSize(45);
		for(int i=0;i<songs.size();i++){
			if(i<5){
				RectF rectF=new RectF(firstLeftX, firstLeftY, firstLeftX+70, firstLeftY+70);
				canvas.drawBitmap(numBm[i], null, rectF, null);
				canvas.drawText(songs.get(i).getName(), firstLeftX+80, firstLeftY+55, textPaint);
				if(page == 1){
					if(i<3){
						RectF rectF1=new RectF(firstLeftX+360, firstLeftY+28, firstLeftX+398, firstLeftY+60);
						canvas.drawBitmap(heatBm[3-i], null, rectF1, null);
					} else {
						RectF rectF1=new RectF(firstLeftX+360, firstLeftY+28, firstLeftX+398, firstLeftY+60);
						canvas.drawBitmap(heatBm[0], null, rectF1, null);
					}
				} else{
					RectF rectF1=new RectF(firstLeftX+360, firstLeftY+28, firstLeftX+398, firstLeftY+60);
					canvas.drawBitmap(heatBm[0], null, rectF1, null);
				}
				firstLeftY+= 115;
			} else {
				RectF rectF=new RectF(firstRightX, firstRightY, firstRightX+70, firstRightY+70);
				canvas.drawBitmap(numBm[i], null, rectF, null);
				canvas.drawText(songs.get(i).getName(), firstRightX+80, firstRightY+55, textPaint);
				RectF rectF1=new RectF(firstRightX+360, firstRightY+28, firstRightX+398, firstRightY+60);
				canvas.drawBitmap(heatBm[0], null, rectF1, null);
				firstRightY+=115;
			}
		}
	}
	
	public void setSongs(ArrayList<Song> songs){
		this.songs=songs;
		this.invalidate();
	}	
	
	public void setPage(int page){
		this.page = page;
	}
	
	public int getPage(){
		return page;
	}
	
	public void setMaxPage(int maxPage){
		this.maxPage = maxPage;
	}
	
	public int getMagPage(){
		return maxPage;
	}
	
	public Song getSongByKeyCode(int keyCode){
		int i = keyCode - KeyEvent.KEYCODE_0;
		if(i>9||i<0||i >= songs.size()){
			return null;
		} else {
			return  songs.get(i);
		}
	}
}
