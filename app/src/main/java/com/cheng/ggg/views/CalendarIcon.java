package com.cheng.ggg.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheng.ggg.MainActivity;
import com.cheng.ggg.R;
import com.cheng.ggg.utils.Settings;
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

//        @Override
//        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//                int width = measureWidth(widthMeasureSpec);
//                int height = measureHeight(heightMeasureSpec);
//
//                //设置宽高
//                setMeasuredDimension(width, height);
//                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
//
//        //根据xml的设定获取宽度
//        private int measureWidth(int measureSpec) {
//                int specMode = MeasureSpec.getMode(measureSpec);
//                int specSize = MeasureSpec.getSize(measureSpec);
//                //wrap_content
//                if (specMode == MeasureSpec.AT_MOST){
//
//                }
//                //fill_parent或者精确值
//                else if (specMode == MeasureSpec.EXACTLY){
//
//                }
//                Log.i("这个控件的宽度----------","specMode="+specMode+" specSize="+specSize);
//
//                return specSize;
//        }
//
//        //根据xml的设定获取高度
//        private int measureHeight(int measureSpec) {
//                int specMode = MeasureSpec.getMode(measureSpec);
//                int specSize = MeasureSpec.getSize(measureSpec);
//                //wrap_content
//                if (specMode == MeasureSpec.AT_MOST){
//
//                }
//                //fill_parent或者精确值
//                else if (specMode == MeasureSpec.EXACTLY){
//
//                }
//                Log.i("这个控件的高度----------","specMode:"+specMode+" specSize:"+specSize);
//
//                return specSize;
//        }

        //        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//                int width = measureWidth(widthMeasureSpec);
//                int height = measureHeight(heightMeasureSpec);
//
//                //设置宽高
//                setMeasuredDimension(width, height);
//        }
  
        private void initView(){
                view = View.inflate(mContext,R.layout.layout_calendar_icon, this);
                dayTextView = (TextView) view.findViewById(R.id.textViewCalendarDay);

        }

        public void setDay(int day){
                String str;
                if(day > 99999){
                    str = "99999";
                }
                else{
                    str = day+"";
                }
                dayTextView.setText(str);
//                MainActivity.setTextViewColorAndCount(dayTextView,day);
        }
  
} 