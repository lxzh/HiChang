package hichang.test;

import hichang.Song.Song;
import hichang.Song.SongList;

public class SongListTest {
	public static void main(String[] args) {
		SongList songList=new SongList();
		Song song =new Song();
		song.setSongID(1);
		songList.bookSong(song);
		System.out.println(""+songList.nextSong().getSinger1());
		System.out.println(""+songList.nextSong());

	}
}
