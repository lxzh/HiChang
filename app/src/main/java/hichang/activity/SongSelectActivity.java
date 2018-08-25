package hichang.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.android.flypigeon.util.Constant;

import hichang.Song.Singer;
import hichang.Song.Song;
import hichang.ourView.InputBoxView;
import hichang.ourView.RankView;
import hichang.ourView.SingerView;
import hichang.ourView.SongView;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class SongSelectActivity extends Activity {

	RankView rankView1, rankView2;
	SongView songView1, songView2;
	SingerView singerView1, singerView2;
	InputBoxView songBoxView1, songBoxView2, singerBoxView1, singerBoxView2;
	AbsoluteLayout songLayout1, songLayout2, singerLayout1, singerLayout2;
	ImageView rightBtn, leftBtn, upBtn, downBtn;
	int whichView, whichOne;
	TranslateAnimation toLeft1, fromLeft1, toRight1, fromRight1, toUp1, fromUp1, toDown1,
			fromDown1;
	TranslateAnimation toLeft2, fromLeft2, toRight2, fromRight2, toUp2, fromUp2, toDown2,
			fromDown2;
	Timer changeTimer, arrowTimer;
	Handler handler;
	final int RANKVIEW = 1, SONGVIEW = 2, SINGERVIEW = 3, MSG_HIDE_RANK1 = 1, MSG_HIDE_RANK2 = 2,
			MSG_HIDE_SONG1 = 3, MSG_HIDE_SONG2 = 4, MSG_HIDE_SINGER1 = 5, MSG_HIDE_SINGER2 = 6,
			MSG_LEFT_ARROW = 7, MSG_RIGHT_ARROW = 8, MSG_UP_ARROW = 9, MSG_DOWN_ARROW = 10;
	Song songDA;
	Singer singerDA;
	boolean isSelectedBySinger = false;
	Singer nowSinger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songselect);
		rankView1 = (RankView) findViewById(R.id.rank_view1);
		rankView2 = (RankView) findViewById(R.id.rank_view2);
		songView1 = (SongView) findViewById(R.id.song_view1);
		songView2 = (SongView) findViewById(R.id.song_view2);
		singerView1 = (SingerView) findViewById(R.id.singer_view1);
		singerView2 = (SingerView) findViewById(R.id.singer_view2);
		songBoxView1 = (InputBoxView) findViewById(R.id.song_input_view1);
		songBoxView2 = (InputBoxView) findViewById(R.id.song_input_view2);
		singerBoxView1 = (InputBoxView) findViewById(R.id.singer_input_view1);
		singerBoxView2 = (InputBoxView) findViewById(R.id.singer_input_view2);
		songLayout1 = (AbsoluteLayout) findViewById(R.id.song_layout1);
		songLayout2 = (AbsoluteLayout) findViewById(R.id.song_layout2);
		singerLayout1 = (AbsoluteLayout) findViewById(R.id.singer_layout1);
		singerLayout2 = (AbsoluteLayout) findViewById(R.id.singer_layout2);
		leftBtn = (ImageView) findViewById(R.id.left_btn);
		rightBtn = (ImageView) findViewById(R.id.right_btn);
		upBtn = (ImageView) findViewById(R.id.up_btn);
		downBtn = (ImageView) findViewById(R.id.down_btn);

		songBoxView1.init();
		songBoxView2.init();
		singerBoxView1.init();
		singerBoxView2.init();

		initAnim();
		initHandler();
		changeTimer = new Timer();
		arrowTimer = new Timer();
		whichOne = 1;
		whichView = RANKVIEW;
		songDA = new Song(this);
		singerDA = new Singer(this);

		rankView1.setSongs((ArrayList<Song>) songDA.queryTenSongByTwoClicks(rankView1.getPage()));
		
		leftBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				keyLeftClick();
			}
		});
		rightBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				keyRightClick();
			}
		});
		upBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				keyUpClick();
			}
		});
		downBtn.setOnClickListener(new View.OnClickListener() {
					
			public void onClick(View arg0) {
				keyDownClick();
			}
		});
	}

	private void keyUpClick(){
		upBtn.setImageResource(R.drawable.upwhite);
		arrowTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(MSG_UP_ARROW);
			}
		}, 120);
		moveToDown();
	}
	private void keyDownClick(){
		downBtn.setImageResource(R.drawable.downwhite);
		arrowTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(MSG_DOWN_ARROW);
			}
		}, 120);
		moveToUp();
	}
	private void keyLeftClick(){
		leftBtn.setImageResource(R.drawable.leftwhite);
		arrowTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(MSG_LEFT_ARROW);
			}
		}, 120);
		moveToRight();
	}
	private void keyRightClick(){
		rightBtn.setImageResource(R.drawable.rightwhite);
		arrowTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(MSG_RIGHT_ARROW);
			}
		}, 120);
		moveToLeft();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Toast.makeText(this, whichView+" "+keyCode,
		// Toast.LENGTH_SHORT).show();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			keyUpClick();
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			keyDownClick();
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			keyLeftClick();
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			keyRightClick();
			break;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_1:
		case KeyEvent.KEYCODE_2:
		case KeyEvent.KEYCODE_3:
		case KeyEvent.KEYCODE_4:
		case KeyEvent.KEYCODE_5:
		case KeyEvent.KEYCODE_6:
		case KeyEvent.KEYCODE_7:
		case KeyEvent.KEYCODE_8:
		case KeyEvent.KEYCODE_9:
		case 219:
			input(keyCode);
			break;
		case KeyEvent.KEYCODE_ENTER:
			if (whichView == SONGVIEW) {
				Toast.makeText(this, songBoxView1.getAvailable() + "", Toast.LENGTH_SHORT).show();
				if (songBoxView1.getAvailable()) {
					songBoxView1.setAvailable(false);
					songBoxView2.setAvailable(false);
				} else {
					songBoxView1.setAvailable(true);
					songBoxView2.setAvailable(true);
				}
			} else if (whichView == SINGERVIEW) {
				Toast.makeText(this, singerBoxView1.getAvailable() + "", Toast.LENGTH_SHORT).show();
				if (singerBoxView1.getAvailable()) {
					singerBoxView1.setAvailable(false);
					singerBoxView2.setAvailable(false);
				} else {
					singerBoxView1.setAvailable(true);
					singerBoxView2.setAvailable(true);
				}
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void moveToLeft() {
		switch (whichView) {
		case RANKVIEW:
			whichView = SONGVIEW;
			isSelectedBySinger = false;
			if (whichOne == 1) {
				rankView1.startAnimation(toLeft1);
				songLayout1.setVisibility(View.VISIBLE);
				initSong();
				songLayout1.startAnimation(toLeft2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_RANK1);
					}
				}, 350);
			} else {
				rankView2.startAnimation(toLeft1);
				songLayout1.setVisibility(View.VISIBLE);
				initSong();
				songLayout1.startAnimation(toLeft2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_RANK2);
					}
				}, 350);
			}
			break;
		case SONGVIEW:
			whichView = SINGERVIEW;
			isSelectedBySinger = false;
			songBoxView1.setAvailable(false);
			songBoxView2.setAvailable(false);
			if (whichOne == 1) {
				songLayout1.startAnimation(toLeft1);
				singerLayout1.setVisibility(View.VISIBLE);
				initSinger();
				singerLayout1.startAnimation(toLeft2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_SONG1);
					}
				}, 350);
			} else {
				songLayout2.startAnimation(toLeft1);
				singerLayout1.setVisibility(View.VISIBLE);
				initSinger();
				singerLayout1.startAnimation(toLeft2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_SONG2);
					}
				}, 350);
			}
			break;
		case SINGERVIEW:
			whichView = RANKVIEW;
			singerBoxView1.setAvailable(false);
			singerBoxView2.setAvailable(false);
			if (whichOne == 1) {
				singerLayout1.startAnimation(toLeft1);
				rankView1.setVisibility(View.VISIBLE);
				initRank();
				rankView1.startAnimation(toLeft2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_SINGER1);
					}
				}, 350);
			} else {
				singerLayout2.startAnimation(toLeft1);
				rankView1.setVisibility(View.VISIBLE);
				initRank();
				rankView1.startAnimation(toLeft2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_SINGER2);
					}
				}, 350);
			}
			break;
		default:
			break;
		}
		whichOne = 1;
	}

	public void moveToRight() {
		switch (whichView) {
		case RANKVIEW:
			whichView = SINGERVIEW;
			if (whichOne == 1) {
				rankView1.startAnimation(toRight1);
				singerLayout1.setVisibility(View.VISIBLE);
				initSinger();
				singerLayout1.startAnimation(toRight2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_RANK1);
					}
				}, 350);
			} else {
				rankView2.startAnimation(toRight1);
				singerLayout1.setVisibility(View.VISIBLE);
				initSinger();
				singerLayout1.startAnimation(toRight2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_RANK2);
					}
				}, 350);
			}
			break;
		case SONGVIEW:
			whichView = RANKVIEW;
			isSelectedBySinger = false;
			songBoxView1.setAvailable(false);
			songBoxView2.setAvailable(false);
			if (whichOne == 1) {
				songLayout1.startAnimation(toRight1);
				rankView1.setVisibility(View.VISIBLE);
				initRank();
				rankView1.startAnimation(toRight2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_SONG1);
					}
				}, 350);
			} else {
				songLayout2.startAnimation(toRight1);
				rankView1.setVisibility(View.VISIBLE);
				initRank();
				rankView1.startAnimation(toRight2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_SONG2);
					}
				}, 350);
			}
			break;
		case SINGERVIEW:
			whichView = SONGVIEW;
			singerBoxView1.setAvailable(false);
			singerBoxView2.setAvailable(false);
			if (whichOne == 1) {
				singerLayout1.startAnimation(toRight1);
				songLayout1.setVisibility(View.VISIBLE);
				initSong();
				songLayout1.startAnimation(toRight2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_SINGER1);
					}
				}, 350);
			} else {
				singerLayout2.startAnimation(toRight1);
				songLayout1.setVisibility(View.VISIBLE);
				initSong();
				songLayout1.startAnimation(toRight2);
				changeTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(MSG_HIDE_SINGER2);
					}
				}, 350);
			}
			break;
		default:
			break;
		}
		whichOne = 1;
	}

	public void moveToUp() {
		switch (whichView) {
		case RANKVIEW:
			if (whichOne == 1) {
				if (rankView1.getPage() < songDA.getSongPage()) {
					whichOne = 2;
					rankView1.startAnimation(toUp1);
					rankView2.setPage(rankView1.getPage() + 1);
					rankView2.setSongs((ArrayList<Song>) songDA.queryTenSongByTwoClicks(rankView2
							.getPage()));
					rankView2.setVisibility(View.VISIBLE);
					rankView2.startAnimation(toUp2);
					Toast.makeText(
							this,
							"rankview1 " + rankView1.getPage() + " rankview2 "
									+ rankView2.getPage(), Toast.LENGTH_SHORT).show();
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_RANK1);
						}
					}, 360);
				}
			} else {
				if (rankView2.getPage() < songDA.getSongPage()) {
					whichOne = 1;
					rankView1.setPage(rankView2.getPage() + 1);
					rankView1.setSongs((ArrayList<Song>) songDA.queryTenSongByTwoClicks(rankView1
							.getPage()));
					rankView1.setVisibility(View.VISIBLE);
					rankView1.startAnimation(toUp2);
					rankView2.startAnimation(toUp1);
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_RANK2);
						}
					}, 360);
				}
			}
			break;
		case SONGVIEW:
			if (whichOne == 1) {
				if (isSelectedBySinger) {
					Toast.makeText(
							this,
							songDA.getSongPageBySgAndSn(nowSinger.getSimpleName(),
									songBoxView1.getText())
									+ "", Toast.LENGTH_SHORT);
					if (songView1.getPage() < songDA.getSongPageBySgAndSn(nowSinger.getName(),
							songBoxView1.getText())) {
						whichOne = 2;
						songLayout1.startAnimation(toUp1);
						songView2.setPage(songView1.getPage() + 1);
						songView2.setSongs((ArrayList<Song>) songDA.findTenSongBySgAndSn(
								nowSinger.getName(), songBoxView2.getText(), songView2.getPage()));
						songLayout2.setVisibility(View.VISIBLE);
						songLayout2.startAnimation(toUp2);
						changeTimer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_HIDE_SONG1);
							}
						}, 360);
					}
				} else {
					if (songView1.getPage() < songDA
							.getSongPageBySimpleName(songBoxView1.getText())) {
						Toast.makeText(this,
								songDA.getSongPageBySimpleName(songBoxView1.getText()) + "",
								Toast.LENGTH_SHORT).show();
						whichOne = 2;
						songLayout1.startAnimation(toUp1);
						songView2.setPage(songView1.getPage() + 1);
						songView2.setSongs((ArrayList<Song>) songDA.findTenSongBySimpleName(
								songBoxView2.getText(), songView2.getPage()));
						songLayout2.setVisibility(View.VISIBLE);
						songLayout2.startAnimation(toUp2);
						changeTimer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_HIDE_SONG1);
							}
						}, 360);
					}
				}
			} else {
				if (isSelectedBySinger) {
					if (songView2.getPage() < songDA.getSongPageBySgAndSn(nowSinger.getName(),
							songBoxView2.getText())) {
						whichOne = 1;
						songView1.setPage(songView2.getPage() + 1);
						songView1.setSongs((ArrayList<Song>) songDA.findTenSongBySgAndSn(
								nowSinger.getName(), songBoxView1.getText(), songView1.getPage()));
						songLayout1.setVisibility(View.VISIBLE);
						songLayout1.startAnimation(toUp2);
						songLayout2.startAnimation(toUp1);
						changeTimer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_HIDE_SONG2);
							}
						}, 360);
					}
				} else {
					if (songView2.getPage() < songDA
							.getSongPageBySimpleName(songBoxView2.getText())) {
						whichOne = 1;
						songView1.setPage(songView2.getPage() + 1);
						songView1.setSongs((ArrayList<Song>) songDA.findTenSongBySimpleName(
								songBoxView1.getText(), songView1.getPage()));
						songLayout1.setVisibility(View.VISIBLE);
						songLayout1.startAnimation(toUp2);
						songLayout2.startAnimation(toUp1);
						changeTimer.schedule(new TimerTask() {

							@Override
							public void run() {
								handler.sendEmptyMessage(MSG_HIDE_SONG2);
							}
						}, 360);
					}
				}
			}
			break;
		case SINGERVIEW:
			if (whichOne == 1) {
				if (singerView1.getPage() < singerDA.getSingerPageNumBySN(singerBoxView1.getText())) {
					whichOne = 2;
					singerLayout1.startAnimation(toUp1);
					singerView2.setPage(singerView1.getPage() + 1);
					singerView2.setSingers((ArrayList<Singer>) singerDA.queryNineSingerBySN(
							singerBoxView2.getText(), singerView2.getPage()));
					singerLayout2.setVisibility(View.VISIBLE);
					singerLayout2.startAnimation(toUp2);
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_SINGER1);
						}
					}, 360);
				}
			} else {
				if (singerView2.getPage() < singerDA.getSingerPageNumBySN(singerBoxView1.getText())) {
					whichOne = 1;
					singerView1.setPage(singerView2.getPage() + 1);
					singerView1.setSingers((ArrayList<Singer>) singerDA.queryNineSingerBySN(
							singerBoxView1.getText(), singerView1.getPage()));
					singerLayout1.setVisibility(View.VISIBLE);
					singerLayout1.startAnimation(toUp2);
					singerLayout2.startAnimation(toUp1);
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_SINGER2);
						}
					}, 360);
				}
			}
			break;
		default:
			break;
		}
	}

	public void moveToDown() {
		switch (whichView) {
		case RANKVIEW:
			if (whichOne == 1) {
				if (rankView1.getPage() > 1) {
					whichOne = 2;
					rankView2.setPage(rankView1.getPage() - 1);
					rankView2.setSongs((ArrayList<Song>) songDA.queryTenSongByTwoClicks(rankView2
							.getPage()));
					rankView1.startAnimation(toDown1);
					rankView2.setVisibility(View.VISIBLE);
					rankView2.startAnimation(toDown2);
					// rankView1.setPage(rankView2.getPage() - 1);
					Toast.makeText(this,"rankview1 " + rankView1.getPage() + " rankview2 "
									+ rankView2.getPage(), Toast.LENGTH_SHORT).show();
					changeTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_RANK1);
						}
					}, 360);
				}
			} else {
				if (rankView2.getPage() > 1) {
					whichOne = 1;
					rankView1.setPage(rankView2.getPage() - 1);
					rankView1.setSongs((ArrayList<Song>) songDA.queryTenSongByTwoClicks(rankView1
							.getPage()));
					rankView1.setVisibility(View.VISIBLE);
					rankView1.startAnimation(toDown2);
					rankView2.startAnimation(toDown1);
					// rankView2.setPage(rankView1.getPage() - 1);
					Toast.makeText(
							this,
							"rankview1 " + rankView1.getPage() + " rankview2 "
									+ rankView2.getPage(), Toast.LENGTH_SHORT).show();
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_RANK2);
						}
					}, 360);
				}
			}
			break;
		case SONGVIEW:
			if (whichOne == 1) {
				if (songView1.getPage() > 1) {
					whichOne = 2;
					songLayout1.startAnimation(toDown1);
					songView2.setPage(songView1.getPage() - 1);
					if (isSelectedBySinger) {
						songView2.setSongs((ArrayList<Song>) songDA.findTenSongBySgAndSn(
								nowSinger.getName(), songBoxView2.getText(), songView2.getPage()));
					} else {
						songView2.setSongs((ArrayList<Song>) songDA.findTenSongBySimpleName(
								songBoxView2.getText(), songView2.getPage()));
					}
					songLayout2.setVisibility(View.VISIBLE);
					songLayout2.startAnimation(toDown2);
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_SONG1);
						}
					}, 360);
				}
			} else {
				if (songView2.getPage() > 1) {
					whichOne = 1;
					songView1.setPage(songView2.getPage() - 1);
					if (isSelectedBySinger) {
						songView1.setSongs((ArrayList<Song>) songDA.findTenSongBySgAndSn(
								nowSinger.getName(), songBoxView1.getText(), songView1.getPage()));
					} else {
						songView1.setSongs((ArrayList<Song>) songDA.findTenSongBySimpleName(
								songBoxView1.getText(), songView1.getPage()));
					}
					songLayout1.setVisibility(View.VISIBLE);
					songLayout1.startAnimation(toDown2);
					songLayout2.startAnimation(toDown1);
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_SONG2);
						}
					}, 360);
				}
			}
			break;
		case SINGERVIEW:
			if (whichOne == 1) {
				if (singerView1.getPage() > 1) {
					whichOne = 2;
					singerLayout1.startAnimation(toDown1);
					singerView2.setPage(singerView1.getPage() - 1);
					singerView2.setSingers((ArrayList<Singer>) singerDA.queryNineSingerBySN(
							singerBoxView2.getText(), singerView2.getPage()));
					singerLayout2.setVisibility(View.VISIBLE);
					singerLayout2.startAnimation(toDown2);
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_SINGER1);
						}
					}, 360);
				}
			} else {
				if (singerView2.getPage() > 1) {
					whichOne = 1;
					singerView1.setPage(singerView2.getPage() - 1);
					singerView1.setSingers((ArrayList<Singer>) singerDA.queryNineSingerBySN(
							singerBoxView1.getText(), singerView1.getPage()));
					singerLayout1.setVisibility(View.VISIBLE);
					singerLayout1.startAnimation(toDown2);
					singerLayout2.startAnimation(toDown1);
					changeTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_HIDE_SINGER2);
						}
					}, 360);
				}
			}
			break;
		default:
			break;
		}
	}

	public void input(int keyCode) {
		switch (whichView) {
		case RANKVIEW:
			if (whichOne == 1) {
				Song nowSong = rankView1.getSongByKeyCode(keyCode);
				if (nowSong != null) {
					nextActivity(nowSong);
				}
			} else {
				Song nowSong = rankView2.getSongByKeyCode(keyCode);
				if (nowSong != null) {
					nextActivity(nowSong);
				}
			}
			break;
		case SONGVIEW:
			if (songBoxView1.getAvailable()) {
				songBoxView1.pressKey(keyCode);
				songBoxView2.pressKey(keyCode);
				if (isSelectedBySinger) {
					changeSongBySinger(nowSinger.getSimpleName(), songBoxView1.getText());
				} else {
					changeSong(songBoxView1.getText());
				}
			} else {
				if (whichOne == 1) {
					Song nowSong = songView1.getSongByKeyCode(keyCode);
					if (nowSong != null) {
						nextActivity(nowSong);
					}
				} else {
					Song nowSong = songView2.getSongByKeyCode(keyCode);
					if (nowSong != null) {
						nextActivity(nowSong);
					}
				}
			}
			break;
		case SINGERVIEW:
			if (singerBoxView1.getAvailable()) {
				singerBoxView1.pressKey(keyCode);
				singerBoxView2.pressKey(keyCode);
				changeSinger(singerBoxView1.getText());
			} else {
				if (whichOne == 1) {
					// Toast.makeText(this, ""+1, Toast.LENGTH_SHORT).show();
					nowSinger = singerView1.getSingerBy(keyCode);
					if (nowSinger != null) {
						isSelectedBySinger = true;
						moveToRight();
					}
				} else {
					// Toast.makeText(this, ""+2, Toast.LENGTH_SHORT).show();
					nowSinger = singerView2.getSingerBy(keyCode);
					if (nowSinger != null) {
						isSelectedBySinger = true;
						moveToRight();
					}
				}
			}
			break;
		default:
			break;
		}
	}

	public void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_HIDE_RANK1:
					rankView1.clearAnimation();
					rankView1.setVisibility(View.INVISIBLE);
					break;
				case MSG_HIDE_RANK2:
					rankView2.clearAnimation();
					rankView2.setVisibility(View.INVISIBLE);
					break;
				case MSG_HIDE_SONG1:
					songLayout1.clearAnimation();
					songLayout1.setVisibility(View.INVISIBLE);
					break;
				case MSG_HIDE_SONG2:
					songLayout2.clearAnimation();
					songLayout2.setVisibility(View.INVISIBLE);
					break;
				case MSG_HIDE_SINGER1:
					singerLayout1.clearAnimation();
					singerLayout1.setVisibility(View.INVISIBLE);
					break;
				case MSG_HIDE_SINGER2:
					singerLayout2.clearAnimation();
					singerLayout2.setVisibility(View.INVISIBLE);
					break;
				case MSG_LEFT_ARROW:
					leftBtn.setImageResource(R.drawable.arrow_left);
					break;
				case MSG_RIGHT_ARROW:
					rightBtn.setImageResource(R.drawable.arrow_right);
					break;
				case MSG_UP_ARROW:
					upBtn.setImageResource(R.drawable.arrow_up);
					break;
				case MSG_DOWN_ARROW:
					downBtn.setImageResource(R.drawable.arrow_down);
					break;
				default:
					break;
				}
			}
		};

	}

	public void initRank() {
		rankView1.setPage(1);
		rankView1.setSongs((ArrayList<Song>) songDA.queryTenSongByTwoClicks(1));
	}

	public void nextActivity(Song nowSong) {
		Intent intent = getIntent();
		int afferentParam = intent.getIntExtra("type", -1);
		intent.putExtra("songId", nowSong.getSongID());
		intent.putExtra("isReturn", 0);
		if (afferentParam == 0) {
			intent.putExtra("activityId", 0);
		} else if (afferentParam == 1) {
			intent.putExtra("activityId", 1);
		} else {
			intent.putExtra("activityId", 2);
		}
		intent.setClass(SongSelectActivity.this, progressBarActivity.class);
		startActivity(intent);
	}

	public void initSong() {
		songView1.setPage(1);
		songBoxView1.setAvailable(true);
		songBoxView2.setAvailable(true);
		songBoxView1.setText("");
		songBoxView2.setText("");
		if (isSelectedBySinger) {
			// Toast.makeText(this, ""+nowSinger.getSimpleName(),
			// Toast.LENGTH_SHORT).show();
			songView1.setSongs((ArrayList<Song>) songDA.findTenSongBySgAndSn(nowSinger.getName(),
					"", 1));
		} else {
			songView1.setSongs((ArrayList<Song>) songDA.findTenSongBySimpleName("", 1));
		}
	}

	public void initSinger() {
		singerView1.setPage(1);
		singerBoxView1.setAvailable(true);
		singerBoxView2.setAvailable(true);
		singerBoxView1.setText("");
		singerBoxView2.setText("");
		singerView1.setSingers((ArrayList<Singer>) singerDA.queryNineSingerBySN("", 1));
	}

	public void changeSong(String text) {
		if (whichOne == 1) {
			songView1.setPage(1);
			songView1.setSongs((ArrayList<Song>) songDA.findTenSongBySimpleName(text, 1));
		} else {
			songView2.setPage(1);
			songView2.setSongs((ArrayList<Song>) songDA.findTenSongBySimpleName(text, 1));
		}
	}

	public void changeSinger(String text) {
		if (whichOne == 1) {
			singerView1.setPage(1);
			singerView1.setSingers((ArrayList<Singer>) singerDA.queryNineSingerBySN(text, 1));
		} else {
			singerView2.setPage(1);
			singerView2.setSingers((ArrayList<Singer>) singerDA.queryNineSingerBySN(text, 1));
		}
	}

	public void changeSongBySinger(String text, String singer) {
		if (whichOne == 1) {
			songView1.setPage(1);
			songView1.setSongs((ArrayList<Song>) songDA.findTenSongBySgAndSn(singer, text, 1));
		} else {
			songView2.setPage(1);
			songView2.setSongs((ArrayList<Song>) songDA.findTenSongBySgAndSn(singer, text, 1));
		}

	}

	public void initAnim() {
		toLeft1 = new TranslateAnimation(0, -1920, 0, 0);
		toRight1 = new TranslateAnimation(0, 1920, 0, 0);
		toUp1 = new TranslateAnimation(0, 0, 0, -1080);
		toDown1 = new TranslateAnimation(0, 0, 0, 1080);
		toLeft2 = new TranslateAnimation(1920, 0, 0, 0);
		toRight2 = new TranslateAnimation(-1920, 0, 0, 0);
		toUp2 = new TranslateAnimation(0, 0, 1080, 0);
		toDown2 = new TranslateAnimation(0, 0, -1080, 0);

		toLeft1.setDuration(360);
		toRight1.setDuration(360);
		toUp1.setDuration(360);
		toDown1.setDuration(360);
		toLeft2.setDuration(360);
		toRight2.setDuration(360);
		toUp2.setDuration(360);
		toDown2.setDuration(360);

		toLeft1.setFillAfter(true);
		toRight1.setFillAfter(true);
		toUp1.setFillAfter(true);
		toDown1.setFillAfter(true);
		toLeft2.setFillAfter(true);
		toRight2.setFillAfter(true);
		toUp2.setFillAfter(true);
		toDown2.setFillAfter(true);
	}
}
