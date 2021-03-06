package com.cheng.ggg.types;

import android.database.Cursor;

import com.cheng.ggg.utils.COM;

import java.io.Serializable;

/**功过具体类型.*/
public class GongGuoDetail implements Serializable {
	final String TAG = "GongGuoBase";
	
	public int id;
	public String name;
	public int count;
	/**用户自定义功过*/
	public boolean bUserdefine = false;
	public int userCount;//用户在此功过上的累计个数

	//用于首页快捷键/////////////////////////////////////////////
	public static int TYPE_NORMAL = 0;
	/**用于首页快捷键*/
	public static int TYPE_HOTKEY = 1;
	public int status = TYPE_NORMAL;

	public void dump(){
		COM.LOGE(TAG, "id: "+id);
		COM.LOGE(TAG, "name: "+name);
		COM.LOGE(TAG, "count: "+count);
	}
	
	public static GongGuoDetail getFromCursor(Cursor cursor){
		GongGuoDetail detail = new GongGuoDetail();
		detail.id = cursor.getInt(0);
		detail.name = cursor.getString(1);
		detail.count = cursor.getInt(2);
		return detail;
	}
}
