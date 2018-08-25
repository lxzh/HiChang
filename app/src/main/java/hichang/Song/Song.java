package hichang.Song;

import hichang.database.DataBase;
import hichang.database.SongDA;

import java.util.List;

import android.content.Context;

public class Song {
	/**
	 * 歌曲ID
	 */
	private int songID;
    /**
     * 歌曲名
     */
    private  String name;
    /**
     * 歌曲名首字母简写
     */
    private String simpleName;
    /**
     * 歌手名1
     */
    private String singer1;
    /**
     * 歌手名2
     */
    private String singer2;
    /**
     * 歌曲点击次数
     */
    private int clicks;
    /**
     * 是否可用
     */
    private int isAvailable;
    /**
     * 原唱路径
     */
    private String musicPath;
    /**
     * 伴唱路径
     */
    private String accomanimentPath;
    /**
     * 歌词路径
     */
    private String songLyricUrl;

    //预约号，未被预约时为-1
    public int bookNum=-1;

    /**
     * 歌曲文件夹的路径
     */

    private final String PATH="/sdcard/HiChang/Songs/";
    /**
     * DataBase对象，用于Song(Context context)传递参数
     */
	private DataBase databaseHelper;
	/**
	 * Song的构造函数
	 */
	public Song()
	{	
	}
	/**
	 * Song用于数据库操作时的构造函数
	 * @param context activity上下文环境
	 */
	public Song(Context context) {
		databaseHelper=new DataBase(context);
		SongDA.connectDB(databaseHelper);
	}
	/*下面的set和get方法用于设置或获取HiSong实例当前属性值*/
	public int getSongID() {
		return songID;
	}
	public void setSongID(int songID) {
		this.songID = songID;
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
	public String getSinger1() {
		return singer1;
	}
	public void setSinger1(String singer1) {
		this.singer1 = singer1;
	}
	public String getSinger2() {
		return singer2;
	}
	public void setSinger2(String singer2) {
		this.singer2 = singer2;
	}
	public int getClicks() {
		return clicks;
	}
	public void setClicks(int clicks) {
		this.clicks = clicks;
	}
	public int getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(int isAvailable) {
		this.isAvailable = isAvailable;
	}
	public String getMusicPath() {
		return musicPath;
	}
	//由于每首歌有三个文件，用ID表示一个文件夹，文件夹内ID后跟‘v’的表示原唱文件，加‘i’的表示伴奏文件，txt格式的文件是歌词配置文件
	public void setMusicPath(int songId) {
		this.musicPath = PATH+songId+"/"+songId+"_v.mp3";
	}
	public String getAccomanimentPath() {
		return accomanimentPath;
	}
	public void setAccomanimentPath(int songId) {
		this.accomanimentPath = PATH+songId+"/"+songId+"_i.mp3";
	}
	public String getSongLyricUrl() {
		return songLyricUrl;
	}
	public void setSongLyricUrl(int songId) {
		this.songLyricUrl = PATH+songId+"/"+songId+".txt";
	}
	/**
	 * 构造函数，创建HiSong对象
	 * @param songID 歌曲ID
	 * @param name  歌曲名
	 * @param simpleName 歌曲名首字母简写
	 * @param singer1 歌手1
	 * @param singer2 歌手2
	 * @param clicks 点击次数
	 * @param isAvailable 是否可用
	 * @param musicPath 原唱路径
	 * @param accomanimentPath 伴奏路径
	 * @param songLyricUrl 歌词路径
	 */
	public Song(int songID, String name, String simpleName, String singer1,
			String singer2, int clicks, int isAvailable, String musicPath,
			String accomanimentPath, String songLyricUrl) {
		super();
		this.songID = songID;
		this.name = name;
		this.simpleName = simpleName;
		this.singer1 = singer1;
		this.singer2 = singer2;
		this.clicks = clicks;
		this.isAvailable = isAvailable;
		this.musicPath = musicPath;
		this.accomanimentPath = accomanimentPath;
		this.songLyricUrl = songLyricUrl;
	}
	/**
	 * 返回所有的歌曲
	 * @return 歌曲Song列表
	 */
	public List<Song> findAllSong()
	{
		return SongDA.findAllSong();
	}
	/**
	 * 添加歌曲
	 * @param name            歌曲名
	 * @param simplename      歌曲名首字母
	 * @param singer1                              一个演唱者
	 * @param singer2                             其他演唱者
	 * @param clicks         点击次数
	 * @param isAvailable    是否由此歌曲文件
	 */
	public void addSong(String name, String simplename, String singer1,String singer2,int clicks, int isAvailable)
	{
		SongDA.addSong(name, simplename, singer1, singer2, clicks, isAvailable);
	}
	/**
	 * 删除歌曲
	 * @param name 歌曲名，通过歌曲名删除文件
	 */
	public void deleteSong(String name)
	{
		SongDA.deleteSong(name);
	}	
	/**
	 * 按不完全歌曲名查找歌曲，返回歌曲列表
	 * @param subname  不完全歌曲名
	 * @return         返回歌曲列表
	 */
	public List<Song> querySongBySubname(String subname)
	{
		return SongDA.querySongBySubname(subname);
	}		
	/**
	 * 按歌曲名简写查找歌曲，返回歌曲列表
	 * @param simplename  歌曲名称简写
	 * @return           返回歌曲列表
	 */
	public List<Song> querySongBySimpleName(String simplename)
	{
		return SongDA.querySongBySimpleName(simplename);
	}	
	/**
	 * 按歌曲点击次数高低顺序返回歌曲列表
	 * @return  歌曲列表
	 */
	public List<Song> querySongByClicks()
	{
		return SongDA.querySongByClicks();
	}	
	/**
	 * 按网络（方便扩展）歌曲点击次数和用户点击次数之和的高低顺序返回歌曲列表
	 * @return  歌曲列表
	 */
	public List<Song> querySongByTwoClicks()
	{
		return SongDA.querySongByTwoClicks();
	}	
	/**
	 * 按用户点击次数高低顺序返回歌曲列表
	 * @return  歌曲列表
	 */
	public List<Song> querySongByUserClicks()
	{
		return SongDA.querySongByUserClicks();
	}	
	/**
	 * 根据歌手名查找歌曲ID
	 * @param name 歌手名
	 * @return
	 */
	public List<Song> querySongBySingerName(String name)
	{
		return SongDA.querySongBySingerName(name);
	}		
	/**
	 * 从歌曲列表中检索歌曲名简写的歌曲子列表
	 * @param songlist 歌曲列表
	 * @param simplename 歌曲名简写
	 * @return
	 */
	public List<Song> findSubSongList(List<Song> songlist,String simplename)
	{
		return SongDA.findSubSongList(songlist, simplename);
	}	
	/**
	 * 获取歌曲总数
	 * @return
	 */
	public int getSongCount()
	{
		return SongDA.getSongCount();
	}
	/**
	 * 按顺序拿取十首歌曲
	 * @param page 页面编号
	 * @return
	 */
	public List<Song> findTenSong(int page)
	{
		return SongDA.findTenSong(page);
	}
	/**
	 * 用歌曲名简写搜索并按顺序拿取十首歌曲
	 * @param simplename 歌曲名简写
	 * @param page 页面编号
	 * @return
	 */
	public List<Song> findTenSongBySimpleName(String simplename,int page)
	{
		return SongDA.findTenSongBySimpleName(simplename, page);
	}
	/**
	 * 按歌曲点击次数和用户点击次数之和的高低顺序返回歌曲列表
	 * @param page 页面编号
	 * @return
	 */
	public List<Song> queryTenSongByTwoClicks(int page)
	{
		return SongDA.queryTenSongByTwoClicks(page);
	}	
	/**
	 * 获取按指定歌曲名简写匹配到的歌曲总数
	 * @param simplename 歌曲名简写
	 * @return
	 */
	public int getSongCountBySimpleName(String simplename)
	{
		return SongDA.getSongCountBySimpleName(simplename);
	}
	/**
	 * 获取按指定歌曲名简写匹配到的歌曲总页数（每页10条)
	 * @param simplename 歌曲名简写
	 * @return
	 */
	public int getSongPageBySimpleName(String simplename)
	{
		int count = SongDA.getSongCountBySimpleName(simplename);
		int page ;
		if(count%10 == 0){
			page= count/10;
		} else {
			page = count/10+1;
		}
		return page;
	}
	/**
	 * 获取榜单排行列表的总页数（每页10条)
	 * @return
	 */
	public int getSongPage()
	{
		return SongDA.getSongPage();
	}
	/**
	 * 根据歌手名获取10首歌曲
	 * @return
	 */
	public List<Song> findTenSongBySinger(String name,int page)
	{
		return SongDA.findTenSongBySinger(name,page);
	}
	/**
	 * 根据歌手名获取包含该歌手的歌曲总数
	 * @return
	 */
	public int getSongCountBySigner(String name)
	{
		return SongDA.getSongCountBySigner(name);
	}
	/**
	 * 根据歌手名和歌曲名简写搜索相关歌曲总数
	 * @param singer 歌手名
	 * @param simplename 歌曲名简写
	 * @param page 页码
	 */
	public int getSongCountBySgAndSn(String singer,String simplename)
	{
		return SongDA.getSongCountBySgAndSn(singer, simplename);
	}
	
	public int getSongPageBySgAndSn(String singer,String simplename){
		int i=getSongCountBySgAndSn(singer,simplename);
		int page ;
		if(i%10 == 0 ){
			page = i/10;
		} else {
			page = i/10+1;
		}
		return page;
	}
	/**
	 * 根据歌手名和歌曲名简写获取10首歌曲
	 */
	public  List<Song> findTenSongBySgAndSn(String singer,String simplename,int page)
	{
		return SongDA.findTenSongBySgAndSn(singer, simplename, page);
	}
	//根据歌曲ID获取歌曲
	public Song findSongById(int songid)
	{
		return SongDA.findSongById(songid);
	}
}
