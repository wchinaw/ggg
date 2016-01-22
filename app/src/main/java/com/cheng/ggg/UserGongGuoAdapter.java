package com.cheng.ggg;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.TimeDate;
import com.cheng.ggg.views.CalendarIcon;
import com.cheng.ggg.views.calendar.CalendarActivity;

import java.util.ArrayList;

/**
 * Created by cg on 16-1-20.
 */
public class UserGongGuoAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Activity mActivity;
    ArrayList<UserGongGuo> mUserGongGuoList;
    String strTotal,strGong,strGuo;
    String strTimes = "";
    int day = TimeDate.getToday();

    public UserGongGuoAdapter(Activity activity){
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
        strTimes = mActivity.getString(R.string.times);
        strTotal = mActivity.getString(R.string.total);
        strGong = mActivity.getString(R.string.gong);
        strGuo = mActivity.getString(R.string.guo);
    }

    public void setList(ArrayList<UserGongGuo> list){
        mUserGongGuoList = list;
    }

    public ArrayList<UserGongGuo> getList(){
        if(mUserGongGuoList != null){
            return mUserGongGuoList;
        }
        else{
            mUserGongGuoList = new ArrayList<UserGongGuo>();
        }
        return mUserGongGuoList;
    }

    public int getCount() {
        if(mUserGongGuoList != null)
            return mUserGongGuoList.size();
        return 0;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View view, ViewGroup arg2) {

        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.listview_user_detail, null);
            holder.layoutDayInfo = (RelativeLayout) view.findViewById(R.id.layoutDayInfo);
            holder.layoutTimesCalendar = view.findViewById(R.id.layoutTimesCalendar);
            holder.layoutTimesCalendar.setOnClickListener(mCalendarClickListener);
            holder.layoutTimesCalendar.setTag(holder);

            CalendarIcon icon = (CalendarIcon) view.findViewById(R.id.imageViewCalendar);
            icon.setDay(day);

            holder.titleDate = (TextView) view.findViewById(R.id.titleDate);
            holder.titleCount = (TextView) view.findViewById(R.id.titleCount);
            holder.name = (TextView) view.findViewById(R.id.TextItemName);
            holder.date = (TextView) view.findViewById(R.id.TextItemDate);
            holder.times = (TextView) view.findViewById(R.id.TextViewGuoTitle);
            holder.comment = (TextView) view.findViewById(R.id.TextViewComments);

            holder.name.setTextSize(MainActivity.TEXT_SIZE);
            holder.date.setTextSize(MainActivity.TEXT_SIZE-5);
            holder.times.setTextSize(MainActivity.TEXT_SIZE);
            holder.comment.setTextSize(MainActivity.TEXT_SIZE-3);

            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        UserGongGuo gongguo = mUserGongGuoList.get(position);

        if(gongguo.isFirst){
            holder.layoutDayInfo.setVisibility(View.VISIBLE);
            holder.titleDate.setText(gongguo.todayInfo);
            holder.titleCount.setText(strTotal+" "+gongguo.todayCount);
        }
        else{
            holder.layoutDayInfo.setVisibility(View.GONE);
        }

        holder.name.setText(gongguo.parent_name+" "+gongguo.name);
        holder.date.setText(TimeDate.intTime2HourMinute(mActivity, gongguo.time));
        if(gongguo.times>1)
            holder.times.setText(gongguo.times+strTimes);
        else
            holder.times.setText("");
        if(gongguo.comment == null || gongguo.comment.equals("")){
            holder.comment.setVisibility(View.GONE);
        }
        else{
            holder.comment.setText("  "+gongguo.comment);//strComment+
            holder.comment.setVisibility(View.VISIBLE);
        }

        holder.position = position;
        setGongGuoColor(holder.name,gongguo.count);
        setGongGuoColor(holder.titleCount,gongguo.todayCount);
//			if(gongguo.count > 0){
//				if(MainActivity.COLOR_SWAP){
//					holder.name.setTextColor(COM.COLOR_GUO);
//				}
//				else{
//					holder.name.setTextColor(COM.COLOR_GONG);
//				}
//			}
//			else{
//				if(MainActivity.COLOR_SWAP)
//					holder.name.setTextColor(COM.COLOR_GONG);
//				else
//					holder.name.setTextColor(COM.COLOR_GUO);
//			}

//			if(gongguo.todayCount > 0){
//				if(MainActivity.COLOR_SWAP){
//					holder.titleCount.setTextColor(COM.COLOR_GUO);
//				}
//				else{
//					holder.titleCount.setTextColor(COM.COLOR_GONG);
//				}
//			}
//			else{
//				if(MainActivity.COLOR_SWAP)
//					holder.titleCount.setTextColor(COM.COLOR_GONG);
//				else
//					holder.titleCount.setTextColor(COM.COLOR_GUO);
//			}

        return view;
    }

    public class ViewHolder{
        RelativeLayout layoutDayInfo; //每日信息
        View layoutTimesCalendar;//点击之后进入日历界面
        TextView titleDate;//每天的日期
        TextView titleCount; //每天功过统计
        TextView name;
        TextView date;
        TextView times;
        TextView comment;
        int position;
    }

    View.OnClickListener mCalendarClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if(holder != null){
                UserGongGuo gongguo = mUserGongGuoList.get(holder.position);
                if(gongguo != null){
                    GongGuoBase base = new GongGuoBase();
                    base.name = gongguo.parent_name;
                    base.count = gongguo.count;

                    GongGuoDetail detail = new GongGuoDetail();
                    detail.id = COM.parseInt(gongguo.parent_id);
                    detail.name = gongguo.name;
                    CalendarActivity.startActvitiyForGongGuoDate(mActivity, base, detail, true, (gongguo.count > 0) ? true : false); // 单一具体的功过
                }
            }
        }

    };

    public static void setGongGuoColor(TextView textView,int count)
    {
        if(count > 0){
            if(MainActivity.COLOR_SWAP){
                textView.setTextColor(COM.COLOR_GUO);
            }
            else{
                textView.setTextColor(COM.COLOR_GONG);
            }
        }
        else{
            if(MainActivity.COLOR_SWAP)
                textView.setTextColor(COM.COLOR_GONG);
            else
                textView.setTextColor(COM.COLOR_GUO);
        }
    }


}
