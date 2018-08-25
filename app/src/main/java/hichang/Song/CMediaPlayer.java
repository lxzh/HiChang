/*
- * .CMediaPlayer.java
 * class:CMediaPlayer
 */
package hichang.Song;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
/**
 * CMediaPlayer
 * 功能说明:模仿用到的超类MediaPlayer中的方法并进行封装，此处已在实现双播放器的播放
 */
public class CMediaPlayer extends MediaPlayer {
	/**
	 * position含义:记录当前播放位置，int类型
	 */
	private int position;
	/**
	 * mediaOrigianl含义:MediaPlayer的实例，用于播放原唱
	 */
	public MediaPlayer mediaOriginal;
	/**
	 * mediaAccompany含义:MediaPlayer的实例，用于播放伴奏
	 */
	private MediaPlayer mediaAccompany;
	/**
	 * pathOriginal:原唱歌曲文件路径
	 */
	private String pathOriginal;
	/**
	 * pathAccompany:伴奏文件路径
	 */
	private String pathAccompany;
    /**
     * isAccompany:记录当前是否是播放伴奏，值为true的时候表示播放伴奏
     */
	private boolean isAccompany;
	/**
	 * isOriginal:当前是否播放的原唱，true表示原唱在播放
	 */
	private boolean isOriginal;
	/**
	 * volume:当前音量值，float类型
	 */
	private float volume;
	/**
	 * CMediaPlayer类的构造函数，完成初始化的工作
	 * <p>方法详述:在此构造方法中，建立原唱和伴奏的播放器，并设置音量初始值为0.3</p>
	 */
	public CMediaPlayer(){
		mediaOriginal = new MediaPlayer();
		mediaAccompany = new MediaPlayer();
		isOriginal = false;
		isAccompany = false;
		volume = 0.30f;
	}
	public CMediaPlayer(Context context){
		mediaOriginal = getMediaPlayer(context);
		mediaAccompany = getMediaPlayer(context);
		isOriginal = false;
		isAccompany = false;
		volume = 0.30f;
	}
	
	static MediaPlayer getMediaPlayer(Context context){

	    MediaPlayer mediaplayer = new MediaPlayer();

//	    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
	    if (android.os.Build.VERSION.SDK_INT < 19) {
	        return mediaplayer;
	    }

	    try {
	        Class<?> cMediaTimeProvider = Class.forName( "android.media.MediaTimeProvider" );
	        Class<?> cSubtitleController = Class.forName( "android.media.SubtitleController" );
	        Class<?> iSubtitleControllerAnchor = Class.forName( "android.media.SubtitleController$Anchor" );
	        Class<?> iSubtitleControllerListener = Class.forName( "android.media.SubtitleController$Listener" );

	        Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});

	        Object subtitleInstance = constructor.newInstance(context, null, null);

	        Field f = cSubtitleController.getDeclaredField("mHandler");

	        f.setAccessible(true);
	        try {
	            f.set(subtitleInstance, new Handler());
	        }
	        catch (IllegalAccessException e) {return mediaplayer;}
	        finally {
	            f.setAccessible(false);
	        }

	        Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);

	        setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
	        //Log.e("", "subtitle is setted :p");
	    } catch (Exception e) {}

	    return mediaplayer;
	}
	
	/**
	 * 初始化播放器并设置文件路径
	 * <p>对建立的播放器进行初始化并设置文件路径</p>
	 * @param pathOriginal 原唱的文件路径
	 * @param pathAccompany 伴奏的文件路径
	 */
	public void CSetDataSource(String pathOriginal,String pathAccompany){
		this.pathOriginal = pathOriginal;
		this.pathAccompany = pathAccompany;
		//准备路径
		try {
			mediaOriginal.reset();
			mediaOriginal.setDataSource(this.pathOriginal);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mediaAccompany.reset();
			mediaAccompany.setDataSource(this.pathAccompany);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 对播放器进行准备工作
	 * <p>按照官方提供的MediaPlayer提供的MediaPlayer生命周期进行修改的同步方法</p>
	 */
	public void CPrepare(){
		try {
			mediaOriginal.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		}
		try {
			mediaAccompany.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	/**
	 * 开始播放
	 * <p>此方法调用前请记得先先后调用CMediaPlayer、CSetDataSource、CPrepare方法，开始方法中默认是播放伴奏</p>
	 * 
	 */
	public void CStart() {
			mediaAccompany.start();
			mediaAccompany.setVolume(volume,volume);
			//播放伴奏将isAccompany设置为true，表示当前播放的是伴奏
			isAccompany = true;
			//由于是双播放器实现，此时将原唱的播放器声音静掉
			mediaOriginal.setVolume(0,0);
			mediaOriginal.start();
	}
	
	/**
	 * 设置原唱
	 * 方便在上层中调用此方法，实现切换到原唱
	 */
	public void CSetOriginal(){
        //打开原唱播放器的声音
		mediaOriginal.setVolume(volume,volume);
		//isOriginal设置为true，表示当前播放的是原唱
		isOriginal = true;
		//静掉伴奏的声音
		mediaAccompany.setVolume(0,0);
	    //isAccompany设置为false，表示当前没有播放伴奏
		isAccompany = false;
	}
	
	/**
	 * 设置伴奏
	 * 方便在上层中调用此方法，实现切换到伴奏
	 */
	public void CSetAccompany(){
		//此方法的编写方法同CSetOriginal()
		mediaAccompany.setVolume(volume,volume);
		isAccompany = true;
		mediaOriginal.setVolume(0,0);
		isOriginal = false;
	}
	
	/**
	 * 设置循环播放
	 * 方便在上层中调用此方法，实现切换到循环播放模式
	 */
	public void CSetLooping(){
		mediaAccompany.setLooping(true);
		mediaOriginal.setLooping(true);
	}
	/**
	 * 设置不循环播放
	 * 方便在上层中调用此方法，实现切换到循环非播放模式
	 */
	public void CSetNotLooping(){
		mediaAccompany.setLooping(false);
		mediaOriginal.setLooping(false);
	}
	/**
	 * 获取当前是否是循环播放模式
	 * @return true表示当前为循环播放模式，false表示非循环模式
	 */
	public boolean CGetIsLooping(){
		return (mediaAccompany.isLooping()&&mediaOriginal.isLooping());
	}
	
	/**
	 * 获取当前歌曲的长度，时间单位为ms
	 * @return 整首歌的时间
	 */
	public int CGetDuration(){
		return mediaAccompany.getDuration();
	}
	
	/**
	 * 获取当前播放的位置
	 * @return 当前歌曲播放的位置
	 */
	public int CGetCurrentPosition(){
		position =  mediaAccompany.getCurrentPosition();
		return position;
	}
	
	/**
	 * 设置歌曲的播放位置
	 * @param pos 设置想要歌曲播放的位置
	 */
	public void CSeekTo(int pos){
		mediaAccompany.seekTo(pos);
		mediaOriginal.seekTo(pos);
	}
	
	/**
	 *调节媒体音量 
	 * @param vol想要设置的音量值
	 */
	public void CSetVolume(float vol){
		this.volume = vol;
		if((!isAccompany)&&(!isOriginal)){
			mediaAccompany.setVolume(volume,volume);
			mediaOriginal.setVolume(volume,volume);
		}
		if((!isAccompany)&&(isOriginal)){
			mediaOriginal.setVolume(volume,volume);
		}
		if((!isOriginal)&&(isAccompany)){
			mediaAccompany.setVolume(volume,volume);
		}
	}
	
	public float CGetVolume(){
		return volume;
	}
	
	/**
	 * 暂停播放
	 */
	public void CPause(){
		mediaAccompany.pause();
		mediaOriginal.pause();
	}
	/**
	 * 暂停后继续播放,pause后播放
	 */
	public void CReStart(){
		mediaAccompany.start();
		mediaOriginal.start();
	}
	/**
	 * stop播放,stop后要记得设置路径CSetDataSource，然后调用prepare()
	 * CStart()*/
	public void CStop(){
		mediaAccompany.stop();
		mediaOriginal.stop();
	}
	/**
	 * CMediaPlayer不用时一定记得release
	 */
	public void CRelease(){
		mediaAccompany.release();
		mediaOriginal.release();
	}
	
	/**
	 * CMediaPlayer在CRelease完成之后再次使用需要CReset
	 */
	public void CReset(){
		mediaAccompany.reset();
		mediaOriginal.reset();
	}
}
