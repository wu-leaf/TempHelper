package com.wwl.temphelper.Activity;


import com.wwl.temphelper.R;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class CreateRemarkPopDialog extends PopupWindow{
   private Context mContext;
   private View view;
   private Button btn_save_pop;
   public EditText remark_info;
   
   public CreateRemarkPopDialog(Activity mContext,View.OnClickListener itemsOnClick) {
	this.mContext = mContext;
	this.view = LayoutInflater.from(mContext).inflate(R.layout.remark_dialog, null);
	remark_info = (EditText)view.findViewById(R.id.remark_info);
	btn_save_pop = (Button)view.findViewById(R.id.btn_save_pop);
	
	//设置按钮监听
	btn_save_pop.setOnClickListener(itemsOnClick);
    //设置外部可点击
	this.setOutsideTouchable(true);
	
	//设置弹出窗口特征
	//加载视图
	this.setContentView(this.view);
	 //设置弹出窗体的宽高
	   /**
	    * 获取弹出框的窗口对象及参数对象以修改对话框的布局设置，
	    * 可以直接调用getWindow().表示获得这个Activity得对象，
	    * 这样就可以以同样的方式改变这个Activity的属性
	    */
	
	   Window dialogWindow = mContext.getWindow();
	   WindowManager m = mContext.getWindowManager();
	   Display d = m.getDefaultDisplay();//获取屏宽度，高度
	   WindowManager.LayoutParams p = dialogWindow.getAttributes();//获取对话框当前的参数值
	   
	   this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
	   this.setWidth((int)(d.getWidth()*0.8));
	   
	   //设置弹出窗口可点击
	   this.setFocusable(true);
	
	
	
	
   }

}
