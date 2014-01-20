package com.cheng.ggg.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.cheng.ggg.R;
import com.cheng.ggg.receiver.AlarmReceiver;

public class COM {
	
	public static final String UMENG_APP_KEY = "50dc4a665270151896000017";
	public static final String DBNAME = "ggg";//"/sdcard/ggg";
	public static final String INTENT_GONG = "INTENT_GONG";
	public static final String INTENT_TYPE = "INTENT_TYPE";
	public static final String INTENT_USERDEFINE = "INTENT_USERDEFINE";//用户自定义标志
	
	public static final String INTENT_USERDEFINE_TIPS = "INTENT_USERDEFINE_TIPS";
	
	public static final String BACKUP_FILENAME = "ggg.db";
	public static final String EXPORT_USERDEFINE_GONGGUO_FILENAME = "ggg.csv";
	/**了凡四训*/
	public static final String LFSX_TXT = "lfsx.txt";
	/**了凡四训白话文*/
	public static final String LFSXBHW_TXT = "lfsxbhw.txt";
	/**功过格在SD卡上的目录 */
	public static final String GGG_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ggg";
	
	/**首页图片*/
	public static final String HOMG_IMG = "home.jpg";
	//进行恢复时，先对数据库文件进行备份，以便恢复失败时可以使用原来的数据。
	public static final String BACKUP_FOR_RESTORE_EXT = "_backup";
	
	public static final int COLOR_GONG = Color.RED;
	public static final int COLOR_GUO = Color.GREEN;
	
	public static final String DEFAULT_FONT_SIZE = "20";
	
	/**闹钟重复时间*/
	public static final long ALRAM_REPEAT_TIME_MS = 24*60*60*1000;//1天
	
	public final static boolean DEBUG = true;
	public static int LOGE(String TAG, String msg){
		if(DEBUG)
			return Log.e(TAG, msg);
		else
			return 0;
	}
	
	public static String getVersionName(Context context){
		String versionName = "";
		if(context == null)
			return "";
		try {
			
			 PackageManager pm = context.getPackageManager();
			
			 PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(),PackageManager.GET_CONFIGURATIONS);
			 versionName = pinfo.versionName;
			 } catch(NameNotFoundException e){
			 }
		return versionName;


	}
	
	public static String getBackupFilePath(){
//		return Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+BACKUP_FILENAME;
		return GGG_DIRECTORY_PATH+"/"+BACKUP_FILENAME;
	}
	
	public static String getExportUserGoneGuoFilePath(){
//		return Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+EXPORT_USERDEFINE_GONGGUO_FILENAME;
		return GGG_DIRECTORY_PATH+"/"+EXPORT_USERDEFINE_GONGGUO_FILENAME;
	}
	
	
	
	public static int copyFile(String fromFile, String toFile)
	{
		try 
		{
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) 
			{
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;
	
		} 
		catch (Exception ex) 
		{
			return -1;
		}
	}
	
	public static int parseInt(String str){
		int value = 0;
		if(str == null)
			return 0;
		
		try{
			value = Integer.parseInt(str);
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		
		return value;
	}
	
	/**
	 * 
	 * @param context
	 * @param triggerTimeMs 闹钟第一次提醒
	 * @param repeatTimeMs  闹钟循环提醒时间
	 */


}
