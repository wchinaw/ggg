package com.cheng.ggg.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.cheng.ggg.R;
import com.cheng.ggg.types.TimeRange;

/**日期，时间相关函数*/
public class TimeDate {
    
    public static final int MODE_CURRENT = 0; //正常情况下
    public static final int MODE_TO_PRE = -1; //前一个周期
    public static final int MODE_TO_NEXT = 1; //后一个周期
    
    public static final long ONE_DAY_S = 24*60*60;
    public static final long ONE_DAY_MS = ONE_DAY_S*1000;

	public static final int CURRENT_YEAR = 0;
	public static final int CURRENT_QUARTER  = 1;
	public static final int CURRENT_MONTH = 2;
	public static final int CURRENT_WEEK = 3;
    
	public static String twoZeroPre(int intValue){
		String value = String.format("%02d", intValue);
		return value;
	}
	
	public static String intTime2TodayInfo(Activity activity, int value, String weekDayArray[]){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(value*1000L);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String date = calendar.get(Calendar.YEAR)+"-"
                +twoZeroPre((calendar.get(Calendar.MONTH)+1))+"-"
                +twoZeroPre(calendar.get(Calendar.DAY_OF_MONTH))+" "+weekDayArray[dayOfWeek-1];
        
        
        return date;
    }
	
	public static String intTime2DateAll(Activity activity, int value){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(value*1000L);
		
		String date = calendar.get(Calendar.YEAR)+activity.getString(R.string.year)
				+twoZeroPre((calendar.get(Calendar.MONTH)+1))+activity.getString(R.string.month)
				+twoZeroPre(calendar.get(Calendar.DAY_OF_MONTH))+activity.getString(R.string.day)+" "
				+twoZeroPre(calendar.get(Calendar.HOUR_OF_DAY))+activity.getString(R.string.hour)
				+twoZeroPre(calendar.get(Calendar.MINUTE))+activity.getString(R.string.minute)
				+twoZeroPre(calendar.get(Calendar.SECOND))+activity.getString(R.string.second);
		
		return date;
	}
	
	/**
	 * 完整日期的前半部分 年 月 日
	 * @param activity
	 * @param value
	 * @return
	 */
	public static String intTime2Date1(Context activity, Calendar calendar){
		
		String date = calendar.get(Calendar.YEAR)+activity.getString(R.string.year)
				+twoZeroPre((calendar.get(Calendar.MONTH)+1))+activity.getString(R.string.month)
				+twoZeroPre(calendar.get(Calendar.DAY_OF_MONTH))+activity.getString(R.string.day);
		
		return date;
	}
	
	/**
	 * 完整日期的前半部分 年 月 日
	 * @param activity
	 * @param value
	 * @return
	 */
	public static String intTime2Date1(Context activity, int value){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(value*1000L);
		String date = calendar.get(Calendar.YEAR)+activity.getString(R.string.year)
				+twoZeroPre((calendar.get(Calendar.MONTH)+1))+activity.getString(R.string.month)
				+twoZeroPre(calendar.get(Calendar.DAY_OF_MONTH))+activity.getString(R.string.day);
		
		return date;
	}
	
	/**
	 * 完整日期的后半部分 时 分
	 * @param activity
	 * @param value
	 * @return
	 */
	public static String intTime2Date2(Context activity, Calendar calendar){
		
		String date = twoZeroPre(calendar.get(Calendar.HOUR_OF_DAY))+activity.getString(R.string.hour)
				+twoZeroPre(calendar.get(Calendar.MINUTE))+activity.getString(R.string.minute);
		
		return date;
	}
	
	/**
	 * 完整日期的后半部分 时 分
	 * @param activity
	 * @param value
	 * @return
	 */
	public static String intTime2Date2(Context activity, int value){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(value*1000L);
		
		String date = twoZeroPre(calendar.get(Calendar.HOUR_OF_DAY))+activity.getString(R.string.hour)
				+twoZeroPre(calendar.get(Calendar.MINUTE))+activity.getString(R.string.minute);
		
		return date;
	}
	
	/**简洁格式*/
	public static String intTime2HourMinute(Activity activity, int value){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(value*1000L);
        
        String date = 
                twoZeroPre(calendar.get(Calendar.HOUR_OF_DAY))+":"
                +twoZeroPre(calendar.get(Calendar.MINUTE));
        
        return date;
    }

	public static int getToday() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	//////////////////////////////////////////////////////////////////////////
	//用于明细和统计
	//
	
	public static TimeRange getCurrentYearRange(){
	    return getCurrentYearRange(null,MODE_CURRENT);
	}
	//本年
	public static TimeRange getCurrentYearRange(TimeRange range , int mode){
	    
	    Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        int startYear;
        
	    if(range == null){
	        range = new TimeRange();
	        long currentMs = System.currentTimeMillis();
	        calendar.setTimeInMillis(currentMs);
	    }
	    else{
	        calendar.setTimeInMillis(range.mStartTimeMS);
	    }
		
	    startYear = calendar.get(Calendar.YEAR)+mode;   
		
		calendar.set(startYear, 0, 1, 0, 0, 0);
		range.mStartTimeMS = calendar.getTimeInMillis();
		calendar.set(startYear +1, 0, 1, 0, 0, 0);
		range.mEndTimeMS = calendar.getTimeInMillis();
		range.setTimeRangeS();
		
		return range;
	}
	
	private static TimeRange getCurrentQuarterTimeRange(Calendar calendar,int year, int startMonth)
	{
		TimeRange range = new TimeRange();
		calendar.set(year, startMonth, 1, 0, 0, 0);
		range.mStartTimeMS = calendar.getTimeInMillis();
		calendar.set(year, startMonth+3, 1, 0, 0, 0);
		range.mEndTimeMS = calendar.getTimeInMillis();
		range.setTimeRangeS();
		return range;
	}
	
	public static void test(Activity activity){
		long currentMs = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(currentMs);
		
		int year = calendar.get(Calendar.YEAR);
		calendar.set(Calendar.YEAR, year);
		Log.e("",intTime2DateAll(activity, (int)(calendar.getTimeInMillis()/1000)));
//		for(int i=1; i<=12; i++){
////			calendar.set(year, i+1, 1, 0, 0, 0);
//			Log.e("",);
//		}
		
		
	}
	
	//本季 三个月
	public static TimeRange getCurrentQuarterTimeRange(){
	    return getCurrentQuarterTimeRange(null, MODE_CURRENT);
	}
	public static TimeRange getCurrentQuarterTimeRange(TimeRange range, int mode){
		
	    Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
		if(range == null){
            range = new TimeRange();
            long currentMs = System.currentTimeMillis();
            calendar.setTimeInMillis(currentMs);
		}
		else{
		    calendar.setTimeInMillis(range.mStartTimeMS);
		}
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		
		mode *= 3;  //往前三个月 或往后三个月
		month += mode;
		
		if(month < 0){ //month小于0 则往前一年。
			month+=12;
			year--;
		}
		else if(month>11){
			year++;
			month-=11;
		}
		
		switch(month){
		case 0:	case 1:	case 2:
			range = getCurrentQuarterTimeRange(calendar,year,0);
			break;
		case 3:	case 4:	case 5:
			range = getCurrentQuarterTimeRange(calendar,year,3);
			break;
		case 6:	case 7:	case 8:
			range = getCurrentQuarterTimeRange(calendar,year,6);
			break;
		case 9: case 10: case 11:
			range = getCurrentQuarterTimeRange(calendar,year,9);
			break;
		default :
			range = getCurrentQuarterTimeRange(calendar,year,0);
			break;
		}
		
		return range;
	}
	
	public static TimeRange getCurrentMonthRange(){
        return getCurrentMonthRange(null, MODE_CURRENT);
    }
	//本月
		public static TimeRange getCurrentMonthRange(TimeRange range, int mode){
			
		    Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            int startMonth;
            
		    if(range == null){
		        range = new TimeRange();
		        long currentMs = System.currentTimeMillis();
	            calendar.setTimeInMillis(currentMs);
		    }
		    else{
		        calendar.setTimeInMillis(range.mStartTimeMS); 
		    }
		    
		    startMonth = calendar.get(Calendar.MONTH)+mode;			
			int year = calendar.get(Calendar.YEAR);
			calendar.set(year, startMonth , 1, 0, 0, 0);
			range.mStartTimeMS = calendar.getTimeInMillis();
			
			calendar.set(year, startMonth +1, 1, 0, 0, 0);
			range.mEndTimeMS = calendar.getTimeInMillis();
			range.setTimeRangeS();
			return range;
		}
		
		public static boolean isToday(long todayMs, long timeMs)
		{
		    int todayYear,todayMonth,todayDay;
		    int year,month,day;
		    Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            calendar.setTimeInMillis(todayMs);
            
            todayYear = calendar.get(Calendar.YEAR);
            todayMonth = calendar.get(Calendar.MONTH);
            todayDay = calendar.get(Calendar.DAY_OF_MONTH);
            
            calendar.setTimeInMillis(timeMs);
            
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            
            if(todayYear == year && todayMonth == month && todayDay == day){
                return true;
            }
            else
                return false;
		}
		
		/**
		 * 获取当前日期的天 DAY_OF_MONTH
		 * ChenGang
		 * 2014-1-23
		 * @param range
		 * @param mode
		 * @return
		 */
		public static String getCurrentDay(int time){
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            int day;
            
            calendar.setTimeInMillis(time*1000L); 
            
            day = calendar.get(Calendar.DAY_OF_MONTH);
            
            return day+"";
        }
		
		/**
		 * 获取当前日期的天 DAY_OF_MONTH
		 * ChenGang
		 * 2014-1-23
		 * @param range
		 * @param mode
		 * @return
		 */
		public static String getCurrentMonth(int time){
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            int month;
            
            calendar.setTimeInMillis(time*1000L); 
            
            month = calendar.get(Calendar.MONTH)+1;
            
            return month+"";
        }
		
		/**
		 * 获取一天之内的起始时间和结束时间 用于按天分组排序。
		 * ChenGang
		 * 2014-1-18
		 * @param range
		 * @param mode
		 * @return
		 */
		public static int getCurrentDayStartTimeS(int time, int mode){
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            int startDay;
            
            calendar.setTimeInMillis(time*1000L); 
            
            startDay = calendar.get(Calendar.DAY_OF_MONTH)+mode;         
            
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) , startDay , 0, 0, 0);
            return (int) (calendar.getTimeInMillis()/1000);
        }
		
		/**
		 * 获取一天之内的起始时间和结束时间 用于按天分组排序。
		 * ChenGang
		 * 2014-1-18
		 * @param range
		 * @param mode
		 * @return
		 */
		public static TimeRange getCurrentDayRange(TimeRange range, int mode){
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            int startDay;
            
            if(range == null){
                range = new TimeRange();
                long currentMs = System.currentTimeMillis();
                calendar.setTimeInMillis(currentMs);
            }
            else{
                calendar.setTimeInMillis(range.mStartTimeMS); 
            }
            
            startDay = calendar.get(Calendar.DAY_OF_MONTH)+mode;         
            
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) , startDay , 0, 0, 0);
            range.mStartTimeMS = calendar.getTimeInMillis();
            
            calendar.set(calendar.get(Calendar.YEAR), startDay +1, 1, 0, 0, 0);
            range.mEndTimeMS = calendar.getTimeInMillis();
            range.setTimeRangeS();
            return range;
        }
	
//	//本周
		public static TimeRange getCurrentWeekRange(){
	        return getCurrentWeekRange(null, MODE_CURRENT);
	    }
	public static TimeRange getCurrentWeekRange(TimeRange range, int mode){
	    
	    Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
	    
	    if(range == null){
	        range = new TimeRange();
	        long currentMs = System.currentTimeMillis();
	        calendar.setTimeInMillis(currentMs);
	    }
	    else{
	        calendar.setTimeInMillis(range.mStartTimeMS);
	    }
		
//		calendar.set(calendar.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		//周日 SUNDAY 1  MONDAY 2 ... 周六7  外国日历
		//中国日历 MONDAY 1 ...周日7
		
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if(dayOfWeek == Calendar.SUNDAY){//如果是周日 进行换算周一的位置
		    dayOfMonth -= 6;
		}
		else{
		    dayOfMonth -= (dayOfWeek-Calendar.MONDAY);
		}
		
		mode*=7; //往前7天，往后7天
		dayOfMonth += mode;
		
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		range.mStartTimeMS = calendar.getTimeInMillis();
		
		dayOfMonth += 7;
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        range.mEndTimeMS = calendar.getTimeInMillis();
        range.setTimeRangeS();
		
		//本周
//		Log.e("", "dayOfMonth："+dayOfMonth +" dayOfWeek:"+dayOfWeek);
		
		return range;//calendar.getTimeInMillis(); 
	}
	
	public static Calendar getCalendar(long timeMs)
	{
	    Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(timeMs);
        return calendar;
	}
	
	public static TimeRange getTimeRangeByIndex(TimeRange range,int index, int mode){
	    switch(index){
	        case CURRENT_YEAR: //本年
	            return getCurrentYearRange(range, mode);
	        case CURRENT_QUARTER: //本季
	            return getCurrentQuarterTimeRange(range, mode);
	        case CURRENT_WEEK: //本周
	            return getCurrentWeekRange(range, mode);
	        case CURRENT_MONTH: //本月 默认本月
	        default :
	            return getCurrentMonthRange(range, mode);
	    }
	}
	
	public static String getTimeRangeDateString(Activity activity, TimeRange range, int index)
	{
	    Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(range.mStartTimeMS);
        
        String to = activity.getString(R.string.date_to);
        
        String date;
        
        switch(index){
            case 0: //本年
            case 1: //本季
                date = calendar.get(Calendar.YEAR)+activity.getString(R.string.year)
                +twoZeroPre((calendar.get(Calendar.MONTH)+1))+activity.getString(R.string.month);
                
                date += to;
                
                calendar.setTimeInMillis(range.mEndTimeMS-10000);
                date += calendar.get(Calendar.YEAR)+activity.getString(R.string.year)
                +twoZeroPre((calendar.get(Calendar.MONTH)+1))+activity.getString(R.string.month);
                break;
            case 3: //本周
                 date = twoZeroPre(calendar.get(Calendar.YEAR))+"-"
                +twoZeroPre((calendar.get(Calendar.MONTH)+1))+"-"
                +twoZeroPre(calendar.get(Calendar.DAY_OF_MONTH));
                
                date += to;
                
                calendar.setTimeInMillis(range.mEndTimeMS-10000);
                date += twoZeroPre(calendar.get(Calendar.YEAR))+"-"
                +twoZeroPre((calendar.get(Calendar.MONTH)+1))+"-"
                +twoZeroPre(calendar.get(Calendar.DAY_OF_MONTH));
                break;
            case 2: //本月
            default:
                 date = calendar.get(Calendar.YEAR)+activity.getString(R.string.year)
                +twoZeroPre((calendar.get(Calendar.MONTH)+1))+activity.getString(R.string.month)
                +twoZeroPre(calendar.get(Calendar.DAY_OF_MONTH))+activity.getString(R.string.day);
                break;
        }
        
        return date;
	}
	//////////////////////////////////////////////////////////////////////////
}
