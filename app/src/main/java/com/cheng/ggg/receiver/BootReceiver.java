package com.cheng.ggg.receiver;

import com.cheng.ggg.utils.AlarmNotification;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        COM.LOGE("GGG BootReceiver", "onReceive action:"+action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            //重新计算闹铃时间，并调第一步的方法设置闹铃时间及闹铃间隔时间
        	COM.LOGE("GGG BootReceiver", "BootReceiver ACTION_BOOT_COMPLETED");
        	COM.LOGE("GGG BootReceiver", "Settings.getIsEnableAlarm(context):"+Settings.getIsEnableAlarm(context));
        	COM.LOGE("GGG BootReceiver", "Settings.getAlarmTime(context):"+Settings.getAlarmTime(context));
        	if(Settings.getIsEnableAlarm(context)){
        		AlarmNotification.setAlarm(context, Settings.getAlarmTime(context),Settings.getRepeatTimeMs(context),true,false);
        	}
        	
        }
    }
}
