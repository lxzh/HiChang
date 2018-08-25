package hichang.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.util.Log;

public class FileUtil {
	/**
     * 将默认音乐拷贝到sdcard路径下
     */
    public static boolean copyFile(AssetManager am,String path,String assetsFolder,String filename)throws IOException
    {
    	String outFileName = path + filename;
		// 判断目录是否存在。如不存在则创建一个目录
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(outFileName);
		if (!file.exists()) {
			try {
				Log.d("开始拷贝文件", outFileName);
				file.createNewFile();
				InputStream myInput = am.open(assetsFolder+filename);
				Log.d("assets", assetsFolder+filename);
				OutputStream myOutput = new FileOutputStream(outFileName);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer)) > 0) {
					myOutput.write(buffer, 0, length);
				}
				myOutput.flush();
				myOutput.close();
				myInput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	return true;
    }
}
