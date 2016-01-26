package com.cheng.ggg.views;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.AddConfirmActivity;
import com.cheng.ggg.GongGuoListActivity;
import com.cheng.ggg.MainActivity;
import com.cheng.ggg.MyApp;
import com.cheng.ggg.R;
import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.InsertGongGuoListener;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.Settings;
import com.cheng.ggg.views.calendar.CalendarActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cg on 16-1-22.
 */
public class GWidget extends AppWidgetProvider {
    Context mContext;
    ArrayList<UserGongGuo> mHotUserGongGuoList;
    public static final String ACTION_GRID_ITEM_CLICK = "com.cheng.ggg.ACTION_GRID_ITEM_CLICK";
    public static final String ACTION_GRID_ITEM_CALENDAR_CLICK = "com.cheng.ggg.ACTION_GRID_ITEM_CALENDAR_CLICK";
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.e("yao", "HelloWidgetProvider --> onReceive");
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        if(intent != null){
            String action = intent.getAction();
            if(ACTION_GRID_ITEM_CALENDAR_CLICK.equals(action)){//GridView item被点击
//                UserGongGuo gongguo = (UserGongGuo) intent.getSerializableExtra(COM.INTENT_GONGGUO);
//                gongguo.time = (int) (System.currentTimeMillis()/1000);
                int pos = intent.getIntExtra(COM.INTENT_TYPE,0);
                Log.e("","=========================1pos:"+pos);
                mHotUserGongGuoList = Settings.getHomeHotGongGuoList(mContext);
                if(mHotUserGongGuoList != null && pos>=0 && pos<mHotUserGongGuoList.size()){
                    UserGongGuo gongguo = mHotUserGongGuoList.get(pos);
                    if(gongguo != null){
                        CalendarActivity.startActvitiyForGongGuoDate(mContext, gongguo, true);
                    }
                }
            }
            else if(ACTION_GRID_ITEM_CLICK.equals(action)){
                int pos = intent.getIntExtra(COM.INTENT_TYPE,0);
                Log.e("","=========================2pos:"+pos);
                mHotUserGongGuoList = Settings.getHomeHotGongGuoList(mContext);
                if(mHotUserGongGuoList != null && pos>=0 && pos<mHotUserGongGuoList.size()){
                    UserGongGuo gongguo = mHotUserGongGuoList.get(pos);
                    if(gongguo != null){
                        boolean isCalendar = intent.getBooleanExtra(COM.INTENT_ISCALENDAR,false);
                        if(isCalendar){
                            CalendarActivity.startActvitiyForGongGuoDate(mContext, gongguo, true);
                        }
                        else{
                            gongguo.time = (int)(System.currentTimeMillis()/1000);
                            AddConfirmActivity.startActivity(mContext,gongguo);
                        }
                    }
                }
            }
        }
        super.onReceive(context, intent);
    }



    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i("yao", "HelloWidgetProvider --> onDeleted");
        mContext = context;
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.i("yao", "HelloWidgetProvider --> onEnabled");
        mContext = context;
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        mContext = context;
        super.onDisabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.i("yao", "HelloWidgetProvider --> onUpdate");
        mContext = context;
        boolean COLOR_SWAP = Settings.getIsColorSwap(mContext);
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new MyTime(context,appWidgetManager), 1, 60000);
        mHotUserGongGuoList = Settings.getHomeHotGongGuoList(mContext);
        int len = appWidgetIds.length;
        for(int i=0; i<len ; i++){
            RemoteViews remote = new RemoteViews(context.getPackageName(),
                    R.layout.widget);

            Intent intent = new Intent(context, MainActivity.class);// 设置Activity
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
            remote.setOnClickPendingIntent(R.id.textView, pendingIntent);
            remote.setTextViewText(R.id.textView, "hello in update.");
            SQLiteHelper mSQLiteHelper = SQLiteHelper.getInstance(mContext);
            SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
            int mGongCount = mSQLiteHelper.getUserGongCount(db);
            int mGuoCount = mSQLiteHelper.getUserGuoCount(db);
            int monthTotal = mGongCount+mGuoCount;

            setTextViewColorAndCount(remote,R.id.textViewGong, COLOR_SWAP, mGongCount);
            setTextViewColorAndCount(remote,R.id.textViewGuo,COLOR_SWAP,mGuoCount);
            setTextViewColorAndCount(remote,R.id.textViewTotal,COLOR_SWAP,monthTotal);

//            intent = new Intent(context, MainActivity.class);// 设置Activity
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
//            remote.setOnClickPendingIntent(R.id.textView, pendingIntent);

            Intent serviceIntent = new Intent(context, GridWidgetService.class);
//            serviceIntent.putExtra(COM.INTENT_LIST,mHotUserGongGuoList);
            remote.setRemoteAdapter(R.id.gridView, serviceIntent);

            Intent gridIntent = new Intent();
            gridIntent.setAction(ACTION_GRID_ITEM_CLICK);
            gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
            pendingIntent = PendingIntent.getBroadcast(context, 0, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 设置intent模板
            remote.setPendingIntentTemplate(R.id.gridView, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remote);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void setTextViewColorAndCount(RemoteViews remote,int viewId,boolean COLOR_SWAP, int count){
        if(viewId == 0)
            return;
        remote.setTextViewText(viewId, count + "");

        int gongColor = COM.COLOR_GONG;
        int guoColor = COM.COLOR_GUO;

        if(COLOR_SWAP){
            gongColor = COM.COLOR_GUO;
            guoColor = COM.COLOR_GONG;
        }


        if(count > 0){
            remote.setTextColor(viewId,gongColor);
        }
        else if(count < 0){
            remote.setTextColor(viewId,guoColor);
        }
        else {
            remote.setTextColor(viewId,Color.WHITE);
        }
    }

    public void guoTitleClick(View v){
        Log.i("","guoTitleClick");
//        if(mGuoCount==0){
//            Toast.makeText(this, R.string.empty_user_detaillist_guo, Toast.LENGTH_LONG).show();
//        }
//        else
//            CalendarActivity.startActvitiyForGongGuoDate(this, null, null, false, false);
    }

    public void gongTitleClick(View v){
        Log.i("","gongTitleClick");
//        if(mGongCount == 0){
//            Toast.makeText(this, R.string.empty_user_detaillist_gong, Toast.LENGTH_LONG).show();
//        }
//        else
//            CalendarActivity.startActvitiyForGongGuoDate(this,null,null,false,true);
    }

    public void totalTitleClick(View v){
        Log.i("","totalTitleClick");
//        if(mGuoCount==0 && mGongCount == 0){
//            Toast.makeText(this, R.string.empty_user_detaillist, Toast.LENGTH_LONG).show();
//        }
//        else
//            CalendarActivity.startActvitiyForGongGuoDate(this, null, null, false, true, true);
    }
}
