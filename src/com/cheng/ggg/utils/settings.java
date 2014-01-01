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
	
	public static final int CHINESE_SIMPLE = 0;  //简体中文
	public static final int CHINESE_TRADITION = 1; //繁体中文
	
	public static final String gongguoconfirm_dialog = "gongguoconfirm_dialog";
	public static final String is_enable_password = "is_enable_password";
	public static final String password = "password";
	public static final String defaultpic = "defaultpic";
	public static final String tips = "tips";
	public static final String fontSize = "fontSize";
	public static final String colorSwap = "colorSwap";//功过颜色互换
	
	public static void changeLauguage(Context context, int lang){
//		  在代码中切换语言：
     Resources resources = context.getResources();//获得res资源对象
     Configuration config = resources.getConfiguration();//获得设置对象
     DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
     
     if(lang == CHINESE_SIMPLE)
    	 config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
     else if(lang == CHINESE_TRADITION)
    	 config.locale = Locale.TRADITIONAL_CHINESE; //繁体中文
     
     resources.updateConfiguration(config, dm);
	}
	
	public static boolean getIsEnablePassword(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getBoolean(is_enable_password, false);
	}
	
	public static boolean getIsColorSwap(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getBoolean(colorSwap, false);
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
		String fontValue = sp.getString(fontSize, "26");
		return COM.parseInt(fontValue);
	}
	
	public static void setPic(Context context, String pwd){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		Editor editor = sp.edit();
		editor.putString(defaultpic, pwd);
		editor.commit();
	}
}
