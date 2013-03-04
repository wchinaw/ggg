package com.cheng.ggg.types;

import android.database.Cursor;

import com.cheng.ggg.utils.COM;

/**������������.*/
public class GongGuoDetail{
	final String TAG = "GongGuoBase";
	
	public int id;
	public String name;
	public int count;
	/**�û��Զ��幦��*/
	public boolean bUserdefine = false;
	public int userCount;//�û��ڴ˹����ϵ��ۼƸ���
	
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
