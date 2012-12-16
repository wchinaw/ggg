package com.cheng.ggg.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.cheng.ggg.R;

import android.app.Activity;
import android.util.Log;

public class COM {
	public static final String DBNAME = "/sdcard/ggg";
	public static final String INTENT_GONG = "INTENT_GONG";
	public static final String INTENT_TYPE = "INTENT_TYPE";
	
	public final static boolean DEBUG = true;
	public static int LOGE(String TAG, String msg){
		if(DEBUG)
			return Log.e(TAG, msg);
		else
			return 0;
	}
	
	public static String twoZeroPre(int intValue){
		String value = String.format("%02d", intValue);
		return value;
	}
	
	public static String intTime2Date(Activity activity, int value){
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeZone(TimeZone.getDefault());
		calendar1.setTimeInMillis(value*1000L);
		
		String date = calendar1.get(Calendar.YEAR)+activity.getString(R.string.year)
				+twoZeroPre((calendar1.get(Calendar.MONTH)+1))+activity.getString(R.string.month)
				+twoZeroPre(calendar1.get(Calendar.DAY_OF_MONTH))+activity.getString(R.string.day)+" "
				+twoZeroPre(calendar1.get(Calendar.HOUR_OF_DAY))+activity.getString(R.string.hour)
				+twoZeroPre(calendar1.get(Calendar.MINUTE))+activity.getString(R.string.minute)
				+twoZeroPre(calendar1.get(Calendar.SECOND))+activity.getString(R.string.second);
		
		return date;
	}
}
