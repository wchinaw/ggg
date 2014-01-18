package com.cheng.ggg.types;

import android.app.Activity;
import android.util.Log;

import com.cheng.ggg.utils.TimeDate;

public class TimeRange {
	public int mStartTimeS = 0;
	public int mEndTimeS = 0;
	
	public long mStartTimeMS = 0;
    public long mEndTimeMS = 0;
    
    private String mToString;
    
//    public void setTimeRangeS(long startTimeMs, long endTimeMs){
//        mStartTimeMS = startTimeMs;
//        mEndTimeMS = endTimeMs;
//        
//        mStartTimeS= (int) (startTimeMs/1000L);
//        mEndTimeS = (int) (endTimeMs/1000L);
//    }
    
    public void setTimeRangeS(){
        mStartTimeS= (int) (mStartTimeMS/1000L);
        mEndTimeS = (int) (mEndTimeMS/1000L);
    }
	
	public String toString(Activity activity){
	    String str = TimeDate.getTimeRangeDateString(activity, this, 3);
	    Log.i("TimeRange",str);
	    return str;
	}
}
