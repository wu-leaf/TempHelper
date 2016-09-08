package com.wwl.temphelper.Activity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.wwl.temphelper.R;
import com.wwl.temphelper.DB.TempHelperDB;
import com.wwl.temphelper.R.layout;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShowTemps extends Activity {
	
	Handler h;
	CreateRemarkPopDialog createRemarkPopDialog;//对话框对象
	
    String mem_name;//根据该成员名查询数据库
    TempHelperDB tempHelperDB;
	
    TextView mBig_Temp;
    TextView mReMark;
    TextView mHint;
    
    String currentTime;
    
    String []reMark_buffer = new String[99];
    
    int count;
    
    //服务listView的初始化参数
    private int[] imgID_temp;
    String temp[] = new String[99]; //存放成员们的temp数组,测试暂定个
	String date[] = new String[99]; //存放成员们的date数组
    private ListView lv_temps;     //温度类别的ListView
    private List<Map<String,Object>> listitem_temp;
    private Map<String,Object> showItem_temp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_temps);
		this.mem_name=getIntent().getStringExtra("mem_name");
		Toast.makeText(this, mem_name, Toast.LENGTH_SHORT).show();
		setTitle(mem_name);
		
		tempHelperDB = new TempHelperDB(getApplicationContext());
		tempHelperDB .open();
		
		mBig_Temp = (TextView)findViewById(R.id.Big_Temp);
		mReMark = (TextView)findViewById(R.id.remark);
		lv_temps = (ListView)findViewById(R.id.Temp_lv);      
		mHint = (TextView)findViewById(R.id.Hint);
		
        IsHaveTemp(mem_name);
      //填充备注数据缓存区：里面根据时间查询到备注信息
        setReMarkBuffer(date);
        
        /**
         * 线程通信
         * 从子线程中调用在主线程的更新 UI
         */
        h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                case 0:
              Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                break;
                case 1:
              Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                break;
                }        
            }
        };
	}

	private void IsHaveTemp(String mem_name2) {
		Cursor cursor = tempHelperDB.getTempDiary(mem_name2);
		//this.count = cursor.getCount();
		if(cursor.getCount()==0){
			Toast.makeText(this, "请连接设备记录温度", Toast.LENGTH_SHORT).show();
		    mHint.setText("");
		}
		else{
			int i=0;
			int tempColumnIndex = cursor.getColumnIndex("temp");
			int dateColumnIndex = cursor.getColumnIndex("date");

			temp[i] = cursor.getString(tempColumnIndex)+" ℃";
			mBig_Temp.setText(temp[i]);
			date[i] = cursor.getString(dateColumnIndex);
			i++;
			while(cursor.moveToNext())
			{								
				this.temp[i] = cursor.getString(tempColumnIndex)+" ℃";
				this.date[i] = cursor.getString(dateColumnIndex);
				i++;
			}			
			
			
			//把保存的温度显示出来，用ListView
			 this.listitem_temp = new ArrayList();
		      for (int j = 0; j < cursor.getCount(); j++) {
		         this.showItem_temp = new HashMap();
		         this.showItem_temp.put("img_temp", Integer.valueOf(this.imgID_temp[j]));
		         this.showItem_temp.put("temp", this.temp[j]);
				 this.showItem_temp.put("date", this.date[j]);
		         this.listitem_temp.add(this.showItem_temp);
		        }
		      this.lv_temps.setAdapter(new SimpleAdapter(getApplicationContext(),
		    		  this.listitem_temp, R.layout.temp_date_list, 
		    		  new String[]{"date","img_temp", "temp"},
		    		  new int[]{R.id.temp_Time,R.id.temp_picture,R.id.temp_Vaule}));		
		      this.lv_temps.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					/**
					 * 1.点击温度item.改变顶部为当前温度数值
					 * 2.当前item图标颜色改变====未实现
					 * 3.从数据库读取当前item的备注信息
					 */					            
					String Top_temp;
					Top_temp = temp[position];
					if(Top_temp == null){
						Message msg = new Message();
						msg.what = 0;
						msg.obj = "暂无数据记录";
						h.sendMessage(msg);						
					}else
						if(reMark_buffer[position] == null){
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "暂无备注记录";
							h.sendMessage(msg);
							mBig_Temp.setText(Top_temp);
					}else{
						mBig_Temp.setText(Top_temp);
						mReMark.setText(date[position]+"\n"+reMark_buffer[position]);						
					}
				    																		
				}
			});
		      this.lv_temps.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					/**
					 * 1.长按温度Item，弹出对话框：提示输入备注信息
					 * 2.点击确定，被输入的备注信息根据 当前温度item的时间插入数据库保存
					 */			
					showEditPopWin(view,position);
					currentTime = date[position];
					return false;
				}
			});
		
		}
		
		
		
	}
	private void setReMarkBuffer(String[] date2) {
		for(int i = 0;i < date2.length ;i++){		
			Cursor cursor = tempHelperDB.getRemarkDiary(date2[i]);
			if(cursor.getCount() == 0){
				//Toast.makeText(this, "您未添加备注", Toast.LENGTH_SHORT).show();
			}
			else{
				int markColumnIndex = cursor.getColumnIndex("mark");
				String mRemark = cursor.getString(markColumnIndex);
				reMark_buffer[i] = mRemark;
			}
		}
	}

	public void showEditPopWin(View view,int position) {
		this.createRemarkPopDialog = new CreateRemarkPopDialog(this, onClickListener);
		this.createRemarkPopDialog.showAtLocation(findViewById(R.id.show_temp_layout), Gravity.CENTER, 0, 0);  
		this.createRemarkPopDialog.remark_info.setText(reMark_buffer[position]);
	}
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case  R.id.btn_save_pop:
				//点击确定，被输入的备注信息根据 当前温度item的时间插入数据库保存
				String remark_info = createRemarkPopDialog.remark_info.getText().toString().trim();				
				//把remark_info根据当前点击item的时间为选择 插入数据库
				tempHelperDB.upDateRemark(remark_info, currentTime);								
				createRemarkPopDialog.dismiss();
				onResume();
			}		
		}
	};

	public ShowTemps() {
		//尼玛恶心死了
		this.imgID_temp= new int[]{R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,
				R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,
				R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2
				,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2,R.drawable.pic_temp2};
	}
	
	@Override
	protected void onResume() {		
		super.onResume();	
		//填充备注数据缓存区：里面根据时间查询到备注信息
        setReMarkBuffer(date);
	}
}
