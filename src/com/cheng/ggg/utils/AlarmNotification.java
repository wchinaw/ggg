package com.cheng.ggg.utils;

import java.util.Date;

import com.cheng.ggg.MainActivity;
import com.cheng.ggg.R;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;

/**
 * 用于功过格闹钟提醒，每天定时提醒用户记录功过。
 * @author cg
 *
 */
public class AlarmNotification {
	public static final String TAG = "AlarmGGG";
	
	
	/**
	 * 	闹钟设计：

		将小时分钟 作为毫秒存储在 alarm_time
		将闹钟要闹的日期作为  alarm_day 存储
		
		在首次设置时 
		若 current_time > alarm_time  则是第二天提醒
		若 current_time<=alarm_time 今天闹
		
		在关机后开机时
		若 current_time > alarm_time  若 current_day == alarm_day 提醒之 并按首次设置 否则只按首次设置
		若 current_time<=alarm_time 若 current_day == alarm_day 则按首次设置 否则 提醒之，并按首次设置

	 * @param valueMs 当天从0时开始的毫秒数
	 */
	public static void setAlarm(Context context, long valueMs, long repeatTimeMs, boolean bFromBoot, boolean bShowTaost){
		
		Date todayDate = new Date(System.currentTimeMillis());
		Date currentDate = new Date(0, 0, 0, todayDate.getHours(), todayDate.getMinutes(), 0);			
		long curretnTimeMs = currentDate.getTime();
		
		long startTimeMs = 0;
		
		final int DAY_OF_MILISECONDS = 24*60*60*1000;
		final long CURRENT_MS = System.currentTimeMillis();
		
		/*
		 *
		如果首次设置时，小时和分钟 大于 目前的小时与分钟 则设置为现在之后的今天的时间则 startAlarmTime = 今天的alarmTime加今天完整日期毫秒数
		如果首次设置时，小时和分钟数 小于 目前的小时与分钟 则设置为明天的时间
		startAlarmTime = alarmTime加明天完整的日期毫秒数 
		 * 
		 * 
		 * 
		如果是关机之后开机：
		当前时间大于 startAlarmTime 则需要进行闹钟提醒，并计算下一次需要进行闹钟提醒的时间，即理论上应该按首次进行设置，但考虑到有repeatTime,如果repeatTime小于1天的话，则需要重新考虑。
		即下一次需要提醒的时间为  (现在时间-startAlarmTime)%repeatTime
		即下一次相对于现在需要进行提醒的时间是：
 		startAlarmTime = (现在时间-startAlarmTime)%repeatTime
		startAlarmTime  += 现在时间。
		 * 
		 */
		COM.LOGE(TAG, "valueMs:"+valueMs+" repeatTimeMs:"+repeatTimeMs+" bFromBoot:"+bFromBoot+" getAlarmDateMs:"+Settings.getAlarmDateMs(context));
				
		if(bFromBoot){//关机并开机时
			long alarmDate = Settings.getAlarmDateMs(context);
			if(CURRENT_MS >= alarmDate){
				//提醒 并计算下一次需要进行闹钟提醒的时间
				startTimeMs = (CURRENT_MS - alarmDate)%repeatTimeMs;
			}
			else{
				startTimeMs = (alarmDate - CURRENT_MS);
			}
		}
		else{//首次设置
			//现在的小时分钟数，大于要设置的小时分钟数，则是第二天开始。
			if(curretnTimeMs > valueMs){
				startTimeMs = (valueMs + DAY_OF_MILISECONDS) - curretnTimeMs;
			}
			else{
				startTimeMs = valueMs - curretnTimeMs;
			}
		}
		
		int startTimeS = (int) (startTimeMs / 1000);
		int s = startTimeS % 60;
		int m = (startTimeS/60)%60;
		int h = (startTimeS/60/60);		

		String info = "";
		if(h == 0){
			if(m == 0)
				info = String.format("%d秒 后提醒", s);
			else
				info = String.format("%d分%d秒 后提醒", m,s);
		}
		else{
			info = String.format("%d小时%d分%d秒 后提醒", h,m,s);
		}
		
		COM.LOGE(TAG, "onDialogClosed startTimeMs:"+startTimeMs+" "+info);
		
		if(bShowTaost)
			Toast.makeText(context, info, Toast.LENGTH_LONG).show();
		
		
		COM.LOGE(TAG, "startTimeMs:"+startTimeMs);
		startTimeMs += System.currentTimeMillis();
		Settings.setAlarmDateMs(context, startTimeMs);

		setAlarm(context,startTimeMs , repeatTimeMs);
		
	}
	
	
	private static  void setAlarm(Context context,long triggerTimeMs, long repeatTimeMs) {
		
//		cancelAlarm(context);
		
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent("android.alarm.ggg.action");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
//		am.cancel(pendingIntent);
		am.set(AlarmManager.RTC_WAKEUP, triggerTimeMs,
				pendingIntent);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				triggerTimeMs, repeatTimeMs,
				pendingIntent);
	}
	
	/**
	 * 取消闹钟
	 * @param context
	 */
	public static void cancelAlarm(Context context){
		Intent intent = new Intent("android.alarm.ggg.action"); 
		PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent,0); 
		AlarmManager alarm=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE); 
		alarm.cancel(pi); 
	}
	
	/**
     * 在状态栏显示通知
     */
    public static void showNotification(Context context){
        // 创建一个NotificationManager的引用   
        NotificationManager notificationManager = (NotificationManager)    
        		context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);   
         
        // 定义Notification的各种属性   
        Notification notification = new Notification(R.drawable.ggg_icon,   
                "每日功过提醒", System.currentTimeMillis()); 
        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT 通知放置在正在运行
        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中   
//        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用   
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;   
        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
        //DEFAULT_LIGHTS  使用默认闪光提示
        //DEFAULT_SOUNDS  使用默认提示声音
        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_LIGHTS; 
        //叠加效果常量
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;   
        notification.ledOnMS =5000; //闪光时间，毫秒
         
        // 设置通知的事件消息   
        CharSequence contentTitle ="每日功过提醒"; // 通知栏标题   
        CharSequence contentText ="请记得每天按时记录功过，今日事今日毕。"; // 通知栏内容   
        Intent notificationIntent =new Intent(context, MainActivity.class); // 点击该通知后要跳转的Activity
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, 0);   
        notification.setLatestEventInfo(context, contentTitle, contentText, contentItent);   
         
        // 把Notification传递给NotificationManager   
        notificationManager.notify(0, notification);   
    }
    //删除通知    
    public static void clearNotification(Context context){
        // 启动后删除之前我们定义的通知   
        NotificationManager notificationManager = (NotificationManager) context 
                .getSystemService(Context.NOTIFICATION_SERVICE);   
        notificationManager.cancel(0);  
 
    }
}
