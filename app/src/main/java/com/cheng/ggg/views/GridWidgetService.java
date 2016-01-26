package com.cheng.ggg.views;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.cheng.ggg.R;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.Settings;
import com.cheng.ggg.utils.TimeDate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cg on 16-1-22.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GridWidgetService  extends RemoteViewsService {
    final String TAG = "GridWidgetService";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this, intent);
    }

    private class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;
        private int mAppWidgetId;
        int day = TimeDate.getToday();

        private String IMAGE_ITEM = "imgage_item";
        private String TEXT_ITEM = "text_item";
        private ArrayList<HashMap<String, Object>> data ;

        private String[] arrText = new String[]{
                "Picture 1", "Picture 2", "Picture 3",
                "Picture 4", "Picture 5", "Picture 6",
                "Picture 7", "Picture 8", "Picture 9"
        };


        ArrayList<UserGongGuo> mHotUserGongGuoList;

        /**
         * 构造GridRemoteViewsFactory
         * @author skywang
         */
        public GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.i(TAG, "GridRemoteViewsFactory mAppWidgetId:" + mAppWidgetId);

        }

        @Override
        public RemoteViews getViewAt(int position) {
            HashMap<String, Object> map;
            if(mHotUserGongGuoList == null || mHotUserGongGuoList.size() == 0)
                return null;

            // 获取 grid_view_item.xml 对应的RemoteViews
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.main_userdefine_hotbutton_widget);

            // 设置 第position位的“视图”的数据

            UserGongGuo gongguo= mHotUserGongGuoList.get(position);

            Log.i(TAG, "GridRemoteViewsFactory getViewAt:"+position+" name:"+gongguo.name+" times:"+gongguo.times);

            rv.setTextViewText(R.id.textView, gongguo.name);
            setTextViewColorAndCount(rv, R.id.textView, gongguo.count);
            rv.setTextViewText(R.id.textViewCalendarDay, gongguo.times + "");
//            setTextViewColorAndCount(rv, R.id.textViewCalendarDay, gongguo.count);
            // 设置 第position位的“视图”对应的响应事件
            Intent fillInIntent = new Intent();
            fillInIntent.setAction(GWidget.ACTION_GRID_ITEM_CLICK);
            fillInIntent.putExtra(COM.INTENT_TYPE, position);
            rv.setOnClickFillInIntent(R.id.layoutTextView, fillInIntent);

            Intent fillInIntent1 = new Intent();
            fillInIntent1.putExtra(COM.INTENT_TYPE, position);
            fillInIntent1.putExtra(COM.INTENT_ISCALENDAR,true);
            rv.setOnClickFillInIntent(R.id.calendarView, fillInIntent1);

//            Intent btIntent = new Intent().setAction(GWidget.ACTION_GRID_ITEM_CALENDAR_CLICK);
//            PendingIntent btPendingIntent = PendingIntent.getBroadcast(mContext, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            rv.setOnClickPendingIntent(R.id.calendarView, btPendingIntent);


            return rv;
        }

        public void setTextViewColorAndCount(RemoteViews rv,int textViewId, int count){
            if(textViewId == 0 || rv == null)
                return;

            int gongColor = COM.COLOR_GONG;
            int guoColor = COM.COLOR_GUO;
            boolean COLOR_SWAP = Settings.getIsColorSwap(mContext);
            if(COLOR_SWAP){
                gongColor = COM.COLOR_GUO;
                guoColor = COM.COLOR_GONG;
            }


            if(count > 0){
                rv.setTextColor(textViewId, gongColor);
//                view.setTextColor(gongColor);
            }
            else if(count < 0){
                rv.setTextColor(textViewId, guoColor);
//                view.setTextColor(guoColor);
            }
            else {
//                view.setTextColor(Color.WHITE);
                rv.setTextColor(R.id.textViewCalendarDay, Color.WHITE);
            }
        }

        @Override
        public void onCreate() {
            Log.i(TAG, "onCreate");
            // 初始化“集合视图”中的数据
            mHotUserGongGuoList = Settings.getHomeHotGongGuoList(mContext);
        }

        @Override
        public int getCount() {
            // 返回“集合视图”中的数据的总数
            if(mHotUserGongGuoList == null)
                return 0;
            else
                return mHotUserGongGuoList.size();
        }

        @Override
        public long getItemId(int position) {
            // 返回当前项在“集合视图”中的位置
            return position;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            // 只有一类 GridView
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onDataSetChanged() {
            mHotUserGongGuoList = Settings.getHomeHotGongGuoList(mContext);
        }

        @Override
        public void onDestroy() {
            if(mHotUserGongGuoList != null)
                mHotUserGongGuoList.clear();
        }
    }
}
