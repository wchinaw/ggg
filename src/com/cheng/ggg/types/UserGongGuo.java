package com.cheng.ggg.types;

import android.database.Cursor;

public class UserGongGuo{
	public String parent_id;
	public String parent_name;
	public int id;       //id
	public String name; //����
	public int count; //��������
	public int time;  //ʱ��
	public int times; //����
	public String comment; //��ע
	
	public boolean isFirst = false; //������ϸlistView,�����ֵΪtrue,����ʾ�������ڡ�
	public int todayCount = 0; //������ϸlistView,�����ֵΪtrue,����ʾ���칦��������
	public String todayInfo; //������ϸlistView,�����ֵΪtrue,����ʾ���칦��������
	
	//���б��е����һ��
	public void setFirstDay()
	{
	    isFirst = true;
	    todayCount = count*times;
	}
	
	public static UserGongGuo getFromCursor(Cursor cursor){
		UserGongGuo detail = new UserGongGuo();
		int i=0;
		detail.id = cursor.getInt(i++);
		detail.parent_id = cursor.getString(i++);
		detail.parent_name = cursor.getString(i++);
		detail.name = cursor.getString(i++);
		detail.count = cursor.getInt(i++);
		detail.time = cursor.getInt(i++);
		detail.times = cursor.getInt(i++);
		detail.comment = cursor.getString(i++);
		return detail;
	}
}
