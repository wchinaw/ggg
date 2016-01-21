package com.cheng.ggg.types;

import android.database.Cursor;

import java.io.Serializable;

public class UserGongGuo implements Serializable{
	/**gongguoDetail表中的id*/
	public String parent_id;
	/**gongguoBase表中的名字*/
	public String parent_name;
	public int id;       //id
	/**gongguoDetail表中的名字*/
	public String name; //名称
	public int count; //功过数量
	public int time;  //时间
	public int times; //次数
	public String comment; //备注
	
	public boolean isFirst = false; //用于明细listView,如果此值为true,则显示当天日期。
	public int todayCount = 0; //用于明细listView,如果isFirst为true,则显示当天功过总数。
	public int todayGong = 0; //用于图表当天功总数。
	public int todayGuo = 0; //用于图表当天过总数。
	public String todayInfo; //

	//
	public boolean isUserDefine;//是否是用户自定义的功过 用于首页自定义快捷键使用
	public boolean isFinded = false;//用于在添加首页快捷键功过列表中,对功过列表在首页的状态进行标注.
	
	//是列表中当天第一个
	public void setFirstDay()
	{
	    isFirst = true;
	    todayCount = count*times;
	    if(todayCount > 0){
	    	todayGong = todayCount;
	    	todayGuo = 0;
	    }
	    else{
	    	todayGuo = todayCount;
	    	todayGong = 0;
	    }
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
		
		if(detail.times == 0){
			detail.times = 1;
		}
		return detail;
	}
}
