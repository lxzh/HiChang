package hichang.ourView;

import java.util.ArrayList;

import hichang.Song.Song;
import hichang.activity.R;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class SongView  extends View{
	
	private ArrayList<Song> songs =new ArrayList<Song>();
	private Paint textPaint;
	private int page;
	Bitmap numBm[];
	public SongView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		textPaint=new Paint();
		textPaint.setColor(Color.GRAY);
		textPaint.setTextSize(40);
		
		numBm=new Bitmap[10];
		for(int i =0;i<10;i++)
		{
			numBm[i]=BitmapFactory.decodeResource(getResources(), R.drawable.button0+i);
			songs.add(new Song());
			songs.get(i).setName("");
		}
		
		page=1;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		int firstLeftX= 12;
		int firstLeftY= 27;
		int firstRightX= 502;
		int firstRightY= 27;
		if(songs == null){
			return;
		}
		for(int i=0;i<songs.size();i++){
			if(i<5){
				RectF rectF=new RectF(firstLeftX, firstLeftY, firstLeftX+55, firstLeftY+55);
				canvas.drawBitmap(numBm[i], null, rectF, null);
				canvas.drawText(songs.get(i).getName(), firstLeftX+65, firstLeftY+45, textPaint);
				firstLeftY+= 115;
			} else {
				RectF rectF=new RectF(firstRightX, firstRightY, firstRightX+55, firstRightY+55);
				canvas.drawBitmap(numBm[i], null, rectF, null);
				canvas.drawText(songs.get(i).getName(), firstRightX+65, firstRightY+45, textPaint);
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
	
	public Song getSongByKeyCode(int keyCode){
		int i = keyCode - KeyEvent.KEYCODE_0;
		if(i>9||i<1||i >= songs.size()){
			return null;
		} else {
			return  songs.get(i);
		}
	}
}
