package com.wwl.temphelper.util;

import org.junit.Test;

import android.database.Cursor;

import com.wwl.temphelper.DB.TempHelperDB;


public class DBtest {
	TempHelperDB helper;
	
	/*
	 * 查看成员
	 *
	 */
	public Cursor check(){
	Cursor cursor	= helper.getMemDiary("wu");
	return cursor;
	}
}
