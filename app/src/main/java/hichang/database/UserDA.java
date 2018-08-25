package hichang.database;

import hichang.Song.User;
import hichang.database.DataBase;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * UserDA<br/>
 * User的DA类，与数据库直接交互
 */
public class UserDA {
	private static DataBase databaseHelper;
	private static SQLiteDatabase db;
	/**
	 * HiUserDA 的构造函数
	 */
	public UserDA()
	{
		
	}
	/**
	 * 建立连接
	 * @param dbHelper 从调用方传递的HiDataBase对象
	 */
	public static void connectDB(DataBase nowDB)
	{
		databaseHelper=nowDB;
	}
	// HiUser表的增删查改
	 
	/**
	 * 添加用户使用记录1-录音操作,只加录音名与歌曲ID
	 * @param recordname 录音名
	 * @param songid 歌曲ID
	 */	
	public static void addRecord(String recordname,int songid)
	{
		db = databaseHelper.getWritableDatabase(); 
		db.beginTransaction();
		try 
		{
			db.execSQL("insert into USER(RecordName,SongID) values(?,?)", 
					new Object[] { recordname, songid});
			db.setTransactionSuccessful();
		}
		catch (Exception e)
		{ }
		db.endTransaction();
	}
	/**
	 * 修改第一名分数的信息
	 * @param id 歌曲ID
	 * @param name 用户名
	 * @param score 分数
	 */
	public static void alterFirst(int id,String name,int score)
	{
		db.close();
		db = databaseHelper.getWritableDatabase(); 
		db.execSQL("update USER set FirstName='"+name+"',FirstScore="+score+" where ID="+id);
		db.close();
	}	
	/**
	 * 修改第二名得分记录
	 * @param id 歌曲ID
	 * @param name 用户名
	 * @param score 分数
	 */
	public static void alterSecond(int id,String name,int score)
	{

		db = databaseHelper.getWritableDatabase(); 
		db.execSQL("update USER set SecondName='"+name+"',SecondScore="+score+" where ID="+id);
		db.close();
	}	
	/**
	 * 修改第三名得分记录
	 * @param id 歌曲ID
	 * @param name 用户名
	 * @param score 分数
	 */
	public static void alterThird(int id,String name,int score)
	{
		db = databaseHelper.getWritableDatabase(); 
		db.execSQL("update USER set ThirdName='"+name+"',ThirdScore="+score+" where ID="+id);
		db.close();
	}	
	/**
	 * 修改点击次数(即点击次数+1),修改不存在录音的记录，即初始记录
	 * @param songid 歌曲ID
	 */
	public static void alterClicks(int songid)
	{
		db=databaseHelper.getWritableDatabase();
		db.execSQL("update USER set Clicks=Clicks+1 where SongID="+songid+" and RecordName is null");
		db.close();
	}
	/**
	 * 获取指定歌曲ID的前三名演唱者名字
	 * @param songid 歌曲名
	 * @return  指定歌曲ID的前三名演唱者名字
	 */
	public static String[] queryThreeName(int songid)
	{
		db=databaseHelper.getReadableDatabase();
		String[] name=new String[3];
		Cursor result=db.rawQuery("select FirstName,SecondName,ThirdName from USER where SongID="+songid+" and RecordName is null", null);
		result.moveToFirst();
		if(result.isNull(0))return null;//第一名名字为空意味着未查找到或此歌曲从未演唱过
		else name[0]=result.getString(0);
		if(!result.isNull(1))name[1]=result.getString(1);
		else name[1]="";
		if(!result.isNull(2))name[2]=result.getString(2);
		else name[2]="";
		result.close();
		db.close();
		return name;
	}
	/**
	 * 根据歌曲ID获取该歌曲的最高分
	 */
	public static int queryFirstScore(int songid)
	{
		db=databaseHelper.getReadableDatabase();
		int firstscore=0;
		Cursor result=db.rawQuery("select FirstScore from USER where SongID="+songid+" and RecordName is null", null);
		result.moveToFirst();
		firstscore=result.getInt(0);
		result.close();
		db.close();
		return firstscore;
	}
	/**
	 * 获取指定歌曲ID的前三名得分
	 * @param songid 歌曲ID
	 * @return 指定歌曲ID的前三名得分
	 */
	public static int[] queryThreeScore(int songid)
	{
		db=databaseHelper.getReadableDatabase();
		int[] score=new int[3];
		Cursor result=db.rawQuery("select FirstScore,SecondScore,ThirdScore from USER where SongID="+songid+" and RecordName is null", null);
		result.moveToFirst();
		//FirstScore,SecondScore,ThirdScore 的默认值为0，不用判断是否为空
		score[0]=result.getInt(0);
		score[1]=result.getInt(1);
		score[2]=result.getInt(2);
		result.close();
		db.close();
		return score;
	}	
	/**
	 * 根据歌曲ID获取录音名
	 * @param songid 歌曲ID
	 * @return 录音名
	 */
	public static String queryRecordName(int songid)
	{
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("select RecordName from USER where SongID="+songid+" and RecordName is not null", null);
		result.moveToFirst();
		if(result.isNull(0))return null;
		return result.getString(0);
	}
	/**
	 * 根据录音名获取歌曲ID
	 * @param recordname 录音名
	 * @return 歌曲ID
	 */ 
	public static int querySongIdByRecordName(String recordname)
	{
		db=databaseHelper.getReadableDatabase();
		Cursor result=db.rawQuery("select SongID from USER where RecordName='?'", new String[]{recordname});
		result.moveToFirst();
		if(result.isNull(0))return 0;
		return result.getInt(0);
	}
	/**
	 * 通过歌曲ID返回唱过此首歌的User列表
	 * @param songid 歌曲ID
	 * @return  唱过此首歌的User列表
	 */
	public static User queryUserBySongId(int songid)
	{
		db=databaseHelper.getReadableDatabase();
		User user=new User();
		Cursor result=db.rawQuery("select * from USER where SongID="+songid+" order by ID", null);
		result.moveToFirst();
		if(!result.isAfterLast())
		{
			user.setID(result.getInt(0));
			if(!result.isNull(1))user.setRecordName(result.getString(1));
			else user.setRecordName("");
			if(!result.isNull(2))user.setFirstName(result.getString(2));
			else user.setFirstName("");
			user.setFirstScore(result.getInt(3));
			if(!result.isNull(4))user.setSecondName(result.getString(4));
			else user.setSecondName("");
			user.setSecondScore(result.getInt(5));
			if(!result.isNull(6))user.setThirdName(result.getString(6));
			else user.setThirdName("");
			user.setThirdScore(result.getInt(7));
			user.setClicks(result.getInt(8));
			user.setSongID(result.getInt(9));
		}
		result.close();
		db.close();
		return user;
	}
}
