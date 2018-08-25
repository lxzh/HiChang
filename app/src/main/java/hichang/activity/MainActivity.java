package hichang.activity;

import hichang.Song.ImageAdapter;
import hichang.database.DataBase;
import hichang.ourView.GalleryFlow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.flypigeon.service.MainService;
import com.android.flypigeon.util.Constant;
import com.android.flypigeon.util.Person;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private MyBroadcastRecv broadcastRecv = null;
	private IntentFilter bFilter = null;

	public static final String DB_NAME = "HICHANG.db";
	public static final int VERSION = 1;
	public DataBase dbHelper = new DataBase(this, DB_NAME, null, VERSION);
	public boolean isSDCardExist = true;
	public boolean userFolderExist = true;
	public static final String SD_PATH = Environment.getExternalStorageDirectory() + "/HiChang/";

	private Intent intent = null;
	Handler handler;
	ProgressDialog progressdialog;
	private AbsoluteLayout layout;
	GalleryFlow gallery;
	ImageAdapter adapter;
	int[] images;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
//		regBroadcastRecv();
		intent = new Intent(MainActivity.this, MainService.class);
		startService(intent);
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		layout = (AbsoluteLayout) findViewById(R.id.widget0);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					layout.setVisibility(View.INVISIBLE);
					progressdialog = ProgressDialog.show(MainActivity.this, "请等待...",
							"    第一次使用\n正在初始化数据...");
				} else if (msg.what == 1) {
					progressdialog.cancel();
					layout.setVisibility(View.VISIBLE);
				} else if (msg.what == 66) {
					gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				}
			}
		};

		new Thread() {
			@Override
			public void run() {
				String settingpath = SD_PATH + "hichang.ini";
				File fileDirs = new File(settingpath);
				if (!fileDirs.exists()) {
					handler.sendEmptyMessage(0);
					try {
						dbHelper.copyFile(SD_PATH, "", "hichang.ini");
					} catch (IOException e) {
						e.printStackTrace();
					}
					createUserFolder();
					copyTestMusic();
					handler.sendEmptyMessage(1);
				}
			}
		}.start();
		images = new int[]{ R.drawable.main_practice, R.drawable.main_sing, R.drawable.main_party,
				R.drawable.main_help, R.drawable.main_practice, R.drawable.main_sing,
				R.drawable.main_party, R.drawable.main_help };
		gallery = (GalleryFlow) findViewById(R.id.gallery);
		adapter = new ImageAdapter(this);
		adapter.setImages(images, 600, 600);
		gallery.setAdapter(adapter);
		gallery.setSpacing(-50);
		gallery.setSelection(Integer.MAX_VALUE / 2 - 2);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				int i = position % 4;
				if (i == 0) {
					intent.putExtra("type", 1);
					intent.setClass(MainActivity.this, SongSelectActivity.class);
//					intent.setClass(MainActivity.this, RemoteMusicActivity.class);
				} else if (i == 1) {
					intent.putExtra("type", 0);
					intent.setClass(MainActivity.this, RemoteMusicActivity.class);
				} else if (i == 2) { 
					intent.putExtra("type", 2);
					intent.setClass(MainActivity.this, RemoteMusicActivity.class);
				} else if (i == 3) {
					intent.setClass(MainActivity.this, HelpActivity.class);
					gallery.destroyDrawingCache();
				}
				Log.d("setOnItemClickListener", "type="+intent.getIntExtra("type", 0));
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 66) {
			Intent intent = new Intent();
			int i = (int) gallery.getSelectedItemId() % 4;

			if (i == 0) {
				intent.putExtra("type", 1);
				intent.setClass(MainActivity.this, SongSelectActivity.class);
			} else if (i == 1) {
				intent.putExtra("type", 0);
				intent.setClass(MainActivity.this, RemoteMusicActivity.class);
			} else if (i == 2) {
				intent.putExtra("type", 2);
				intent.setClass(MainActivity.this, RemoteMusicActivity.class);
			} else if (i == 3) {
				intent.setClass(MainActivity.this, HelpActivity.class);
				gallery.destroyDrawingCache();
			}
			startActivity(intent);
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			stopService(intent);
			System.exit(0);
		}
		return false;
	}

	/*
	 * 在用户SDCard创建应用文件夹
	 */
	public void createUserFolder() {
		if (!dbHelper.checkSDCard()) {
			isSDCardExist = false;
			userFolderExist = false;
			return;
		} else {
			isSDCardExist = true;
			if (!dbHelper.createUserFolder()) {
				userFolderExist = false;
			} else
				userFolderExist = true;
		}
	}
	
	public void copyTestMusic() {
		// 内置歌曲id，用于拷贝歌曲(原唱、伴唱、歌词);
		String[] songid;
		// 内置歌手id，用于拷贝图片
		String[] singerid;
		// 用户文件夹路径
		String SONGPATH = SD_PATH + "Songs/";
		String PICTUREPATH = SD_PATH + "Singer/";
		songid = new String[] { "153", "408", "476", "686", "842", "1236", "1353", "1757", "1823" };
		singerid = new String[] { "1", "3", "5", "8", "9", "12", "17", "31", "644" };
		String[] songprefix={"_v.mp3","_i.mp3",".txt"};
		String[] picprefix={"_p.png","_p_r.png"};
		if (isSDCardExist) {
			try {
				String filepath;
				String filename;
				String pullname;
				// 拷贝原唱歌曲
				for(int i=0;i<songprefix.length;i++){
					for (int j = 0; j < songid.length; j++) {
						filename=songid[j] + songprefix[i];
						pullname=SONGPATH + filename;
						File file = new File(pullname);
						if (!file.exists()) {
							dbHelper.copyFile(SONGPATH + songid[j] + "/", "music/", filename);
						}
					}
				}
				for(int i=0;i<picprefix.length;i++){
					for (int j = 0; j < songid.length; j++) {
						filename=singerid[j] + picprefix[i];
						pullname=PICTUREPATH + filename;
						File file = new File(pullname);
						if (!file.exists()) {
							dbHelper.copyFile(PICTUREPATH + singerid[j] + "/", "picture/", filename);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// =========================广播接收器==========================================================
	private class MyBroadcastRecv extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				if (intent.getAction().equals(Constant.receiveKeyPressedAction)) {
					Integer keyCode = (Integer) intent.getExtras().get("keycode");
					int code = keyCode.intValue();
					int pressedKey = -1;
					if (code == Constant.KEYOK) {
						pressedKey = 66;
						onKeyDown(pressedKey, null);
					} else {
						switch (code) {
						case Constant.KEYRIGHT:
							pressedKey = KeyEvent.KEYCODE_DPAD_RIGHT;
							break;
						case Constant.KEYLEFT:
							pressedKey = KeyEvent.KEYCODE_DPAD_LEFT;
							break;
						case Constant.KEYBACK:
							onKeyDown(KeyEvent.KEYCODE_BACK, null);
							break;
						default:
							break;
						}
						gallery.onKeyDown(pressedKey, null);
					}
				} else if (intent.getAction().equals(Constant.getCurrentModeAction)) {
					final Person psn = (Person) intent.getExtras().get("person");

					Intent in = new Intent(MainActivity.this, MainService.class);
					in.putExtra("mode", Constant.MAIN);
					in.putExtra("person", psn);
					in.setAction(Constant.returnCurrentModeAction);
					startService(in);
				}
			}
		}
	}

	// =========================广播接收器结束==========================================================

	// 广播接收器注册
	private void regBroadcastRecv() {
		broadcastRecv = new MyBroadcastRecv();
		bFilter = new IntentFilter();
		bFilter.addAction(Constant.getCurrentModeAction);
		bFilter.addAction(Constant.receiveKeyPressedAction);
		registerReceiver(broadcastRecv, bFilter);
	}
}
