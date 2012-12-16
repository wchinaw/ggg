package com.cheng.ggg.types;

import android.database.Cursor;

public class UserGongGuo{
	public String parent_id;
	public String parent_name;
	public int id;
	public String name;
	public int count;
	public int time;
	
	public static UserGongGuo getFromCursor(Cursor cursor){
		UserGongGuo detail = new UserGongGuo();
		int i=0;
		detail.id = cursor.getInt(i++);
		detail.parent_id = cursor.getString(i++);
		detail.parent_name = cursor.getString(i++);
		detail.name = cursor.getString(i++);
		detail.count = cursor.getInt(i++);
		detail.time = cursor.getInt(i++);
		return detail;
	}
}
