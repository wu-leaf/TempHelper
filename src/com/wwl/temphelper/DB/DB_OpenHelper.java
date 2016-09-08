package com.wwl.temphelper.DB;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_OpenHelper extends SQLiteOpenHelper{
    public static final String CREATE_USER="create table user (_id integer primary key autoincrement, username text not null,password text not null);";
    public static final String CREATE_MEMBERS="create table menbers (m_id integer primary key autoincrement, name text not null,user_name text not null);";
    public static final String CREATE_TEMP="create table temp (t_id integer primary key autoincrement, temp text not null,mem_name text not null,date text not null,mark text);";
    public static final String DB_NAME= "database";
    public static final int VERSION = 6;
    
    
	public DB_OpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_USER);
	    db.execSQL(CREATE_MEMBERS);	
	    db.execSQL(CREATE_TEMP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS user");
		db.execSQL("DROP TABLE IF EXISTS menbers");
		db.execSQL("DROP TABLE IF EXISTS temp");
		onCreate(db);
	}

}
