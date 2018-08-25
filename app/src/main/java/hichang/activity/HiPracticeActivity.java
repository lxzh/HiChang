package hichang.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.android.flypigeon.service.MainService;
import com.android.flypigeon.util.Constant;
import com.android.flypigeon.util.Person;

import hichang.Song.*;
import hichang.audio.AudRec;
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
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class HiPracticeActivity extends Activity {

	/**
	 * 当前播放歌曲
	 */
	private Song nowSong;
	/**
	 * 解析后的歌曲信息
	 */
	private ReadText nowSongText;
	/**
	 * 解析后的歌词句子组
	 */
	private ArrayList<Sentence> nowSongSentence;
	/**
	 * 歌曲的总时长（毫秒）
	 */
	private int nowSongLength;
	/**
	 * 当前播放到的毫秒数
	 */
	private int nowSongTime;
	/**
	 * 当前播放到的句子的游标（初始值0）
	 */
	private int sentenceFlag = 0;
	/**
	 * 当前播放到的句子,下一句和最后一句
	 */
	private Sentence nowSentence, nextSentence, lastSentence;
	/**
	 * 当前歌的句子数目
	 */
	private int sentenceSize;
	/**
	 * 使用timer控件控制整个练歌过程
	 */
	private Timer songtimer;
	/**
	 * 歌词显示布局
	 */
	private ListView listView;
	/**
	 * 自定义的List适配器
	 */
	private MyAdapter adapter;
	/**
	 * 歌词存放的地方（int 初始值为-1）
	 */
	public int select_item;// 现在播放的某句歌词
	/**
	 * 滚动的位置（int 初始值为-367）
	 */
	public int mposition;
	/**
	 * 判断点击的参数，无需理会
	 */
	public static int a = 0;
	/**
	 * 歌词 的曲线
	 */
	public VoiceView voiceView;
	/**
	 * 左边两句歌词（LrcTextView）
	 */
	private LrcTextView lrcView;
	/**
	 * 跑动的时针
	 */
	private ImageView timeHand;
	/**
	 * 是否调节麦克音量
	 */
	private boolean isMic = false;
	/**
	 * 是否在调节媒体音量
	 */
	private boolean isMedia = false;
	/**
	 * 是否调节伴奏音乐
	 */
	private boolean isComa = false;
	/**
	 * 是否调节模式
	 */
	private boolean isKTV = false;
	/**
	 * 初始位置常量（-466）
	 */
	private int INITPOSITION = -466;
	/**
	 * 消息队列
	 */
	private Handler handler;
	/**
	 * 媒体音量
	 */
	private int mediaVolumn = 30;
	/**
	 * 麦克风音量
	 */
	private int micVolumn = 30;
	/**
	 * 数字图片
	 */
	Bitmap[] smallWhiteNums = new Bitmap[10];
	/**
	 * 数字图片
	 */
	Bitmap[] whiteNums = new Bitmap[10];
	/**
	 * 数字图片
	 */
	Bitmap[] redNums = new Bitmap[10];

	/**
	 * 评价图片
	 */
	Bitmap[] evaluates = new Bitmap[4];
	/**
	 * 功能模块的图片
	 */
	Bitmap[] accompany = new Bitmap[2];
	/**
	 * 功能模块的图片
	 */
	Bitmap[] original = new Bitmap[2];
	/**
	 * 功能模块的图片
	 */
	Bitmap[] ktvMode = new Bitmap[2];
	/**
	 * 功能模块的图片
	 */
	Bitmap[] professional = new Bitmap[2];
	/**
	 * 功能模块的图片
	 */
	Bitmap[] volumns = new Bitmap[2];
	/**
	 * 星星的图片
	 */
	Bitmap darkBigStar;
	/**
	 * 星星的图片
	 */
	Bitmap lightBigStar, darkSmallStar, lightSmallStar;
	/**
	 * 星星的动画
	 */
	Timer starTimer;
	/**
	 * 资源
	 */
	private Resources resources;
	/**
	 * 动画
	 */
	private Animation numTurnFirstAnim;
	/**
	 * 动画
	 */
	private Animation numTurnSecAnim;
	/**
	 * 动画
	 */
	private Animation starFirstAnim;
	/**
	 * 动画
	 */
	private Animation starSecAnim;
	/**
	 * 动画
	 */
	private Animation evaluateFirstAnim;
	/**
	 * 动画
	 */
	private Animation evaluateSecAnim;
	/**
	 * 动画
	 */
	private Animation jumpFirstAnim;
	/**
	 * 动画
	 */
	private Animation jumpSecAnim;
	/**
	 * 动画
	 */
	private TranslateAnimation timeAnim;
	/**
	 * 分数的ImageView
	 */
	private ImageView leftImage, rightImage;
	/**
	 * 分数图的高度和宽度
	 */
	private int numImageHeight, numImageWidth;
	/**
	 * 分数图及中间切换
	 */
	private Bitmap leftBm, rightBm, changeLeftBm, changeRightBm;
	/**
	 * 评价图及中间切换
	 */
	private Bitmap evaluateBm, changeEvaBm;
	/**
	 * 哪一个评价
	 */
	private int whichevaluate;
	/**
	 * 星星的ImageView
	 */
	private ImageView starImage;
	/**
	 * 每句分数的十位和各位数字
	 */
	private int leftScoreNum, rightScoreNum;
	/**
	 * 最下方功能提示处的ImageView
	 */
	private ImageView funcRedImage, funcGreenImage, funcYellowImage, funcBlueImage;
	/**
	 * 原唱、ktv是否开启显示
	 */
	private ImageView funcFirstImage;
	/**
	 * 伴奏、专业是否开启显示
	 */
	private ImageView funcSecImage;
	/**
	 * 原唱伴奏或者ktv专业中部斜杠
	 */
	private ImageView funcSprit;
	/**
	 * 功能键右侧，即原唱/伴奏、ktv/专业以及seekbar、麦克风图标等显示区域
	 */
	private AbsoluteLayout funcLayout;
	/**
	 * 音量条
	 */
	private SeekBar volumnSeekBar;
	/**
	 * 音量条图标
	 */
	private ImageView volumnImage;
	/**
	 * 评价ImageView
	 */
	private ImageView evaluateImage;
	/**
	 * 倒计时图标
	 */
	private ImageView countBlueImage, countRedImage, countYellowImage;
	/**
	 * 是否是刚开始播放
	 */
	private boolean isStart = true;
	/**
	 * 是否是选择开始播放
	 */
	private boolean isSelectStart = true;
	/**
	 * 倒计时的时间
	 */
	private double leftTimes;
	/**
	 * 倒计时的timer
	 */
	private Timer disTimer;
	/**
	 * 时针的timer
	 */
	private Timer handTimer;
	/**
	 * 分数的timer
	 */
	private Timer scoreTimer;
	/**
	 * scoreTimer的Task
	 */
	private TimerTask scoreTimerTask;
	/**
	 * 评价的动画
	 */
	private Timer evaluateTimer;
	/**
	 * 播放器
	 */
	private CMediaPlayer nowMediaPlayer;
	/**
	 * 音频接收
	 */
	private AudRec audRec;
	/**
	 * 时针的步长
	 */
	final int TIMESTEP = 40;
	/**
	 * 当前画点的标准时间
	 */
	int standTime;
	/**
	 * 时针的坐标
	 */
	int timeHandX;
	/**
	 * songtimer是否cancel掉了
	 */
	private boolean isSongTimerCancel;
	/**
	 * 倒计时是否cancel了
	 */
	private boolean isDisTimerCancel;
	/**
	 * handTimer是否cancel了
	 */
	private boolean isHandTimerCancel;
	/**
	 * scoreTimer是否cancel了
	 */
	private boolean isScoreTimerCancel;
	/**
	 * starTimer是否cancel了
	 */
	private boolean isStarTimerCancel;
	/**
	 * evaluateTimer是否取消了
	 */
	private boolean isEvaluateTimerCancel;
	/**
	 * 是否是刚开始之前就按左键或上键了，初始值为false
	 */
	private boolean isStartPre;
	/**
	 * 用于内存显示的timer
	 */
	private Timer timer;
	/**
	 * timer是否cancel了
	 */
	private boolean isTimerCancel;
	// SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
	// int soundId;

	TextView text1;
	int[] pids = new int[1];
	ActivityManager am;
	MemoryInfo outInfo;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.practice);

		isStartPre = false;
		select_item = -1;
		mposition = -466;
		// voiceView = (VoiceView) findViewById(R.id.voiceview);
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
		// evaluateImage = (ImageView) findViewById(R.id.evaluteimage);
		volumnSeekBar = (SeekBar) findViewById(R.id.player_seekbar);
		volumnImage = (ImageView) findViewById(R.id.volumntype);
		// countBlueImage = (ImageView) findViewById(R.id.countblue);
		// countRedImage = (ImageView) findViewById(R.id.countred);
		// countYellowImage = (ImageView) findViewById(R.id.countyellow);
		listView = (ListView) findViewById(R.id.listview_list);
		// lrcView = (LrcTextView) findViewById(R.id.lrcview);
		// timeHand = (ImageView) findViewById(R.id.timeHand);
		starImage = (ImageView) findViewById(R.id.starimage);

		resources = this.getResources();

		// 歌曲文件路径
		Intent intent = new Intent();
		int songid = intent.getIntExtra("songId", 0);
		nowSong = new Song(getBaseContext());
		nowSong = nowSong.findSongById(songid);

		nowMediaPlayer = new CMediaPlayer();
		// 各种初始化
		InitSong();
		InitPic();
		InitAnim();

		songtimer = new Timer();
		isSongTimerCancel = false;
		disTimer = new Timer();
		isDisTimerCancel = false;
		handTimer = new Timer();
		isHandTimerCancel = false;
		scoreTimer = new Timer();
		isScoreTimerCancel = false;
		starTimer = new Timer();
		isStarTimerCancel = false;
		evaluateTimer = new Timer();
		isEvaluateTimerCancel = false;

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
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
				} else if (msg.what == 102) {
					evaluateImage.setImageBitmap(evaluates[0]);
					evaluateImage.startAnimation(evaluateFirstAnim);
				} else if (msg.what == 103) {
					evaluateImage.setImageBitmap(evaluates[1]);
					evaluateImage.startAnimation(evaluateFirstAnim);
				} else if (msg.what == 104) {
					evaluateImage.setImageBitmap(evaluates[2]);
					evaluateImage.startAnimation(evaluateFirstAnim);
				} else if (msg.what == 105) {
					evaluateImage.setImageBitmap(evaluates[3]);
					evaluateImage.startAnimation(evaluateFirstAnim);
				} else if (msg.what == 106) {
					evaluateImage.startAnimation(evaluateSecAnim);
				} else if (msg.what == 107) {
					starImage.setImageBitmap(lightBigStar);
					starImage.startAnimation(starFirstAnim);
				} else if (msg.what == 108) {
					starImage.startAnimation(starSecAnim);
				} else if (msg.what == 109) {
					starImage.setImageBitmap(darkBigStar);
				} else if (msg.what == 110) {
					countYellowImage.setVisibility(ImageView.INVISIBLE);
				} else if (msg.what == 111) {
					countRedImage.setVisibility(ImageView.INVISIBLE);
				} else if (msg.what == 112) {
					countBlueImage.setVisibility(ImageView.INVISIBLE);
				} else if (msg.what == 113) {
					SongStart();
				} else if (msg.what == 114) {
					StartRoundDis();
				} else if (msg.what == 115) {

					am.getMemoryInfo(outInfo);
					List<RunningAppProcessInfo> runApps = am.getRunningAppProcesses();
					Debug.MemoryInfo[] dmf = am.getProcessMemoryInfo(pids);
					text1.setText("当前activity占用内存" + dmf[0].dalvikPrivateDirty + "KB" + "可用内存"
							+ outInfo.availMem / 1024 + "KB");
				} else if (msg.what == 116) {
					timeHand.setVisibility(ImageView.VISIBLE);
					timeHand.startAnimation(timeAnim);

				} else if (msg.what == 117) {
					timeHand.setVisibility(ImageView.INVISIBLE);
					timeHand.setX(voiceView.getX());
				} else if (msg.what == 118) {
					timeAnim.cancel();
					timeHand.setAnimation(null);
				} else if (msg.what == 119) {
					timeHand.setVisibility(ImageView.VISIBLE);
					timeHand.setX(timeHand.getX() + ((float) voiceView.getWidth())
							/ nowSentence.LastTimeofThis * 40);
				} else if (msg.what == 120) {
					timeHand.setVisibility(ImageView.INVISIBLE);
					timeHand.setX(voiceView.getX());
				} else if (msg.what == 121) {
					countBlueImage.setVisibility(ImageView.VISIBLE);
					countRedImage.setVisibility(ImageView.VISIBLE);
					countYellowImage.setVisibility(ImageView.VISIBLE);
				} else if (msg.what == 122) {
					timeHand.setX(voiceView.getX());
					timeHand.setY(voiceView.getY());
					timeHand.setVisibility(ImageView.VISIBLE);
					timeHand.setX(timeHandX);
				}
			}
		};

		nowMediaPlayer.CStart();

		songtimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				nowSongTime = nowMediaPlayer.CGetCurrentPosition();
				if (nowSongTime >= nowSongSentence.get(0).StartTimeofThis - 2500) {
					handler.sendEmptyMessage(114);
					this.cancel();
					if (!isSongTimerCancel) {
						songtimer.cancel();
						isSongTimerCancel = true;
					}
				}
			}
		}, 0, 50);

		// 创建一个线性布局
		listView.scrollTo(0, mposition);
		// 创建一个ScrollView对象
		adapter = new MyAdapter(nowSongSentence, getBaseContext());// 适配器的初始化方法，前一个是歌词的List，后一个是上下文
		listView.setAdapter(adapter);
		setListViewHeightBasedOnChildren(listView);

		int myPid = Process.myPid();
		pids[0] = myPid;
		text1 = (TextView) findViewById(R.id.text);
		timer = new Timer();
		isTimerCancel = false;

		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		text1.setTextSize(15);
		outInfo = new MemoryInfo();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(115);
			}
		}, 0, 500);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
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
		// 音量加
		else if (keyCode == 24) {
			// 此处注释掉的代码需要重新写
			// if (isMedia) {
			// nowMediaPlayer.CAddVolumn();
			// volumnSeekBar
			// .setProgress((int) (nowMediaPlayer.CGetVolume() * 100));
			// } else if (isMic) {
			//
			// }
		}
		// 音量减
		else if (keyCode == 25) {
			// 此处注释掉的代码要重写
			// if (isMedia) {
			// nowMediaPlayer.CSubVolumn();
			// volumnSeekBar
			// .setProgress((int) (nowMediaPlayer.CGetVolume() * 100));
			// } else if (isMic) {
			//
			// }
		}
		return true;
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

		voiceView.init(nowSongText.max, nowSongText.min);
		lrcView.init(nowSongLength);

		// 初始化歌的句子的一些信息
		sentenceSize = nowSongSentence.size();
		nowSentence = nowSongSentence.get(0);
		nextSentence = nowSongSentence.get(1);
		sentenceFlag++;
		isStart = true;
		lastSentence = nowSongSentence.get(sentenceSize - 1);
		// 初始化音频接收
		audRec = new AudRec(nowSongSentence);
		audRec.init();

	}

	/**
	 * 加载图片资源
	 */
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

	// 时针动画
	public void StartTime(int duration) {

		timeAnim = new TranslateAnimation(0, voiceView.getWidth() - 5, 0, 0);
		timeAnim.setDuration(duration);
		timeAnim.setInterpolator(new LinearInterpolator());
		handler.sendEmptyMessage(116);
		handTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(117);
			}
		}, duration);
	}

	/**
	 * 每句话开始时，上一句的分数显示动画
	 * 
	 * @param sentenceScore
	 *            上一句的分数
	 */
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
		if (isScoreTimerCancel) {
			scoreTimer = new Timer();
			isScoreTimerCancel = false;
		}
		scoreTimer.schedule(new TimerTask() {
			int height = numImageHeight / 10;
			int eHeight = evaluateImage.getHeight() / 10;

			@Override
			public void run() {
				// TODO Auto-generated method stub
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
					if (!isScoreTimerCancel) {
						scoreTimer.cancel();
						isScoreTimerCancel = true;
					}
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

	/**
	 * 星星的动画
	 */
	public void StartStarAnim() {
		handler.sendEmptyMessage(107);
		if (isStarTimerCancel) {
			starTimer = new Timer();
			isStarTimerCancel = false;
		}
		starTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(108);
			}
		}, starFirstAnim.getDuration());

		starTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(109);
			}
		}, starFirstAnim.getDuration() + starSecAnim.getDuration());
	}

	/**
	 * 倒计时以及下面歌词出现
	 */
	public void StartRoundDis() {
		if (!isDisTimerCancel) {
			disTimer.cancel();
			isDisTimerCancel = true;
		}
		isSelectStart = true;

		voiceView.ClearTotal();
		voiceView.ClearTotal();

		lrcView.Clear();
		lrcView.Clear();
		// audRec.setStartTime(nowSentence.StartTimeofThis);
		standTime = nowSentence.StartTimeofThis;

		if (timeAnim != null) {
			handler.sendEmptyMessage(118);
		}

		leftTimes = 2.5;
		if (isDisTimerCancel) {
			disTimer = new Timer();
			isDisTimerCancel = false;
		}
		disTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(117);
				if (leftTimes == 2) {
					handler.sendEmptyMessage(121);
					voiceView.DrawLrc(nowSentence, isKTV);
					if (sentenceFlag == sentenceSize) {
						lrcView.myDrawText(nowSentence, null, isKTV);
					} else {
						lrcView.myDrawText(nowSentence, nextSentence, isKTV);
					}
				}
				if (leftTimes == 1.5) {
					handler.sendEmptyMessage(110);
				} else if (leftTimes == 1) {
					handler.sendEmptyMessage(111);
				} else if (leftTimes == 0.5) {
					handler.sendEmptyMessage(112);
				} else if (leftTimes == 0) {
					if (isStart) {
						isStart = false;
						handler.sendEmptyMessage(123);
						audRec.start();
					} else
						nowMediaPlayer.CReStart();
					if (isStartPre) {
						nowMediaPlayer.CReStart();
					}
					// StartTime(nowSentence.LastTimeofThis);
					handler.sendEmptyMessage(113);
					this.cancel();
					if (!isDisTimerCancel) {
						disTimer.cancel();
						isDisTimerCancel = true;
					}
				}
				leftTimes -= 0.5;
			}
		}, 0, 500);
	}

	/**
	 * 开始练歌
	 */
	public void SongStart() {
		if (isSongTimerCancel) {
			songtimer = new Timer();
			isSongTimerCancel = false;
		}
		songtimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				nowSongTime = nowMediaPlayer.CGetCurrentPosition();
				if (nowSongTime >= lastSentence.StartTimeofThis + lastSentence.LastTimeofThis) {
					StartNumTurnAnim(60);
					nowSongSentence.get(sentenceFlag - 1).Score = 60;
					// StartEvaluateAnim(60);
					handler.sendEmptyMessage(123);
					handler.sendEmptyMessage(117);
					sentenceFlag++;
					voiceView.Clear(false);
					voiceView.Clear(false);
					lrcView.Clear();
					lrcView.Clear();
					// audRec.free();
					// soundPool.play(soundId, 1, 1, 1, 2, 1);
					this.cancel();
					if (!isSongTimerCancel) {
						songtimer.cancel();
						isSongTimerCancel = true;
					}
				} else {
					if (nowSongTime >= nextSentence.StartTimeofThis) {
						// 给前一句打分

						nowSentence = nextSentence;
						// handler.sendEmptyMessage(120);
						standTime = nowSentence.StartTimeofThis;
						StartTime(nowSentence.LastTimeofThis + nowSentence.StartTimeofThis
								- nowSongTime);
						StartNumTurnAnim(90);
						nowSongSentence.get(sentenceFlag - 1).Score = 90;
						handler.sendEmptyMessage(123);
						// StartEvaluateAnim(90);
						sentenceFlag++;
						if (sentenceFlag == sentenceSize) {
							nextSentence = new Sentence(nowSongLength, nowSongLength);
							lrcView.myDrawText(nowSentence, null, isKTV);
						} else {
							nextSentence = nowSongSentence.get(sentenceFlag);
							lrcView.myDrawText(nowSentence, nextSentence, isKTV);
						}
						voiceView.DrawLrc(nowSentence, isKTV);
					}
					if (nowSongTime >= nowSentence.StartTimeofThis + TIMESTEP) {
						// handler.sendEmptyMessage(119);
						int min = -4;
						int max = 4;
						Random random = new Random();

						voiceView.drawPoints(standTime, 0);
						// timeHandX = (int) voiceView.getX() +
						// voiceView.drawPoints(standTime,random.nextInt(max -
						// min + 1) + min);
						standTime += TIMESTEP;
						// handler.sendEmptyMessage(122);
					}
					lrcView.DrawSentence(nowSongTime);
					// voiceView.drawPoints(nowSongTime-40,
					// audRec.getNote(nowSongTime));
				}

			}
		}, 0, TIMESTEP);
	}

	public void NextSen() {
		if (isStart) {
			isStart = false;
			handler.sendEmptyMessage(123);
		}
		if (!isSongTimerCancel) {
			songtimer.cancel();
			isSongTimerCancel = true;
		}

		if (sentenceFlag < sentenceSize - 1) {
			ScrollToNext(adapter);
			sentenceFlag++;
			nowSentence = nowSongSentence.get(sentenceFlag - 1);
			nextSentence = nowSongSentence.get(sentenceFlag);
		} else if (sentenceFlag == sentenceSize - 1) {
			ScrollToNext(adapter);
			sentenceFlag++;
			nowSentence = nowSongSentence.get(sentenceFlag - 1);
			nextSentence = new Sentence(nowSongLength, nowSongLength);
		}
		nowMediaPlayer.CSeekTo(nowSentence.StartTimeofThis);
		nowMediaPlayer.CPause();
		StartRoundDis();
	}

	public void PreSen() {
		if (isStart) {
			if (!isStartPre) {
				isStartPre = true;
			}
		}
		if (!isSongTimerCancel) {
			songtimer.cancel();
			isSongTimerCancel = true;
		}

		if (sentenceFlag < 2) {
			sentenceFlag = 1;
		} else {
			sentenceFlag--;
			ScrollToPast(adapter);
		}
		nowSentence = nowSongSentence.get(sentenceFlag - 1);
		nextSentence = nowSongSentence.get(sentenceFlag);
		nowMediaPlayer.CSeekTo(nowSentence.StartTimeofThis);
		nowMediaPlayer.CPause();
		StartRoundDis();
	}

	public void NextThreeSen() {
		if (isStart) {
			isStart = false;
			handler.sendEmptyMessage(123);
		}
		if (!isSongTimerCancel) {
			songtimer.cancel();
			isSongTimerCancel = true;
		}
		if (sentenceFlag < sentenceSize - 3) {
			ScrollToNext(adapter);
			ScrollToNext(adapter);
			ScrollToNext(adapter);
			sentenceFlag++;
			sentenceFlag++;
			sentenceFlag++;
			nowSentence = nowSongSentence.get(sentenceFlag - 1);
			nextSentence = nowSongSentence.get(sentenceFlag);
		} else if (sentenceFlag == sentenceSize - 3) {
			ScrollToNext(adapter);
			ScrollToNext(adapter);
			ScrollToNext(adapter);
			sentenceFlag++;
			sentenceFlag++;
			sentenceFlag++;
			nowSentence = nowSongSentence.get(sentenceFlag - 1);
			nextSentence = new Sentence(nowSongLength, nowSongLength);
		} else if (sentenceFlag == sentenceSize - 2) {
			ScrollToNext(adapter);
			ScrollToNext(adapter);
			sentenceFlag++;
			sentenceFlag++;
			nowSentence = nowSongSentence.get(sentenceFlag - 1);
			nextSentence = new Sentence(nowSongLength, nowSongLength);
		} else if (sentenceFlag == sentenceSize - 1) {
			ScrollToNext(adapter);
			sentenceFlag++;
			nowSentence = nowSongSentence.get(sentenceFlag - 1);
			nextSentence = new Sentence(nowSongLength, nowSongLength);
		}
		nowMediaPlayer.CSeekTo(nowSentence.StartTimeofThis);
		nowMediaPlayer.CPause();
		StartRoundDis();
	}

	public void PreThreeSen() {
		if (isStart) {
			if (!isStartPre) {
				isStartPre = true;
			}
		}
		if (!isSongTimerCancel) {
			songtimer.cancel();
			isSongTimerCancel = true;
		}
		if (sentenceFlag == 1) {
			sentenceFlag = 1;
		} else if (sentenceFlag == 2) {
			sentenceFlag = 1;
			ScrollToPast(adapter);
		} else if (sentenceFlag == 3) {
			sentenceFlag = 1;
			ScrollToPast(adapter);
			ScrollToPast(adapter);
		} else {
			sentenceFlag -= 3;
			ScrollToPast(adapter);
			ScrollToPast(adapter);
			ScrollToPast(adapter);
		}
		nowSentence = nowSongSentence.get(sentenceFlag - 1);
		nextSentence = nowSongSentence.get(sentenceFlag);
		nowMediaPlayer.CSeekTo(nowSentence.StartTimeofThis);
		nowMediaPlayer.CPause();
		StartRoundDis();
	}

	public void RedPress() {
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
	}

	public void GreenPress() {
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
			isKTV = false;
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
			isKTV = true;
			funcFirstImage.setImageBitmap(ktvMode[0]);
			funcSecImage.setImageBitmap(professional[1]);
		}
		if (!isSongTimerCancel) {
			songtimer.cancel();
			isSongTimerCancel = true;
		}
		nowMediaPlayer.CSeekTo(nowSentence.StartTimeofThis);
		nowMediaPlayer.CPause();

		AbsoluteLayout.LayoutParams vLayoutParams = (android.widget.AbsoluteLayout.LayoutParams) voiceView
				.getLayoutParams();
		voiceView.setLayoutParams((android.widget.AbsoluteLayout.LayoutParams) lrcView
				.getLayoutParams());
		lrcView.setLayoutParams(vLayoutParams);

		// timeHand.setLayoutParams(new AbsoluteLayout.LayoutParams(timeHand
		// .getWidth(), voiceView.getHeight(), (int) voiceView.getX(),
		// (int) voiceView.getY()));

		StartRoundDis();
	}

	public void YellowPress() {
		funcFirstImage.setVisibility(ImageView.INVISIBLE);
		funcSecImage.setVisibility(ImageView.INVISIBLE);
		funcSprit.setVisibility(ImageView.INVISIBLE);
		volumnImage.setVisibility(ImageView.VISIBLE);
		volumnSeekBar.setVisibility(SeekBar.VISIBLE);
		isMic = false;
		isMedia = true;
		volumnImage.setImageBitmap(volumns[0]);
		volumnSeekBar.setProgress((int) (nowMediaPlayer.CGetVolume() * 100));

	}

	public void BluePress() {
		funcFirstImage.setVisibility(ImageView.INVISIBLE);
		funcSecImage.setVisibility(ImageView.INVISIBLE);
		funcSprit.setVisibility(ImageView.INVISIBLE);
		volumnImage.setVisibility(ImageView.VISIBLE);
		volumnSeekBar.setVisibility(SeekBar.VISIBLE);

		isMic = true;
		isMedia = false;
		volumnImage.setImageBitmap(volumns[1]);
		volumnSeekBar.setProgress(micVolumn);
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
			mposition = mposition + 79;
		}
		listView.scrollTo(0, mposition);

		myAdapter.notifyDataSetChanged();
	}

	// 向前滚动，参数是自定义的适配器
	public void ScrollToPast(MyAdapter myAdapter) {
		select_item = select_item - 1;
		if (select_item < 0) {
			mposition = INITPOSITION + (sentenceSize - 1) * 79;
			select_item = sentenceSize - 1;
		} else {
			mposition = mposition - 79;
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

		public MyAdapter(ArrayList<Sentence> list, Context con) {
			super();
			this.list = list;
			this.con = con;
			smallLeftBm = smallWhiteNums[0];
			smallRightBm = smallWhiteNums[0];
			smallImageHeight = smallWhiteNums[0].getHeight();
			smallImageWidth = smallWhiteNums[0].getWidth();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
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
			holder.lyricTV.setTextSize(20);

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
		// TODO Auto-generated method stub
		super.onPause();
		nowMediaPlayer.CPause();
		if (!isDisTimerCancel) {
			disTimer.cancel();
			isDisTimerCancel = true;
		}
		if (!isEvaluateTimerCancel) {
			evaluateTimer.cancel();
			isEvaluateTimerCancel = true;
		}
		if (!isHandTimerCancel) {
			handTimer.cancel();
			isHandTimerCancel = true;
		}
		if (!isScoreTimerCancel) {
			scoreTimer.cancel();
			isScoreTimerCancel = true;
		}
		if (!isSongTimerCancel) {
			songtimer.cancel();
			isSongTimerCancel = true;
		}
		if (!isStarTimerCancel) {
			starTimer.cancel();
			isStarTimerCancel = true;
		}
		if (!isTimerCancel) {
			timer.cancel();
		}
		listView.setAdapter(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		nowMediaPlayer.release();
		nowSong = null;
		this.finish();
	}

}
