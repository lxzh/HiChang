package hichang.Song;

import hichang.Song.Singer;
import hichang.activity.MainActivity;
import hichang.activity.R;
import hichang.database.DataBase;
import hichang.database.SingerDA;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Singer<br/>
 * 定义Singer
 */
public class Singer {
	/**
	 * 歌手ID
	 */
    public int iD;
    /**
     * 歌手名
     */
    public String name;
    /**
     * 歌手名首字母简写
     */
    public String simpleName;
    /**
     * 歌手性别
     */
    public String gender;
    /**
     * 歌曲ID
     */
    public String songID;
    /**
     * HiDataBase对象，用于与数据访问类交互
     */
	private DataBase databaseHelper;
	/**
	 * 构造函数
	 */
	public Singer()
	{	
	}
	/**
	 * 构造函数用于与数据访问类交互
	 * @param context
	 */
	public Singer(Context context) {
		databaseHelper=new DataBase(context);
		SingerDA.connectDB(databaseHelper);
	}
	// Singer中属性的getter和setter
	public int getiD() {
		return iD;
	}
	public void setiD(int iD) {
		this.iD = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Bitmap getImage(Context context){
		String picPath = MainActivity.SD_PATH + "Singer/" + iD + "/" + iD + "_p_r.png";
		File imagefile = new File(picPath);
		if (imagefile.exists() && LocalBitmap.getLoacalBitmap(picPath) != null){
			return LocalBitmap.getLoacalBitmap(picPath);
		} else{
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.photo);
		}
	}
	/**
	 * 添加歌手
	 * @param name  歌手名
	 * @param simplename 歌手的名字首字母简写
	 * @param gender 歌手性别
	 */
	public void addSinger(String name,String simplename,String gender)
	{
		SingerDA.addSinger(name, simplename, gender);
	}	
	/**
	 * 按歌手名查找歌手信息
	 */
	public static Singer querySingerByName(String name)
	{
		return SingerDA.querySingerByName(name);
	}
	/**
	 * 按不完全歌手名查找歌手,返回歌手列表
	 * @param subName  歌手不完全名
	 * @return  歌手列表
	 */
	public List<Singer> querySingerBySubName(String subName)
	{
		return SingerDA.querySingerBySubName(subName);
	}	
	/**
	 * 按歌手名简写查找歌手，返回歌手列表
	 * @param simplename  歌手名简写
	 * @return  歌手列表
	 */
	public List<Singer> querySingerBySimpleName(String simplename)
	{
		return SingerDA.querySingerBySimpleName(simplename);
	}		
	/**
	 * 根据性别返回歌手名列表
	 * @param gender 歌手性别
	 * @return 歌手列表
	 */
	public List<Singer> querySingerByGender(String gender)
	{
		return SingerDA.querySingerByGender(gender);
	}
	/**
	 * 返回所有歌手
	 * @return 歌手列表
	 */
	public List<Singer> queryAllSinger()
	{
		return SingerDA.queryAllSinger();
	}	
	/**
	 * 歌手名简写返回歌手列表的子列表
	 * @param singerlist 歌手原始列表
	 * @param simplename 歌手名简写
	 * @return 歌手列表的子列表
	 */
	public List<Singer> querySubSingerList(List<Singer> singerlist,String simplename)
	{
		return SingerDA.querySubSingerList(singerlist, simplename);
	}	
	/**
	 * 获取歌手总数
	 * @return 歌手总数
	 */
	public int getSingerCount()
	{
		return SingerDA.getSingerCount();
	}
	/**
	 * 按顺序拿取九个歌手信息
	 * @param page 页面编号
	 * @return
	 */
	public List<Singer> queryNineSinger(int page)
	{
		return SingerDA.queryNineSinger(page);
	}
	/**
	 * 按顺序拿取九个歌手信息
	 * @param page 页面编号 
	 * @return 歌手信息
	 */
	public List<String> findNineSinger(int page)
	{
		return SingerDA.findNineSinger(page);
	}
	/**
	 * 根据歌手名简写获取歌手总数
	 * @return
	 */
	public int getSingerCountBySimpleName(String simplename)
	{
		return SingerDA.getSingerCountBySimpleName(simplename);
	}
	
	public int getSingerPageNumBySN(String simplename){
		int count = getSingerCountBySimpleName(simplename);
		int page;
		if(count%9 == 0){
			page = count/9;
		} else {
			page = count/9+1;
		}
		return page;
	}
	/**
	 * 按歌手名简写拿取九个歌手名
	 * @param simplename 歌手名简写
	 * @param page 页面编号
	 * @return
	 */
	public List<String> findNineSingerBySN(String simplename,int page)
	{
		return SingerDA.findNineSingerBySN(simplename, page);
	}
	/**
	 * 按歌手名简写拿取九个歌手
	 * @param simplename 歌手名简写
	 * @param page 页面编号
	 * @return
	 */
	public List<Singer> queryNineSingerBySN(String simplename,int page)
	{
		return SingerDA.queryNineSingerBySN(simplename, page);
	}
}
