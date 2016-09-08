package com.wwl.temphelper.Activity;

import com.wwl.temphelper.R;
import com.wwl.temphelper.DB.TempHelperDB;
//import com.wwl.temphelper.DB.UserDbAdapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText mUserText;
	private EditText mPasswordText;
	private EditText mConPasswordText;
	//private UserDbAdapter mDbHelper;
	private TempHelperDB mDbHelper;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new TempHelperDB(getApplicationContext());
		mDbHelper.open();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		mUserText = (EditText) findViewById(R.id.userNameAuto);
		mPasswordText = (EditText) findViewById(R.id.password);
		mPasswordText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mConPasswordText = (EditText) findViewById(R.id.conpasswordET);
		mConPasswordText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		Button confirmButton = (Button) findViewById(R.id.subBT);
		Button cancelButton = (Button) findViewById(R.id.cancBT);

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String username = mUserText.getText().toString();
				String password = mPasswordText.getText().toString();
				String conPassword = mConPasswordText.getText().toString();
				if((username == null||username.equalsIgnoreCase("")) || (password == null||password.equalsIgnoreCase("")) || (conPassword == null||conPassword.equalsIgnoreCase(""))){
					Toast.makeText(RegisterActivity.this, "The user name and password are necessary.",
							Toast.LENGTH_SHORT).show();
				}else{
					Cursor cursor = mDbHelper.getDiary(username);
					if(cursor.moveToFirst()){
						Toast.makeText(RegisterActivity.this, "The user name already exists.",
								Toast.LENGTH_SHORT).show();
					}else if (!password.equals(conPassword)) {
						Toast.makeText(RegisterActivity.this, "Two password is not consistent, please enter again.",
								Toast.LENGTH_SHORT).show();
					}else{
						mDbHelper.createUser(username, password);//注册进入
						Toast.makeText(RegisterActivity.this, "Register successfully. Wait for going to the login page...",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(RegisterActivity.this, MainActivity.class);
						startActivity(intent);
					}
				}				
			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, MainActivity.class);
				startActivity(intent);
			}

		});
	}
	public TempHelperDB getTempHelper(){
		mDbHelper= mDbHelper.open();
		return mDbHelper;		
	}
	
}
