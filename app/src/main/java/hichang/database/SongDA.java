package hichang.database;

import hichang.Song.Song;
import hichang.database.DataBase;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SongDA<br/>
 * 对数据库访问的类
 */
public class SongDA {
	private static DataBase databaseHelper;
	private static SQLiteDatabase db;
	public SongDA() {   	
	}	
	public static void connectDB(DataBase nowDB){
		databaseHelper=nowDB;
	}
	///歌曲表的增删查改
		/**
		 * 添加歌曲
		 * @param name   歌曲名
		 * @param simplename 歌曲每个字首字母
		 * @param singerOne 一个演唱者的
		 * @param singerTwo 其他演唱者的名字
		 * @param clicks    点击次数
		 * @param isAvailable 是否可用
		 */
	public static void addSong(String name, String simplename, String singer1,String singer2,int clicks, int isAvailable)
	{
		db = databaseHelper.getWritableDatabase(); 
		db.beginTransaction();
		try 
		{
			db.execSQL("insert into SONG(Name,SimpleName,Singer1,Singer2,Clicks,IsAvailable) values(?,?,?,?,?,?)", 
					new Object[] { name, simplename,singer1,singer2,isAvailable});
			db.setTransactionSuccessful();
		}
		catch (Exception e)
		{ }
		db.endTransaction();
	}				
		
	/**
	 * 删除歌曲
	 * @param name 歌曲名
	 */
	public static void deleteSong(String name)
	{
		db = databaseHelper.getWritableDatabase(); 
		db.execSQL("delete from SONG where Name=?", new Object[]{name});
		db.close();
	}			
	/**
	 * 按不完全歌曲名查找歌曲，返回歌曲列表
	 * @param subname 不完全歌曲名
	 * @return  歌曲列表
	 */
	public static List<Song> querySongBySubname(String subname)
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("select * from SONG where Name like '"+subname+"%' order by Name", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}		
	/**
	 * 按歌曲名简写查找歌曲，返回歌曲列表
	 * @param simplename 歌曲名的简写
	 * @return 歌曲列表
	 */
	public static List<Song> querySongBySimpleName(String simplename)
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("select * from SONG where SimpleName like '"+simplename+"%' order by Name DESC", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}			
	/**
	 * 按歌曲点击次数高低顺序返回歌曲列表
	 * @return 歌曲列表
	 */
	public static List<Song> querySongByClicks()
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("select * from SONG order by Clicks DESC", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}	
	/**
	 * 按歌曲点击次数和用户点击次数之和的高低顺序返回歌曲列表
	 * @return 歌曲列表
	 */
	public static List<Song> querySongByTwoClicks()
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("SELECT SONG.SongID,SONG.Name,SONG.SimpleName,SONG.Singer1,SONG.Singer2,SONG.Clicks+USER.Clicks Clicks,SONG.IsAvailable FROM SONG,USER WHERE USER.RecordName IS NULL AND SONG.SongID=USER.SongID ORDER BY SONG.Clicks+USER.Clicks DESC", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}
	/**
	 * 按用户点击次数高低顺序返回歌曲列表
	 * @return 歌曲列表
	 */
	public static List<Song> querySongByUserClicks()
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("SELECT SONG.SongID,SONG.Name,SONG.SimpleName,SONG.Singer1,SONG.Singer2,USER.Clicks,SONG.IsAvailable FROM SONG,USER WHERE SONG.SongID=USER.SongID AND USER.RecordName ISNULL ORDER BY USER.Clicks DESC", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}			
	/**
	 * 根据歌手名查找歌曲ID
	 * @param name 歌手名
	 * @return 歌曲列表
	 */
	public static List<Song> querySongBySingerName(String name)
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("select * from SONG where Singer1 like '"+name+"' or Singer2 like '"+name+"%' or Singer2 like '%"+name+"'", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}		
	/**
	 * 返回所有歌曲
	 * @return 歌曲列表
	 */
	public static List<Song> findAllSong()
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("select * from SONG order by Clicks desc", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}
	/**
	 * 从当前歌曲列表中检索歌曲名简写的歌曲子列表
	 * @param songlist   当前list
	 * @param simplename 歌曲简写
	 * @return
	 */
	public static List<Song> findSubSongList(List<Song> songlist,String simplename)
	{
		List<Song> data=new ArrayList<Song>();
		if(songlist.isEmpty())return null;
		for(int i=0;i<songlist.size();i++)
		{
			if(songlist.get(i).getSimpleName().indexOf(simplename)==0)
				data.add(songlist.get(i));
		}
		return data;
	}
	/**
	 * 获取歌曲总数
	 * @return
	 */
	public static int getSongCount()
	{
		int count=0;
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("SELECT count(SongID) from SONG", null);
		result.moveToFirst();
		if(result.isNull(0))count= 0;
		else count=result.getInt(0);
		if(result!=null)result.close();
		db.close();
		return count;
	}
	/**
	 * 按顺序拿取十首歌曲
	 * @param page 页面编号
	 * @return
	 */
	public static List<Song> findTenSong(int page)
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("select * from SONG where SongID not in (select SongID from SONG limit "
				+page+"*10-10) limit 10", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}
	/**
	 * 用歌曲名简写搜索并按顺序拿取十首歌曲
	 * @param simplename  歌曲名简写
	 * @param page 页面编号
	 * @return
	 */
	public static List<Song> findTenSongBySimpleName(String simplename,int page)
	{
		if(page<1)return null;
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("select * from SONG where SimpleName like '"+simplename+"%' and " +
				"SongID not in (select SongID from SONG where SimpleName like '"+simplename+"%' " +
						"limit "+page+"*10-10) limit 10", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}
	/**
	 * 按歌曲点击次数和用户点击次数之和的高低顺序返回歌曲列表
	 * @param page 页面编号
	 * @return
	 */
	public static List<Song> queryTenSongByTwoClicks(int page)
	{
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("SELECT SONG.SongID,SONG.Name,SONG.SimpleName,SONG.Singer1,SONG.Singer2," +
				"SONG.Clicks+USER.Clicks Clicks,SONG.IsAvailable FROM SONG,USER WHERE USER.RecordName IS NULL " +
				"AND SONG.SongID=USER.SongID AND SONG.SongID NOT IN (SELECT SONG.SongID FROM SONG,USER WHERE "+
				"USER.RecordName IS NULL AND SONG.SongID=USER.SongID LIMIT "+page+"*10-10 )"+
				"ORDER BY Clicks DESC LIMIT 10", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}		
	/**
	 * 获取按指定歌曲名简写匹配到的歌曲总数
	 * @param simplename 歌曲名简写
	 * @return
	 */
	public static int getSongCountBySimpleName(String simplename)
	{
		int count=0;
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("SELECT count(SongID) from SONG where SimpleName like '"+simplename+"%'", null);
		result.moveToFirst();
		if(result.isNull(0))count= 0;
		else count=result.getInt(0);
		if(result!=null)result.close();
		db.close();
		return count;
	}
	/**
	 * 获取按指定歌曲名简写匹配到的歌曲总页数（每页10条)
	 * @param simplename 歌曲名简写
	 * @return
	 */
	public static int getSongPageBySimpleName(String simplename)
	{
		int count=getSongCountBySimpleName(simplename);
		if(count==0)return 0;
		int page=(count%10==0)?(count/10):(count/10+1);
		return page;
	}
	
	/**
	 * 获取榜单排行列表的总页数（每页10条)
	 * @return
	 */
	public static int getSongPage()
	{
		int count=getSongCount();
		if(count==0)return 0;
		int page=(count%10==0)?(count/10):(count/10+1);
		return page;
	}
	/**
	 * 根据歌手名获取10首歌曲
	 * @return
	 */
	public static List<Song> findTenSongBySinger(String name,int page)
	{
		if(page<0)return null;
		if(page>getSongCountBySigner(name)*10)return null;
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("SELECT * FROM SONG WHERE Singer1 LIKE '"+name+"%' AND SongID NOT IN (SELECT SongID FROM SONG WHERE Singer1 LIKE '"+name+"%' OR Singer2 LIKE '"+name+"%' OR Singer2 LIKE '%"+name+"' LIMIT "+page+"*10-10) OR Singer2 like '%"+name+"' OR Singer2 LIKE '%,"+name+"' LIMIT 10 ", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}
	/**
	 * 根据歌手名获取包含该歌手的歌曲总数
	 * @return
	 */
	public static int getSongCountBySigner(String name)
	{
		int count=0;
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("SELECT count(SongID) from SONG where Singer1 like '"+name+"%' or Singer2 like '"+name+"%' or Singer2 like '%"+name+"'", null);
		result.moveToFirst();
		if(result.isNull(0))count= 0;
		else count=result.getInt(0);
		if(result!=null)result.close();
		db.close();
		return count;
	}
	/**
	 * 根据歌手名和歌曲名简写搜索相关歌曲总数
	 * @param singer 歌手名
	 * @param simplename 歌曲名简写
	 * @param page 页码
	 */
	public static int getSongCountBySgAndSn(String singer,String simplename)
	{
		int count=0;
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("SELECT count(SongID) from SONG where SimpleName like '"+simplename+"%'and SongID in (select SongID where Singer1 like '"+singer+"%' or Singer2 like '"+singer+"%' or Singer2 like '%"+singer+"')", null);
		result.moveToFirst();
		if(result.isNull(0))count= 0;
		else count=result.getInt(0);
		if(result!=null)result.close();
		db.close();
		return count;
	}
	
	/**
	 * 根据歌手名和歌曲名简写获取10首歌曲
	 */
	public static List<Song> findTenSongBySgAndSn(String singer,String simplename,int page)
	{
		if(page<0)return null;
		if(page>getSongCountBySgAndSn(singer,simplename)*10)return null;
		db=databaseHelper.getReadableDatabase();
		List<Song> data = new ArrayList<Song>();
		Cursor result=db.rawQuery("SELECT * FROM SONG WHERE SimpleName LIKE '"+simplename+"%' " +
				"AND SongID NOT IN(SELECT SongID FROM SONG WHERE SimpleName LIKE '"+simplename+"%' " +
				"AND SongID IN (SELECT SongID FROM SONG WHERE Singer1 LIKE '"+singer+"%' OR Singer2 LIKE '"+singer+"%' " +
				"OR Singer2 LIKE '%"+singer+"') LIMIT "+page+"*10-10)AND SongID IN (" +
				"SELECT SongID FROM SONG WHERE  Singer1 LIKE '"+singer+"%' OR Singer2 LIKE '"+singer+"%' " +
				"OR Singer2 LIKE '%"+singer+"') LIMIT 10", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Song song = new Song();
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
			data.add(song);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}
	//根据歌曲ID获取歌曲
	public static Song findSongById(int songid)
	{
		Song song=new Song();
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("select * from SONG where SongID ="+songid, null);
		result.moveToFirst();
		if(!result.isNull(0))
		{
			song.setSongID(result.getInt(0));
			song.setName(result.getString(1));
			song.setSimpleName(result.getString(2));
			song.setSinger1(result.getString(3));
			if(!result.isNull(4))song.setSinger2(result.getString(4));
			song.setClicks(result.getInt(5));
			song.setIsAvailable(result.getInt(6));
			song.setMusicPath(song.getSongID());
			song.setAccomanimentPath(song.getSongID());
			song.setSongLyricUrl(song.getSongID());
		}
		if(result!=null)result.close();
		else song=null;
		return song;
	}
}
