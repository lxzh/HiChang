package hichang.ourView;

import hichang.activity.R;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class InputBoxView extends ViewGroup {

	private boolean isAvailable = true;
	private Button[] btns;
	TextView textView;
	Timer backTimer;
	MyTimerTask backTask;
	Handler handler;
	public InputBoxView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		backTimer=new Timer();
		handler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case KeyEvent.KEYCODE_0:
					btns[9].setBackgroundResource(R.drawable.remote_zero1);
					break;
				case KeyEvent.KEYCODE_1:
					btns[0].setBackgroundResource(R.drawable.remote_one1);
					break;
				case KeyEvent.KEYCODE_2:
					btns[1].setBackgroundResource(R.drawable.remote_two1);
					break;
				case KeyEvent.KEYCODE_3:
					btns[2].setBackgroundResource(R.drawable.remote_three1);
					break;
				case KeyEvent.KEYCODE_4:
					btns[3].setBackgroundResource(R.drawable.remote_four1);
					break;
				case KeyEvent.KEYCODE_5:
					btns[4].setBackgroundResource(R.drawable.remote_five1);
					break;
				case KeyEvent.KEYCODE_6:
					btns[5].setBackgroundResource(R.drawable.remote_six1);
					break;
				case KeyEvent.KEYCODE_7:
					btns[6].setBackgroundResource(R.drawable.remote_seven1);
					break;
				case KeyEvent.KEYCODE_8:
					btns[7].setBackgroundResource(R.drawable.remote_eight1);
					break;
				case KeyEvent.KEYCODE_9:
					btns[8].setBackgroundResource(R.drawable.remote_nine1);
					break;
				case 219:
					btns[11].setBackgroundResource(R.drawable.remote_jiaoti1);
					break;
				default:
					break;
				}
			};
		};
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		Log.v("add", this.getChildCount() + "");
		if (this.getChildCount() > 0) {
			View v = getChildAt(0);
			v.layout(70, 177, 162, 254);
		}
		if(this.getChildCount() > 1){
			View v = getChildAt(1);
			v.layout(179, 177, 271, 254);
		}
		if(this.getChildCount() > 2){
			View v = getChildAt(2);
			v.layout(288, 177, 380, 254);
		}
		if(this.getChildCount() > 3){
			View v = getChildAt(3);
			v.layout(70, 275, 162, 352);
		}
		if(this.getChildCount() > 4){
			View v = getChildAt(4);
			v.layout(179, 275, 271, 352);
		}
		if(this.getChildCount() > 5){
			View v = getChildAt(5);
			v.layout(288, 275, 380, 352);
		}
		if(this.getChildCount() > 6){
			View v = getChildAt(6);
			v.layout(70, 373, 162, 450);
		}
		if(this.getChildCount() > 7){
			View v = getChildAt(7);
			v.layout(179, 373, 271, 450);
		}
		if(this.getChildCount() > 8){
			View v = getChildAt(8);
			v.layout(288, 373, 380, 450);
		}
		if(this.getChildCount() > 9){
			View v = getChildAt(9);
			v.layout(179, 471, 271, 548);
		}
		if(this.getChildCount() > 10){
			View v = getChildAt(10);
			v.layout(70, 471, 162, 548);
		}
		if(this.getChildCount() > 11){
			View v = getChildAt(11);
			v.layout(288, 471, 380, 548);
		}
		if(this.getChildCount() > 12){
			View v = getChildAt(12);
			v.layout(50, 57, 377, 103);
		}
	}

	public void init() {
		btns = new Button[12];
		btns[0] = new Button(getContext());
		btns[1] = new Button(getContext());
		btns[2] = new Button(getContext());
		btns[3] = new Button(getContext());
		btns[4] = new Button(getContext());
		btns[5] = new Button(getContext());
		btns[6] = new Button(getContext());
		btns[7] = new Button(getContext());
		btns[8] = new Button(getContext());
		btns[9] = new Button(getContext());
		btns[10] = new Button(getContext());
		btns[11] = new Button(getContext());
		btns[0].setBackgroundResource(R.drawable.remote_one1);
		btns[1].setBackgroundResource(R.drawable.remote_two1);
		btns[2].setBackgroundResource(R.drawable.remote_three1);
		btns[3].setBackgroundResource(R.drawable.remote_four1);
		btns[4].setBackgroundResource(R.drawable.remote_five1);
		btns[5].setBackgroundResource(R.drawable.remote_six1);
		btns[6].setBackgroundResource(R.drawable.remote_seven1);
		btns[7].setBackgroundResource(R.drawable.remote_eight1);
		btns[8].setBackgroundResource(R.drawable.remote_nine1);
		btns[9].setBackgroundResource(R.drawable.remote_zero1);
		btns[10].setBackgroundResource(R.drawable.remote_pingxian1);
		btns[11].setBackgroundResource(R.drawable.remote_jiaoti1);
		this.addView(btns[0]);
	    this.addView(btns[1]);
		
		  this.addView(btns[2]); this.addView(btns[3]); this.addView(btns[4]);
		  this.addView(btns[5]); this.addView(btns[6]); this.addView(btns[7]);
		  this.addView(btns[8]); this.addView(btns[9]); this.addView(btns[10]);
		  this.addView(btns[11]);
		for(int i =0 ;i<12;i++){
			btns[i].clearFocus();
			btns[i].setFocusable(false);
		}
		  textView=new TextView(getContext());
		  textView.setBackgroundResource(R.drawable.searchframe);
		  textView.setTextSize(40);
		  textView.setText("");
		  this.addView(textView);
	

	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public boolean getAvailable() {
		return isAvailable;
	}
	
	public String getText(){
		return textView.getText().toString();
	}
	
	public void setText(String text){
		textView.setText(text);
	}
	
	public void pressKey(int keyCode){
		if (isAvailable) {
			if(textView.getText().length()>15 && keyCode!=219){
				return ;
			}
			switch (keyCode) {
			case KeyEvent.KEYCODE_0:
				btns[9].setBackgroundResource(R.drawable.remote_zero2);
				textView.setText(textView.getText()+"0");
				break;
			case KeyEvent.KEYCODE_1:
				btns[0].setBackgroundResource(R.drawable.remote_one2);
				textView.setText(textView.getText()+"1");
				break;
			case KeyEvent.KEYCODE_2:
				btns[1].setBackgroundResource(R.drawable.remote_two2);
				textView.setText(textView.getText()+"2");
				break;
			case KeyEvent.KEYCODE_3:
				btns[2].setBackgroundResource(R.drawable.remote_three2);
				textView.setText(textView.getText()+"3");
				break;
			case KeyEvent.KEYCODE_4:
				btns[3].setBackgroundResource(R.drawable.remote_four2);
				textView.setText(textView.getText()+"4");
				break;
			case KeyEvent.KEYCODE_5:
				btns[4].setBackgroundResource(R.drawable.remote_five2);
				textView.setText(textView.getText()+"5");
				break;
			case KeyEvent.KEYCODE_6:
				btns[5].setBackgroundResource(R.drawable.remote_six2);
				textView.setText(textView.getText()+"6");
				break;
			case KeyEvent.KEYCODE_7:
				btns[6].setBackgroundResource(R.drawable.remote_seven2);
				textView.setText(textView.getText()+"7");
				break;
			case KeyEvent.KEYCODE_8:
				btns[7].setBackgroundResource(R.drawable.remote_eight2);
				textView.setText(textView.getText()+"8");
				break;
			case KeyEvent.KEYCODE_9:
				btns[8].setBackgroundResource(R.drawable.remote_nine2);
				textView.setText(textView.getText()+"9");
				break;
			case 219:
				btns[11].setBackgroundResource(R.drawable.remote_jiaoti2);
				int length=textView.getText().toString().length();
				if(length>0){
					textView.setText(textView.getText().toString().substring(0, length-1));
				}
				break;
			default:
				break;
			}
			backTask=new MyTimerTask();
			backTask.setkeyCode(keyCode);
			backTimer.schedule(backTask, 200);
		} 
	}
	
	class MyTimerTask extends TimerTask{
		
		private int keyCode;
		public void setkeyCode(int keyCode){
			this.keyCode=keyCode;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(keyCode);
		}
	}
}
