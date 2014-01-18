package com.cheng.ggg.utils;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;


public class Settings {
	
	public static final int CHINESE_SIMPLE = 0;  //��������
	public static final int CHINESE_TRADITION = 1; //��������
	
	public static final String gongguoconfirm_dialog = "gongguoconfirm_dialog";
	public static final String is_enable_password = "is_enable_password";
	public static final String password = "password";
	public static final String defaultpic = "defaultpic";
	public static final String tips = "tips";
	public static final String fontSize = "fontSize";
	public static final String colorSwap = "colorSwap";//������ɫ����
	public static final String alarm_time = "alarm_time";//��ʱ����ʱ�� Сʱ���ӻ���ĺ�����
	public static final String is_enable_alarm = "is_enable_alarm";
	public static final String alarm_date_ms = "alarm_date_ms";//��ʱ���ѵ��������ڵĺ�����
	public static final String repeat_time_ms = "repeat_time_ms";//�ظ����ѵ�ʱ����
	
	public static final String time_range_index = "time_range_index";//��ϸ���Ե�ʱ�䷶Χ ��Ӧ��arrays.xml list_date_range
	
	public static void changeLauguage(Context context, int lang){
//		  �ڴ������л����ԣ�
     Resources resources = context.getResources();//���res��Դ����
     Configuration config = resources.getConfiguration();//������ö���
     DisplayMetrics dm = resources .getDisplayMetrics();//�����Ļ��������Ҫ�Ƿֱ��ʣ����صȡ�
     
     if(lang == CHINESE_SIMPLE)
    	 config.locale = Locale.SIMPLIFIED_CHINESE; //��������
     else if(lang == CHINESE_TRADITION)
    	 config.locale = Locale.TRADITIONAL_CHINESE; //��������
     
     resources.updateConfiguration(config, dm);
	}
	
	public static boolean getIsEnablePassword(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getBoolean(is_enable_password, false);
	}
	
	//��ϸ���Ե�ʱ�䷶Χ ��Ӧ��arrays.xml list_date_range
	public static int getTimeRangeIndex(Context context){
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
        return sp.getInt(time_range_index, 2); //Ĭ��Ϊ����
    }
    
	//��ϸ���Ե�ʱ�䷶Χ ��Ӧ��arrays.xml list_date_range
    public static void setTimeRangeIndex(Context context,int index){
        setInt(context,time_range_index, index);
    }
	
	public static long getAlarmDateMs(Context context){
		return getLong(context,alarm_date_ms);
	}
	
	/**��һ������ʱ��*/
	public static void setAlarmDateMs(Context context,long date_ms){
		setLong(context,alarm_date_ms, date_ms);
	}
	/**��һ������ʱ��*/
	public static long getRepeatTimeMs(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getLong(repeat_time_ms, TimeDate.ONE_DAY_MS);
	}
	
	public static void setRepeatTimeMs(Context context,long date_ms){
		setLong(context,repeat_time_ms, date_ms);
	}
	
	public static boolean getIsEnableAlarm(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getBoolean(is_enable_alarm, false);
	}
	
	public static void setIsEnableAlarm(Context context,boolean bEnable){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		Editor editor = sp.edit();
		editor.putBoolean(is_enable_alarm, bEnable);
		editor.commit();
	}
	
	public static boolean getIsColorSwap(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getBoolean(colorSwap, false);
	}
	
	public static long getAlarmTime(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getLong(alarm_time,0);
	}
	
	public static String getPassword(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getString(password,"");
	}
	
	public static void setPassword(Context context, String pwd){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		Editor editor = sp.edit();
		editor.putString(password, pwd);
		editor.commit();
	}
	
	public static String getPic(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getString(defaultpic,"");
	}
	
	public static String getUserdefineTips(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getString(tips,"");
	}
	
	public static int getFontSize(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//		String fontValue = sp.getString(fontSize, "20");
		String fontValue = sp.getString(fontSize, "");
		if(fontValue.equals("")){
			setString(context,fontSize,COM.DEFAULT_FONT_SIZE);
			fontValue = COM.DEFAULT_FONT_SIZE;
		}
		return COM.parseInt(fontValue);
	}
	
	public static void setPic(Context context, String pwd){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		Editor editor = sp.edit();
		editor.putString(defaultpic, pwd);
		editor.commit();
	}
	
	public static void setString(Context context,String key, String strValue){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		Editor editor = sp.edit();
		editor.putString(key, strValue);
		editor.commit();
	}
	
	public static long getLong(Context context, String key){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getLong(key, 0);
	}
	
	public static void setLong(Context context,String key, long value){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static void setInt(Context context,String key, int value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
