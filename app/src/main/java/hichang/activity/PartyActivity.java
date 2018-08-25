package hichang.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.android.flypigeon.service.MainService;
import com.android.flypigeon.util.Constant;
import com.android.flypigeon.util.Person;

import hichang.Song.CMediaPlayer;
import hichang.Song.LocalBitmap;
import hichang.Song.ReadText;
import hichang.Song.Sentence;
import hichang.Song.Singer;
import hichang.Song.Song;
import hichang.Song.SongList;
import hichang.Song.User;
import hichang.audio.AudRec;
import hichang.ourView.CurveAndLrc;
import hichang.ourView.KeyEditText;
import hichang.ourView.LrcTextView;
import hichang.ourView.VoiceView;
import hichang.ourView.CurveAndLrc.ModeType;
import hichang.test.DswLog;
import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PartyActivity extends Activity {

	private MyBroadcastRecv broadcastRecv = null;
	private IntentFilter bFilter = null;
	private int personId;

	// 音量条
	private SeekBar volumnSeekBar;
	// 是否调节伴奏音乐
	private boolean isComa;
	// 是否调节模式
	private boolean isKTV;

	// 媒体音量
	private float mediaVolume;
	private int mediaVolumn_int;
	// 麦克风音量
	private int micVolumn;
	// 判断timer是否开启
	private boolean isStart;
	// 功能图片
	Bitmap[] accompany;
	Bitmap[] original;
	Bitmap[] ktvMode;
	Bitmap[] professional;
	Bitmap volumnnote;
	// 资源
	private Resources resources;
	// 音樂播放器
	private CMediaPlayer media;
	// 原唱和伴奏的路径
	private String oriPath;
	private String accomPath;
	private ImageView funcRedImage, funcGreenImage, funcYellowImage, funcBlueImage;
	private ImageView funcFirstImage, funcSecImage, funcSprit, volumnIcon;
	private ImageView singerImage;
	private TextView musicInfo;
	private ListView partyLV;
	private ImageView menuFirstIV, menuSecIV, menuThirdIV;
	private Bitmap[] menuNum = new Bitmap[10];
	private ImageView[] keybordImageViews = new ImageView[12];
	private ImageView keybordOK, keybordMenu;
	SongList nowSongList;
	int bookListPage = 1;
	int rankListPage = 1;
	int searchListPage = 1;
	AbsoluteLayout listLayout, keyBoardLayout;
	MyAdapter adapter;
	boolean isMenuShow = false;
	int whichList = 2;
	private Bitmap[] menuBook = new Bitmap[2];
	private Bitmap[] menuBooked = new Bitmap[2];
	private Bitmap[] menuRank = new Bitmap[2];
	Song song, nowSong;
	Singer singer;
	User user;
	VoiceView nowVoiceView;
	LrcTextView nowLrcTV;

	ArrayList<Sentence> nowSongSentences;
	Timer startTimer, disTimer, songTimer, timeTimer, selecTimer;
	int nowSongTime;
	Sentence nowSentence, nextSentence, lastSentence;
	int sentenceFlag;
	ImageView[] colorBalls = new ImageView[3];
	double leftTimes;
	int nowSongLength;
	final int TIMESTEP = 40;
	int standTime;
	int timeHandX;
	int nowSongScore;
	int songId;
	int highScore;
	int singerid;
	ImageView timeHand;
	Handler handler;
	TimerTask timerTask1, timerTask2;
	private int[] keybord_num_b = new int[12];
	private int[] keybord_num_a = new int[12];
	private int[] keybord_num_c = new int[12];
	private int[] keybord_num_d = new int[12];
	private int[] keybord_num_b_yellow = new int[12];
	private int[] keybord_num_a_yellow = new int[12];
	private int[] keybord_num_c_yellow = new int[12];
	private int[] keybord_num_d_yellow = new int[12];
	private int[] keybordID = new int[12];
	boolean isPlay = true; // 是否在放歌
	TextView searchTV;
	boolean isOk = false;

	Animation lightAnim, darkAnim;
	ImageView menuLeftSelectFirst, menuLeftSelectSec, menuRightSelectFirst, menuRightSelectSec;

	private TextView textView;
	private TimerTask task;
	private Timer timer;
	private boolean isTimerCancel;

	CurveAndLrc curveLrc;
	AudRec audRec;
	private int curveW, curveH, curveX, curveY;
	private int lrcH, lrcW, lrcX, lrcY;

	final static int MSG_TURN_COLORBARS = 100, MSG_HIDE_YELLOWBALL = 101, MSG_HIDE_REDBALL = 102,
			MSG_HIDE_BLUEBALL = 103, MSG_SHOW_TIME = 105, MSG_SHOW_SCORE = 104,
			MSG_START_SONG = 200, MSG_KEY_ZERO = 201, MSG_KEY_ONE = 202, MSG_KEY_TWO = 203;
	final static int MSG_KEY_THREE = 204;
	final static int MSG_KEY_FOUR = 205;
	final static int MSG_KEY_FIVE = 206;
	final static int MSG_KEY_SIX = 207;
	final static int MSG_KEY_SEVEN = 208;
	final static int MSG_KEY_EIGHT = 209;
	final static int MSG_KEY_NINE = 210;
	final static int MSG_KEY_ZERO_YELLOW = 211;
	final static int MSG_KEY_ONE_YELLOW = 212;
	final static int MSG_KEY_TWO_YELLOW = 213;
	final static int MSG_KEY_THREE_YELLOW = 214;
	final static int MSG_KEY_FOUR_YELLOW = 215;
	final static int MSG_KEY_FIVE_YELLOW = 216;
	final static int MSG_KEY_SIX_YELLOW = 217;
	final static int MSG_KEY_SEVEN_YELLOW = 218;
	final static int MSG_KEY_EIGHT_YELLOW = 219;
	final static int MSG_KEY_NINE_YELLOW = 220;

	final static int MSG_MENU_1 = 300;
	final static int MSG_MENU_2 = 301;

	final static int MSG_OK_1 = 302;
	final static int MSG_OK_2 = 303;
	final static int MSG_OK_3 = 304;
	final static int MSG_OK_4 = 305;
	final static int MSG_START_SHOW_TIME = 106;
	final static int MSG_MENU_OUT = 306;
	final static int MSG_MENU_IN = 307;

	String songTime;

	ImageView staffImage;
	ImageView helpImage;
	SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
	int[] voiceId = new int[4];
	TranslateAnimation translateAnimation1, translateAnimation2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.party);

		regBroadcastRecv();

		isTimerCancel = false;
		// for(int i = 0;i<12;i++)
		// {
		// keybordImageViews[i] =
		// (ImageView)findViewById(R.id.party_keyboard_num_0 + i);
		// }
		keybordID = new int[] { R.id.party_keyboard_num_0, R.id.party_keyboard_num_1,
				R.id.party_keyboard_num_2, R.id.party_keyboard_num_3, R.id.party_keyboard_num_4,
				R.id.party_keyboard_num_5, R.id.party_keyboard_num_6, R.id.party_keyboard_num_7,
				R.id.party_keyboard_num_8, R.id.party_keyboard_num_9, R.id.party_keyboard_num_jiao,
				R.id.party_keyboard_num_pin };
		keybord_num_a = new int[] { R.drawable.a10, R.drawable.a11, R.drawable.a12, R.drawable.a13,
				R.drawable.a14, R.drawable.a15, R.drawable.a16, R.drawable.a17, R.drawable.a18,
				R.drawable.a19, R.drawable.a1jiaoti, R.drawable.a1pingxian };
		keybord_num_b = new int[] { R.drawable.b10, R.drawable.b11, R.drawable.b12, R.drawable.b13,
				R.drawable.b14, R.drawable.b15, R.drawable.b16, R.drawable.b17, R.drawable.b18,
				R.drawable.b19, R.drawable.b1jiaoti, R.drawable.b1pingxian };
		keybord_num_c = new int[] { R.drawable.c10, R.drawable.c11, R.drawable.c12, R.drawable.c13,
				R.drawable.c14, R.drawable.c15, R.drawable.c16, R.drawable.c17, R.drawable.c18,
				R.drawable.c19, R.drawable.c1jiaoti, R.drawable.c1pingxian };
		keybord_num_d = new int[] { R.drawable.d10, R.drawable.d11, R.drawable.d12, R.drawable.d13,
				R.drawable.d14, R.drawable.d15, R.drawable.d16, R.drawable.d17, R.drawable.d18,
				R.drawable.d19, R.drawable.d1jiaoti, R.drawable.d1pingxian };
		keybord_num_a_yellow = new int[] { R.drawable.a20, R.drawable.a21, R.drawable.a22,
				R.drawable.a23, R.drawable.a24, R.drawable.a25, R.drawable.a26, R.drawable.a27,
				R.drawable.a28, R.drawable.a29, R.drawable.a30, R.drawable.a31 };
		keybord_num_b_yellow = new int[] { R.drawable.b10, R.drawable.b21, R.drawable.b22,
				R.drawable.b23, R.drawable.b24, R.drawable.b15, R.drawable.b16, R.drawable.b17,
				R.drawable.b18, R.drawable.b19, R.drawable.b1jiaoti, R.drawable.b1pingxian };
		keybord_num_c_yellow = new int[] { R.drawable.c20, R.drawable.c21, R.drawable.c22,
				R.drawable.c23, R.drawable.c24, R.drawable.c25, R.drawable.c26, R.drawable.c27,
				R.drawable.c28, R.drawable.c29, R.drawable.c30, R.drawable.c31 };
		keybord_num_d_yellow = new int[] { R.drawable.d20, R.drawable.d21, R.drawable.d22,
				R.drawable.d23, R.drawable.d24, R.drawable.d25, R.drawable.d26, R.drawable.d27,
				R.drawable.d28, R.drawable.d29, R.drawable.d30, R.drawable.d31 };
		for (int i = 0; i < 12; i++) {
			keybordImageViews[i] = (ImageView) findViewById(keybordID[i]);
		}

		keybordMenu = (ImageView) findViewById(R.id.party_keyboard_menu);
		keybordOK = (ImageView) findViewById(R.id.party_keyboard_ok);
		singerImage = (ImageView) findViewById(R.id.party_image);
		musicInfo = (TextView) findViewById(R.id.party_musicinfo);
		textView = (TextView) findViewById(R.id.party_text_zcl);
		volumnSeekBar = (SeekBar) findViewById(R.id.party_player_seekbar);
		funcRedImage = (ImageView) findViewById(R.id.party_function_red);
		funcGreenImage = (ImageView) findViewById(R.id.party_function_green);
		funcBlueImage = (ImageView) findViewById(R.id.party_function_blue);
		funcYellowImage = (ImageView) findViewById(R.id.party_function_yellow);
		funcFirstImage = (ImageView) findViewById(R.id.party_funcfirstimage);
		funcSecImage = (ImageView) findViewById(R.id.party_funcsecimage);
		funcSprit = (ImageView) findViewById(R.id.party_funcsprit);
		volumnIcon = (ImageView) findViewById(R.id.party_volumntype);
		volumnSeekBar.setMax(100);
		partyLV = (ListView) findViewById(R.id.party_list);
		menuFirstIV = (ImageView) findViewById(R.id.party_menu_image1);
		menuSecIV = (ImageView) findViewById(R.id.party_menu_image2);
		menuThirdIV = (ImageView) findViewById(R.id.party_menu_image3);
		listLayout = (AbsoluteLayout) findViewById(R.id.party_list_layout);
		colorBalls[0] = (ImageView) findViewById(R.id.party_countblue);
		colorBalls[1] = (ImageView) findViewById(R.id.party_countred);
		colorBalls[2] = (ImageView) findViewById(R.id.party_countyellow);
		timeHand = (ImageView) findViewById(R.id.party_timeHand);
		searchTV = (TextView) findViewById(R.id.party_search_edit);
		keyBoardLayout = (AbsoluteLayout) findViewById(R.id.party_keyboard_layout);
		menuLeftSelectFirst = (ImageView) findViewById(R.id.party_left_first);
		menuLeftSelectSec = (ImageView) findViewById(R.id.party_left_sec);
		menuRightSelectFirst = (ImageView) findViewById(R.id.party_right_first);
		menuRightSelectSec = (ImageView) findViewById(R.id.party_right_sec);
		curveLrc = (CurveAndLrc) findViewById(R.id.party_curveandlrc);
		staffImage = (ImageView) findViewById(R.id.party_staff);
		helpImage = (ImageView) findViewById(R.id.party_sentence);
		resources = this.getResources();
		// searchTV.setKeyBoard(keyBoardLayout);
		partyLV.setFocusable(false);

		song = new Song(getBaseContext());
		singer = new Singer(getBaseContext());
		user = new User(getBaseContext());

		isComa = false;
		isKTV = false;
		mediaVolume = 0.3f;
		mediaVolumn_int = 30;
		micVolumn = 30;
		accompany = new Bitmap[2];
		original = new Bitmap[2];
		ktvMode = new Bitmap[2];
		professional = new Bitmap[2];

		task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 3;
				handler.sendMessage(message);
			}
		};
		timerTask1 = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(1);
			}
		};
		timerTask2 = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(2);
			}
		};
		// 获取点歌界面传来的SongId
		Intent intent = getIntent();
		songId = intent.getIntExtra("songId", 0);
		nowSong = song.findSongById(songId);
		personId = intent.getIntExtra("personid", -1);

		singerid = singer.querySingerByName(nowSong.getSinger1()).getiD();
		highScore = user.queryFirstScore(nowSong.getSongID());
		String songName = nowSong.getName();
		if (songName.charAt(0) > 0 && songName.charAt(0) < 128 && songName.length() > 12) {
			songName = songName.substring(0, 12) + "...";
		} else if ((songName.charAt(0) < 0 || songName.charAt(0) > 128) && songName.length() > 7) {
			songName = songName.substring(0, 7) + "...";
		}
		String picPath = MainActivity.SD_PATH+"Singer/" + singerid + "/" + singerid + "_p.png";
		singerImage.setImageBitmap(LocalBitmap.getLoacalBitmap(picPath));
		musicInfo.setText(nowSong.getName() + "\n最高分 - " + highScore + "\n");

		handler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					funcFirstImage.setVisibility(ImageView.INVISIBLE);
					funcSecImage.setVisibility(ImageView.INVISIBLE);
					funcSprit.setVisibility(ImageView.INVISIBLE);
					break;
				case 2:
					funcFirstImage.setVisibility(ImageView.INVISIBLE);
					funcSecImage.setVisibility(ImageView.INVISIBLE);
					funcSprit.setVisibility(ImageView.INVISIBLE);
					break;
				case 3:
					volumnIcon.setVisibility(ImageView.INVISIBLE);
					volumnSeekBar.setVisibility(SeekBar.INVISIBLE);
					textView.setVisibility(TextView.INVISIBLE);
					break;
				// 倒计时小球出现
				case MSG_TURN_COLORBARS:
					colorBalls[0].setVisibility(ImageView.VISIBLE);
					colorBalls[1].setVisibility(ImageView.VISIBLE);
					colorBalls[2].setVisibility(ImageView.VISIBLE);
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
					break;
				// 倒数第三个小球隐藏
				case MSG_HIDE_YELLOWBALL:
					colorBalls[2].setVisibility(ImageView.INVISIBLE);
					break;
				// 倒数第二个小球隐藏
				case MSG_HIDE_REDBALL:
					colorBalls[1].setVisibility(ImageView.INVISIBLE);
					break;
				// 倒数第一个小球隐藏
				case MSG_HIDE_BLUEBALL:
					colorBalls[0].setVisibility(ImageView.INVISIBLE);
					break;
				case 13:
					menuLeftSelectFirst.startAnimation(lightAnim);
					menuLeftSelectSec.startAnimation(darkAnim);
					menuRightSelectFirst.startAnimation(lightAnim);
					menuLeftSelectSec.startAnimation(darkAnim);
					break;
				case MSG_SHOW_TIME:
					if (!isStart && nowSongTime > nowSongSentences.get(0).StartTimeofThis - 3000) {
						handler.sendEmptyMessage(MSG_TURN_COLORBARS);
						isStart = true;
					}
					nowSongTime = media.CGetCurrentPosition();
					int time = nowSongTime / 1000;
					int second = time % 60;
					int minute = (time - second) / 60;
					if (second < 10) {
						musicInfo.setText(nowSong.getName() + "\n最高分 - " + highScore + "\n"
								+ minute + ": " + "0" + second + "/" + songTime);
					} else {
						musicInfo.setText(nowSong.getName() + "\n最高分 - " + highScore + "\n"
								+ minute + ": " + second + "/" + songTime);
					}
					break;
				case MSG_START_SHOW_TIME:
					timeTimer = new Timer();
					timeTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_SHOW_TIME);
						}
					}, 0, 200);
					break;
				case MSG_START_SONG:
					media.CStart();
					songTimer = new Timer();
					songTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							nowSongTime = media.CGetCurrentPosition();
							// handler.sendEmptyMessage(MSG_SHOW_TIME);
							if (sentenceFlag < nowSongSentences.size()) {
								int nowSenEnd = nowSongSentences.get(sentenceFlag).StartTimeofThis
										+ nowSongSentences.get(sentenceFlag).LastTimeofThis;
								if (nowSongTime >= nowSenEnd) {
									sentenceFlag++;
								}
							}
							curveLrc.drawCurveAndLrc(nowSongTime - 440,
									audRec.getNote(nowSongTime - 440));
						}
					}, 40, 40);

					break;
				case MSG_KEY_ZERO:
				case MSG_KEY_ZERO + 1:
				case MSG_KEY_ZERO + 2:
				case MSG_KEY_ZERO + 3:
				case MSG_KEY_ZERO + 4:
				case MSG_KEY_ZERO + 5:
				case MSG_KEY_ZERO + 6:
				case MSG_KEY_ZERO + 7:
				case MSG_KEY_ZERO + 8:
				case MSG_KEY_ZERO + 9:
					if (isMenuShow && whichList == 1) {
						keybordImageViews[msg.what - 201]
								.setImageResource(keybord_num_c[msg.what - 201]);
					} else if (isMenuShow && whichList == 2) {
						keybordImageViews[msg.what - 201]
								.setImageResource(keybord_num_d[msg.what - 201]);
					} else if (isMenuShow && whichList == 3) {
						if (isOk) {
							keybordImageViews[msg.what - 201]
									.setImageResource(keybord_num_d[msg.what - 201]);
						} else {
							keybordImageViews[msg.what - 201]
									.setImageResource(keybord_num_a[msg.what - 201]);
						}
					} else if (!isMenuShow) {
						keybordImageViews[msg.what - 201]
								.setImageResource(keybord_num_b[msg.what - 201]);
					}
					break;
				case MSG_KEY_ZERO_YELLOW:
				case MSG_KEY_ZERO_YELLOW + 1:
				case MSG_KEY_ZERO_YELLOW + 2:
				case MSG_KEY_ZERO_YELLOW + 3:
				case MSG_KEY_ZERO_YELLOW + 4:
				case MSG_KEY_ZERO_YELLOW + 5:
				case MSG_KEY_ZERO_YELLOW + 6:
				case MSG_KEY_ZERO_YELLOW + 7:
				case MSG_KEY_ZERO_YELLOW + 8:
				case MSG_KEY_ZERO_YELLOW + 9:
					if (isMenuShow && whichList == 1) {
						keybordImageViews[msg.what - 211]
								.setImageResource(keybord_num_c_yellow[msg.what - 211]);
					} else if (isMenuShow && whichList == 2) {
						keybordImageViews[msg.what - 211]
								.setImageResource(keybord_num_d_yellow[msg.what - 211]);
					} else if (isMenuShow && whichList == 3) {
						if (isOk) {
							keybordImageViews[msg.what - 211]
									.setImageResource(keybord_num_d_yellow[msg.what - 211]);
						} else {
							keybordImageViews[msg.what - 211]
									.setImageResource(keybord_num_a_yellow[msg.what - 211]);
						}
					} else if (!isMenuShow) {
						keybordImageViews[msg.what - 211]
								.setImageResource(keybord_num_b_yellow[msg.what - 211]);
					}
					final int msgwhat = msg.what;
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(msgwhat - 10);
						}
					}, 300);
					break;
				case MSG_MENU_1:
					keybordMenu.setImageResource(R.drawable.menu_diange);
					break;
				case MSG_MENU_2:
					keybordMenu.setImageResource(R.drawable.menu_shouhui);
					break;
				case MSG_OK_1:
					keybordOK.setImageResource(R.drawable.ok1);
					break;
				case MSG_OK_2:
					keybordOK.setImageResource(R.drawable.okqiege);
					break;
				case MSG_OK_3:
					keybordOK.setImageResource(R.drawable.ok2);
					break;
				case MSG_OK_4:
					for (int i = 0; i < 12; i++) {
						keybordImageViews[i].setImageResource(keybord_num_b[i]);
					}
					break;
				case MSG_MENU_OUT:
					onKeyDown(82, null);
					onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
					break;
				case MSG_MENU_IN:
					onKeyDown(82, null);
					break;
				}

			}
		};

		timer = new Timer();
		songTimer = new Timer();
		disTimer = new Timer();
		timeTimer = new Timer();
		media = new CMediaPlayer();
		initPic();
		initMenu();
		initVoice();
		startMenuSelectAnim();
		initMediaPlayer();
		for (int i = 0; i < 12; i++) {
			keybordImageViews[i].setImageResource(keybord_num_b[i]);
		}
		play();

		sendNextActivityMsg(Constant.PARTY);
		sendStartSongMsg();
	}

	public void initVoice() {
		voiceId[0] = soundPool.load(this, R.raw.cheer, 1);
		voiceId[1] = soundPool.load(this, R.raw.whistle, 1);
		voiceId[2] = soundPool.load(this, R.raw.boo, 1);
		voiceId[3] = soundPool.load(this, R.raw.crow, 1);
	}

	public void onTextChanged() {
		adapter.setSongs((ArrayList<Song>) song.findTenSongBySimpleName(searchTV.getText()
				.toString(), searchListPage));
		adapter.notifyDataSetChanged();
	}

	public void setKTV(boolean isKtv, boolean isMenu) {
		isKTV = isKtv;
		if (isKTV) {
			if (isMenu) {
				lrcH = (int) resources.getDimension(R.dimen.party_menu_ktv_lrc_height);
				lrcW = (int) resources.getDimension(R.dimen.party_menu_ktv_lrc_width);
				lrcX = (int) resources.getDimension(R.dimen.party_menu_ktv_lrc_x);
				lrcY = (int) resources.getDimension(R.dimen.party_menu_ktv_lrc_y);
				curveW = (int) resources.getDimension(R.dimen.party_menu_ktv_curve_width);
				curveH = (int) resources.getDimension(R.dimen.party_menu_ktv_curve_height);
				curveX = (int) resources.getDimension(R.dimen.party_menu_ktv_curve_x);
				curveY = (int) resources.getDimension(R.dimen.party_menu_ktv_curve_y);
			} else {
				lrcH = (int) resources.getDimension(R.dimen.party_ktv_lrc_height);
				lrcW = (int) resources.getDimension(R.dimen.party_ktv_lrc_width);
				lrcX = (int) resources.getDimension(R.dimen.party_ktv_lrc_x);
				lrcY = (int) resources.getDimension(R.dimen.party_ktv_lrc_y);
				curveW = (int) resources.getDimension(R.dimen.party_ktv_curve_width);
				curveH = (int) resources.getDimension(R.dimen.party_ktv_curve_height);
				curveX = (int) resources.getDimension(R.dimen.party_ktv_curve_x);
				curveY = (int) resources.getDimension(R.dimen.party_ktv_curve_y);
			}
			staffImage.setX(curveX + curveLrc.getX());
			staffImage.setY(curveY + curveLrc.getY());
			staffImage.setVisibility(View.VISIBLE);

		} else {
			if (isMenu) {
				lrcH = (int) resources.getDimension(R.dimen.party_menu_vocational_lrc_height);
				lrcW = (int) resources.getDimension(R.dimen.party_menu_vocational_lrc_width);
				lrcX = (int) resources.getDimension(R.dimen.party_menu_vocational_lrc_x);
				lrcY = (int) resources.getDimension(R.dimen.party_menu_vocational_lrc_y);
				curveW = (int) resources.getDimension(R.dimen.party_menu_vocational_curve_width);
				curveH = (int) resources.getDimension(R.dimen.party_menu_vocational_curve_height);
				curveX = (int) resources.getDimension(R.dimen.party_menu_vocational_curve_x);
				curveY = (int) resources.getDimension(R.dimen.party_menu_vocational_curve_y);
			} else {
				lrcH = (int) resources.getDimension(R.dimen.party_vocational_lrc_height);
				lrcW = (int) resources.getDimension(R.dimen.party_vocational_lrc_width);
				lrcX = (int) resources.getDimension(R.dimen.party_vocational_lrc_x);
				lrcY = (int) resources.getDimension(R.dimen.party_vocational_lrc_y);
				curveW = (int) resources.getDimension(R.dimen.party_vocational_curve_width);
				curveH = (int) resources.getDimension(R.dimen.party_vocational_curve_height);
				curveX = (int) resources.getDimension(R.dimen.party_vocational_curve_x);
				curveY = (int) resources.getDimension(R.dimen.party_vocational_curve_y);
			}
			staffImage.setVisibility(View.INVISIBLE);
		}
		curveLrc.setCurveXYWH(curveX, curveY, curveW, curveH);
		curveLrc.setLrcXYWH(lrcX, lrcY, lrcW, lrcH);
		curveLrc.setKTV(isKTV);
	}

	public void initSong() {
		ReadText nowText = new ReadText(nowSong.getSongLyricUrl());
		nowSongSentences = nowText.ReadData();

		nowSentence = nowSongSentences.get(0);
		nextSentence = nowSongSentences.get(1);
		lastSentence = nowSongSentences.get(nowSongSentences.size() - 1);
		sentenceFlag = 0;
		nowSongTime = 0;
		curveLrc.init(nowSongSentences, nowText.max, nowText.min, ModeType.MODE_PARTY);
		setKTV(isKTV, isMenuShow);

		audRec = new AudRec(nowSongSentences, handler, media);
		audRec.init();

		nowSongLength = media.CGetDuration();
		int sec = (nowSongLength / 1000) % 60;
		int min = (nowSongLength / 1000 - sec) / 60;
		if (sec < 10) {
			songTime = min + ":" + "0" + sec;
		} else {
			songTime = min + ":" + sec;
		}
		handler.sendEmptyMessage(MSG_START_SHOW_TIME);
		// Toast.makeText(this, nowSong.name+"initSong",
		// Toast.LENGTH_SHORT).show();
	}

	public void play() {
		media.CReset();
		media.CSetDataSource(nowSong.getMusicPath(), nowSong.getAccomanimentPath());
		media.CPrepare();
		initSong();
		audRec.start();
		mediaVolume = media.CGetVolume();
	}

	public void initMediaPlayer() {
		nowSongList = new SongList();
		// 手动添加歌曲进入列表
		// Song firstSong = new Song(getBaseContext());
		// firstSong = firstSong.findSongById(1236);
		// Song secSong = new Song(getBaseContext());
		// secSong = secSong.findSongById(842);
		// nowSongList.bookSong(firstSong);
		// nowSongList.bookSong(secSong);
		media.mediaOriginal.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// audRec.free();
				timeTimer.cancel();
				songTimer.cancel();
				disTimer.cancel();
				audRec.free();
				audRec = null;
				Toast.makeText(getBaseContext(), "next", Toast.LENGTH_SHORT).show();
				nextSong();
			}
		});
	}

	public void startMenuSelectAnim() {
		translateAnimation1 = new TranslateAnimation(-495, 0, 0, 0);
		translateAnimation1.setDuration(500);
		translateAnimation1.setFillAfter(true);
		translateAnimation2 = new TranslateAnimation(0, -495, 0, 0);
		translateAnimation2.setDuration(500);
		translateAnimation2.setFillAfter(true);
		lightAnim = AnimationUtils.loadAnimation(this, R.anim.twinkle_appear);
		lightAnim.setFillAfter(true);
		darkAnim = AnimationUtils.loadAnimation(this, R.anim.twinkle_disappear);
		darkAnim.setFillAfter(true);
		selecTimer = new Timer();
		selecTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(13);
			}
		}, 0, 1000);
	}

	public void initPic() {
		volumnnote = BitmapFactory.decodeResource(resources, R.drawable.musicalnote);
		// 功能模块的图片
		for (int i = 0; i < 2; i++) {
			ktvMode[i] = BitmapFactory.decodeResource(resources, R.drawable.ktv_selected + i);
			professional[i] = BitmapFactory.decodeResource(resources,
					R.drawable.professional_selected + i);
			original[i] = BitmapFactory.decodeResource(resources, R.drawable.original_selected + i);
			accompany[i] = BitmapFactory.decodeResource(resources, R.drawable.accompany_selected
					+ i);

			menuBook[i] = BitmapFactory.decodeResource(resources, R.drawable.party_book_selected
					+ i);
			menuBooked[i] = BitmapFactory.decodeResource(resources,
					R.drawable.party_booked_selected + i);
			menuRank[i] = BitmapFactory.decodeResource(resources, R.drawable.party_rank_selected
					+ i);
		}

		for (int i = 0; i < 10; i++) {
			menuNum[i] = BitmapFactory.decodeResource(resources, R.drawable.party_menu_button0 + i);
		}

	}

	public void initMenu() {
		whichList = 2;
		menuFirstIV.setImageBitmap(menuBooked[1]);
		menuSecIV.setImageBitmap(menuRank[0]);
		menuThirdIV.setImageBitmap(menuBook[1]);
		adapter = new MyAdapter((ArrayList<Song>) song.queryTenSongByTwoClicks(rankListPage), this);
		partyLV.setAdapter(adapter);
	}

	public void RedPress(){
		// StartNumTurnAnim(99);
		timerTask1.cancel();
		timerTask1 = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(1);
			}
		};
		textView.setVisibility(TextView.INVISIBLE);
		volumnIcon.setVisibility(ImageView.INVISIBLE);
		volumnSeekBar.setVisibility(SeekBar.INVISIBLE);
		funcFirstImage.setVisibility(ImageView.VISIBLE);
		funcSecImage.setVisibility(ImageView.VISIBLE);
		funcSprit.setVisibility(ImageView.VISIBLE);
		if (isComa == false) {
			Toast.makeText(this, "伴奏", Toast.LENGTH_SHORT).show();
			isComa = true;
			funcFirstImage.setImageBitmap(accompany[0]);
			funcSecImage.setImageBitmap(original[1]);
			media.CSetAccompany();
		} else {
			Toast.makeText(this, "原唱", Toast.LENGTH_SHORT).show();
			isComa = false;
			funcFirstImage.setImageBitmap(accompany[1]);
			funcSecImage.setImageBitmap(original[0]);
			media.CSetOriginal();
		}
		timer.schedule(timerTask1, 3000);
	}
	
	public void GreenPress() {
		timerTask2.cancel();
		timerTask2 = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(2);
			}
		};
		textView.setVisibility(TextView.INVISIBLE);
		volumnIcon.setVisibility(ImageView.INVISIBLE);
		volumnSeekBar.setVisibility(SeekBar.INVISIBLE);
		funcFirstImage.setVisibility(ImageView.VISIBLE);
		funcSecImage.setVisibility(ImageView.VISIBLE);
		funcSprit.setVisibility(ImageView.VISIBLE);
		if (isKTV == true) {
			Toast.makeText(this, "专业模式", Toast.LENGTH_SHORT).show();
			isKTV = false;
			funcFirstImage.setImageBitmap(ktvMode[1]);
			funcSecImage.setImageBitmap(professional[0]);
		} else {
			Toast.makeText(this, "ktv模式", Toast.LENGTH_SHORT).show();
			isKTV = true;
			funcFirstImage.setImageBitmap(ktvMode[0]);
			funcSecImage.setImageBitmap(professional[1]);
		}
		setKTV(isKTV, isMenuShow);
		timer.schedule(timerTask2, 3000);
	}
	
	public void YellowPress() {
		task.cancel();
		task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 3;
				handler.sendMessage(message);
			}
		};
		Toast.makeText(this, "减小背景音的音量", Toast.LENGTH_SHORT).show();
		funcFirstImage.setVisibility(ImageView.INVISIBLE);
		funcSecImage.setVisibility(ImageView.INVISIBLE);
		funcSprit.setVisibility(ImageView.INVISIBLE);
		volumnIcon.setImageBitmap(volumnnote);
		mediaVolume = mediaVolume - 0.1f;
		if (mediaVolume < 0) {
			mediaVolume = 0;
		}
		media.CSetVolume(mediaVolume);
		mediaVolumn_int = (int) (mediaVolume * 100);
		volumnSeekBar.setProgress(mediaVolumn_int);
		textView.setText("" + mediaVolumn_int / 10);
		volumnIcon.setVisibility(ImageView.VISIBLE);
		volumnSeekBar.setVisibility(SeekBar.VISIBLE);
		textView.setVisibility(TextView.VISIBLE);
		timer.schedule(task, 5000);
	}
	
	public void BluePress() {
		task.cancel();
		task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 3;
				handler.sendMessage(message);
			}
		};
		Toast.makeText(this, "减小背景音的音量", Toast.LENGTH_SHORT).show();
		funcFirstImage.setVisibility(ImageView.INVISIBLE);
		funcSecImage.setVisibility(ImageView.INVISIBLE);
		funcSprit.setVisibility(ImageView.INVISIBLE);
		volumnIcon.setImageBitmap(volumnnote);
		mediaVolume = mediaVolume + 0.1f;
		if (mediaVolume > 1) {
			mediaVolume = 1;
		}
		media.CSetVolume(mediaVolume);
		mediaVolumn_int = (int) (mediaVolume * 100);
		volumnSeekBar.setProgress(mediaVolumn_int);
		textView.setText("" + mediaVolumn_int / 10);
		volumnIcon.setVisibility(ImageView.VISIBLE);
		volumnSeekBar.setVisibility(SeekBar.VISIBLE);
		textView.setVisibility(TextView.VISIBLE);
		timer.schedule(task, 5000);
	}
	
	public void nextSong() {
		helpImage.setVisibility(View.INVISIBLE);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		curveLrc.clearTotal();
		isStart = false;
		nowSong = nowSongList.nextSong();

		if (nowSong == null) {
			Toast.makeText(this, "没歌了啊", Toast.LENGTH_SHORT);
			isPlay = false;
		} else {
			isPlay = true;
			Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
			singerid = singer.querySingerByName(nowSong.getSinger1()).getiD();
			highScore = user.queryFirstScore(nowSong.getSongID());
			String songName = nowSong.getName();
			if (songName.charAt(0) > 0 && songName.charAt(0) < 128 && songName.length() > 12)
				songName = songName.substring(0, 12) + "...";
			else if ((songName.charAt(0) < 0 || songName.charAt(0) > 128) && songName.length() > 7)
				songName = songName.substring(0, 7) + "...";
			String picPath = MainActivity.SD_PATH+"Singer/" + singerid + "/" + singerid + "_p.png";
			singerImage.setImageBitmap(LocalBitmap.getLoacalBitmap(picPath));
			play();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 7:
			OnNumKeyDown(0);
			break;
		case 8:
			OnNumKeyDown(1);
			break;
		case 9:
			OnNumKeyDown(2);
			break;
		case 10:
			OnNumKeyDown(3);
			break;
		case 11:
			OnNumKeyDown(4);
			break;
		case 12:
			OnNumKeyDown(5);
			break;
		case 13:
			OnNumKeyDown(6);
			break;
		case 14:
			OnNumKeyDown(7);
			break;
		case 15:
			OnNumKeyDown(8);
			break;
		case 16:
			OnNumKeyDown(9);
			break;
		default:
			break;
		}
		if (keyCode == 66) {
			if (isPlay) {
				if (isMenuShow && whichList == 3) {
					isOk = !isOk;
					if (isOk) {
						keybordOK.setImageResource(R.drawable.pressok2);
						for (int i = 0; i < 12; i++) {
							keybordImageViews[i].setImageResource(keybord_num_d[i]);
						}
						timer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_OK_1);
							}
						}, 300);
					} else {
						keybordOK.setImageResource(R.drawable.pressok);
						for (int i = 0; i < 12; i++) {
							keybordImageViews[i].setImageResource(keybord_num_a[i]);
						}
						timer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_OK_3);
							}
						}, 300);
					}
				} else {
					keybordOK.setImageResource(R.drawable.pressokqiege);
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_OK_2);
						}
					}, 300);
					timeTimer.cancel();
					disTimer.cancel();
					songTimer.cancel();
					audRec.free();
					audRec = null;

					if (nowSongList.size() == 0) {
						isPlay = false;
						media.CStop();
						curveLrc.clearTotal();
						helpImage.setVisibility(View.VISIBLE);
						Toast.makeText(this, "没歌了，快去点歌吧", Toast.LENGTH_SHORT);
					} else {
						nextSong();
					}
				}
			}
		}
		if (keyCode == 219) {
			if (!isOk) {
				String text = "";
				int length = searchTV.getText().length();
				if (length > 0) {
					text = searchTV.getText().toString().substring(0, length - 1);
				}
				searchTV.setText(text);
				onTextChanged();
			}
		}
		if (keyCode == 82) {
			if (isMenuShow) {
				keybordOK.setImageResource(R.drawable.ok1);
				keybordMenu.setImageResource(R.drawable.menu_shouhui2);
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_MENU_1);
					}
				}, 300);
				for (int i = 0; i < 12; i++) {
					keybordImageViews[i].setImageResource(keybord_num_b[i]);
				}
				listLayout.startAnimation(translateAnimation1);
				searchTV.startAnimation(translateAnimation1);
				keyBoardLayout.startAnimation(translateAnimation1);
				isMenuShow = false;
			} else {

				keybordMenu.setImageResource(R.drawable.menu_diange2);
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_MENU_2);
					}
				}, 300);
				for (int i = 0; i < 12; i++) {
					keybordImageViews[i].setImageResource(keybord_num_d[i]);
				}
				initMenu();

				listLayout.setVisibility(AbsoluteLayout.VISIBLE);
				listLayout.startAnimation(translateAnimation2);
				searchTV.startAnimation(translateAnimation2);
				keyBoardLayout.startAnimation(translateAnimation2);
				isMenuShow = true;
			}
			setKTV(isKTV, isMenuShow);
		}
		// 按键为左键
		else if (keyCode == 21) {
			if (isMenuShow) {
				searchTV.setText("");
				isOk = false;
				switch (whichList) {
				case 1:
					for (int i = 0; i < 12; i++) {
						keybordImageViews[i].setImageResource(keybord_num_a[i]);
					}
					bookListPage = 1;
					whichList = 3;
					keybordOK.setImageResource(R.drawable.ok2);
					searchTV.setVisibility(View.INVISIBLE);
					menuFirstIV.setImageBitmap(menuRank[1]);
					menuSecIV.setImageBitmap(menuBook[0]);
					menuThirdIV.setImageBitmap(menuBooked[1]);
					adapter.setSongs((ArrayList<Song>) song.findTenSongBySimpleName("",
							searchListPage));
					break;
				case 2:
					for (int i = 0; i < 12; i++) {
						keybordImageViews[i].setImageResource(keybord_num_c[i]);
					}
					rankListPage = 1;
					whichList = 1;
					keybordOK.setImageResource(R.drawable.okqiege);
					searchTV.setVisibility(View.INVISIBLE);
					menuFirstIV.setImageBitmap(menuBook[1]);
					menuSecIV.setImageBitmap(menuBooked[0]);
					menuThirdIV.setImageBitmap(menuRank[1]);
					adapter.setSongs(nowSongList.getSongsByPage(bookListPage));
					break;
				case 3:
					for (int i = 0; i < 12; i++) {
						keybordImageViews[i].setImageResource(keybord_num_d[i]);
					}
					searchListPage = 1;
					whichList = 2;
					keybordOK.setImageResource(R.drawable.okqiege);
					searchTV.setVisibility(View.VISIBLE);
					menuFirstIV.setImageBitmap(menuBooked[1]);
					menuSecIV.setImageBitmap(menuRank[0]);
					menuThirdIV.setImageBitmap(menuBook[1]);
					adapter.setSongs((ArrayList<Song>) song.queryTenSongByTwoClicks(rankListPage));
					break;
				default:
					break;
				}
				adapter.notifyDataSetChanged();
			}
		}

		// 按键为右键
		else if (keyCode == 22) {
			if (isMenuShow) {
				searchTV.setText("");
				isOk = false;
				switch (whichList) {
				case 1:
					for (int i = 0; i < 12; i++) {
						keybordImageViews[i].setImageResource(keybord_num_d[i]);
					}
					bookListPage = 1;
					whichList = 2;
					isOk = true;
					keybordOK.setImageResource(R.drawable.okqiege);
					searchTV.setVisibility(View.VISIBLE);
					menuFirstIV.setImageBitmap(menuBooked[1]);
					menuSecIV.setImageBitmap(menuRank[0]);
					menuThirdIV.setImageBitmap(menuBook[1]);
					adapter.setSongs((ArrayList<Song>) song.queryTenSongByTwoClicks(rankListPage));
					break;
				case 2:
					for (int i = 0; i < 12; i++) {
						keybordImageViews[i].setImageResource(keybord_num_a[i]);
					}
					rankListPage = 1;
					whichList = 3;
					keybordOK.setImageResource(R.drawable.ok2);
					isOk = false;
					searchTV.setVisibility(View.INVISIBLE);
					menuFirstIV.setImageBitmap(menuRank[1]);
					menuSecIV.setImageBitmap(menuBook[0]);
					menuThirdIV.setImageBitmap(menuBooked[1]);
					adapter.setSongs((ArrayList<Song>) song.findTenSongBySimpleName("",
							searchListPage));
					break;
				case 3:
					for (int i = 0; i < 12; i++) {
						keybordImageViews[i].setImageResource(keybord_num_c[i]);
					}
					searchListPage = 1;
					whichList = 1;
					isOk = false;
					keybordOK.setImageResource(R.drawable.okqiege);
					searchTV.setVisibility(View.INVISIBLE);
					menuFirstIV.setImageBitmap(menuBook[1]);
					menuSecIV.setImageBitmap(menuBooked[0]);
					menuThirdIV.setImageBitmap(menuRank[1]);
					adapter.setSongs(nowSongList.getSongsByPage(bookListPage));
					break;
				default:
					break;
				}
				adapter.notifyDataSetChanged();
			}
		} else if (keyCode == 19) {
			if (isMenuShow) {
				switch (whichList) {
				case 1:
					bookListPage--;
					if (bookListPage <= 0) {
						bookListPage = nowSongList.getPageNum();
						adapter.setSongs(nowSongList.getSongsByPage(bookListPage));
					} else {
						adapter.setSongs(nowSongList.getSongsByPage(bookListPage));
					}
					break;
				case 2:
					rankListPage--;
					if (rankListPage <= 0) {
						rankListPage = song.getSongPage();
						adapter.setSongs((ArrayList<Song>) song
								.queryTenSongByTwoClicks(rankListPage));
					} else {
						adapter.setSongs((ArrayList<Song>) song
								.queryTenSongByTwoClicks(rankListPage));
					}
					break;
				case 3:
					searchListPage--;
					if (searchListPage <= 0) {
						searchListPage = song.getSongPageBySimpleName("");
						adapter.setSongs((ArrayList<Song>) song.findTenSongBySimpleName(searchTV
								.getText().toString(), searchListPage));
					} else {
						adapter.setSongs((ArrayList<Song>) song.findTenSongBySimpleName(searchTV
								.getText().toString(), searchListPage));
					}
					break;
				default:
					break;
				}
				adapter.notifyDataSetChanged();
			}
		} else if (keyCode == 20) {
			if (isMenuShow) {
				switch (whichList) {
				case 1:
					bookListPage++;
					ArrayList<Song> theBookedSongs = nowSongList.getSongsByPage(bookListPage);
					if (theBookedSongs == null) {
						bookListPage = 1;
						adapter.setSongs(nowSongList.getSongsByPage(bookListPage));
					} else {
						adapter.songs = theBookedSongs;
					}
					break;
				case 2:
					rankListPage++;
					ArrayList<Song> theRankSongs = (ArrayList<Song>) song
							.queryTenSongByTwoClicks(rankListPage);
					if (theRankSongs == null) {
						rankListPage = 1;
						adapter.setSongs((ArrayList<Song>) song
								.queryTenSongByTwoClicks(rankListPage));
					} else {
						adapter.songs = theRankSongs;
					}
					break;
				case 3:
					searchListPage++;
					ArrayList<Song> theBookSongs = (ArrayList<Song>) song.findTenSongBySimpleName(
							searchTV.getText().toString(), searchListPage);
					if (theBookSongs == null) {
						searchListPage = 1;
						adapter.setSongs((ArrayList<Song>) song.findTenSongBySimpleName(searchTV
								.getText().toString(), searchListPage));
					} else {
						adapter.songs = theBookSongs;
					}
					break;
				default:
					break;
				}
				adapter.notifyDataSetChanged();
			}
		}

		// 按键为返回键
		if (keyCode == 4) {
			Toast.makeText(this, "返回", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.putExtra("type", 2);
			intent.setClass(PartyActivity.this, RemoteMusicActivity.class);
			startActivity(intent);
			this.finish();
		}
		// 按键为红色键
		if (keyCode == 183) {
			RedPress();
		}
		// 按键为绿色键
		if (keyCode == 184) {
			GreenPress();
		}
		// 按键为黄色键
		if (keyCode == 185) {
			YellowPress();
		}
		// 按键为蓝色键
		if (keyCode == 186) {
			BluePress();
		}
		return true;
	}
	
	public void OnFuncMenuClicked(View view) {
		switch (view.getId()) {
		case R.id.party_function_red:
			RedPress();
			break;
		case R.id.party_function_green:
			GreenPress();
			break;
		case R.id.party_function_yellow:
			YellowPress();
			break;
		case R.id.party_function_blue:
			BluePress();
			break;
		}
	}
	
	public void OnBtnCLicked(View view) {
		switch (view.getId()) {
		case R.id.party_keyboard_num_1:
			OnNumKeyDown(1);
			break;
		case R.id.party_keyboard_num_2:
			OnNumKeyDown(2);
			break;
		case R.id.party_keyboard_num_3:
			OnNumKeyDown(3);
			break;
		case R.id.party_keyboard_num_4:
			OnNumKeyDown(4);
			break;
		case R.id.party_keyboard_num_5:
			OnNumKeyDown(5);
			break;
		case R.id.party_keyboard_num_6:
			OnNumKeyDown(6);
			break;
		case R.id.party_keyboard_num_7:
			OnNumKeyDown(7);
			break;
		case R.id.party_keyboard_num_8:
			OnNumKeyDown(8);
			break;
		case R.id.party_keyboard_num_9:
			OnNumKeyDown(9);
			break;
		case R.id.party_keyboard_num_pin:

			break;
		case R.id.party_keyboard_num_0:
			OnNumKeyDown(0);
			break;
		case R.id.party_keyboard_num_jiao:

			break;
		case R.id.party_keyboard_menu:

			break;
		case R.id.party_keyboard_ok:

			break;
		}
	}
	
	public void OnNumKeyDown(int key) {
		handler.sendEmptyMessage(MSG_KEY_ZERO_YELLOW + key);
		if (isMenuShow) {
			if (whichList == 1) {
				if (adapter.songs.size() > key) {
					nowSongList.SetToFirst(bookListPage, key);
					adapter.setSongs(nowSongList.getSongsByPage(bookListPage));
				}
			} else if (whichList == 2) {
				if (adapter.songs.size() > key) {
					if (adapter.songs.get(key).getIsAvailable() == 1) {
						nowSongList.bookSong(adapter.songs.get(key));
					} else {
						Toast.makeText(this, "抱歉，该歌曲暂时不可用，请重新选歌", Toast.LENGTH_SHORT).show();
					}
				}
				if (!isPlay) {
					nextSong();
				}
			} else {
				if (isOk) {
					if (adapter.songs.size() > key) {
						if (adapter.songs.get(key).getIsAvailable() == 1) {
							nowSongList.bookSong(adapter.songs.get(key));
						} else {
							Toast.makeText(this, "抱歉，该歌曲暂时不可用，请重新选歌", Toast.LENGTH_SHORT).show();
						}
					}
					if (!isPlay) {
						nextSong();
					}
				} else {
					String strSearch = searchTV.getText().toString();
					searchTV.setText("" + strSearch + key);
					onTextChanged();
				}
			}
			adapter.notifyDataSetChanged();
		} else {
			if (key > 0 && key < 5) {
				soundPool.play(voiceId[key - 1], 1, 1, 1, 0, 1);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if (isPlay) {
			media.CPause();
		}
		task.cancel();
		timerTask1.cancel();
		timerTask2.cancel();
		songTimer.cancel();
		disTimer.cancel();
		timer.cancel();
		timeTimer.cancel();
		selecTimer.cancel();
		// startTimer.cancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (audRec != null) {
			DswLog.v("audrec", "off");
			audRec.free();
			audRec = null;
		}
		media.CRelease();

		Intent in = new Intent(PartyActivity.this, MainService.class);
		in.setAction(Constant.stopSongAction);
		in.putExtra("personid", personId);
		startService(in);

		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DswLog.v("party", "stop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DswLog.v("party", "destroy");
	}

	class MyAdapter extends BaseAdapter {
		public ArrayList<Song> songs;
		private Context context;

		public MyAdapter(ArrayList<Song> songs, Context context) {
			this.songs = songs;
			this.context = context;
			if (this.songs == null) {
				this.songs = new ArrayList<Song>();
			}
		}

		public int getCount() {
			return 10;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public void setSongs(ArrayList<Song> songs) {
			this.songs = songs;
			if (this.songs == null) {
				this.songs = new ArrayList<Song>();
			}
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View item = LayoutInflater.from(context).inflate(R.layout.party_list_item, null);
			ImageView numImage = (ImageView) item.findViewById(R.id.party_menu_num);
			TextView songTV = (TextView) item.findViewById(R.id.party_menu_song);
			TextView singerTV = (TextView) item.findViewById(R.id.party_menu_singer);

			numImage.setImageBitmap(menuNum[position]);
			if (songs.size() <= position) {
				songTV.setText("");
				singerTV.setText("");
			} else {
				if (songs.get(position).getIsAvailable() == 1) {
					songTV.setTextColor(Color.WHITE);
					singerTV.setTextColor(Color.WHITE);
				}
				songs.get(position).bookNum = nowSongList.getBookedNum(songs.get(position)
						.getSongID());
				if (songs.get(position).bookNum != -1) {
					songTV.setText(songs.get(position).getName() + "   预约"
							+ songs.get(position).bookNum);
				} else {
					songTV.setText(songs.get(position).getName());
				}
				singerTV.setText(songs.get(position).getSinger1());
			}
			return item;
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
			} else if (intent.getAction().equals(Constant.receiveRemotedSongAction)) {
				int songId = intent.getIntExtra("songid", -1);
				final Person psn = (Person) intent.getExtras().get("person");
				if (songId == -1)
					return;
				if (song.findSongById(songId).getIsAvailable() == 1) {
					personId = psn.personId;
					nowSongList.bookSong(song.findSongById(songId));
					if (isMenuShow) {
						onKeyDown(82, null);
						timer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_MENU_OUT);
							}
						}, 500);
						timer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_MENU_IN);
							}
						}, 3000);
					} else {
						onKeyDown(82, null);
						onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
						timer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_MENU_IN);
							}
						}, 3000);
					}
				}
			} else if (intent.getAction().equals(Constant.requestOrderedSongAction)) {
				Intent mMainServiceIntent = new Intent(getBaseContext(), MainService.class);
				final Person psn = (Person) intent.getExtras().get("person");
				ArrayList<Integer> songList = new ArrayList<Integer>();

				Iterator<Song> it1 = nowSongList.getSongList().iterator();
				String tempString = "";
				while (it1.hasNext()) {
					Song tempSong = it1.next();
					songList.add(new Integer(tempSong.getSongID()));
					tempString += tempSong.getName() + " " + tempSong.getSongID();
				}
				DswLog.d("songList", tempString);

				mMainServiceIntent.putIntegerArrayListExtra("songlist", songList);
				mMainServiceIntent.putExtra("person", psn);
				mMainServiceIntent.setAction(Constant.orderedSongListAction);

				startService(mMainServiceIntent);
			} else if (intent.getAction().equals(Constant.receiveKeyPressedAction)) {
				Integer keyCode = (Integer) intent.getExtras().get("keycode");
				final Person psn = (Person) intent.getExtras().get("person");
				int code = keyCode.intValue();
				int pressedKey = -1;
				switch (code) {
				case Constant.KEY0:
					pressedKey = KeyEvent.KEYCODE_0;
					break;
				case Constant.KEY1:
					pressedKey = KeyEvent.KEYCODE_1;
					break;
				case Constant.KEY2:
					pressedKey = KeyEvent.KEYCODE_2;
					break;
				case Constant.KEY3:
					pressedKey = KeyEvent.KEYCODE_3;
					break;
				case Constant.KEY4:
					pressedKey = KeyEvent.KEYCODE_4;
					break;
				case Constant.KEY5:
					pressedKey = KeyEvent.KEYCODE_5;
					break;
				case Constant.KEY6:
					pressedKey = KeyEvent.KEYCODE_6;
					break;
				case Constant.KEY7:
					pressedKey = KeyEvent.KEYCODE_7;
					break;
				case Constant.KEY8:
					pressedKey = KeyEvent.KEYCODE_8;
					break;
				case Constant.KEY9:
					pressedKey = KeyEvent.KEYCODE_9;
					break;
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
				case Constant.KEYOK:
					if (psn.personId == personId) {
						pressedKey = KeyEvent.KEYCODE_ENTER;
					}
					break;
				case Constant.KEYTURN:
					pressedKey = 219;
					break;
				case Constant.KEYSCREENDISPLAY:
					pressedKey = KeyEvent.KEYCODE_DPAD_LEFT;
					break;
				case Constant.KEYBACK:
					if (psn.personId == personId) {
						pressedKey = KeyEvent.KEYCODE_BACK;
					}
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

				Intent in = new Intent(PartyActivity.this, MainService.class);
				in.putExtra("mode", Constant.PARTY);
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
		bFilter.addAction(Constant.receiveRemotedSongAction);
		bFilter.addAction(Constant.requestOrderedSongAction);
		registerReceiver(broadcastRecv, bFilter);
	}

	private void sendNextActivityMsg(int activityId) {
		Intent in = new Intent(PartyActivity.this, MainService.class);
		in.putExtra("activityid", activityId);
		in.putExtra("personid", personId);
		in.setAction(Constant.nextActivityAction);
		startService(in);
	}

	private void sendStartSongMsg() {
		Intent in = new Intent(PartyActivity.this, MainService.class);
		in.setAction(Constant.startSongAction);
		in.putExtra("personid", personId);
		startService(in);
	}
}
