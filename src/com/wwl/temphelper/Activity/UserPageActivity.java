package com.wwl.temphelper.Activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



import com.wwl.temphelper.R;
import com.wwl.temphelper.R.id;
import com.wwl.temphelper.R.layout;
import com.wwl.temphelper.DB.TempHelperDB;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 这里主要根据用户登录信息进行：
 * 
 *
 */
public class UserPageActivity extends Activity implements OnItemClickListener{
	/*
	 * 蓝牙设备初始化参数
	 */
	private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
	private InputStream is;              //输入流，用来接收蓝牙数据
    private String smsg = "";            //显示用数据缓存
    BluetoothDevice _device = null;      //蓝牙设备
    BluetoothSocket _socket = null;      //蓝牙通信socket   
    boolean bRun = true;
    boolean bThread = false;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    
    //获取本地蓝牙适配器，即蓝牙设备
    
    private TextView mTitle;

	String username;
	
	private int[] imgIds;
	private String[] names;//抽屉列表名字
	private ListView mDrawerList;//抽屉菜单的ListView
    private List<Map<String, Object>> listitem;
    private Map<String, Object> showitem;
    
    private int[] imgID_memb;
    String name[] = new String[5];//存放成员们的name数组
    private ListView lt_members; //成员类别的ListView
    private List<Map<String,Object>> listitem_memb;
    private Map<String,Object> showItem_menb;
    
    private DrawerLayout mDrawerLayout;   
    TempHelperDB tempHelperDB;
    
    String name_s[] = new String[5];//封装后的数组
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);   //自定义标题
		setContentView(R.layout.activity_user_page);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title); 
		
		 mTitle = (TextView) findViewById(R.id.title_left_text);  //左边显示APP名字
	     mTitle.setText(R.string.app_name);
	     mTitle = (TextView) findViewById(R.id.title_right_text);   //右边显示蓝牙连接状态
	     mTitle.setText("未连接");
	     
	     /**
	      * 蓝牙相关设置 
	      * 如果打开本地蓝牙设备不成功，提示信息，结束程序
	      */
		      if (_bluetooth == null){
		        	Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
		            finish();
		            return;
		        }
		       //设置设备可以被搜索  
		        new Thread(){
		     	   public void run(){
		     		   if(_bluetooth.isEnabled()==false){
		         		_bluetooth.enable();
		     		   }
		     	   }   	   
		        }.start();      
			 
	     
		this.username=getIntent().getStringExtra("username");
		this.names = new String[]{username,"反馈","设置","关于","退出"};
		
		
		 this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	     this.mDrawerList = (ListView) findViewById(R.id.left_drawer);
	     this.listitem = new ArrayList();
	      for (int i = 0; i < this.names.length; i++) {
	         this.showitem = new HashMap();
	         this.showitem.put("touxiang", Integer.valueOf(this.imgIds[i]));
	         this.showitem.put("name", this.names[i]);
	         this.listitem.add(this.showitem);
	        }
	      this.mDrawerList.setAdapter(new SimpleAdapter(getApplicationContext(),
	    		  this.listitem, R.layout.list_item, 
	    		  new String[]{"touxiang", "name"}, 
	    		  new int[]{R.id.imgtou, R.id.name}));
	      this.mDrawerList.setOnItemClickListener(this);
	     
	      mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {  
	            @Override  
	            public void onDrawerClosed(View drawerView) {  
	                super.onDrawerClosed(drawerView);  
	                setTitle("User Page");
	            }  
	            @Override  
	            public void onDrawerOpened(View drawerView) {  
	                super.onDrawerOpened(drawerView); 
	                setTitle("Page");
	            }  
	        });  
	      
	      	      
	  this.lt_members = (ListView) findViewById(R.id.lt_members);
	     
	  this.tempHelperDB = new TempHelperDB(getApplicationContext());
	  this.tempHelperDB .open();
	  IsSaveMembers(username);
	}

	/**
	 * 判断当前用户是否已经保存了成员
	 * 如果有，则把保存的成员显示出来，用ListView
	 * 如果无，则提示去【设置】处添加成员
	 * @param username2 
	 */
	private void IsSaveMembers(String username2) {
		Cursor cursor =	tempHelperDB.getMemDiary(username2);
		if(cursor.getCount()==0){
			Toast.makeText(this, "尚未添加成员，请前往【设置】添加", Toast.LENGTH_SHORT).show();
		}
		else{			
			int i=0;
			int nameColumnIndex = cursor.getColumnIndex("name");
			name[i] = cursor.getString(nameColumnIndex);
			i++;
			while(cursor.moveToNext())
			{								
				this.name[i] = cursor.getString(nameColumnIndex);
				i++;
			}			
			//把保存的成员显示出来，用ListView
			 this.listitem_memb = new ArrayList();
		      for (int j = 0; j < this.name.length; j++) {
		         this.showItem_menb = new HashMap();
		         this.showItem_menb.put("img", Integer.valueOf(this.imgID_memb[j]));
		         this.showItem_menb.put("mem_name", this.name[j]);
		         this.listitem_memb.add(this.showItem_menb);
		      }
		      this.lt_members.setAdapter(new SimpleAdapter(getApplicationContext(),
		    		  this.listitem_memb, R.layout.list_item_mem, 
		    		  new String[]{"img", "mem_name"}, 
		    		  new int[]{R.id.img, R.id.mem_name}));
		      this.lt_members.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					
					String mem_name;
					switch(position){
					case 0:				
					 mem_name = name[0];
					 ShowTemp(mem_name);
					 break;
					case 1:
				     mem_name = name[1];
					 ShowTemp(mem_name);
						break;
					case 2:
					mem_name = name[2];
				    ShowTemp(mem_name);	
						break;
					case 3:
				    mem_name = name[3];
					ShowTemp(mem_name);		
						break;
					case 4:
				    mem_name = name[4];
				    ShowTemp(mem_name);	
						break;
					}
					return false;
				}
			});		  
		  packUpName(name);     		      		      
		}		
	}
	  /*
	   * 通信协议：沟通下
	   * 封装好 name_s数组，如name= {"s3Tom\n","s4Jack\n","s4Lily\n","s4Jany\n","s4Saly\n"};
	   */
	private void packUpName(String[] name2) {
		String s3 ="s3";
		String s4 ="s4";
		String s5 ="s5";
		String t = "\n";
		for(int i = 0;i<5;i++){
			if(name2[i].length()==3){
				name_s[i] = s3+name2[i]+t;
			}else if(name2[i].length()==4){
				name_s[i] = s4+name2[i]+t;
			}else if(name2[i].length()==5){
				name_s[i] = s5+name2[i]+t;
			}			
		}		
	}

	protected void ShowTemp(String name2) {
		Intent intent = new Intent(UserPageActivity.this,ShowTemps.class);
		intent.putExtra("mem_name", name2);
		startActivity(intent);
	}

	public UserPageActivity() {		
	    this.imgIds = new int[]{R.drawable.logined,
				R.drawable.feetback,R.drawable.add,
				R.drawable.about,R.drawable.logout};
	    this.imgID_memb = new int[]{R.drawable.imgg,
	    		R.drawable.imgg,R.drawable.imgg,
	    		R.drawable.imgg,R.drawable.imgg };
	}
	
	
	public void LinkDec(View v){
		if(_bluetooth.isEnabled()==false){  //如果蓝牙服务不可用则提示
    		Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
    		return;
    	}

	    //如未连接设备则打开DeviceListActivity进行设备搜索
		Button btn = (Button) findViewById(R.id.LinkDec);
		if(_socket==null){
			Intent serverIntent = new Intent(this, DeviceListActivity.class); 
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
		}
		else{
			 //关闭连接socket
		    try{
		    	is.close();
		    	_socket.close();
		    	_socket = null;
		    	bRun = false;
		    	btn.setText("连接");
		    	mTitle.setText("未连接");
		    }catch(IOException e){}   
		}
		return;
	}
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // 当DeviceListActivity返回与设备连接的消息
            if (resultCode == Activity.RESULT_OK) {//连接成功，由DeviceListActivity设置返回
          	// MAC地址，由DeviceListActivity设置返回
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // // 得到蓝牙设备句柄   
               _device = _bluetooth.getRemoteDevice(address);
               // 用服务号得到socket
               try{
               	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
               }catch(IOException e){
               	Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
               }
               //连接socket
           	Button btn = (Button) findViewById(R.id.LinkDec);
               try{
               	_socket.connect();
               	Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
               	mTitle.setText("已连接");  //标题右侧设为已连接
               	btn.setText("断开");
               }catch(IOException e){
               	try{
               		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
               		_socket.close();
               		_socket = null;
               	}catch(IOException ee){
               		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
               	}
               	
               	return;
               }
               
               //打开接收线程
               try{
           		is = _socket.getInputStream();   //得到蓝牙数据输入流
           		}catch(IOException e){
           			Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
           			return;
           		}
           		if(bThread==false){
           			ReadThread.start();
           			bThread=true;
           		}else{
           			bRun = true;
           		}
           }
   		break;
   	default:break;
   	}
   }
    
  //接收数据线程
    Thread ReadThread=new Thread(){   	
    	public void run(){
    		bRun = true;
    		//接收线程
    		while(true){
    			try{ 
    				byte[] sbuffer=new byte[1024];   				
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					sbuffer[0]=(byte) is.read();       //读取第一个字节
    					if (sbuffer[0]=='m'||sbuffer[0]=='s'){
    						sbuffer[1]=(byte) is.read();   //读取第二个字节
    						String c = new String(sbuffer, 1,1);// 读取数字
    						int q=Integer.parseInt(c);  //变为整形
    					  if (q>0 && q<10){
    						  
    					     for(int s=2;;s++){
    					    		 sbuffer[s]=(byte) is.read();
    					    		 if (sbuffer[s]==0x0a){break;}
    					    	 }   					   
    						 String x= new String(sbuffer,0,sbuffer.length);
    							smsg=x; 
    					    }  						
					}else continue;
    			    if(is.available()==0)break;  //短时间没有数据才跳出进行显示
    				}
    				//发送显示消息，进行显示刷新
    					handler.sendMessage(handler.obtainMessage());       	    		
    	    		}catch(IOException e){
    	    		}
    		}
    	}
    };
    
	   // 此Handler处理   ## 硬件设备  ##  传来的消息   
		  private final Handler handler = new Handler() {	  
			  int i=0;
			  String OK="m2ok\n";  //回应硬件字符串，表接收到温度数据
	          byte[] K=OK.getBytes();//send(K);即回送确认给硬件
			  public void handleMessage(Message msg) {
				  super.handleMessage(msg);
				  byte[] readBuf=smsg.getBytes();   //蓝牙接收到的数据			  
				 if (readBuf[0]=='m' ){             //读取名字
			          if (readBuf[6]=='+'){
			        	  i++;
			        	  if(i>4) 
			        		  i=0;
			        	  }
			          else if (readBuf[6]=='-'){
			        	  i--;
			        	  if(i<0)
			        		  i=4;
			        	  }    
			          	String N=name_s[i];
			          	byte[] send=N.getBytes();
			          	Send(send);
			          	}
				 
			    else if (readBuf[0]=='s'){   
		    	       String c = new String(readBuf, 1,1);// 读取数字
		    	       int a=Integer.parseInt(c);
		            
		        if (a>4 && a<10){  	    		  
		    	          /**
		    	           * readname 为硬件发送回来数据段里面的成员名
		    	           * readDate 为实际的温度数值 
		    	           */
		    	    	 int b=a-5;  //人名占的字符  
		    	         String readname = new String(readBuf, 2,b);// 读取人名
		    	         String readData=new String(readBuf, b+2,4);  //读取数据
		            	 float Data=((float)(Integer.parseInt(readData)))/100;
			             String trc=Float.toString(Data);  	//实测数据   
			            	            
		        	   //打印当前时间
		                 SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd");   
		                 String   date = sDateFormat.format(new   java.util.Date());   
		                 SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");
		                 String  hour = time.format(new  java.util.Date());
		                 String datatime= date+","+hour;
		                 		                 
		                 /*
		                  * 发送ok给硬件设备表示收到（成员名+温度）数据
		                  */
		                 String nameString ="";
		                 for(int i=0;i<name.length;i++){
		                	 nameString+=name[i];
		                 }	                                
		                /*
		                 *  1.把 获取时间datatime，接收到的温度trc， 插入数据库的温度表
		                 *  2.将这些数据显示出来:查询数据库（长按点击事件中，启动activity）
		                 */
		                 //把 获取时间datatime，接收到的温度trc， 插入数据库的温度表
		                 tempHelperDB.createTemp_Data(trc, readname, datatime);
		                 if(nameString.toString().contains(readname)){
		                	 Send(K);
		                 } 
		        }			
	 }		  
 }
};
			//发送数据
		      public void Send(byte[] bos){
		    	  int i=0;
		      	  int n=0;
		      	try{
		    		OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流	    		
		    		for(i=0;i<bos.length;i++){
		    			if(bos[i]==0x0a)n++;
		    		}
		    		byte[] bos_new = new byte[bos.length+n];
		    		n=0;
		    		for(i=0;i<bos.length;i++){ //手机中换行为0a,将其改为0d 0a后再发送
		    			if(bos[i]==0x0a){
		    				bos_new[n]=0x0d;
		    				n++;
		    				bos_new[n]=0x0a;
		    			}else{
		    				bos_new[n]=bos[i];
		    			}
		    			n++;
		    		}		    		
		    		os.write(bos_new);	
		    	}catch(IOException e){  		
		    	}  	
		      	  
		      }
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		 String text = ((Map) this.listitem.get(position)).get("name").toString();
		 switch(text){
		    case "反馈":
		    	Toast.makeText(this, "反馈", Toast.LENGTH_SHORT).show();
			  break;
		    case "设置":
		    	Intent addnumberintent = new Intent();
		    	addnumberintent.putExtra("username", username);	
		    	addnumberintent.setClass(UserPageActivity.this,AddNumbersActivity.class);
			    startActivity(addnumberintent);	
			  break;
		    case "关于":
		    	Intent aboutintent = new Intent(UserPageActivity.this,AboutActivity.class);
			    startActivity(aboutintent);		    
		    	break;
		    case "退出":
		    	finish();
			  break;
			  
		 }
		 this.mDrawerLayout.closeDrawer(this.mDrawerList);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//强制竖屏，不得横屏
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
		Log.d("TAG", "User_onResume");
		IsSaveMembers(username);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("TAG", "User_onDestroy");
		if(_socket!=null)  //关闭连接socket
	      	try{
	      		_socket.close();
	      	}catch(IOException e){}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("TAG", "User_onPause");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d("TAG", "User_onRestart");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("TAG", "User_onStop");
	}
	
	
	//程序退出提示
		private long exitTime = 0;
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if ((System.currentTimeMillis() - exitTime) > 2000) {
					Toast.makeText(getApplicationContext(), "再按一次退出程序",
							Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {
					finish();
					System.exit(0);
				}
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		
		 /**
	     * 从字节数组到十六进制字符串转换 
	     */
	    public static String bytes2HexString(byte[] b) {
	  	  String ret = "";
	  	  for (int i = 0; i < b.length; i++) {
	  	   String hex = Integer.toHexString(b[ i ] & 0xFF);
	  	   if (hex.length() == 1) {
	  	    hex = '0' + hex;
	  	   }
	  	   ret += hex.toUpperCase();
	  	  }
	  	  return ret;
	  	}


}
