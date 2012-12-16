package com.cheng.ggg.utils;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class settings {
	
	public static final int CHINESE_SIMPLE = 0;  //简体中文
	public static final int CHINESE_TRADITION = 1; //繁体中文
	
	
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
}
