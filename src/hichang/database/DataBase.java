package hichang.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DataBase extends SQLiteOpenHelper{
	/**
	 * 数据库文件名
	 */
	public static final String DB_NAME="HICHANG.db";
	/**
	 * 版本号
	 */
	public static final int VERSION=1;
	public DataBase databaseHelper;
	/**
	 * 数据库所在路径
	 */
	private static String DB_PATH="/data/data/hichang.activity/databases/"; 
	/**
	 * 默认音乐文件路径
	 */
	private static String MUSIC_PATH;
	/**
	 * activity设备上下文
	 */
	private Context context;
	/**
	 * 构造函数
	 * @param context activity上下文环境
	 */
	public DataBase(Context context)
	{
		super(context,DB_NAME,null,VERSION);
		MUSIC_PATH=Environment.getExternalStorageDirectory()+"/HiChang/Songs";
	}
	/**
	 * 构造函数
	 * @param context activity上下文环境
	 * @param name    数据库名
	 * @param factory  to use for creating cursor objects, or null for the default 
	 * @param version  if the database is older, onUpgrade(SQLiteDatabase, int, int) will be used to upgrade the database; if the database is newer, onDowngrade(SQLiteDatabase, int, int) will be used to downgrade the database 
	 */
	public DataBase(Context context,String name,CursorFactory factory,int version)
	{
		super(context,name,factory,version);
		this.context=context;
	}	
	/**
	 * 创建数据库
	 * @throws IOException 创建数据库失败发出异常
	 */
	public void createDataBase() throws IOException{ 
		boolean dbExist = checkDataBase();
		if(!dbExist)
		{
			try 
			{
				File dir = new File(DB_PATH);
				if(!dir.exists())
				{
					dir.mkdirs();
				}
				File dbf = new File(DB_PATH + DB_NAME);
				SQLiteDatabase.openOrCreateDatabase(dbf, null);
					// 复制assets中的db文件到DB_PATH下
				copyDataBase();
			} catch (IOException e)
			{
				throw new Error("数据库创建失败");
			}
		}
	}
	/**
	  * 检查数据库是否有效 
	  * @return 有效时返回true
	  */
    private boolean checkDataBase(){ 
        SQLiteDatabase checkDB = null; 
        String myPath = DB_PATH + DB_NAME; 
        try
        {             
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY); 
        }catch(SQLiteException e)
        { 
            //database does't exist yet. 
        } 
        if(checkDB != null)
        {
        	checkDB.close(); 
        } 
        return checkDB != null ? true : false; 
   } 
    /**
     * 复制asseets中的db文件到DB_PATH下
     * @throws IOException
     */
    private void copyDataBase() throws IOException{ 
    	InputStream myInput = context.getAssets().open(DB_NAME);
    	String outFileName = DB_PATH + DB_NAME;
    	OutputStream myOutput = new FileOutputStream(outFileName);
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}
    	
		myOutput.flush();
		myInput.close();
		myOutput.close();
    }
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
	{
		
	}
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
	}
	public boolean checkSDCard()
	{
		String state=android.os.Environment.getExternalStorageState();
		if(!state.equals(android.os.Environment.MEDIA_MOUNTED))
			return false;
		else return true;
	}
	//创建多重用户文件路径，创建成功则返回true，否则返回false
    public boolean createUserFolder()
    {
    	String state=android.os.Environment.getExternalStorageState();
    	if(!state.equals(android.os.Environment.MEDIA_MOUNTED))return false;
    	File rootDirectory1=new File(android.os.Environment.getExternalStorageDirectory()+"/HiChang/Songs/");
    	File rootDirectory2=new File(android.os.Environment.getExternalStorageDirectory()+"/HiChang/Record/");
    	File rootDirectory3=new File(android.os.Environment.getExternalStorageDirectory()+"/HiChang/Singer/");
		if(!rootDirectory1.exists())
		{
			if(!rootDirectory1.mkdirs())return false;
		}
		if(!rootDirectory2.exists())
		{
			if(!rootDirectory2.mkdirs())return false;
		}
		if(!rootDirectory3.exists())
		{
			if(!rootDirectory3.mkdirs())return false;
		}
		return true;
    }
    /**
     * 将默认音乐拷贝到sdcard路径下
     */
    public boolean copyFile(String path,String assetsFolder,String filename)throws IOException
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
				InputStream myInput = context.getAssets().open(assetsFolder+filename);
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
