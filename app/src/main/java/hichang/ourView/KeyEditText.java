package hichang.ourView;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class KeyEditText extends EditText{

	public AbsoluteLayout keyBoard;
	public boolean isOk=false;
	public KeyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		this.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(!isOk){
					switch (keyCode) {
					
					case 7:
						KeyEditText.this.setText("0"+KeyEditText.this.getText());
						break;
					case 8:
						KeyEditText.this.setText("1"+KeyEditText.this.getText());
						break;
					case 9:
						KeyEditText.this.setText("2"+KeyEditText.this.getText());
						break;
					case 10:
						KeyEditText.this.setText("3"+KeyEditText.this.getText());
						break;
					case 11:
						KeyEditText.this.setText("4"+KeyEditText.this.getText());
						break;
					case 12:
						KeyEditText.this.setText("5"+KeyEditText.this.getText());
						break;
					case 13:
						KeyEditText.this.setText("6"+KeyEditText.this.getText());
						break;
					case 14:
						KeyEditText.this.setText("7"+KeyEditText.this.getText());
						break;
					case 15:
						KeyEditText.this.setText("8"+KeyEditText.this.getText());
						break;
					case 16:
						KeyEditText.this.setText("9"+KeyEditText.this.getText());
						break; 
					default:
						break;
					}
					
				}
				return false;
			}
		});
		
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					isOk=false;
					keyBoard.setVisibility(AbsoluteLayout.VISIBLE);
					KeyEditText.this.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
					KeyEditText.this.setCursorVisible(true);
				} else {
					keyBoard.setVisibility(AbsoluteLayout.INVISIBLE);
				}
			}
		});
		
		this.setOnEditorActionListener(new OnEditorActionListener() {
			
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
			    isOk=!isOk;
				Toast.makeText(getContext(), ""+isOk, Toast.LENGTH_SHORT).show();
				
			    
				KeyEditText.this.clearFocus();
				KeyEditText.this.setFocusable(false);
				isOk=true;
					
				
			    		
				
				return false;
			}
		});
	}
    
	/**
	 * 给控件绑定一个软件盘
	 * @param keyBoard 软件盘
	 */
	public void setKeyBoard(AbsoluteLayout keyBoard){
		this.keyBoard=keyBoard;
	}
}
