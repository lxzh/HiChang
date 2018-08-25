package hichang.ourView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.TypedValue;

public class StringUtil {

	public static void drawText(String text,int x,int y,int maxWidth,Canvas canvas,Paint paint){
		float width=paint.measureText(text);
		FontMetrics fontMet=paint.getFontMetrics();
		float textHeight=fontMet.bottom-fontMet.ascent;
		if(width<=maxWidth){
			canvas.drawText(text, x, (int)(y+textHeight), paint);
		}else{
			int len=text.length();
			int tmpLen=len-1;
			String tmpText;
			do{
				tmpText=text.substring(0, tmpLen)+"...";
				width=paint.measureText(tmpText);
				tmpLen--;
			}while(tmpLen>0&&width>maxWidth);
			canvas.drawText(tmpText, x, (int)(y+textHeight), paint);
		}
	}

	public static boolean IsNullOrEmpty(String str){
		return str==null||str.equals("");
	}

	/** 
	 * 获取当前分辨率下指定单位对应的像素大小（根据设备信息） 
	 * px,dip,sp -> px 
	 *  
	 * Paint.setTextSize()单位为px 
	 *  
	 * 代码摘自：TextView.setTextSize() 
	 *  
	 * @param unit  TypedValue.COMPLEX_UNIT_* 
	 * @param size 
	 * @return 
	 */  
	public static float getRawSize(Context context,int unit, float size) {  
	       Resources r;  
	       if (context == null)  
	           r = Resources.getSystem();  
	       else  
	           r = context.getResources();  
	       return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());  
	}   

}
