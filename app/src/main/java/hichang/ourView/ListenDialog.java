package hichang.ourView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hichang.activity.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListenDialog extends Dialog{

	private Context context;  
    private ListView listview; 
	public ListenDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public ListenDialog(Context context, int theme)
	{
		super(context, theme);    
        this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listen_dialog);
		listview = (ListView)findViewById(R.id.listview);
		ArrayList<Map<String,String>> data=new ArrayList<Map<String,String>>();  
        Map<String,String> map=null; 
        for(int i=0;i<10;i++){  
            map=new HashMap<String,String>();  
            map.put("simple_item_1", "item"+i);  
            data.add(map);  
        }  
        SimpleAdapter adapter = new SimpleAdapter(context, data, R.layout.row_simple_list_item_1, 
        		new String[]{"simple_item_1"}, new int[]{R.id.simple_item_1});
        listview.setAdapter(adapter);  
	}
	

}
