package com.cheng.ggg.tests;

import com.cheng.ggg.types.TimeRange;
import com.cheng.ggg.utils.TimeDate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends Activity
{
    final String TAG = "TestActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TimeRange currentYearMs = TimeDate.getCurrentYearRange();
        Log.e(TAG, "currentYearMs_start:"+TimeDate.intTime2DateAll(this, (int)(currentYearMs.mStartTimeS/1000L)));
        Log.e(TAG, "currentYearMs_end:"+TimeDate.intTime2DateAll(this, (int)(currentYearMs.mEndTimeS/1000L)));
        
        currentYearMs = TimeDate.getCurrentMonthRange();
        Log.e(TAG, "getCurrentMonthMs_start:"+TimeDate.intTime2DateAll(this, (int)(currentYearMs.mStartTimeS/1000L)));
        Log.e(TAG, "getCurrentMonthMs_end:"+TimeDate.intTime2DateAll(this, (int)(currentYearMs.mEndTimeS/1000L)));
        
        currentYearMs = TimeDate.getCurrentQuarterTimeRange();
        Log.e(TAG, "getCurrentQuarterTimeRange_start:"+TimeDate.intTime2DateAll(this, (int)(currentYearMs.mStartTimeS/1000L)));
        Log.e(TAG, "getCurrentQuarterTimeRange_end:"+TimeDate.intTime2DateAll(this, (int)(currentYearMs.mEndTimeS/1000L)));
        
        currentYearMs = TimeDate.getCurrentWeekRange();
        Log.e(TAG, "getCurrentWeekRange_start:"+TimeDate.intTime2DateAll(this, (int)(currentYearMs.mStartTimeS/1000L)));
        Log.e(TAG, "getCurrentWeekRange_end:"+TimeDate.intTime2DateAll(this, (int)(currentYearMs.mEndTimeS/1000L)));
        
    }

}
