package hichang.Song;

import hichang.database.DataBase;
import hichang.database.UserDA;

import java.util.List;

import android.content.Context;

/**
 * 定义User类<br/>
 * 定义一个User的PD类，三层结构的第二层，也方便与DA类的交互
 */
public class User {
	/**
	 * 用户ID
	 */
	private int iD;
	/**
	 * 录音名
	 */
	private String recordName;
	/**
	 * 第一名名字
	 */
	private String firstName;
	/**
	 * 第二名名字
	 */
	private String secondName;
	/**
	 * 第三名名字
	 */
	private String thirdName;
	/**
	 * 第一名得分
	 */
	private int firstScore;
	/**
	 * 第二名得分
	 */
	private int secondScore;
	/**
	 * 第三名得分
	 */
	private int thirdScore;
	/**
	 * 用户点击次数
	 */
	private int clicks;
	/**
	 * 歌曲ID
	 */
	private int songID;
	/**
	 * DataBase对象，用于与数据库的交互中
	 */
	private DataBase databaseHelper;
	//一系列与HiUser属性相关的setter和getter方法
	public int getID() {
		return iD;
	}
	public void setID(int iD) {
		this.iD = iD;
	}
	public String getRecordName() {
		return recordName;
	}
	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getThirdName() {
		return thirdName;
	}
	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}
	public int getFirstScore() {
		return firstScore;
	}
	public void setFirstScore(int firstScore) {
		this.firstScore = firstScore;
	}
	public int getSecondScore() {
		return secondScore;
	}
	public void setSecondScore(int secondScore) {
		this.secondScore = secondScore;
	}
	public int getThirdScore() {
		return thirdScore;
	}
	public void setThirdScore(int thirdScore) {
		this.thirdScore = thirdScore;
	}
	public int getClicks() {
		return clicks;
	}
	public void setClicks(int clicks) {
		this.clicks = clicks;
	}
	public int getSongID() {
		return songID;
	}
	public void setSongID(int songID) {
		this.songID = songID;
	}
	/**
	 * HiUser的构造方法
	 */
	public User()
	{
		
	}
	/**
	 * HiUser的构造方法，方便连接数据库
	 * @param context 设备上下文
	 */
	public User(Context context)
	{
		databaseHelper=new DataBase(context);
		UserDA.connectDB(databaseHelper);
	}
	/**
	 * 添加用户使用记录1-录音操作,只加录音名与歌曲ID
	 * @param recordname 录音名
	 * @param songid     歌曲id
	 */
	public void addRecord(String recordname,int songid)
	{
		UserDA.addRecord(recordname, songid);
	}
	/**
	 * 修改第一名得分记录
	 * @param id  拿到此得分的用户ID
	 * @param name 拿到此得分用户名
	 * @param score 分数
	 */
	public void alterFirst(int id,String name,int score)
	{
		UserDA.alterFirst(id, name, score);
	}	
	/**
	 * 修改第二名得分记录
	 * @param id 拿到此得分的用户ID
	 * @param name 拿到此得分用户名
	 * @param score 分数
	 */
	public void alterSecond(int id,String name,int score)
	{
		UserDA.alterSecond(id, name, score);
	}	
	/**
	 * 修改第三名得分记录
	 * @param id 拿到此得分的用户ID
	 * @param name 拿到此得分用户名
	 * @param score 分数
	 */
	public void alterThird(int id,String name,int score)
	{
		UserDA.alterThird(id, name, score);
	}	
	/**
	 * 修改点击次数(即点击次数+1),修改不存在录音的记录，即初始记录
	 * @param songid 歌曲ID
	 */
	public void alterClicks(int songid)
	{
		UserDA.alterClicks(songid);
	}	
	/**
	 * 根据歌曲ID获取该歌曲的最高分
	 */
	public int queryFirstScore(int songid)
	{
		return UserDA.queryFirstScore(songid);
	}
	/**
	 * 获取指定歌曲ID的前三名演唱者名字
	 * @param songid  歌曲ID
	 * @return 返回前三名名字的数组
	 */
	public String[] queryThreeName(int songid)
	{
		String[] name=UserDA.queryThreeName(songid);
		return name;
	}	
	/**
	 * 获取指定歌曲ID的前三名得分
	 * @param songid  歌曲ID
	 * @return   返回前三名得分的数组
	 */
	public int[] queryThreeScore(int songid)
	{
		int[] score=UserDA.queryThreeScore(songid);
		return score;
	}	
	/**
	 * 根据歌曲ID获取录音名
	 * @param songid  歌曲ID
	 * @return
	 */
	public String queryRecordName(int songid)
	{
		return UserDA.queryRecordName(songid);
	}
	/**
	 * 根据录音名获取歌曲ID
	 * @param recordname 录音名
	 * @return
	 */
	public int querySongIdByRecordName(String recordname)
	{
		return UserDA.querySongIdByRecordName(recordname);
	}	
	/**
	 * 通过歌曲ID返回User
	 * @param songid 歌曲ID
	 * @return
	 */
	public User queryUserBySongId(int songid)
	{
		return UserDA.queryUserBySongId(songid);
	}	
}
