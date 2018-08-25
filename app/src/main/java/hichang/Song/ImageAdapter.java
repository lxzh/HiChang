package hichang.Song;

import hichang.ourView.GalleryFlow;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	private ImageView[] mImages;

	public ImageAdapter(Context c) {
		mContext = c;
	}

	public boolean setImages(int[] imageid, int width, int height) {
		mImages = new ImageView[imageid.length];
		ImageView imageView;
		for (int i = 0; i < imageid.length; i++) 
		{
			imageView = new ImageView(mContext);
			imageView.setImageResource(imageid[i]);
			imageView.setLayoutParams(new GalleryFlow.LayoutParams(width, height));
			mImages[i] = imageView;
		}
		return true;
	}

	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < 0) {
			position += mImages.length;
		}
		return mImages[position % mImages.length];
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}
}