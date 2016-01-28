package com.cheng.ggg.views;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cg on 16-1-22.
 */
public class GWidget extends AppWidgetProvider {
    Context mContext;
    ArrayList<UserGongGuo> mHotUserGongGuoList;
    public static final String ACTION_GRID_ITEM_CLICK = "com.cheng.ggg.ACTION_GRID_ITEM_CLICK";
    public static final String ACTION_WIDGET_UPDATE_BY_DATACHANGE = "com.cheng.ggg.ACTION_WIDGET_UPDATE_BY_DATACHANGE";

    int request = 0;
    public static int randomNumber = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.e("yao", "HelloWidgetProvider --> onReceive appWidgetId:"+appWidgetId);

        if(intent != null){
            String action = intent.getAction();
            Log.e("yao", "action --> "+action);
            if(ACTION_WIDGET_UPDATE_BY_DATACHANGE.equals(action)){
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                int ids[]=manager.getAppWidgetIds(new ComponentName(mContext.getPackageName(), this.getClass().getName()));
                update(context, AppWidgetManager.getInstance(context), ids);
            }
            else if(COM.BROADCAST_WIDGET_UPDATE.equals(action)){

            }

            else if(ACTION_GRID_ITEM_CLICK.equals(action)){
                int pos = intent.getIntExtra(COM.INTENT_TYPE,0);
                Log.e("","=====ACTION_GRID_ITEM_CLICK===2pos:"+pos);
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

        update(context, appWidgetManager, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public synchronized void update(Context context, AppWidgetManager appWidgetManager,
                       int[] appWidgetIds){
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

//            Intent intent = new Intent(context, MainActivity.class);// 设置Activity
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
//            remote.setOnClickPendingIntent(R.id.textView, pendingIntent);
//            remote.setTextViewText(R.id.textView, "hello in update.");

//            Intent intent = new Intent(mContext, CalendarActivity.class);
//            intent.putExtra(COM.INTENT_ISDETAIL,false);
//            intent.putExtra(COM.INTENT_GONG, false);
//            intent.putExtra(COM.INTENT_ISTOTAL, true);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
//            remote.setOnClickPendingIntent(R.id.linearLayoutTotal, pendingIntent);
            if(request >= 1000000){
                request = 0;
            }

            Intent intent = new Intent(mContext, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, request++,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
            remote.setOnClickPendingIntent(R.id.layoutall, pendingIntent);

            intent = new Intent(mContext, MainActivity.class);
            pendingIntent = PendingIntent.getActivity(context, request++,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
            remote.setOnClickPendingIntent(R.id.linearLayoutTotal, pendingIntent);

            intent = new Intent(mContext, CalendarActivity.class);
            intent.putExtra(COM.INTENT_ISDETAIL,false);
            intent.putExtra(COM.INTENT_GONG,true);
            intent.putExtra(COM.INTENT_ISTOTAL,false);
            pendingIntent = PendingIntent.getActivity(context, request++,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
            remote.setOnClickPendingIntent(R.id.linearLayoutGong, pendingIntent);

            intent = new Intent(mContext, CalendarActivity.class);
            intent.putExtra(COM.INTENT_ISDETAIL,false);
            intent.putExtra(COM.INTENT_GONG, false);
            intent.putExtra(COM.INTENT_ISTOTAL,false);
            pendingIntent = PendingIntent.getActivity(context, request++,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
            remote.setOnClickPendingIntent(R.id.linearLayoutGuo, pendingIntent);


            intent = new Intent(context,GongGuoListActivity.class);
            intent.putExtra(COM.INTENT_GONG, true);
            intent.putExtra(COM.INTENT_TYPE, GongGuoListActivity.TYPE_HOT_GONG_GUO_SELECT);
            intent.putExtra(COM.INTENT_LIST,mHotUserGongGuoList);
            pendingIntent = PendingIntent.getActivity(context, request++,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
            remote.setOnClickPendingIntent(R.id.imageButtonHotSelect, pendingIntent);


            SQLiteHelper mSQLiteHelper = SQLiteHelper.getInstance(mContext);
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
            int mGongCount = mSQLiteHelper.getUserGongCount(db);
            int mGuoCount = mSQLiteHelper.getUserGuoCount(db);
            db.close();
            int monthTotal = mGongCount+mGuoCount;

            setTextViewColorAndCount(remote,R.id.textViewGong, COLOR_SWAP, mGongCount);
            setTextViewColorAndCount(remote,R.id.textViewGuo,COLOR_SWAP,mGuoCount);
            setTextViewColorAndCount(remote,R.id.textViewTotal,COLOR_SWAP,monthTotal);

//            intent = new Intent(context, MainActivity.class);// 设置Activity
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                    intent, PendingIntent.FLAG_UPDATE_CURRENT);//
//            remote.setOnClickPendingIntent(R.id.textView, pendingIntent);

//            Intent serviceIntent = new Intent(context, GridWidgetService.class);
//            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//            remote.setRemoteAdapter(appWidgetIds[i], R.id.gridView, serviceIntent);
//
//            Intent gridIntent = new Intent();
//            gridIntent.setAction(ACTION_GRID_ITEM_CLICK);
//            gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
//
//            pendingIntent = PendingIntent.getBroadcast(context, request++, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            // 设置intent模板
//            remote.setPendingIntentTemplate(R.id.gridView, pendingIntent);

            addItems(remote, mHotUserGongGuoList, COLOR_SWAP);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remote);
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.gridView);
        }
    }

    int layoutIds[]={R.id.layout0,R.id.layout1,R.id.layout2};
    int layoutTextIds[]={R.id.layoutText0,R.id.layoutText1,R.id.layoutText2};
    int textIds[]={R.id.textView0,R.id.textView1,R.id.textView2};
    int calendarIds[] = {R.id.calendarView0,R.id.calendarView1,R.id.calendarView2};
    int calendarTextIds[]={R.id.textViewCalendarDay0,R.id.textViewCalendarDay1,R.id.textViewCalendarDay2};

    public void addItems(RemoteViews remote, ArrayList<UserGongGuo> list,boolean COLOR_SWAP){
        if(list != null && list.size()>0){
            remote.removeAllViews(R.id.gridView);
            int len = list.size();
            int lines = len/3;
            if(len%3>0){
                lines++;
            }
            int index=0;
            for(int i=0; i<lines; i++){
                RemoteViews r = new RemoteViews(mContext.getPackageName(),
                        R.layout.main_userdefine_hotbutton_widget);
                remote.addView(R.id.gridView,r);
                for(int j=0; j<3; j++){
                    index = i*3+j;
                    if(i*3+j<len){
                        UserGongGuo gongguo = list.get(index);
                        r.setTextViewText(textIds[j], gongguo.name);
                        setTextViewColor(r, textIds[j], COLOR_SWAP, gongguo.count);
                        r.setTextViewText(calendarTextIds[j], gongguo.todayCount + "");
                        r.setViewVisibility(layoutIds[j], View.VISIBLE);
                        setOnClickLisenterTextView(r, gongguo, layoutTextIds[j]);
                        setOnClickLisenterCalendar(r, gongguo, calendarIds[j]);

                    }
                }
            }

        }

    }

    public void setOnClickLisenterCalendar(RemoteViews remote,UserGongGuo gongguo, int id){

        Intent intent = new Intent(mContext,CalendarActivity.class);
        GongGuoBase base = new GongGuoBase();
        base.name = gongguo.parent_name;
        base.count = gongguo.count;

        GongGuoDetail detail = new GongGuoDetail();
        detail.name = gongguo.name;
        detail.id = COM.parseInt(gongguo.parent_id);
        detail.count = gongguo.count;

        intent.putExtra(COM.INTENT_GONGGUOBASE,base);
        intent.putExtra(COM.INTENT_GONGGUODETAIL,detail);
        intent.putExtra(COM.INTENT_ISDETAIL,true);
        intent.putExtra(COM.INTENT_GONG,detail.count > 0 ? true : false);
        intent.putExtra(COM.INTENT_ISTOTAL,false);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, request++,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);//
        remote.setOnClickPendingIntent(id, pendingIntent);
    }

    public void setOnClickLisenterTextView(RemoteViews remote,UserGongGuo gongguo, int id){
//        Intent gridIntent = new Intent();
//        gridIntent.setAction(ACTION_GRID_ITEM_CLICK);
//        gridIntent.putExtra(COM.INTENT_TYPE, index);
//         PendingIntent   pendingIntent = PendingIntent.getBroadcast(mContext, request++, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remote.setOnClickPendingIntent(id,pendingIntent);
        Intent intent = new Intent(mContext,AddConfirmActivity.class);
        intent.putExtra(COM.INTENT_GONGGUO, gongguo);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, request++,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);//
        remote.setOnClickPendingIntent(id, pendingIntent);
    }

    public void setTextViewColor(RemoteViews remote,int viewId,boolean COLOR_SWAP, int count){
        if(viewId == 0)
            return;

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
        Log.i("", "guoTitleClick");
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

    public static void updateBroadcast(Context context){
        if(context == null)
            return;
        Intent intent = new Intent(GWidget.ACTION_WIDGET_UPDATE_BY_DATACHANGE);
        context.sendBroadcast(intent);
    }
}
