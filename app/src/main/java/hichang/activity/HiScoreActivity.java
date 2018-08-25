package hichang.activity;

import hichang.Song.User;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class HiScoreActivity extends Activity {

	private int nowScore, songId;
	private int userId;
	/**
	 * 数据库User表访问接口
	 */
	private User user;
	private User nowUser;
	/**
	 * 第一、二、三名的姓名的textview
	 */
	private TextView firstTV, secTV, thirdTV;
	/**
	 * 第一、二、三名编辑姓名的编辑框
	 */
	private EditText firstEV, secEV, thirdEV;
	/**
	 * 排行榜的成绩数字图片
	 */
	private ImageView firstLeftNum, firstRightNum, secLeftNum, secRightNum, thirdLeftNum,
			thirdRightNum;
	/**
	 * 排行榜，鼓励句子，鼓励图片
	 */
	private ImageView rankList, approveSentence, approveImage;
	/**
	 * 排行榜的数字标号
	 */
	private ImageView rankBigFirst, rankSmallFirst, rankBigSec, rankSmallSec, rankBigThird,
			rankSmallThird;
	/**
	 * 打分的十位和个位
	 */
	private ImageView scoreLeft, scoreRight;
	// 界面会用到的图片
	Bitmap[] rankinglist = new Bitmap[2];
	Bitmap[] approveSentences = new Bitmap[2];
	Bitmap[] approveImages = new Bitmap[6];
	Bitmap[] rankSmallNums = new Bitmap[3];
	Bitmap[] rankBigNums = new Bitmap[3];
	Bitmap[] scoreNums = new Bitmap[10];

	private Resources resources;
	/**
	 * 分数的timer
	 */
	Timer scoreTimer;
	/**
	 * scoreTimer是否被取消了
	 */
	private boolean isScoreTimerCancel;

	Bitmap nowLeftNum, nowRightNum;

	int nowLeft, nowRight;
	float score;
	int breakNum = 0;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score);

		resources = this.getResources();

		firstEV = (EditText) findViewById(R.id.score_firstedit);
		secEV = (EditText) findViewById(R.id.score_secondedit);
		thirdEV = (EditText) findViewById(R.id.score_thirdedit);

		firstTV = (TextView) findViewById(R.id.score_first_name);
		secTV = (TextView) findViewById(R.id.score_second_name);
		thirdTV = (TextView) findViewById(R.id.score_third_name);

		rankBigFirst = (ImageView) findViewById(R.id.score_first);
		rankSmallFirst = (ImageView) findViewById(R.id.score_small_first);
		rankBigSec = (ImageView) findViewById(R.id.score_second);
		rankSmallSec = (ImageView) findViewById(R.id.score_small_second);
		rankBigThird = (ImageView) findViewById(R.id.score_third);
		rankSmallThird = (ImageView) findViewById(R.id.score_small_third);

		rankList = (ImageView) findViewById(R.id.score_rank);
		approveSentence = (ImageView) findViewById(R.id.score_approve_sentence);
		approveImage = (ImageView) findViewById(R.id.score_approve_image);

		firstLeftNum = (ImageView) findViewById(R.id.score_rank_first_left);
		firstRightNum = (ImageView) findViewById(R.id.score_rank_first_right);
		secLeftNum = (ImageView) findViewById(R.id.score_rank_sec_left);
		secRightNum = (ImageView) findViewById(R.id.score_rank_sec_right);
		thirdLeftNum = (ImageView) findViewById(R.id.score_rank_third_left);
		thirdRightNum = (ImageView) findViewById(R.id.score_rank_third_right);

		scoreLeft = (ImageView) findViewById(R.id.score_left_num);
		scoreRight = (ImageView) findViewById(R.id.score_right_num);

		firstEV.setOnEditorActionListener(new MyEditorActionListener());
		secEV.setOnEditorActionListener(new MyEditorActionListener());
		thirdEV.setOnEditorActionListener(new MyEditorActionListener());

		initPicture();

		Intent intent = getIntent();
		songId = intent.getIntExtra("songId", 1);
		nowScore = intent.getIntExtra("score", 0);
		user = new User(getBaseContext());
		nowUser = user.queryUserBySongId(songId);
		userId = nowUser.getID();

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {
					scoreLeft.setImageBitmap(scoreNums[nowLeft]);
					scoreRight.setImageBitmap(scoreNums[nowRight]);
				} else if (msg.what == 2) {
					breakNum = breakRankNum();
				}
			};
		};

		showRank();
		showScore();
	}

	public void initPicture() {
		rankinglist[0] = BitmapFactory.decodeResource(resources, R.drawable.score_rank_list);
		rankinglist[1] = BitmapFactory.decodeResource(resources, R.drawable.score_record_break);

		approveSentences[0] = BitmapFactory.decodeResource(resources, R.drawable.score_go_on);
		approveSentences[1] = BitmapFactory.decodeResource(resources, R.drawable.score_excellent);

		for (int i = 0; i < 6; i++) {
			approveImages[i] = BitmapFactory.decodeResource(resources, R.drawable.score_doll_1 + i);
		}
		for (int i = 0; i < 10; i++) {
			scoreNums[i] = BitmapFactory.decodeResource(resources, R.drawable.score_num_0 + i);
		}
		for (int i = 0; i < 3; i++) {
			rankSmallNums[i] = BitmapFactory.decodeResource(resources, R.drawable.score_rank_small1
					+ i);
			rankBigNums[i] = BitmapFactory
					.decodeResource(resources, R.drawable.score_rank_big1 + i);
		}
	}

	public void showScore() {
		score = 0;
		scoreTimer = new Timer();
		isScoreTimerCancel = false;
		scoreTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (score < nowScore - 5) {
					score++;
					nowRight = (int) score % 10;
					nowLeft = (int) (score - nowRight) / 10;
					handler.sendEmptyMessage(1);
				} else if (score <= nowScore - 2) {
					score += 0.5f;
					if (score % 1 == 0) {
						nowRight = (int) score % 10;
						nowLeft = (int) (score - nowRight) / 10;
						handler.sendEmptyMessage(1);
					}
				} else {
					score += 0.25f;
					if (score % 1 == 0) {
						nowRight = (int) score % 10;
						nowLeft = (int) (score - nowRight) / 10;
						handler.sendEmptyMessage(1);
					}
				}

				if (score == nowScore) {
					handler.sendEmptyMessage(2);
					this.cancel();
					scoreTimer.cancel();
					isScoreTimerCancel = true;
				}
			}
		}, 0, 30);
	}

	public void showRank() {
		int firstRight = nowUser.getFirstScore() % 10;
		int firstLeft = (nowUser.getFirstScore() - firstRight) / 10;
		int secRight = nowUser.getSecondScore() % 10;
		int secLeft = (nowUser.getSecondScore() - secRight) / 10;
		int thirdRight = nowUser.getThirdScore() % 10;
		int thirdLeft = (nowUser.getThirdScore() - thirdRight) / 10;

		firstLeftNum.setImageBitmap(scoreNums[firstLeft]);
		firstRightNum.setImageBitmap(scoreNums[firstRight]);
		secLeftNum.setImageBitmap(scoreNums[secLeft]);
		secRightNum.setImageBitmap(scoreNums[secRight]);
		thirdLeftNum.setImageBitmap(scoreNums[thirdLeft]);
		thirdRightNum.setImageBitmap(scoreNums[thirdRight]);
		String strScore1 = "(空)";
		String strScore2 = "(空)";
		String strScore3 = "(空)";
		if (nowUser.getFirstName() != "")
			strScore1 = nowUser.getFirstName();
		firstTV.setText(strScore1);
		if (nowUser.getSecondName() != "")
			strScore2 = nowUser.getSecondName();
		secTV.setText(strScore2);
		if (nowUser.getThirdName() != "")
			strScore3 = nowUser.getThirdName();
		thirdTV.setText(strScore3);
	}

	public int breakRankNum() {
		int which;
		nowLeft = nowScore / 10;
		nowRight = nowScore % 10;
		if (nowScore > nowUser.getFirstScore()) {
			which = 1;
			user.alterThird(userId, nowUser.getSecondName(), nowUser.getSecondScore());
			user.alterSecond(userId, nowUser.getFirstName(), nowUser.getFirstScore());
			user.alterFirst(userId, thirdEV.getText().toString(), nowScore);
			firstLeftNum.setImageBitmap(scoreNums[nowLeft]);
			firstRightNum.setImageBitmap(scoreNums[nowRight]);
			rankSmallFirst.setVisibility(ImageView.INVISIBLE);
			rankBigFirst.setVisibility(ImageView.VISIBLE);
			approveSentence.setImageBitmap(approveSentences[1]);

			// showRank();
			firstTV.setVisibility(TextView.INVISIBLE);
			firstEV.setVisibility(EditText.VISIBLE);
			firstEV.setFocusable(true);
			firstEV.requestFocus();
			// showSoftBoard(firstEV);

		} else if (nowScore > nowUser.getSecondScore()) {
			which = 2;
			user.alterThird(userId, nowUser.getSecondName(), nowUser.getSecondScore());
			user.alterSecond(userId, thirdEV.getText().toString(), nowScore);
			secLeftNum.setImageBitmap(scoreNums[nowLeft]);
			secRightNum.setImageBitmap(scoreNums[nowRight]);
			rankSmallSec.setVisibility(ImageView.INVISIBLE);
			rankBigSec.setVisibility(ImageView.VISIBLE);
			approveSentence.setImageBitmap(approveSentences[1]);

			// showRank();
			secTV.setVisibility(TextView.INVISIBLE);
			secEV.setVisibility(EditText.VISIBLE);
			secEV.setFocusable(true);
			secEV.requestFocus();
			// showSoftBoard(secEV);

		} else if (nowScore > nowUser.getThirdScore()) {
			which = 3;
			user.alterThird(userId, thirdEV.getText().toString(), nowScore);
			thirdLeftNum.setImageBitmap(scoreNums[nowLeft]);
			thirdRightNum.setImageBitmap(scoreNums[nowRight]);
			rankSmallThird.setVisibility(ImageView.INVISIBLE);
			rankBigThird.setVisibility(ImageView.VISIBLE);
			approveSentence.setImageBitmap(approveSentences[1]);

			// showRank();
			thirdTV.setVisibility(TextView.INVISIBLE);
			thirdEV.setVisibility(EditText.VISIBLE);
			thirdEV.setFocusable(true);
			thirdEV.requestFocus();
			// showSoftBoard(thirdEV);
		} else {
			which = 0;
			approveSentence.setImageBitmap(approveSentences[0]);
		}

		Random random = new Random();
		int i = random.nextInt(6);
		approveImage.setImageBitmap(approveImages[i]);

		return which;
	}

	public void showSoftBoard(EditText editText) {
		editText.requestFocus(); // edittext是一个EditText控件
		// 弹出软键盘的代码
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 8) {

			// scoreTimer.cancel();
			// Intent newIntent = getIntent();
			// setResult(8, newIntent);
			// finish();

		} else if (keyCode == 4) {
			scoreTimer.cancel();
			Intent newIntent = getIntent();
			setResult(4, newIntent);
			finish();
		} else if (keyCode == 66) {
			if (breakNum == 1) {
				firstEV.setFocusable(true);
				firstEV.requestFocus();
				// showSoftBoard(firstEV);
			} else if (breakNum == 2) {
				secEV.setFocusable(true);
				secEV.requestFocus();
				// showSoftBoard(secEV);
			} else if (breakNum == 3) {
				thirdEV.setFocusable(true);
				thirdEV.requestFocus();
				// showSoftBoard(thirdEV);
			}
		}
		return true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (!isScoreTimerCancel) {
			scoreTimer.cancel();
			isScoreTimerCancel = true;
		}
		this.finish();
		super.onStop();
	}

	class MyEditorActionListener implements TextView.OnEditorActionListener {
		boolean isfocus = true;

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			isfocus = !isfocus;
			if (isfocus) {
				if (breakNum == 1) {
					user.alterFirst(userId, firstEV.getText().toString(), nowScore);
					firstEV.clearFocus();
					firstEV.setFocusable(false);
					collapseSoftInputMethod(firstEV);
				} else if (breakNum == 2) {
					user.alterSecond(userId, secEV.getText().toString(), nowScore);
					secEV.clearFocus();
					secEV.setFocusable(false);
					collapseSoftInputMethod(secEV);
				} else if (breakNum == 3) {
					user.alterThird(userId, thirdEV.getText().toString(), nowScore);
					thirdEV.clearFocus();
					thirdEV.setFocusable(false);
					collapseSoftInputMethod(thirdEV);
				}
			}

			return true;
		}
	}

	/**
	 * 收起软键盘并设置提示文字
	 */
	public void collapseSoftInputMethod(EditText myView) {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(myView, 0);
	}

}