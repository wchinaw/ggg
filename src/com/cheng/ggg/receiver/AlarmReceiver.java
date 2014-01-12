package com.cheng.ggg.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.cheng.ggg.MainActivity;
import com.cheng.ggg.R;
import com.cheng.ggg.utils.AlarmNotification;
import com.cheng.ggg.utils.COM;

public class AlarmReceiver extends BroadcastReceiver {
	final String TAG = "AlarmReceiver";
	
    @Override
    public void onReceive(Context context, Intent intent) {

//    	Intent intent1 = new Intent(context, AlarmActivity.class);
//    	intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    	context.startActivity(intent1);
    	COM.LOGE(TAG," action:"+intent.getAction());
    	AlarmNotification.showNotification(context);

    }
    
         
        
    
}
