package hichang.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class progressBarActivity extends Activity{

	private Handler handler;
	private int activity;
	private int songid;
	private int returning;
	private Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress);
		Intent intent1= getIntent();
		timer = new Timer();
		activity = intent1.getIntExtra("activityId", -1);
		songid = intent1.getIntExtra("songId", -1);
		final int personId = intent1.getIntExtra("personid", -1);
		returning = intent1.getIntExtra("isReturn", -1);
		handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == 1)
				{
					if(returning == 0){
						if(activity == 0)
						{
							Intent intent = new Intent();
							intent.putExtra("songId", songid);
							intent.putExtra("personid", personId);
							intent.setClass(progressBarActivity.this, HiSingActivity.class);
							startActivity(intent);
							progressBarActivity.this.finish();
						}
						else if(activity == 1)
						{
							Intent intent = new Intent();
							intent.putExtra("songId", songid);
							intent.putExtra("personid", personId);
							intent.setClass(progressBarActivity.this, PracticeActivity.class);
							startActivity(intent);
							progressBarActivity.this.finish();
						}
						else 
						{
							Intent intent = new Intent();
							intent.putExtra("songId", songid);
							intent.putExtra("personid", personId);
							intent.setClass(progressBarActivity.this, PartyActivity.class);
							startActivity(intent);
							progressBarActivity.this.finish();
						}
					}
					else 
					{
						Intent intent = new Intent();
						intent.setClass(progressBarActivity.this, MainActivity.class);
						startActivity(intent);
						progressBarActivity.this.finish();
					}
				}
			}
			
		};
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(1);
			}
		}, 2500);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		timer.cancel();
		super.onDestroy();
	}
	
}
