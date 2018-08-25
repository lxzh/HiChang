package hichang.Song;

import java.util.ArrayList;

import android.R.integer;

public class SongList {

	ArrayList<Song> songList = new ArrayList<Song>();

	public SongList() {

	}
	
	public ArrayList<Song> getSongList() {
		return songList;
	}

	// 预约歌曲
	public void bookSong(Song song) {
		if (getBookedNum(song.getSongID()) == -1) {
			song.bookNum = songList.size() + 1;
			songList.add(song);
		}
	}

	// 下一首
	public Song nextSong() {
		if(songList.size()==0){
			return null;
		}
		Song nextSong=songList.get(0);
		songList.remove(0);
		for (int i = 0; i < songList.size(); i++) {
			songList.get(i).bookNum--;
		}
		return nextSong;
	}

	// 置顶
	public void SetToFirst(int page, int which) {
		int num = (page - 1) * 10 + which;

		for (int i = 0; i < num; i++) {
			songList.get(i).bookNum++;
		}
		songList.add(0, songList.get(num));
		songList.get(0).bookNum=1;
		songList.remove(num + 1);
	}

	// 获得当页的歌曲
	public ArrayList<Song> getSongsByPage(int page) {
		if (page <= 0) {
			return null;
		} else if (page < getPageNum()) {
			ArrayList<Song> nowPageSongs=new ArrayList<Song>();
			for(int i=0;i<10;i++){
				nowPageSongs.add(songList.get((page - 1) * 10+i));
			}
			return nowPageSongs;
		} else if (page == getPageNum()) {
			ArrayList<Song> nowPageSongs=new ArrayList<Song>();
			for(int i=0;i<songList.size()%10;i++){
				nowPageSongs.add(songList.get((page - 1) * 10+i));
			}
			return nowPageSongs;
		} else {
			return null;
		}
	}

	// 获得页数
	public int getPageNum() {
		if (songList.size() % 10 == 0) {
			return songList.size() / 10;
		} else {
			return songList.size() / 10 + 1;
		}
	}

	public int getBookedNum(int songId) {
		for (int i = 0; i < songList.size(); i++) {
			if (songId == songList.get(i).getSongID()) {
				return songList.get(i).bookNum;
			}
		}
		return -1;
	}
	
	public int size(){
		return songList.size();
	}
}
