package com.cheng.ggg.utils;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class settings {
	
	public static final int CHINESE_SIMPLE = 0;  //��������
	public static final int CHINESE_TRADITION = 1; //��������
	
	
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
}
