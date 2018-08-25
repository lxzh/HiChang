package hichang.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.android.flypigeon.service.MainService;
import com.android.flypigeon.util.Constant;
import com.android.flypigeon.util.Person;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class HelpActivity extends Activity{

	private MyBroadcastRecv broadcastRecv = null;
	private IntentFilter bFilter = null;
	
	private ImageView help_fpoint,help_spoint,help_tpoint;
	private ImageView help_border;
	private Handler handler;
	private Gallery g;
	private Timer timer;
	Animation help_reduceAnimation,help_anifloat;
	private Bitmap[] mThumbIds;
	ImageAdapter imageAdapter;
	private Thread thread;
	private Resources resources;

	
	private Drawable[] imageDrawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		regBroadcastRecv();
        
        help_border = (ImageView)findViewById(R.id.help_float);
        g = (Gallery) findViewById(R.id.help_gallery);
        help_fpoint = (ImageView)findViewById(R.id.help_fpoint);
        help_spoint = (ImageView)findViewById(R.id.help_spoint);
        help_tpoint = (ImageView)findViewById(R.id.help_tpoint);
       //mInfoTV=(TextView)findViewById(R.id.help_minfo);
		timer = new Timer();
		resources=getResources();
		
		help_anifloat = AnimationUtils.loadAnimation(this, R.anim.help_float);        
	    help_reduceAnimation = AnimationUtils.loadAnimation(this, R.anim.help_reduce);
	    help_reduceAnimation.setFillAfter(true);
	    
		mThumbIds = new Bitmap[3];
		//imageDrawable=new Drawable[3];
		thread=new Thread(){
		    
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//super.run();
				synchronized (mThumbIds) {
					try {

//						imageDrawable[0]=resources.getDrawable(R.drawable.help_first);
//						imageDrawable[1]=resources.getDrawable(R.drawable.help_second);
//						imageDrawable[2]=resources.getDrawable(R.drawable.help_third);
						
						
						mThumbIds[1] = BitmapFactory.decodeResource(resources, R.drawable.help_second);
						mThumbIds[2] = BitmapFactory.decodeResource(resources, R.drawable.help_third);
						mThumbIds[0] = BitmapFactory.decodeResource(resources, R.drawable.help_first);
						Thread.sleep(50);
					} catch (OutOfMemoryError e) {
						// TODO: handle exception	
						Toast.makeText(getBaseContext(), "OOM", Toast.LENGTH_SHORT).show();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
					} 
				}	
			}
			
		};
		thread.start();
		
		
		
        handler = new Handler()
        {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == 3) 
				{
					//synchronized (mThumbIds) {
						imageAdapter=new ImageAdapter(getBaseContext(),mThumbIds);
					//}
					    
						g.setAdapter(imageAdapter);	
				}
				else if(msg.what == 1)
				{
					timer.cancel();
					help_anifloat.cancel();
					help_reduceAnimation.cancel();
					Intent intent = new Intent();
					intent.setClass(HelpActivity.this, MainActivity.class);
					//startActivity(intent);
					HelpActivity.this.finish();
				}
				
			}
        };
        
        
		
        
		help_border.startAnimation(help_anifloat);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(3);
			}
		}, 1500);
		
		g.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2 == 0)
				{
					help_spoint.setVisibility(ImageView.INVISIBLE);
					help_fpoint.setVisibility(ImageView.VISIBLE);
				}
				else if(arg2 == 1)
				{
					help_fpoint.setVisibility(ImageView.INVISIBLE);
					help_tpoint.setVisibility(ImageView.INVISIBLE);
					help_spoint.setVisibility(ImageView.VISIBLE);
				}
				else if(arg2 == 2)
				{
					help_spoint.setVisibility(ImageView.INVISIBLE);
					help_tpoint.setVisibility(ImageView.VISIBLE);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			Toast.makeText(this, "返回", 1000).show();
			g.destroyDrawingCache();
			g.setVisibility(Gallery.INVISIBLE);
			
			help_fpoint.setVisibility(ImageView.INVISIBLE);
			help_spoint.setVisibility(ImageView.INVISIBLE);
			help_tpoint.setVisibility(ImageView.INVISIBLE);
			help_border.startAnimation(help_reduceAnimation);
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(1);
				}
			}, 1200);
		}
		return true;
		}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private Bitmap[] mBitmaps;
		
        public ImageAdapter(Context c,Bitmap[] bitmaps) {
            mContext = c;
            mBitmaps=bitmaps;
        }

        public int getCount() {
            return mBitmaps.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	ImageView imageView;
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new Gallery.LayoutParams(1910,1065));
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(18, 18, 18, 18);
            imageView.setImageBitmap(mBitmaps[position]);
            //imageView.setImageDrawable(imageDrawable[position]);
            return imageView;
        }

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		try {
			thread.stop();
			thread.destroy();
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show();
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		try {
			mThumbIds[0].recycle();
			mThumbIds[1].recycle();
			mThumbIds[2].recycle();
			help_border.getBackground().setCallback(null);
			help_fpoint.getBackground().setCallback(null);
			help_spoint.getBackground().setCallback(null);
			help_tpoint.getBackground().setCallback(null);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "123", Toast.LENGTH_SHORT).show();
		}
		imageAdapter=null;
		g=null;
		System.gc();
		
	}
	
	//=========================广播接收器==========================================================
    private class MyBroadcastRecv extends BroadcastReceiver{    		
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Constant.receiveKeyPressedAction)){
				Integer keyCode = (Integer)intent.getExtras().get("keycode");
				int code = keyCode.intValue();
				int pressedKey = -1;
				switch (code) {	
				case Constant.KEYLEFT:
					pressedKey = KeyEvent.KEYCODE_DPAD_LEFT;
					break;
				case Constant.KEYRIGHT:
					pressedKey = KeyEvent.KEYCODE_DPAD_RIGHT;
					break;
				case Constant.KEYBACK:
					pressedKey = KeyEvent.KEYCODE_BACK;
					break;
				default:
					break;
				}
				if(pressedKey != -1) {
					onKeyDown(pressedKey, null);
				}
			}
			else if(intent.getAction().equals(Constant.getCurrentModeAction)) {
				final Person psn = (Person)intent.getExtras().get("person");
				
				Intent in = new Intent(HelpActivity.this,MainService.class);
				in.putExtra("mode", Constant.HELP);
				in.putExtra("person", psn);
				in.setAction(Constant.returnCurrentModeAction);
		        startService(in);
			}
		}
    }
    //=========================广播接收器结束==========================================================
    
    
	//广播接收器注册
	private void regBroadcastRecv(){
        broadcastRecv = new MyBroadcastRecv();
        bFilter = new IntentFilter();
        bFilter.addAction(Constant.getCurrentModeAction);
        bFilter.addAction(Constant.receiveKeyPressedAction);
        registerReceiver(broadcastRecv, bFilter);
	}
}
