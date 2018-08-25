/*
 * .Chapter.java
 * 为歌词中一句话中的字建立的类，包含每个字的属性信息
 */
package hichang.Song;
/**
 * 类名:Character
 * 歌词中字的属性信息，包含相对其实时间、字的持续时间、字的音高以及字的内容
 */
public class Chapter{
	/**
     * 该字相对于其在当前该句话中起始时间
     */ 
	 public int ChpofStart;
	 /**
	  * 这个字的持续时间
	  */
	 public int ChpofLast;
	 /**
	  * 字的音高
	  */
	 public int ChpofHigh;
	 /**
	  * 字的内容
	  */
	 public String text;
	 /**
	  * 类的构造函数
	  * @param start 字相对该句的开始时间
	  * @param last  字的持续时间
	  * @param high     字的音高
	  * @param text      字的内容
	  */
	 public Chapter(int start,int last,int high,String text){
		 this.ChpofStart=start;
		 this.ChpofLast=last;
		 this.ChpofHigh=high;
		 this.text=text;
	 }
}