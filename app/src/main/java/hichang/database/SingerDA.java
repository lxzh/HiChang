package hichang.database;

import hichang.Song.Singer;
import hichang.database.DataBase;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Singer<br/>
 * 对数据库访问的数据访问类
 */
public class SingerDA {
	/**
	 * HiDataBase对象
	 */
	private static DataBase databaseHelper;
	/**
	 * 对数据库操作的对象
	 */
	private static SQLiteDatabase db;
	/**
	 * SingerDA的构造函数
	 */
	public SingerDA() {   	
	}
	/**
	 * 连接数据库
	 * @param nowDB DataBase对象参数
	 */
	public static void connectDB(DataBase nowDB){
		databaseHelper=nowDB;
	}
	///歌手表的增删查改
		/**
		 * 添加歌手
		 * @param name 歌手名
		 * @param simplename 歌手名首字母
		 * @param gender   歌手性别
		 */
	public static void addSinger(String name,String simplename,String gender)
	{
		db=databaseHelper.getWritableDatabase();
		db.beginTransaction();
		try
		{
			db.execSQL("insert into TableSinger(Name,SimpleName,Gender) values(?,?,?)", 
					new Object[] { name, simplename,gender});
			db.setTransactionSuccessful();
		} 
		catch (Exception e) 
		{ } 
		db.endTransaction();
	}		
	/**
	 * 按歌手名查找歌手信息
	 */
	public static Singer querySingerByName(String name)
	{
		db=databaseHelper.getReadableDatabase();
		Singer singer = new Singer();
		Cursor result=db.rawQuery("select * from SINGER where Name like '"+name+"' order by Name", null);
		result.moveToFirst();
		if(!result.isAfterLast())
		{
			singer.setiD((result.getInt(0)));
			singer.setName((result.getString(1)));
			singer.setSimpleName(result.getString(2));
			singer.setGender((result.getString(3)));
		}
		if(result!=null)result.close();
		db.close();
		result=null;
		return singer;
	}
	/**
	 * 按不完全歌手名查找歌手,返回歌手列表
	 * @param subName 不完全歌手名
	 * @return 返回歌手列表
	 */
	public static List<Singer> querySingerBySubName(String subName)
	{
		db=databaseHelper.getReadableDatabase();
		List<Singer> data = new ArrayList<Singer>();
		Cursor result=db.rawQuery("select * from SINGER where Name like '"+subName+"%' order by Name", null);
		result.moveToFirst();
		while(!result.isAfterLast())
		{
			Singer singer = new Singer();
			singer.setiD((result.getInt(0)));
			singer.setName((result.getString(1)));
			singer.setSimpleName(result.getString(2));
			singer.setGender((result.getString(3)));
			data.add(singer);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}	
	/**
	 * 按歌手名简写查找歌手，返回歌手列表
	 * @param simplename 歌手名简写
	 * @return 歌手列表
	 */
	public static List<Singer> querySingerBySimpleName(String simplename)
	{
		List<Singer> data = new ArrayList<Singer>();
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("select * from SINGER where SimpleName like '"+simplename+"' order by Name desc", null);
		result.moveToFirst(); 
		while(!result.isAfterLast())
		{
			Singer singer = new Singer();
			singer.setiD((result.getInt(0)));
			singer.setName((result.getString(1)));
			singer.setSimpleName(result.getString(2));
			singer.setGender((result.getString(3)));
			data.add(singer);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}		
	/**
	 * 根据性别返回歌手列表
	 * @param gender 歌手性别
	 * @return 歌手列表
	 */
	public static List<Singer> querySingerByGender(String gender)
	{
		db=databaseHelper.getReadableDatabase();
		List<Singer> data=new ArrayList<Singer>();
		Cursor result=db.rawQuery("select * from SINGER where Gender like ? order by Name", new String[]{gender});
		result.moveToFirst();
		while(!result.isAfterLast()){
			Singer singer = new Singer();
			singer.setiD((result.getInt(0)));
			singer.setName((result.getString(1)));
			singer.setSimpleName(result.getString(2));
			singer.setGender((result.getString(3)));
			data.add(singer);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;//未找到返回空
		return data;
	}		
	/**
	 * 返回所有歌手
	 * @return 返回所有歌手
	 */
	public static List<Singer> queryAllSinger()
	{
		db=databaseHelper.getReadableDatabase();
		List<Singer> data=new ArrayList<Singer>();
		Cursor result=db.rawQuery("select * from SINGER order by Name", null);
		result.moveToFirst();
		while(!result.isAfterLast()){
			Singer singer = new Singer();
			singer.setiD((result.getInt(0)));
			singer.setName((result.getString(1)));
			singer.setSimpleName(result.getString(2));
			singer.setGender((result.getString(3)));
			data.add(singer);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;//未找到返回空
		return data;
	}	
	/**
	 * 歌手名简写返回歌手列表的子列表
	 * @param singerlist 歌手原始列表
	 * @param simplename 歌手名简写
	 * @return 歌手列表的子列表
	 */
	public static List<Singer> querySubSingerList(List<Singer> singerlist,String simplename)
	{
		List<Singer> data=new ArrayList<Singer>();
		if(singerlist.isEmpty())return null;
		for(int i=0;i<singerlist.size();i++)
		{
			if(singerlist.get(i).getSimpleName().indexOf(simplename)==0)
				data.add(singerlist.get(i));
		}
		return data;
	}
	/**
	 * 获取歌手总数
	 * @return
	 */
	public static int getSingerCount()
	{
		int count=0;
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("SELECT count(ID) from SINGER", null);
		result.moveToFirst();
		if(result.isNull(0))count= 0;
		else count=result.getInt(0);
		if(result!=null)result.close();
		db.close();
		return count;
	}
	/**
	 * 按顺序拿取九个歌手信息
	 * @param page 页面编号
	 * @return
	 */
	public static List<String> findNineSinger(int page)
	{
		db=databaseHelper.getReadableDatabase();
		List<String> data = new ArrayList<String>();
		Cursor result;
		result=db.rawQuery("select Name from SINGER where ID not in (select ID from SINGER limit "+page+"*9-9)  limit 9", null);
		result.moveToFirst();
		while(!result.isAfterLast()){
			data.add(result.getString(0));
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}	
	/**
	 * 按顺序拿取九个歌手信息
	 * @param page 页面编号
	 * @return
	 */
	public static List<Singer> queryNineSinger(int page)
	{
		db=databaseHelper.getReadableDatabase();
		List<Singer> data=new ArrayList<Singer>();
		Cursor result;
		result=db.rawQuery("select * from SINGER where ID not in (select ID from SINGER limit "+page+"*9-9)  limit 9", null);
		result.moveToFirst();
		while(!result.isAfterLast()){
			Singer singer = new Singer();
			singer.setiD((result.getInt(0)));
			singer.setName((result.getString(1)));
			singer.setSimpleName(result.getString(2));
			singer.setGender((result.getString(3)));
			data.add(singer);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}	
	/**
	 * 根据歌手名简写获取歌手总数
	 * @return
	 */
	public static int getSingerCountBySimpleName(String simplename)
	{
		int count=0;
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("SELECT count(ID) from SINGER where SimpleName like '"+simplename+"%'", null);
		result.moveToFirst();
		if(result.isNull(0))count= 0;
		else count=result.getInt(0);
		if(result!=null)result.close();
		db.close();
		return count;
	}
	/**
	 * 按歌手名简写拿取九个歌手名
	 * @param simplename 歌手名简写
	 * @param page 页面编号
	 * @return
	 */
	public static List<String> findNineSingerBySN(String simplename,int page)
	{
		db=databaseHelper.getReadableDatabase();
		List<String> data = new ArrayList<String>();
		Cursor result;
		result=db.rawQuery("select Name from SINGER where SimpleName like '"+simplename+"%' and ID not in (select ID from SINGER where SimpleName like '"+simplename+"%' limit "+page+"*9-9)  limit 9", null);
		result.moveToFirst();
		while(!result.isAfterLast()){
			data.add(result.getString(0));
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}
	/**
	 * 按歌手名简写拿取九个歌手
	 * @param simplename 歌手名简写
	 * @param page 页面编号
	 * @return
	 */
	public static List<Singer> queryNineSingerBySN(String simplename,int page)
	{
		db=databaseHelper.getReadableDatabase();
		List<Singer> data = new ArrayList<Singer>();
		Cursor result;
		result=db.rawQuery("select * from SINGER where SimpleName like '"+simplename+"%' and ID not in (select ID from SINGER where SimpleName like '"+simplename+"%' limit "+page+"*9-9)  limit 9", null);
		result.moveToFirst();
		while(!result.isAfterLast()){
			Singer singer = new Singer();
			singer.setiD((result.getInt(0)));
			singer.setName((result.getString(1)));
			singer.setSimpleName(result.getString(2));
			singer.setGender((result.getString(3)));
			data.add(singer);
			result.moveToNext();
		}
		if(result!=null)result.close();
		db.close();
		if(data.isEmpty())return null;
		return data;
	}
}
