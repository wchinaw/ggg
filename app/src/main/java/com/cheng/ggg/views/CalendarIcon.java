package com.cheng.ggg.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheng.ggg.R;
import com.cheng.ggg.utils.TimeDate;

import java.util.Hashtable;


public class CalendarIcon extends RelativeLayout {
        TextView dayTextView;
        View view;
        Context mContext;
        public CalendarIcon(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
                mContext = context;
                initView();
        }

        public CalendarIcon(Context context, AttributeSet attrs) {
                super(context, attrs);
                mContext = context;
                initView();
        }

        public CalendarIcon(Context context) {
                super(context);
                mContext = context;
                initView();
        }  
  
        private void initView(){
                view = View.inflate(mContext,R.layout.layout_calendar_icon, this);
                dayTextView = (TextView) view.findViewById(R.id.textViewCalendarDay);

        }

        public void setDay(int day){
                dayTextView.setText(day+"");
        }
  
} 