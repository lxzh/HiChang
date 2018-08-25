/*
 * .ReadText.java
 * 包含类：ReadText
 */
package hichang.Song;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.FileReader;

/**
 * 类名：ReadText<br/>
 * 读取歌词文件并对文件进行解析
 */
public class ReadText {
	/**
	 * 读取歌词文件后要返回的所有句子
	 */
	private ArrayList<Sentence> allSentences = null;
	/**
	 * 歌词配置文件的路径
	 */
	private String url;
	/**
	 * 该首歌中出现的歌词音高的最小值
	 */
	public int min;
	/**
	 * 该歌中出现的歌词音高的最大值
	 */
	public int max;

	/**
	 * 类的构造函数
	 * 
	 * @param url
	 *            歌词配置文件的路径
	 */
	public ReadText(String url) {
		// TODO Auto-generated constructor stub
		this.url = url;
		max = 0;
		min = 200;
	}

	/**
	 * 读取歌词配置文件并解析
	 * 
	 * @return 句子HiSentence的集合
	 */
	public ArrayList<Sentence> ReadData() {
		allSentences = new ArrayList<Sentence>();
		try {
			FileReader reader = new FileReader(url);

			BufferedReader br = new BufferedReader(reader);// 读文件
			String rowString;

			while ((rowString = br.readLine()) != null)// 每一行读一次
			{
				allSentences.add(anaylise(rowString));//
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return allSentences;// 返回一句话
	}

	/**
	 * 读取歌词的配置文件并且进行解析
	 * 
	 * @param in
	 * @return
	 */
	public ArrayList<Sentence> ReadData(InputStream in) {
		allSentences = new ArrayList<Sentence>();
		try {
			InputStreamReader inr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inr);// 读文件
			String rowString;

			while ((rowString = br.readLine()) != null)// 每一行读一次
			{
				allSentences.add(anaylise(rowString));//
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return allSentences;// 返回一句话
	}

	/**
	 * 解析从文件中读取的数据
	 * 
	 * @param sentence
	 *            读取的要被解析的数据
	 * @return Sentence对象
	 */
	private Sentence anaylise(String sentence)// 解析从文件中读取的数据
	{
		Sentence mysentence = null;
		String aString;

		Pattern pattern = Pattern.compile("\\[([^\\]]*)]");// 正则表达式匹配该句的时间和长度
		Matcher matcher = pattern.matcher(sentence);

		while (matcher.find()) {
			aString = matcher.group(0);

			String[] strs = aString.split(",");
			String starttime = strs[0].replaceAll("[\\[]", "");
			String lasttime = strs[1].replaceAll("[\\]]", "");
			mysentence = new Sentence(Integer.parseInt(starttime),
					Integer.parseInt(lasttime));
			anayliseChapter(mysentence, sentence); // 解析每个字
		}
		return mysentence;

	}

	// /<parameter=sentence>句子类
	// /<parameter=string>每个字组成的字符串
	private void anayliseChapter(Sentence sentence, String mysString) {
		int ChapofStart = 0;
		int Chapoflast = 0;
		int ChapofHigh = 0;
		String text = "";

		//
		Pattern pattern = Pattern.compile("<\\d*,\\d*,\\d*>[^<]");
		Matcher matcher = pattern.matcher(mysString);
		while (matcher.find()) {
			String aString = matcher.group(0);
			String mystring = aString.replace("<", ",").replace(">", ",");
			String[] strs = mystring.split(",");
			for (int j = 0; j < strs.length; j++) {
				switch (j) {
				case 0:
					ChapofStart = Integer.parseInt(strs[j + 1]);// 启示时间
					break;
				case 1:
					Chapoflast = Integer.parseInt(strs[j + 1]);// 持续时间
					break;
				case 2:
					ChapofHigh = Integer.parseInt(strs[j + 1]);// 音高
					max(ChapofHigh);
					min(ChapofHigh);// 计算每首歌的最大音高和最小音高
					break;
				case 3:
					text = strs[j + 1];// 字
					break;
				default:
					break;
				}
			}

			sentence.AddChapter(ChapofStart, Chapoflast, ChapofHigh, text); // 把改字添加的sentece中
			sentence.AddAnotherChapter(ChapofStart, Chapoflast, ChapofHigh,
						text);
			
		}
	}

	/**
	 * 计算每首歌的最大音高
	 * 
	 * @param high
	 */
	private void max(int high) {
		if (high > max) {
			max = high;
		}
	}

	/**
	 * 计算每首歌的最小音高
	 * 
	 * @param high
	 */
	private void min(int high) {
		if (high < min) {
			min = high;
		}
	}

}
