package hichang.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.flypigeon.service.MainService;
import com.android.flypigeon.util.Constant;
import com.android.flypigeon.util.Person;

import hichang.Song.*;
import hichang.audio.AudRec;
import hichang.ourView.CurveAndLrc;
import hichang.ourView.CurveAndLrc.ModeType;
import hichang.ourView.LrcTextView;
import hichang.ourView.VoiceView;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PracticeActivity extends Activity {

	private MyBroadcastRecv broadcastRecv = null;
	private IntentFilter bFilter = null;
	private int personId;

	// 数据库Song表访问接口
	private Song song;
	// 数据库Singer表访问接口
	private Singer singer;
	// 数据库User表访问接口
	private User user;
	// 当前播放歌曲
	private Song nowSong;
	// 解析后的歌曲信息
	private ReadText nowSongText;
	// 解析后的歌词句子组
	private ArrayList<Sentence> nowSongSentence;
	// 歌曲的总时长（毫秒）
	private int nowSongLength;
	// 当前播放到的毫秒数
	private int nowSongTime;
	// 当前播放到的句子的游标
	private int sentenceFlag = 0;
	// 当前播放到的句子和下一句,最后一句
	private Sentence nowSentence, nextSentence, lastSentence;
	// 当前歌的句子数目
	private int sentenceSize;
	// 使用timer控件控制整个练歌过程
	private Timer songtimer;
	// 歌词布局
	private LinearLayout mLayout;
	// 歌词滚动布局
	private ScrollView sView;
	// 歌词显示布局
	private ListView listView;
	// listView中的几个控件
	private TextView tView1;
	private TextView tView2;
	// 适配器中的一个参数
	private View view;
	// 自定义的List适配器
	private MyAdapter adapter;
	// 歌词存放的地方
	public static int select_item = -1;// 现在播放的某句歌词
	// 滚动的位置
	public static int mposition = -367;
	// 判断点击的参数，你们不用理会
	public static int a = 0;
	// 歌词 的曲线
	public VoiceView voiceView;
	// 左边两句歌词
	private LrcTextView lrcView;
	// 跑动的时针
	private ImageView timeHand;
	// 是否在调节麦克风音量
	// 是否调节麦克音量
	private boolean isMic = false;
	// 是否在调节媒体音量
	private boolean isMedia = false;
	// 是否调节伴奏音乐
	private boolean isComa = false;
	// 是否是KTV模式
	private boolean isKTV = false;
	// 消息队列
	private Handler handler;
	// 媒体音量
	private int mediaVolume = 3;
	private int mediaVolumn_int;
	private float mediaVolumn_float = 0.3f;
	// 麦克风音量
	private int micVolumn = 30;
	// 当前歌曲的最高分
	int highScore;
	// 数字图片
	Bitmap[] smallWhiteNums = new Bitmap[10];
	Bitmap[] whiteNums = new Bitmap[10];
	Bitmap[] redNums = new Bitmap[10];

	// 评价图片
	Bitmap[] evaluates = new Bitmap[4];
	// 功能模块的图片
	Bitmap[] accompany = new Bitmap[2];
	Bitmap[] original = new Bitmap[2];
	Bitmap[] ktvMode = new Bitmap[2];
	Bitmap[] professional = new Bitmap[2];
	Bitmap[] volumns = new Bitmap[2];
	// 星星的图片
	Bitmap darkBigStar, lightBigStar, darkSmallStar, lightSmallStar;
	// 资源
	private Resources resources;
	// 动画
	private Animation numTurnFirstAnim;
	private Animation numTurnSecAnim;
	private Animation starFirstAnim;
	private Animation starSecAnim;
	private Animation evaluateFirstAnim;
	private Animation evaluateSecAnim;
	private Animation jumpFirstAnim;
	private Animation jumpSecAnim;
	private TranslateAnimation timeAnim;
	// 分数的ImageView
	private ImageView leftImage, rightImage;
	private ImageView singerImage;
	private TextView musicInfo, volumnTextView;
	private int numImageHeight, numImageWidth;
	private Bitmap leftBm, rightBm, changeLeftBm, changeRightBm;
	private Bitmap evaluateBm, changeEvaBm;
	private int whichevaluate;
	// 星星的ImageView
	private ImageView starImage;
	// 每句分数的十位和个位数字
	private int leftScoreNum, rightScoreNum;
	// 最下方功能提示处的ImageView
	private ImageView funcRedImage, funcGreenImage, funcYellowImage, funcBlueImage, funcFirstImage,
			funcSecImage, funcSprit;
	private AbsoluteLayout funcLayout;
	// 音量条和图标
	private SeekBar volumnSeekBar;
	private ImageView volumnImage;
	// 评价ImageView
	private ImageView evaluateImage;
	// 倒计时图标
	private ImageView countBlueImage, countRedImage, countYellowImage;
	// 是否是刚开始播放
	private boolean isStart = false;
	// 是否是选择开始播放
	private boolean isSelectStart = true;
	// 倒计时的时间
	private double leftTimes;
	// 倒计时,时针,分数的timer
	private Timer disTimer, handTimer, scoreTimer, senTimer, timeTimer;
	// timertask
	private Timer buttonTimer;
	TimerTask iconTask1, iconTask2, iconTask3;
	TimerTask songTimerTask;
	// 播放器
	// private MediaPlayer mediaPlayer2,mediaPlayer3;
	private CMediaPlayer nowMediaPlayer = new CMediaPlayer();
	// 音频接收
	private AudRec audRec;
	// 时针的步长
	final int TIMESTEP = 40;
	// 当前画点的标准时间
	int standTime;
	// 时针的坐标
	int timeHandX;

	// SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
	// int soundId;

	TextView text1;
	int[] pids = new int[1];
	ActivityManager am;
	MemoryInfo outInfo;

	CurveAndLrc curveLrc;
	private int curveW, curveH, curveX, curveY;
	private int lrcH, lrcW, lrcX, lrcY;

	private TimerTask timerTask, songTask;

	final static int MSG_SHOW_TIME = 155;
	final static int MSG_TURN_COLORBARS = 150;
	final static int MSG_HIDE_YELLOWBALL = 151;
	final static int MSG_HIDE_REDBALL = 152;
	final static int MSG_HIDE_BLUEBALL = 153;
	final static int MSG_SHOW_SCORE = 154;
	final static int MSG_START_SONG = 200;
	final static int MSG_RESTART_SONG = 201;
	final static int MSG_START_SHOW_TIME = 156;
	final static int MSG_SHOW_END = 157;
	final static int MSG_HIDE_END = 158;
	String songTime;
	ImageView staffImage, helpImage;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.practice);

		regBroadcastRecv();

		volumnTextView = (TextView) findViewById(R.id.volumn_textview);
		singerImage = (ImageView) findViewById(R.id.pra_sin_image);
		musicInfo = (TextView) findViewById(R.id.pra_showscore);
		leftImage = (ImageView) findViewById(R.id.num_left);
		rightImage = (ImageView) findViewById(R.id.num_right);
		funcRedImage = (ImageView) findViewById(R.id.function_red);
		funcGreenImage = (ImageView) findViewById(R.id.function_green);
		funcBlueImage = (ImageView) findViewById(R.id.function_blue);
		funcYellowImage = (ImageView) findViewById(R.id.function_yellow);
		funcFirstImage = (ImageView) findViewById(R.id.funcfirstimage);
		funcSecImage = (ImageView) findViewById(R.id.funcsecimage);
		funcSprit = (ImageView) findViewById(R.id.funcsprit);
		funcLayout = (AbsoluteLayout) findViewById(R.id.funclayout);
		evaluateImage = (ImageView) findViewById(R.id.evaluteimage);
		volumnSeekBar = (SeekBar) findViewById(R.id.player_seekbar);
		volumnImage = (ImageView) findViewById(R.id.volumntype);
		countBlueImage = (ImageView) findViewById(R.id.countblue);
		countRedImage = (ImageView) findViewById(R.id.countred);
		countYellowImage = (ImageView) findViewById(R.id.countyellow);
		sView = (ScrollView) this.findViewById(R.id.ScrollView);
		listView = (ListView) findViewById(R.id.listview_list);
		mLayout = (LinearLayout) this.findViewById(R.id.LinearLayout);
		timeHand = (ImageView) findViewById(R.id.timeHand);
		starImage = (ImageView) findViewById(R.id.starimage);
		listView.setFocusable(false);
		sView.setFocusable(false);
		volumnSeekBar.setFocusable(false);
		curveLrc = (CurveAndLrc) findViewById(R.id.practice_curveandlrc);
		staffImage = (ImageView) findViewById(R.id.practice_staff);
		helpImage = (ImageView) findViewById(R.id.practice_sentence);
		resources = this.getResources();
		// 歌曲文件路径
		// 获取点歌界面传来的歌曲ID
		Intent intent = getIntent();
		int songid = intent.getIntExtra("songId", 0);
		personId = intent.getIntExtra("personid", -1);

		song = new Song(getBaseContext());
		singer = new Singer(getBaseContext());
		user = new User(getBaseContext());

		nowSong = song.findSongById(songid);
		int singerid = singer.querySingerByName(nowSong.getSinger1()).getiD();
		highScore = user.queryFirstScore(nowSong.getSongID());
		String songName = nowSong.getName();
		if (songName.charAt(0) > 0 && songName.charAt(0) < 128 && songName.length() > 12)
			songName = songName.substring(0, 12) + "...";
		else if ((songName.charAt(0) < 0 || songName.charAt(0) > 128) && songName.length() > 7)
			songName = songName.substring(0, 7) + "...";
		String picPath = MainActivity.SD_PATH + "Singer/" + singerid + "/" + singerid + "_p.png";
		singerImage.setImageBitmap(LocalBitmap.getLoacalBitmap(picPath));
		// musicInfo.setText(nowSong.getName()+"\n最高分-"+highScore+"\n");

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 4) {
					funcFirstImage.setVisibility(ImageView.INVISIBLE);
					funcSecImage.setVisibility(ImageView.INVISIBLE);
					funcSprit.setVisibility(ImageView.INVISIBLE);
				}
				if (msg.what == 5) {
					funcFirstImage.setVisibility(ImageView.INVISIBLE);
					funcSecImage.setVisibility(ImageView.INVISIBLE);
					funcSprit.setVisibility(ImageView.INVISIBLE);
				}
				if (msg.what == 6) {
					volumnImage.setVisibility(ImageView.INVISIBLE);
					volumnSeekBar.setVisibility(SeekBar.INVISIBLE);
					volumnTextView.setVisibility(TextView.INVISIBLE);
				}
				if (msg.what == 123) {
					ScrollToNext(adapter);
				} else if (msg.what == 2) {
					ScrollToPast(adapter);
				} else if (msg.what == 100) {

					leftImage.setImageBitmap(changeLeftBm);
					rightImage.setImageBitmap(changeRightBm);
					evaluateImage.setImageBitmap(changeEvaBm);
				} else if (msg.what == 101) {
					leftImage.setImageBitmap(leftBm);
					rightImage.setImageBitmap(rightBm);
					evaluateImage.setImageBitmap(evaluateBm);
				} else if (msg.what == 107) {
					starImage.setImageBitmap(lightBigStar);
					starImage.startAnimation(starFirstAnim);
				} else if (msg.what == 108) {
					starImage.startAnimation(starSecAnim);
				} else if (msg.what == 109) {
					starImage.setImageBitmap(darkBigStar);
				} else if (msg.what == 115) {

					am.getMemoryInfo(outInfo);
					List<RunningAppProcessInfo> runApps = am.getRunningAppProcesses();
					Debug.MemoryInfo[] dmf = am.getProcessMemoryInfo(pids);
					text1.setText("当前activity占用内存" + dmf[0].dalvikPrivateDirty + "KB" + "可用内存"
							+ outInfo.availMem / 1024 + "KB " + audRec.getCurrentReadindex() + " "
							+ audRec.getCurrentRecordIndex() + " " + audRec.getCurrentRealNote()
							+ " " + audRec.getProcessTime() + " " + audRec.getCurrentMark() + " "
							// + curveLrc.firstSenStartX + " "
							// + curveLrc.pointFlag + " "
							+ curveLrc.pointsTime[0].length);
				} else if (msg.what == MSG_START_SONG) {
					nowMediaPlayer.CStart();
					songtimer = new Timer();
					songtimer.schedule(songTask, 40, 40);
				} else if (msg.what == MSG_TURN_COLORBARS) {
					countBlueImage.setVisibility(ImageView.VISIBLE);
					countRedImage.setVisibility(ImageView.VISIBLE);
					countYellowImage.setVisibility(ImageView.VISIBLE);
					disTimer = new Timer();
					disTimer.schedule(new TimerTask() {
						int i = 0;

						@Override
						public void run() {
							if (i == 0) {
								handler.sendEmptyMessage(MSG_HIDE_YELLOWBALL);
							} else if (i == 1) {
								handler.sendEmptyMessage(MSG_HIDE_REDBALL);
							} else if (i == 2) {
								handler.sendEmptyMessage(MSG_HIDE_BLUEBALL);
							} else {
								disTimer.cancel();
							}
							i++;
						}
					}, 0, 1000);
				} else if (msg.what == MSG_HIDE_YELLOWBALL) {
					countYellowImage.setVisibility(View.INVISIBLE);
				} else if (msg.what == MSG_HIDE_REDBALL) {
					countRedImage.setVisibility(View.INVISIBLE);
				} else if (msg.what == MSG_HIDE_BLUEBALL) {
					countBlueImage.setVisibility(View.INVISIBLE);
				} else if (msg.what == MSG_RESTART_SONG) {
					countBlueImage.setVisibility(View.INVISIBLE);
					nowMediaPlayer.CReStart();
					songtimer = new Timer();
					audRec.setRecording(true);
					songtimer.schedule(songTask, 40, 40);
				} else if (msg.what == MSG_SHOW_TIME) {
					nowSongTime = nowMediaPlayer.CGetCurrentPosition();
					if (!isStart && nowSongTime > nowSongSentence.get(0).StartTimeofThis - 3400) {
						helpImage.setVisibility(View.INVISIBLE);

						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(MSG_TURN_COLORBARS);
						curveLrc.drawCurveAndLrc(nowSongTime - 400,
								audRec.getNote(nowSongTime - 400));
						isStart = true;
					}
					int time = nowSongTime / 1000;
					int second = time % 60;
					int minute = (time - second) / 60;
					if (second < 10) {
						musicInfo.setText(nowSong.getName() + "\n最高分-" + highScore + "\n" + minute
								+ ":" + "0" + second + "/" + songTime);
					} else {
						musicInfo.setText(nowSong.getName() + "\n最高分-" + highScore + "\n" + minute
								+ ":" + second + "/" + songTime);
					}
				} else if (msg.what == MSG_START_SHOW_TIME) {
					timeTimer = new Timer();
					timeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_SHOW_TIME);
						}
					}, 0, 400);
				}

			}
		};

		// 各种初始化
		InitSong();
		InitPic();
		InitAnim();
		initTask();

		songtimer = new Timer();
		disTimer = new Timer();
		scoreTimer = new Timer();
		senTimer = new Timer();
		buttonTimer = new Timer();
		timeTimer = new Timer();
		// 创建一个线性布局
		listView.scrollTo(0, mposition);
		// 创建一个ScrollView对象
		adapter = new MyAdapter(nowSongSentence, getBaseContext());// 适配器的初始化方法，前一个是歌词的List，后一个是上下文
		listView.setAdapter(adapter);
		setListViewHeightBasedOnChildren(listView);

		// int myPid = Process.myPid();
		// pids[0] = myPid;
		// text1 = (TextView) findViewById(R.id.text);
		// Timer timer = new Timer();
		// am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		// text1.setTextSize(15);
		// outInfo = new MemoryInfo();
		//
		// timer.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// handler.sendEmptyMessage(115);
		// }
		// }, 0, 500);

		audRec.start();

		sendNextActivityMsg(Constant.PRACTICE);
		sendStartSongMsg();
	}

	public void initTask() {
		timerTask = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(MSG_RESTART_SONG);
			}
		};

		songTask = new TimerTask() {
			@Override
			public void run() {
				nowSongTime = nowMediaPlayer.CGetCurrentPosition();
				if (nowSongTime >= nowSongSentence.get(sentenceSize - 1).StartTimeofThis
						+ nowSongSentence.get(sentenceSize - 1).LastTimeofThis + 440) {
					// handler.sendEmptyMessage(MSG_SHOW_END);
					curveLrc.clearTotal();
					this.cancel();
					songtimer.cancel();
				} else {
					if (sentenceFlag < sentenceSize) {
						if (nowSongTime >= nowSongSentence.get(sentenceFlag).StartTimeofThis
								+ nowSongSentence.get(sentenceFlag).LastTimeofThis) {
							nowSongSentence.get(sentenceFlag).Score = audRec.getMark(sentenceFlag);
							handler.sendEmptyMessage(123);
							StartNumTurnAnim(audRec.getMark(sentenceFlag));
							sentenceFlag++;
						}
					}
					curveLrc.drawCurveAndLrc(nowSongTime - 440, audRec.getNote(nowSongTime - 440));
				}

			}
		};
		iconTask1 = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(4);
			}
		};
		iconTask2 = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(5);
			}
		};
		iconTask3 = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(6);
			}
		};
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按键为左键
		if (keyCode == 21) {
			PreThreeSen();
		}
		// 按键为右键
		else if (keyCode == 22) {
			NextThreeSen();
		}
		// 按键为上键
		else if (keyCode == 19) {
			PreSen();
		}
		// 按键为下键
		else if (keyCode == 20) {
			NextSen();
		}
		// 按键为返回键
		else if (keyCode == 4) {
			Toast.makeText(this, "返回", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.putExtra("type", 1);
			intent.setClass(PracticeActivity.this, RemoteMusicActivity.class);
			startActivity(intent);
			this.finish();
		}
		// 按键为红色键
		else if (keyCode == 183) {
			RedPress();
		}
		// 按键为绿色键
		else if (keyCode == 184) {
			GreenPress();
		}
		// 按键为黄色键
		else if (keyCode == 185) {
			YellowPress();
		}
		// 按键为蓝色键
		else if (keyCode == 186) {
			BluePress();
		}
		return true;
	}

	public void OnFuncMenuClicked(View view) {
		switch (view.getId()) {
		case R.id.function_red:
			RedPress();
			break;
		case R.id.function_green:
			GreenPress();
			break;
		case R.id.function_yellow:
			YellowPress();
			break;
		case R.id.function_blue:
			BluePress();
			break;
		}
	}
	// 初始化歌曲信息
	public void InitSong() {
		nowSongText = new ReadText(nowSong.getSongLyricUrl());
		// 读取在sdcard中的.txt
		nowSongSentence = nowSongText.ReadData();

		// //读取在raw中的.txt
		// InputStream in=resources.openRawResource(R.raw.a681);
		// nowSongSentence=nowSongText.ReadData(in);

		nowMediaPlayer.CSetDataSource(nowSong.getMusicPath(), nowSong.getAccomanimentPath());
		nowMediaPlayer.CPrepare();
		nowSongLength = nowMediaPlayer.CGetDuration();

		curveLrc.init(nowSongSentence, nowSongText.max, nowSongText.min, ModeType.MODE_PRACTICE);
		setKTVModel(false);

		// 初始化歌的句子的一些信息
		sentenceSize = nowSongSentence.size();
		nowSentence = nowSongSentence.get(0);
		nextSentence = nowSongSentence.get(1);
		sentenceFlag = 0;
		lastSentence = nowSongSentence.get(sentenceSize - 1);

		// 初试化音频接收
		audRec = new AudRec(nowSongSentence, handler, nowMediaPlayer);
		audRec.init();

		// soundId = soundPool.load(this, R.raw.a, 1);
		nowSongLength = nowMediaPlayer.CGetDuration();
		int sec = (nowSongLength / 1000) % 60;
		int min = (nowSongLength / 1000 - sec) / 60;
		if (sec < 10) {
			songTime = min + ":" + "0" + sec;
		} else {
			songTime = min + ":" + sec;
		}
		handler.sendEmptyMessage(MSG_START_SHOW_TIME);
		handler.sendEmptyMessage(123);
	}

	public void setKTVModel(boolean isKtv) {
		this.isKTV = isKtv;
		if (isKTV) {
			lrcH = (int) resources.getDimension(R.dimen.practice_ktv_lrc_height);
			lrcW = (int) resources.getDimension(R.dimen.practice_ktv_lrc_width);
			lrcX = (int) resources.getDimension(R.dimen.practice_ktv_lrc_x);
			lrcY = (int) resources.getDimension(R.dimen.practice_ktv_lrc_y);
			curveW = (int) resources.getDimension(R.dimen.practice_ktv_curve_width);
			curveH = (int) resources.getDimension(R.dimen.practice_ktv_curve_height);
			curveX = (int) resources.getDimension(R.dimen.practice_ktv_curve_x);
			curveY = (int) resources.getDimension(R.dimen.practice_ktv_curve_y);
			staffImage.setVisibility(View.VISIBLE);

		} else {
			lrcH = (int) resources.getDimension(R.dimen.practice_vocational_lrc_height);
			lrcW = (int) resources.getDimension(R.dimen.practice_vocational_lrc_width);
			lrcX = (int) resources.getDimension(R.dimen.practice_vocational_lrc_x);
			lrcY = (int) resources.getDimension(R.dimen.practice_vocational_lrc_y);
			curveW = (int) resources.getDimension(R.dimen.practice_vocational_curve_width);
			curveH = (int) resources.getDimension(R.dimen.practice_vocational_curve_height);
			curveX = (int) resources.getDimension(R.dimen.practice_vocational_curve_x);
			curveY = (int) resources.getDimension(R.dimen.practice_vocational_curve_y);
			staffImage.setVisibility(View.INVISIBLE);
		}
		curveLrc.setCurveXYWH(curveX, curveY, curveW, curveH);
		curveLrc.setLrcXYWH(lrcX, lrcY, lrcW, lrcH);
		curveLrc.setKTV(isKTV);
	}

	// 加载图片资源
	public void InitPic() {
		// 数字资源图片
		for (int i = 0; i < 10; i++) {
			whiteNums[i] = BitmapFactory.decodeResource(resources, R.drawable.num0 + i);
			redNums[i] = BitmapFactory.decodeResource(resources, R.drawable.red0 + i);
			smallWhiteNums[i] = BitmapFactory.decodeResource(resources, R.drawable.small0 + i);
		}
		// 评价资源图片
		for (int i = 0; i < 4; i++) {
			evaluates[i] = BitmapFactory.decodeResource(resources, R.drawable.evaluate0 + i);
		}

		// 功能模块的图片
		for (int i = 0; i < 2; i++) {
			ktvMode[i] = BitmapFactory.decodeResource(resources, R.drawable.ktv_selected + i);
			professional[i] = BitmapFactory.decodeResource(resources,
					R.drawable.professional_selected + i);
			original[i] = BitmapFactory.decodeResource(resources, R.drawable.original_selected + i);
			accompany[i] = BitmapFactory.decodeResource(resources, R.drawable.accompany_selected
					+ i);
			volumns[i] = BitmapFactory.decodeResource(resources, R.drawable.volumn_media + i);
		}

		darkBigStar = BitmapFactory.decodeResource(resources, R.drawable.dark_bigstar);
		lightBigStar = BitmapFactory.decodeResource(resources, R.drawable.bigstar);
		darkSmallStar = BitmapFactory.decodeResource(resources, R.drawable.dark_smallstar);
		lightSmallStar = BitmapFactory.decodeResource(resources, R.drawable.smallstar);
		numImageHeight = whiteNums[0].getHeight();
		numImageWidth = whiteNums[0].getWidth();
		leftBm = ((BitmapDrawable) leftImage.getDrawable()).getBitmap();
		rightBm = ((BitmapDrawable) rightImage.getDrawable()).getBitmap();
		evaluateBm = ((BitmapDrawable) evaluateImage.getDrawable()).getBitmap();

	}

	// 初始化动画
	public void InitAnim() {
		// 数字翻转
		numTurnFirstAnim = AnimationUtils.loadAnimation(this, R.anim.numturnfirst);
		numTurnSecAnim = AnimationUtils.loadAnimation(this, R.anim.numturnsec);
		starFirstAnim = AnimationUtils.loadAnimation(this, R.anim.starfirst);
		starSecAnim = AnimationUtils.loadAnimation(this, R.anim.starsec);
		evaluateFirstAnim = AnimationUtils.loadAnimation(this, R.anim.evaluatefirst);
		evaluateSecAnim = AnimationUtils.loadAnimation(this, R.anim.evaluatesec);

		evaluateFirstAnim.setFillAfter(true);
		evaluateSecAnim.setFillAfter(true);
	}

	// 每句话开始时，上一句的分数显示动画，sentenceScore为上一句的分数
	public void StartNumTurnAnim(int sentenceScore) {
		StartStarAnim();
		rightScoreNum = sentenceScore % 10;
		leftScoreNum = (sentenceScore - rightScoreNum) / 10;

		if (sentenceScore >= 90) {
			whichevaluate = 0;
		} else if (sentenceScore >= 80) {
			whichevaluate = 1;
		} else if (sentenceScore >= 70) {
			whichevaluate = 2;
		} else {
			whichevaluate = 3;
		}
		scoreTimer = new Timer();
		scoreTimer.schedule(new TimerTask() {
			int height = numImageHeight / 10;
			int eHeight = evaluateImage.getHeight() / 10;

			@Override
			public void run() {
				changeLeftBm = createBitmap(leftBm, whiteNums[leftScoreNum], numImageWidth,
						numImageHeight, height);
				changeRightBm = createBitmap(rightBm, whiteNums[rightScoreNum], numImageWidth,
						numImageHeight, height);
				changeEvaBm = createBitmap(evaluateBm, evaluates[whichevaluate],
						evaluateImage.getWidth(), evaluateImage.getHeight(), eHeight);
				handler.sendEmptyMessage(100);
				height += height;
				eHeight += eHeight;
				if (height >= numImageHeight) {
					leftBm = whiteNums[leftScoreNum];
					rightBm = whiteNums[rightScoreNum];
					evaluateBm = evaluates[whichevaluate];
					handler.sendEmptyMessage(101);
					this.cancel();
					scoreTimer.cancel();
				}
			}
		}, 0, 150);
	}

	/**
	 * 图片合成
	 * 
	 * @param bitmap
	 * @return
	 */
	private Bitmap createBitmap(Bitmap src1, Bitmap src2, int width, int height, int divHeight) {

		Bitmap newSrc1 = Bitmap.createBitmap(src1, 0, divHeight, width, height - divHeight);
		Bitmap newSrc2 = Bitmap.createBitmap(src2, 0, 0, width, divHeight);

		Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);

		cv.drawBitmap(newSrc1, 0, 0, null);

		cv.drawBitmap(newSrc2, 0, height - divHeight, null);

		cv.save();
//		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	// 星星的动画
	public void StartStarAnim() {
		handler.sendEmptyMessage(107);
		Timer starTimer = new Timer();
		starTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(108);
			}
		}, starFirstAnim.getDuration());

		starTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(109);
			}
		}, starFirstAnim.getDuration() + starSecAnim.getDuration());
	}

	// 评价的动画
	public void StartEvaluateAnim(int sentenceScore) {
		if (sentenceScore >= 90) {
			handler.sendEmptyMessage(102);
		} else if (sentenceScore >= 80) {
			handler.sendEmptyMessage(103);
		} else if (sentenceScore >= 70) {
			handler.sendEmptyMessage(104);
		} else {
			handler.sendEmptyMessage(105);
		}
		Timer evaluateTimer = new Timer();
		evaluateTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(106);
			}
		}, 1500);
	}

	// 跳动和具体功能提示块滑出的动画
	public void StartJumpAnim() {

	}

	public void NextSen() {
		senTimer.cancel();
		songtimer.cancel();
		initTask();
		if (sentenceFlag == 0) {
			isStart = true;
			helpImage.setVisibility(View.INVISIBLE);
		}
		if (sentenceFlag == sentenceSize) {
			nowMediaPlayer.CPause();
			curveLrc.clearTotal();
			return;
		} else if (sentenceFlag == sentenceSize - 1) {
			sentenceFlag = sentenceSize;
			handler.sendEmptyMessage(123);
			nowMediaPlayer.CPause();
			curveLrc.clearTotal();
			return;
		} else {
			sentenceFlag++;
			handler.sendEmptyMessage(123);
		}
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		curveLrc.setToSen(sentenceFlag);
		curveLrc.drawCurveAndLrc(nowSongSentence.get(sentenceFlag).StartTimeofThis, new int[0]);

		countBlueImage.setVisibility(View.VISIBLE);

		nowMediaPlayer.CSeekTo(nowSongSentence.get(sentenceFlag).StartTimeofThis);
		nowMediaPlayer.CPause();

		audRec.setRecording(false);
		audRec.setStartTime(nowSongSentence.get(sentenceFlag).StartTimeofThis);

		senTimer = new Timer();
		senTimer.schedule(timerTask, 1000);
	}

	public void PreSen() {
		if (sentenceFlag == 0) {
			return;
		}
		senTimer.cancel();
		songtimer.cancel();
		initTask();
		if (sentenceFlag <= 0) {
			sentenceFlag = 0;
		} else {
			sentenceFlag--;
			handler.sendEmptyMessage(2);
		}

		curveLrc.clearColor(sentenceFlag);
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		curveLrc.setToSen(sentenceFlag);
		curveLrc.drawCurveAndLrc(nowSongSentence.get(sentenceFlag).StartTimeofThis, new int[0]);

		countBlueImage.setVisibility(View.VISIBLE);
		nowMediaPlayer.CSeekTo(nowSongSentence.get(sentenceFlag).StartTimeofThis);
		nowMediaPlayer.CPause();

		audRec.setRecording(false);
		audRec.setStartTime(nowSongSentence.get(sentenceFlag).StartTimeofThis);

		senTimer = new Timer();
		senTimer.schedule(timerTask, 1000);
	}

	public void NextThreeSen() {
		senTimer.cancel();
		songtimer.cancel();
		initTask();
		if (sentenceFlag == 0) {
			isStart = true;
			helpImage.setVisibility(View.INVISIBLE);
		}
		if (sentenceFlag == sentenceSize) {
			nowMediaPlayer.CPause();
			curveLrc.clearTotal();
			return;
		} else if (sentenceFlag >= sentenceSize - 3) {
			for (int i = 0; i < sentenceSize - 1; i++) {
				handler.sendEmptyMessage(123);
			}
			sentenceFlag = sentenceSize;
			nowMediaPlayer.CPause();
			curveLrc.clearTotal();
			return;
		} else {
			sentenceFlag += 3;
			handler.sendEmptyMessage(123);
			handler.sendEmptyMessage(123);
			handler.sendEmptyMessage(123);
		}

		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		curveLrc.setToSen(sentenceFlag);
		curveLrc.drawCurveAndLrc(nowSongSentence.get(sentenceFlag).StartTimeofThis, new int[0]);

		countBlueImage.setVisibility(View.VISIBLE);

		nowMediaPlayer.CSeekTo(nowSongSentence.get(sentenceFlag).StartTimeofThis);
		nowMediaPlayer.CPause();

		audRec.setRecording(false);
		audRec.setStartTime(nowSongSentence.get(sentenceFlag).StartTimeofThis);

		senTimer = new Timer();
		senTimer.schedule(timerTask, 1000);
	}

	public void PreThreeSen() {
		if (sentenceFlag == 0) {
			return;
		}
		senTimer.cancel();
		songtimer.cancel();
		initTask();
		if (sentenceFlag <= 2) {
			for (int i = sentenceFlag; i > 0; i--) {
				handler.sendEmptyMessage(2);
			}
			sentenceFlag = 0;
		} else {
			sentenceFlag -= 3;
			handler.sendEmptyMessage(2);
			handler.sendEmptyMessage(2);
			handler.sendEmptyMessage(2);
		}

		curveLrc.clearColor(sentenceFlag);
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		curveLrc.setToSen(sentenceFlag);
		curveLrc.drawCurveAndLrc(nowSongSentence.get(sentenceFlag).StartTimeofThis, new int[0]);

		countBlueImage.setVisibility(View.VISIBLE);

		nowMediaPlayer.CSeekTo(nowSongSentence.get(sentenceFlag).StartTimeofThis);
		nowMediaPlayer.CPause();

		audRec.setRecording(false);
		audRec.setStartTime(nowSongSentence.get(sentenceFlag).StartTimeofThis);

		senTimer = new Timer();
		senTimer.schedule(timerTask, 1000);
	}

	public void RedPress() {
		iconTask1.cancel();
		iconTask1 = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(4);
			}
		};
		volumnTextView.setVisibility(TextView.INVISIBLE);
		volumnImage.setVisibility(ImageView.INVISIBLE);
		volumnSeekBar.setVisibility(SeekBar.INVISIBLE);
		funcFirstImage.setVisibility(ImageView.VISIBLE);
		funcSecImage.setVisibility(ImageView.VISIBLE);
		funcSprit.setVisibility(ImageView.VISIBLE);
		if (isComa == false) {
			Toast.makeText(this, "伴奏", Toast.LENGTH_SHORT).show();
			isComa = true;
			funcFirstImage.setImageBitmap(accompany[0]);
			funcSecImage.setImageBitmap(original[1]);
			nowMediaPlayer.CSetAccompany();
		} else {
			Toast.makeText(this, "原唱", Toast.LENGTH_SHORT).show();
			isComa = false;
			funcFirstImage.setImageBitmap(accompany[1]);
			funcSecImage.setImageBitmap(original[0]);
			nowMediaPlayer.CSetOriginal();
		}
		buttonTimer.schedule(iconTask1, 3000);
	}

	public void GreenPress() {
		iconTask2.cancel();
		iconTask2 = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(5);
			}
		};
		volumnTextView.setVisibility(TextView.INVISIBLE);
		volumnImage.setVisibility(ImageView.INVISIBLE);
		volumnSeekBar.setVisibility(SeekBar.INVISIBLE);
		funcFirstImage.setVisibility(ImageView.VISIBLE);
		funcSecImage.setVisibility(ImageView.VISIBLE);
		funcSprit.setVisibility(ImageView.VISIBLE);
		if (isKTV == true) {
			countBlueImage.setX(610);
			countBlueImage.setY(815);
			countRedImage.setX(660);
			countRedImage.setY(815);
			countYellowImage.setX(710);
			countYellowImage.setY(815);
			Toast.makeText(this, "专业模式", Toast.LENGTH_SHORT).show();
			funcFirstImage.setImageBitmap(ktvMode[1]);
			funcSecImage.setImageBitmap(professional[0]);

		} else {
			countBlueImage.setX(120);
			countBlueImage.setY(420);
			countRedImage.setX(170);
			countRedImage.setY(420);
			countYellowImage.setX(220);
			countYellowImage.setY(420);
			Toast.makeText(this, "ktv模式", Toast.LENGTH_SHORT).show();
			funcFirstImage.setImageBitmap(ktvMode[0]);
			funcSecImage.setImageBitmap(professional[1]);

		}
		setKTVModel(!isKTV);
		buttonTimer.schedule(iconTask2, 3000);
	}

	public void YellowPress() {
		iconTask3.cancel();
		iconTask3 = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(6);
			}
		};
		funcFirstImage.setVisibility(ImageView.INVISIBLE);
		funcSecImage.setVisibility(ImageView.INVISIBLE);
		funcSprit.setVisibility(ImageView.INVISIBLE);
		volumnImage.setImageBitmap(volumns[0]);
		Toast.makeText(this, "减小背景音的音量", Toast.LENGTH_SHORT).show();
		mediaVolume = mediaVolume - 1;
		mediaVolumn_float = mediaVolumn_float - 0.1f;
		if (mediaVolume < 0) {
			mediaVolume = 0;
			mediaVolumn_float = 0;
		}
		nowMediaPlayer.CSetVolume(mediaVolumn_float);
		mediaVolumn_int = (int) (mediaVolume * 10);
		volumnSeekBar.setProgress(mediaVolumn_int);
		volumnTextView.setText("" + mediaVolume);
		volumnImage.setVisibility(ImageView.VISIBLE);
		volumnSeekBar.setVisibility(SeekBar.VISIBLE);
		volumnTextView.setVisibility(TextView.VISIBLE);
		buttonTimer.schedule(iconTask3, 3000);
	}

	public void BluePress() {
		iconTask3.cancel();
		iconTask3 = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(6);
			}
		};
		funcFirstImage.setVisibility(ImageView.INVISIBLE);
		funcSecImage.setVisibility(ImageView.INVISIBLE);
		funcSprit.setVisibility(ImageView.INVISIBLE);
		volumnImage.setImageBitmap(volumns[0]);
		mediaVolume = mediaVolume + 1;
		mediaVolumn_float = mediaVolumn_float + 0.1f;
		if (mediaVolume > 10) {
			mediaVolume = 10;
			mediaVolumn_float = 1;
		}
		nowMediaPlayer.CSetVolume(mediaVolumn_float);
		mediaVolumn_int = (int) (mediaVolume * 10);
		Toast.makeText(this, "调大媒体音量", Toast.LENGTH_SHORT).show();
		volumnSeekBar.setProgress(mediaVolumn_int);
		volumnTextView.setText("" + mediaVolume);
		volumnImage.setVisibility(ImageView.VISIBLE);
		volumnSeekBar.setVisibility(SeekBar.VISIBLE);
		volumnTextView.setVisibility(TextView.VISIBLE);
		buttonTimer.schedule(iconTask3, 3000);
	}

	// 获取listview的高和宽，网上找的，不太清楚
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.height += 5;// if without this statement,the listview will be a
							// little short
		listView.setLayoutParams(params);
	}

	// 向后滚动一个，参数也是自定义的适配器
	public void ScrollToNext(MyAdapter myAdapter) {
		select_item = select_item + 1;
		if (select_item > sentenceSize) {
			select_item = sentenceSize;
		} else {
			mposition = mposition + 33;
		}
		listView.scrollTo(0, mposition);

		myAdapter.notifyDataSetChanged();
	}

	// 向前滚动，参数是自定义的适配器
	public void ScrollToPast(MyAdapter myAdapter) {

		select_item = select_item - 1;
		if (select_item < 0) {
			mposition = -367 + (sentenceSize - 1) * 38;
			select_item = sentenceSize - 1;
		} else {
			mposition = mposition - 33;
		}
		listView.scrollTo(0, mposition);
		myAdapter.notifyDataSetChanged();

	}

	class ViewHolder {
		TextView lyricTV;
		ImageView leftScoreImage, rightScoreImage, smallStarImage;
	}

	// 自定义适配器
	class MyAdapter extends BaseAdapter {
		List<Sentence> list;
		Context con;
		int smallLeftScore, smallRightScore;
		Bitmap smallLeftChangeBm, smallRightChangeBm, smallLeftBm, smallRightBm;
		Timer smallScoreTimer, smallStarTimer;
		int smallImageHeight, smallImageWidth;
		ViewHolder holder;
		Handler listHandler;

		public MyAdapter(ArrayList<Sentence> list, Context con) {
			super();
			this.list = list;
			this.con = con;
			smallLeftBm = smallWhiteNums[0];
			smallRightBm = smallWhiteNums[0];
			smallImageHeight = smallWhiteNums[0].getHeight();
			smallImageWidth = smallWhiteNums[0].getWidth();
			listHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					if (msg.what == 100) {
						holder.leftScoreImage.setImageBitmap(smallLeftChangeBm);
						holder.rightScoreImage.setImageBitmap(smallRightChangeBm);
					} else if (msg.what == 101) {
						holder.leftScoreImage.setImageBitmap(smallLeftBm);
						holder.rightScoreImage.setImageBitmap(smallRightBm);
					} else if (msg.what == 102) {
						holder.smallStarImage.startAnimation(starSecAnim);
					} else if (msg.what == 103) {
						holder.smallStarImage.setImageBitmap(darkSmallStar);
					} else if (msg.what == 104) {
						holder.smallStarImage.setImageBitmap(lightSmallStar);
						holder.smallStarImage.startAnimation(starFirstAnim);
					}
				}
			};
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(con).inflate(R.layout.lyric_item, null);
				holder = new ViewHolder();
				holder.lyricTV = (TextView) convertView.findViewById(R.id.lyric_tv);
				holder.leftScoreImage = (ImageView) convertView.findViewById(R.id.leftscoreimage);
				holder.rightScoreImage = (ImageView) convertView.findViewById(R.id.rightscoreimage);
				holder.smallStarImage = (ImageView) convertView.findViewById(R.id.smallstarimage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (list.get(position).text.length() > 12) {
				holder.lyricTV.setText(list.get(position).text.subSequence(0, 12) + "...");
			} else {
				holder.lyricTV.setText(list.get(position).text);
			}
//			holder.lyricTV.setTextSize(StringUtil.getRawSize(getBaseContext(), TypedValue.COMPLEX_UNIT_SP, 12));

			smallRightScore = list.get(position).Score % 10;
			smallLeftScore = (list.get(position).Score - smallRightScore) / 10;

			if (select_item == position) {
				holder.lyricTV.setTextColor(Color.RED);
				holder.leftScoreImage.setImageBitmap(redNums[smallLeftScore]);
				holder.rightScoreImage.setImageBitmap(redNums[smallRightScore]);
			} else {
				holder.lyricTV.setTextColor(Color.WHITE);
				holder.leftScoreImage.setImageBitmap(smallWhiteNums[smallLeftScore]);
				holder.rightScoreImage.setImageBitmap(smallWhiteNums[smallRightScore]);
			}
			return convertView;

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		nowMediaPlayer.CPause();
		songtimer.cancel();
		disTimer.cancel();
		senTimer.cancel();
		buttonTimer.cancel();
		timeTimer.cancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		nowMediaPlayer.release();
		audRec.free();
		audRec = null;
		Intent in = new Intent(PracticeActivity.this, MainService.class);
		in.setAction(Constant.stopSongAction);
		in.putExtra("personid", personId);
		startService(in);

		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// =========================广播接收器==========================================================
	private class MyBroadcastRecv extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constant.receivedTalkRequestAction)) {
				Intent mMainServiceIntent = new Intent(getBaseContext(), MainService.class);
				final Person psn = (Person) intent.getExtras().get("person");
				mMainServiceIntent.putExtra("person", psn);
				mMainServiceIntent.setAction(Constant.acceptTalkRequestAction);
				startService(mMainServiceIntent);
			} else if (intent.getAction().equals(Constant.receiveKeyPressedAction)) {
				Integer keyCode = (Integer) intent.getExtras().get("keycode");
				final Person psn = (Person) intent.getExtras().get("person");
				if (psn.personId != personId)
					return;
				int code = keyCode.intValue();
				int pressedKey = -1;
				switch (code) {
				case Constant.KEYUP:
					pressedKey = KeyEvent.KEYCODE_DPAD_UP;
					break;
				case Constant.KEYDOWN:
					pressedKey = KeyEvent.KEYCODE_DPAD_DOWN;
					break;
				case Constant.KEYLEFT:
					pressedKey = KeyEvent.KEYCODE_DPAD_LEFT;
					break;
				case Constant.KEYRIGHT:
					pressedKey = KeyEvent.KEYCODE_DPAD_RIGHT;
					break;
				case Constant.KEYBACK:
					pressedKey = KeyEvent.KEYCODE_BACK;
					break;
				case Constant.KEYMODE:
					pressedKey = 184;
					break;
				case Constant.KEYORIGINAL:
					pressedKey = 183;
					break;
				default:
					break;
				}
				if (pressedKey != -1) {
					onKeyDown(pressedKey, null);
				}
			} else if (intent.getAction().equals(Constant.getCurrentModeAction)) {
				final Person psn = (Person) intent.getExtras().get("person");

				Intent in = new Intent(PracticeActivity.this, MainService.class);
				in.putExtra("mode", Constant.PRACTICE);
				in.putExtra("person", psn);
				in.setAction(Constant.returnCurrentModeAction);
				startService(in);
			} else if (intent.getAction().equals(Constant.volumeChangedAction)) {
				final Person psn = (Person) intent.getExtras().get("person");
				if (psn == null)
					return;
				int volume = intent.getIntExtra("volume", -1);
				if (psn.personId == personId && volume != -1) {
					// 改变音量 volume为0~100的数

				}
			}
		}
	}

	// =========================广播接收器结束==========================================================

	// 广播接收器注册
	private void regBroadcastRecv() {
		broadcastRecv = new MyBroadcastRecv();
		bFilter = new IntentFilter();
		bFilter.addAction(Constant.volumeChangedAction);
		bFilter.addAction(Constant.getCurrentModeAction);
		bFilter.addAction(Constant.receivedTalkRequestAction);
		bFilter.addAction(Constant.receiveKeyPressedAction);
		registerReceiver(broadcastRecv, bFilter);
	}

	private void sendNextActivityMsg(int activityId) {
		Intent in = new Intent(PracticeActivity.this, MainService.class);
		in.putExtra("activityid", activityId);
		in.putExtra("personid", personId);
		in.setAction(Constant.nextActivityAction);
		startService(in);
	}

	private void sendStartSongMsg() {
		Intent in = new Intent(PracticeActivity.this, MainService.class);
		in.setAction(Constant.startSongAction);
		in.putExtra("personid", personId);
		startService(in);
	}
}
