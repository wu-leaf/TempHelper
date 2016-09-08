package com.wwl.temphelper.DB;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TempHelperDB {

   private static TempHelperDB tempHelperDB;
   
   private DB_OpenHelper mDbHelper;
   private SQLiteDatabase db;
   private final Context mCtx; 
   
 
   public static synchronized TempHelperDB getInstance(Context context){
	 //  TempHelperDB tempHelperDB = null;
	   synchronized (TempHelperDB.class){
		   if(tempHelperDB == null){
			   tempHelperDB = new TempHelperDB(context);
		   }
		   tempHelperDB = tempHelperDB;
	   }
	   return tempHelperDB;
   }
   public TempHelperDB(Context context) {
		this.mCtx = context;
	}
   
   public TempHelperDB open() throws SQLException{
	   mDbHelper = new DB_OpenHelper(mCtx);
	   db = mDbHelper.getWritableDatabase();
	   return this;
   }
    //数据库的操作函数放在这下
   public void closeclose(){
	   mDbHelper.close();
   }
   
	
	
	public Cursor getAllNotes() {
		return db.query("user", new String[] { "_id", "username",
				"password" }, null, null, null, null, null);
	}

	public Cursor getDiary(String username) throws SQLException {

		Cursor mCursor =
		db.query(true, "user", new String[] { "_id", "username",
				"password"  }, "username" + "='" + username+"'", null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	**  1.查询登录者所有的家庭成员信息
	*select name from menbers where user_name ="吴威龙"  ;
	**/
	public Cursor getMemDiary(String username) throws SQLException {
		Cursor mCursor =
		db.query(true, "menbers", new String[] {"m_id","name","user_name"},
				"user_name" + "='" + username+"'", null, null,
		null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 ** 2.根据成员名查询成员的体温
	 *select temp from temp where mem_name = 
	 *   "冯伟琼");
	 **/
	public Cursor getTempDiary(String name) throws SQLException {
		Cursor mCursor =
		db.query(true, "temp", new String[] {"t_id","temp","mem_name","date","mark"},
				"mem_name" + "='" + name+"'", null, null,
		null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/**
	 ** 2.根据时间查询备注
	 *select mark from temp where date = 
	 *   "时间");
	 **/
	public Cursor getRemarkDiary(String date) throws SQLException {
		Cursor mCursor =
		db.query(true, "temp", new String[] {"t_id","temp","mem_name","date","mark"},
				"date" + "='" + date+"'", null, null,
		null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	**  3.家庭成员的 温度+时间 数据条目增加
	*  insert into temp(t_id,temp,mem_name) values(13,"38.3c","冯伟琼");
	**/
	public long createTemp_Data(String temp, String mem_name,String date) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("temp", temp);
		initialValues.put("mem_name", mem_name);	
		initialValues.put("date", date);
		return db.insert("temp", null, initialValues);
	}
	
	/**
	 * 添加家庭成员
	 */
	public long createMembers(String name, String user_name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("name", name);
		initialValues.put("user_name", user_name);		
		return db.insert("menbers", null, initialValues);
	}
	
	//添加用户
	public long createUser(String username, String password) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("username", username);
		initialValues.put("password", password);		
		return db.insert("user", null, initialValues);
		
	}
	/**
	 * 更新备注
	 * 例：update temp set mark='参数' where date=“时间参数”;
	 */
	public int upDateRemark(String mark,String date){
		ContentValues markValues = new ContentValues();
		markValues.put("mark", mark);
		return db.update("temp", markValues, "date" + "='" + date+"'" , null);
	}
	
 
}
