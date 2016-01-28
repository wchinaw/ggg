package com.cheng.ggg.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.cheng.ggg.AboutActivity;
import com.cheng.ggg.R;
import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.UserGongGuo;


public class Settings {
	
	public static final int CHINESE_SIMPLE = 0;  //简体中文
	public static final int CHINESE_TRADITION = 1; //繁体中文
	
//	public static final String gongguoconfirm_dialog = "gongguoconfirm_dialog";
	public static final String is_enable_password = "is_enable_password";
	public static final String password = "password";
	public static final String defaultpic = "defaultpic";
	public static final String tips = "tips";
	public static final String fontSize = "fontSize";
	public static final String colorSwap = "colorSwap";//功过颜色互换
	public static final String alarm_time = "alarm_time";//定时提醒时间 小时分钟换算的毫秒数
	public static final String is_enable_alarm = "is_enable_alarm";
	public static final String alarm_date_ms = "alarm_date_ms";//定时提醒的完整日期的毫秒数
	public static final String repeat_time_ms = "repeat_time_ms";//重复提醒的时间间隔
	
	public static final String time_range_index = "time_range_index";//明细明显的时间范围 对应到arrays.xml list_date_range
	public static final String home_text_color = "home_text_color";//首页文字颜色

	public static final String hot_gongguo_list = "hot_gongguo_list";//首页的功过列表 格式如下 功过之间用 "|" 分隔,功过自己内容之间用 "~" 分隔
	
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
	
	public static int getHomeTextColorIndex(Context context){
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
        return sp.getInt(home_text_color, 0xFF777777);
    }
    
    public static void setHomeTextColorIndex(Context context,int color){
        setInt(context,home_text_color, color);
    }
	
	//明细明显的时间范围 对应到arrays.xml list_date_range
	public static int getTimeRangeIndex(Context context){
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
        return sp.getInt(time_range_index, 2); //默认为本月
    }
    
	//明细明显的时间范围 对应到arrays.xml list_date_range
    public static void setTimeRangeIndex(Context context,int index){
        setInt(context,time_range_index, index);
    }
	
	public static long getAlarmDateMs(Context context){
		return getLong(context,alarm_date_ms);
	}
	
	/**下一次闹钟时间*/
	public static void setAlarmDateMs(Context context,long date_ms){
		setLong(context,alarm_date_ms, date_ms);
	}
	/**下一次闹钟时间*/
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
		return sp.getLong(alarm_time, 0);
	}
	
	public static String getPassword(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context); 
		return sp.getString(password, "");
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

	/**首页功过快捷键*/
	public static ArrayList<UserGongGuo> getHomeHotGongGuoList(Context context){

		ArrayList<UserGongGuo> list = new ArrayList<UserGongGuo>();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String oriValue = sp.getString(hot_gongguo_list, "");
		if(oriValue != null){
			String[] arrays = oriValue.split("\\|");
			if(arrays != null){
				UserGongGuo gongguo;
				SQLiteHelper helper = SQLiteHelper.getInstance(context);
				SQLiteDatabase db = helper.getReadableDatabase();
				for(String item : arrays){
					if(item != null){
						String []subArray = item.split("\\~");
						if(subArray != null && subArray.length == 5){
							gongguo = new UserGongGuo();
							gongguo.parent_id = subArray[0];
							gongguo.parent_name = subArray[1];
							gongguo.name = subArray[2];
							gongguo.count = COM.parseInt(subArray[3]);
							gongguo.isUserDefine = COM.parseInt(subArray[4])==1?true:false;
							gongguo.todayCount = helper.getUserGongGuoCountByName(db,gongguo);
							list.add(gongguo);
						}
					}
				}
				if(db.isOpen())
					db.close();
			}
		}
		return list;
	}

	public static void setHomeHotGongGuo(Context context,ArrayList<UserGongGuo> list){
		if(list == null)
			return;

		String  newString = "";
		String item;
		for(UserGongGuo gongguo : list){
			if(gongguo != null){
				item = gongguo.parent_id+"~"+gongguo.parent_name+"~"+gongguo.name+"~"+gongguo.count+"~"+(gongguo.isUserDefine?1:0);
				if("".equals(newString)){
					newString=item;
				}
				else{
					newString+="|"+item;
				}
			}
		}
		setString(context, hot_gongguo_list, newString);
	}

	public static void addHomeHotGongGuo(Context context,ArrayList<UserGongGuo> list, GongGuoBase base,GongGuoDetail detail){
		if(list == null || base == null || detail == null || context == null)
			return;

		for(UserGongGuo gongguo : list){
			if(gongguo != null){
				if(COM.parseInt(gongguo.parent_id) == detail.id && gongguo.name.equals(detail.name) && gongguo.count == detail.count){
					Toast.makeText(context, R.string.exists_hot,Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String oriValue = sp.getString(hot_gongguo_list, "");
		int isUserDefine = detail.bUserdefine ? 1: 0;
		String addString = detail.id+"~"+base.name+"~"+detail.name+"~"+detail.count+"~"+isUserDefine;

		UserGongGuo gongguo = new UserGongGuo();
		gongguo.parent_name = base.name;
		gongguo.parent_id = detail.id+"";
		gongguo.name = detail.name;
		gongguo.count = detail.count;
		gongguo.isUserDefine = detail.bUserdefine;
		list.add(0,gongguo);

		if(oriValue != null && !"".equals(oriValue)){
			setString(context, hot_gongguo_list, oriValue+"|"+addString);
		}
		else{
			setString(context, hot_gongguo_list, addString);
		}
		Toast.makeText(context, R.string.addok,Toast.LENGTH_SHORT).show();
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

	public static String getBackupFilePath(){
//		return Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+BACKUP_FILENAME;
		return COM.GGG_DIRECTORY_PATH+"/"+"hotkey.sp";
	}

	public static void restore(Context context){

		File file = new File(getBackupFilePath());
		if(file.exists()){
			try {
				InputStream fosfrom = new FileInputStream(file);
				ByteArrayOutputStream fosto=new ByteArrayOutputStream();

				byte bt[] = new byte[1024];
				int c;
				while ((c = fosfrom.read(bt)) > 0)
				{
					fosto.write(bt, 0, c);
				}

				String value = fosto.toString("GBK");
				if(value != null && value.length()>5){
					setString(context, hot_gongguo_list, value);
				}

				fosfrom.close();
				fosto.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static boolean backUp(Context context) {
		boolean rc = true;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String hotKey = sp.getString(hot_gongguo_list, "");

		File file = new File(getBackupFilePath());
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				rc = false;
				e.printStackTrace();
			}
		}
		try {
			OutputStream fosto = new FileOutputStream(file);
			fosto.write(hotKey.getBytes("GBK"));
			fosto.flush();
			fosto.close();
		} catch (FileNotFoundException e) {
			rc = false;
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			rc = false;
			e.printStackTrace();
		} catch (IOException e) {
			rc = false;
			e.printStackTrace();
		}

		return rc;
	}

}
