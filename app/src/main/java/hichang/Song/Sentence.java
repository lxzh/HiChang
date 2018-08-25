/*
 * .Sentence.java
 * 包含的类:Sentence
 */
package hichang.Song;

import java.util.ArrayList;
import hichang.Song.Chapter;
/**
 * 类名:HiSentence<br/>
 * 对句子的属性进行初始化，包括句子的开始时间，据字的持续时间，句子内容以及唱该句的得分
 */
public class Sentence {
	/**
	 * 用于记录该句话的每个字的数组,这里的歌词内容可能包含‘~’，因为要显示在曲线区域
	 */
	public ArrayList<Chapter> mychapter=new ArrayList<Chapter>();
	/**
	 * 歌词显示区域的歌词内容，此部分不包含从文件中解析出来的‘~’
	 */
	public ArrayList<Chapter> anotherChapters=new ArrayList<Chapter>();
	/**
	 * 句子的开始时间
	 */
	public int StartTimeofThis;
	/**
	 * 句子的持续时间
	 */
	public int LastTimeofThis;
	/**
	 * 起始字符串
	 */
	public String text="";
	/**
	 * 句子的得分
	 */
	public int Score;
	/**
     * HiSentence的构造函数
     * @param startTime句子的开始时间 
     * @param lastTime句子的持续时间
     */
	public Sentence(int startTime,int lastTime){
		this.StartTimeofThis = startTime;
		this.LastTimeofThis = lastTime;
	}
	/**
     * 无参数的构造函数
     */
	public Sentence(){
		
	}
	/**
	 * 句子的形成过程中的添加字
	 * @param start 字相对于该句的起始时间
	 * @param last  字的持续时间
	 * @param high  字的音高
	 * @param text  字内容
	 */
	public void AddChapter(int start,int last,int high,String text){
		Chapter aChapter=new Chapter(start, last, high, text);
		if(!text.equals("~")){
			this.text += text;
		}
		mychapter.add(aChapter);
		
	}
	/**
	 * 句子的形成过程中的添加字,不包含~
	 * @param start 字相对于该句的起始时间
	 * @param last  字的持续时间
	 * @param high  字的音高
	 * @param text  字内容
	 */
	public void AddAnotherChapter(int start,int last,int high,String text){
		if(text.equals("~")){
			anotherChapters.get(anotherChapters.size()-1).ChpofLast+=last;
		} else {
			Chapter aChapter=new Chapter(start, last, high, text);
			anotherChapters.add(aChapter);
		}
		
	}
	/**
	 * 获取index索引处字的相对开始时间
	 * @param index 索引
	 * @return 返回相对开始时间
	 */
	public int getChapterStart(int index){
		return mychapter.get(index).ChpofStart;
	}
	
	public int getChapterHigh(int index){
		return mychapter.get(index).ChpofHigh;
	}

	public int getChapterLast(int index){
		return mychapter.get(index).ChpofLast;
	}
	
	public int getChaptetCount(){
	    return mychapter.size();
	}
	
	public String getChapterText(int index){
		return mychapter.get(index).text;
	}


}
