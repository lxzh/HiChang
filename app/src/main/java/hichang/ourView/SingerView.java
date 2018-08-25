package hichang.ourView;

import hichang.Song.LocalBitmap;
import hichang.Song.Singer;
import hichang.activity.R;

import java.io.File;
import java.util.ArrayList;


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
import android.view.View;
import android.widget.Toast;

public class SingerView extends View{

	private ArrayList<Singer> singers =new ArrayList<Singer>();
	private Paint textPaint;
	private int page;
	Bitmap numBm[],singerBm;
	public SingerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		textPaint=new Paint();
		textPaint.setColor(Color.GRAY);
		textPaint.setTextSize(40);
		
		numBm=new Bitmap[9];
		singerBm=BitmapFactory.decodeResource(getResources(), R.drawable.photo);
		for(int i =0;i<9;i++)
		{
			numBm[i]=BitmapFactory.decodeResource(getResources(), R.drawable.box1+i);
			singers.add(new Singer());
			singers.get(i).setName("");
		}
		
		page = 1;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int firstX = 12;
		int firstY = 27;
		if(singers == null){
			return;
		}
		for(int i=0;i<singers.size();i++){
			RectF rectF=new RectF(firstX ,firstY ,firstX+315 ,firstY+170);
			canvas.drawBitmap(numBm[i], null, rectF, null);
			canvas.drawText(singers.get(i).getName(), firstX+188, firstY+69, textPaint);
			String path="/sdcard/HiChang/Singer/"+singers.get(i).getiD()+"/"+singers.get(i).getiD()+"_p_r.png";
			File imagefile=new File(path);
			RectF rectF1=new RectF(firstX+18 , firstY+29,firstX+130 ,firstY+141);
			if(imagefile.exists()&&LocalBitmap.getLoacalBitmap(path)!=null){
				canvas.drawBitmap(LocalBitmap.getLoacalBitmap(path), null, rectF1, null);
			} else {
				canvas.drawBitmap(singerBm, null, rectF1, null);
			}
			if((i+1)%3 == 0){
				firstX = 12;
				firstY += 180;
			} else {
				firstX += 330;
			}
		}
	}
	
	public void setSingers(ArrayList<Singer> singers){
		this.singers=singers;
		this.invalidate();
	}
	
	public void setPage(int page){
		this.page=page;
	}
	
	public int getPage(){
		return page;
	}
	
	public Singer getSingerBy(int keyCode){
		int i = keyCode - KeyEvent.KEYCODE_0;
		if(i>9||i<1||i > singers.size()){
			return null;
		} else {
			return  singers.get(i-1);
		}
	}
}
