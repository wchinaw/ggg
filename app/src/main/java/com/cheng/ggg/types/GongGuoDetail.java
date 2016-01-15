package com.cheng.ggg.types;

import android.database.Cursor;

import com.cheng.ggg.utils.COM;

/**功过具体类型.*/
public class GongGuoDetail{
	final String TAG = "GongGuoBase";
	
	public int id;
	public String name;
	public int count;
	/**用户自定义功过*/
	public boolean bUserdefine = false;
	public int userCount;//用户在此功过上的累计个数
	
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
