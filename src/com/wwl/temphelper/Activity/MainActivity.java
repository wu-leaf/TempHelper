package com.wwl.temphelper.Activity;


import com.wwl.temphelper.R;
import com.wwl.temphelper.R.layout;
import com.wwl.temphelper.DB.TempHelperDB;
//import com.wwl.temphelper.DB.UserDbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	AutoCompleteTextView cardNumAuto;
	EditText passwordET;
	Button logBT;
	Button regBT;

	CheckBox savePasswordCB;
	SharedPreferences sp;
	String cardNumStr;
	String passwordStr;
	//private UserDbAdapter mDbHelper;
	private TempHelperDB mDbHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		cardNumAuto = (AutoCompleteTextView) findViewById(R.id.cardNumAuto);
		passwordET = (EditText) findViewById(R.id.passwordET);
		logBT = (Button) findViewById(R.id.logBT);
		regBT = (Button) findViewById(R.id.regBT);
		regBT.setOnClickListener(register_button_listener); //加监听器

		sp = this.getSharedPreferences("passwordFile", MODE_PRIVATE);
		savePasswordCB = (CheckBox) findViewById(R.id.savePasswordCB);
		savePasswordCB.setChecked(true);// 默认为记住密码
		cardNumAuto.setThreshold(1);// 输入1个字母就开始自动提示
		passwordET.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		// 隐藏密码为InputType.TYPE_TEXT_VARIATION_PASSWORD，也就是0x81
		// 显示密码为InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD，也就是0x91

		
		//设置自动填充用户名文本监听
		cardNumAuto.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String[] allUserName = new String[sp.getAll().size()];// sp.getAll().size()返回的是有多少个键值对
				allUserName = sp.getAll().keySet().toArray(new String[0]);
				// sp.getAll()返回一张hash map
				// keySet()得到的是a set of the keys.
				// hash map是由key-value组成的

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainActivity.this,
						android.R.layout.simple_dropdown_item_1line,
						allUserName);

				cardNumAuto.setAdapter(adapter);// 设置数据适配器

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}
			
			// 自动输入密码
			@Override
			public void afterTextChanged(Editable s) {
				passwordET.setText(sp.getString(cardNumAuto.getText()
						.toString(), ""));

			}
		});

		// 登陆
		logBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				cardNumStr = cardNumAuto.getText().toString();
				passwordStr = passwordET.getText().toString();
				if((cardNumStr == null||cardNumStr.equalsIgnoreCase("")) || (passwordStr == null||passwordStr.equalsIgnoreCase(""))){
					Toast.makeText(MainActivity.this, "The user name and password are necessary.",
							Toast.LENGTH_SHORT).show();
				}else{
					Cursor cursor = mDbHelper.getDiary(cardNumStr);

					if(!cursor.moveToFirst()){
						Toast.makeText(MainActivity.this, "The user name doesn't exist.",
								Toast.LENGTH_SHORT).show();
					}else if (!passwordStr.equals(cursor.getString(2))) {
						Toast.makeText(MainActivity.this, "The password is wrong, please enter again.",
								Toast.LENGTH_SHORT).show();
					} else {
						if (savePasswordCB.isChecked()) {// 登陆成功才保存密码
							sp.edit().putString(cardNumStr, passwordStr).commit();
						}
						Toast.makeText(MainActivity.this, "Login successfully. Wait for getting the information of the user...",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra("username", cardNumStr);
						intent.setClass(MainActivity.this, UserPageActivity.class);
						startActivity(intent);
						finish();
                         
					}
				}				
			}
		});
		
		mDbHelper = new TempHelperDB(this);
		mDbHelper.open();
	}
	
	private Button.OnClickListener register_button_listener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, RegisterActivity.class);
			startActivity(intent);
		}
	};
	
	protected void onDestroy() {
		super.onDestroy();
		Log.d("TAG", "onDestroy");
		this.finish();
		
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//强制竖屏，不得横屏
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		Log.d("TAG", "onResume");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("TAG", "onPause");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d("TAG", "onRestart");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("TAG", "onStop");
	}
	

}