package com.wwl.temphelper.Activity;

import com.wwl.temphelper.R;
import com.wwl.temphelper.R.layout;
import com.wwl.temphelper.DB.TempHelperDB;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNumbersActivity extends Activity {

	EditText et1;
	EditText et2;
	EditText et3;
	EditText et4;
	EditText et5;
	Button btn;
	TempHelperDB tempHelperDB;
	String str[]= new String[5];
	String username;
	
	String []name = new String[5];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_numbers);
		
		tempHelperDB = new TempHelperDB(getApplicationContext());
		tempHelperDB .open();
		
		this.et1= (EditText)findViewById(R.id.et_1);
		this.et2= (EditText)findViewById(R.id.et_2);
		this.et3= (EditText)findViewById(R.id.et_3);
		this.et4= (EditText)findViewById(R.id.et_4);
		this.et5= (EditText)findViewById(R.id.et_5);
		  	    
	    this.username=getIntent().getStringExtra("username");
	    
	    isSaveInDB(username);
	   
	}
	
	
	

	/**
	 * 添加判断
	 * 如果该用户成员表已添加成员
	 * 则把成员依次显示在EditText中，即设定 其hint属性
	 * @param view
	 */
	private void isSaveInDB(String username2) {
		Cursor cursor =	tempHelperDB.getMemDiary(username2);
		if(cursor.getCount()==0){
			Toast.makeText(this, "尚未添加成员，请添加", Toast.LENGTH_SHORT).show();
		}
		else{		
			int i=0;
			int nameColumnIndex = cursor.getColumnIndex("name");
			name[i] = cursor.getString(nameColumnIndex);
			i++;
			while(cursor.moveToNext())
			{								
				name[i] = cursor.getString(nameColumnIndex);
				i++;
			}
			
			et1.setHint(name[0]);
			et2.setHint(name[1]);
			et3.setHint(name[2]);
			et4.setHint(name[3]);
			et5.setHint(name[4]);
		}
		
}	
	
	//把EditText的成员名保存入数据库
	public void save(View view){			
		str[0]=et1.getText().toString();
		str[1]=et2.getText().toString();
		str[2]=et3.getText().toString();
		str[3]=et4.getText().toString();
		str[4]=et5.getText().toString();	
		
			try {				
					tempHelperDB.createMembers(str[0], username);
					tempHelperDB.createMembers(str[1], username);
					tempHelperDB.createMembers(str[2], username);
					tempHelperDB.createMembers(str[3], username);
					tempHelperDB.createMembers(str[4], username);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
		Toast.makeText(this, "已成功保存", Toast.LENGTH_SHORT).show();
	}	
	
	@Override
	protected void onResume() {
		
		super.onResume();
		 isSaveInDB(username);
	}
}
