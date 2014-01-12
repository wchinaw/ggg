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
 * ���ڹ������������ѣ�ÿ�춨ʱ�����û���¼������
 * @author cg
 *
 */
public class AlarmNotification {
	public static final String TAG = "AlarmGGG";
	
	
	/**
	 * 	������ƣ�

		��Сʱ���� ��Ϊ����洢�� alarm_time
		������Ҫ�ֵ�������Ϊ  alarm_day �洢
		
		���״�����ʱ 
		�� current_time > alarm_time  ���ǵڶ�������
		�� current_time<=alarm_time ������
		
		�ڹػ��󿪻�ʱ
		�� current_time > alarm_time  �� current_day == alarm_day ����֮ �����״����� ����ֻ���״�����
		�� current_time<=alarm_time �� current_day == alarm_day ���״����� ���� ����֮�������״�����

	 * @param valueMs �����0ʱ��ʼ�ĺ�����
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
		����״�����ʱ��Сʱ�ͷ��� ���� Ŀǰ��Сʱ����� ������Ϊ����֮��Ľ����ʱ���� startAlarmTime = �����alarmTime�ӽ����������ں�����
		����״�����ʱ��Сʱ�ͷ����� С�� Ŀǰ��Сʱ����� ������Ϊ�����ʱ��
		startAlarmTime = alarmTime���������������ں����� 
		 * 
		 * 
		 * 
		����ǹػ�֮�󿪻���
		��ǰʱ����� startAlarmTime ����Ҫ�����������ѣ���������һ����Ҫ�����������ѵ�ʱ�䣬��������Ӧ�ð��״ν������ã������ǵ���repeatTime,���repeatTimeС��1��Ļ�������Ҫ���¿��ǡ�
		����һ����Ҫ���ѵ�ʱ��Ϊ  (����ʱ��-startAlarmTime)%repeatTime
		����һ�������������Ҫ�������ѵ�ʱ���ǣ�
 		startAlarmTime = (����ʱ��-startAlarmTime)%repeatTime
		startAlarmTime  += ����ʱ�䡣
		 * 
		 */
		COM.LOGE(TAG, "valueMs:"+valueMs+" repeatTimeMs:"+repeatTimeMs+" bFromBoot:"+bFromBoot+" getAlarmDateMs:"+Settings.getAlarmDateMs(context));
				
		if(bFromBoot){//�ػ�������ʱ
			long alarmDate = Settings.getAlarmDateMs(context);
			if(CURRENT_MS >= alarmDate){
				//���� ��������һ����Ҫ�����������ѵ�ʱ��
				startTimeMs = (CURRENT_MS - alarmDate)%repeatTimeMs;
			}
			else{
				startTimeMs = (alarmDate - CURRENT_MS);
			}
		}
		else{//�״�����
			//���ڵ�Сʱ������������Ҫ���õ�Сʱ�����������ǵڶ��쿪ʼ��
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
				info = String.format("%d�� ������", s);
			else
				info = String.format("%d��%d�� ������", m,s);
		}
		else{
			info = String.format("%dСʱ%d��%d�� ������", h,m,s);
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
	 * ȡ������
	 * @param context
	 */
	public static void cancelAlarm(Context context){
		Intent intent = new Intent("android.alarm.ggg.action"); 
		PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent,0); 
		AlarmManager alarm=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE); 
		alarm.cancel(pi); 
	}
	
	/**
     * ��״̬����ʾ֪ͨ
     */
    public static void showNotification(Context context){
        // ����һ��NotificationManager������   
        NotificationManager notificationManager = (NotificationManager)    
        		context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);   
         
        // ����Notification�ĸ�������   
        Notification notification = new Notification(R.drawable.ggg_icon,   
                "ÿ�չ�������", System.currentTimeMillis()); 
        //FLAG_AUTO_CANCEL   ��֪ͨ�ܱ�״̬���������ť�������
        //FLAG_NO_CLEAR      ��֪ͨ���ܱ�״̬���������ť�������
        //FLAG_ONGOING_EVENT ֪ͨ��������������
        //FLAG_INSISTENT     �Ƿ�һֱ���У���������һֱ���ţ�֪���û���Ӧ
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // ����֪ͨ�ŵ�֪ͨ����"Ongoing"��"��������"����   
//        notification.flags |= Notification.FLAG_NO_CLEAR; // �����ڵ����֪ͨ���е�"���֪ͨ"�󣬴�֪ͨ�������������FLAG_ONGOING_EVENTһ��ʹ��   
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;   
        //DEFAULT_ALL     ʹ������Ĭ��ֵ�������������𶯣������ȵ�
        //DEFAULT_LIGHTS  ʹ��Ĭ��������ʾ
        //DEFAULT_SOUNDS  ʹ��Ĭ����ʾ����
        //DEFAULT_VIBRATE ʹ��Ĭ���ֻ��𶯣������<uses-permission android:name="android.permission.VIBRATE" />Ȩ��
        notification.defaults = Notification.DEFAULT_LIGHTS; 
        //����Ч������
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;   
        notification.ledOnMS =5000; //����ʱ�䣬����
         
        // ����֪ͨ���¼���Ϣ   
        CharSequence contentTitle ="ÿ�չ�������"; // ֪ͨ������   
        CharSequence contentText ="��ǵ�ÿ�찴ʱ��¼�����������½��ձϡ�"; // ֪ͨ������   
        Intent notificationIntent =new Intent(context, MainActivity.class); // �����֪ͨ��Ҫ��ת��Activity
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, 0);   
        notification.setLatestEventInfo(context, contentTitle, contentText, contentItent);   
         
        // ��Notification���ݸ�NotificationManager   
        notificationManager.notify(0, notification);   
    }
    //ɾ��֪ͨ    
    public static void clearNotification(Context context){
        // ������ɾ��֮ǰ���Ƕ����֪ͨ   
        NotificationManager notificationManager = (NotificationManager) context 
                .getSystemService(Context.NOTIFICATION_SERVICE);   
        notificationManager.cancel(0);  
 
    }
}
