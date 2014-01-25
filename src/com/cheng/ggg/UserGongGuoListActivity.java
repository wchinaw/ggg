package com.cheng.ggg;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.ChartData;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.InsertGongGuoListener;
import com.cheng.ggg.types.TimeRange;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.Settings;
import com.cheng.ggg.utils.TimeDate;
import com.cheng.ggg.views.chart.BarChart;
import com.cheng.ggg.views.chart.SalesComparisonChartWave;
import com.cheng.ggg.views.chart.SalesComparisonChartGongGuo;
import com.cheng.ggg.views.chart.SalesStackedBarChart;
import com.umeng.analytics.MobclickAgent;


/**用户功过明细*/
public class UserGongGuoListActivity extends Activity implements OnClickListener{

    final String TAG = "UserGongGuoListActivity";
	Activity mActivity;
	public static final int TYPE_GONG = 0; 
	public static final int TYPE_GUO = 1; 
	public static final int TYPE_ALL = 2; 
	ListView mListView;
	UserGongGuoAdapter mAdapter;
	SQLiteHelper mSQLiteHelper;
	ArrayList<UserGongGuo> mUserGongGuoList;
	int mType = TYPE_ALL;
	String strTimes = "";
	
	TextView mTextViewTimeRange;
	long startTimeMs, endTimeMs;
	TimeRange mTimeRange = null;
	int mTimeRangeIndex = 0;
	Button btnLeft,btnRight;
	String[] mWeekdayArray;
	
	String strTotal,strGong,strGuo;
	int mGong,mGuo;
	TextView mTextViewGong,mTextViewGuo,mTextViewTotal;
	
	boolean mbGongguoconfirm_dialog = false;
	SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
        sp = PreferenceManager.getDefaultSharedPreferences(this); 
        mActivity = this;
        getBundles();
        strTimes = getString(R.string.times);
        strTotal = getString(R.string.total);
        strGong = getString(R.string.gong);
        strGuo = getString(R.string.guo);
        
        mTextViewGong = (TextView)findViewById(R.id.textView1);
        mTextViewGuo = (TextView)findViewById(R.id.textView2);
        mTextViewTotal = (TextView)findViewById(R.id.textView3);
        
        mTextViewGong.setTextSize(MainActivity.TEXT_SIZE-2);
        mTextViewGuo.setTextSize(MainActivity.TEXT_SIZE-2);
        mTextViewTotal.setTextSize(MainActivity.TEXT_SIZE-2);
//        ((Button)findViewById(R.id.buttonGraphic)).setTextSize(MainActivity.TEXT_SIZE-2);
        
        mWeekdayArray = getResources().getStringArray(R.array.list_weekday);
        
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(this);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnRight.setOnClickListener(this);
        
        mTimeRangeIndex = Settings.getTimeRangeIndex(this);
        mTimeRange = TimeDate.getTimeRangeByIndex(mTimeRange,mTimeRangeIndex,TimeDate.MODE_CURRENT);
        
        mSQLiteHelper = SQLiteHelper.getInstance(this);
        
        getList(mTimeRange);
        
        mListView = (ListView) findViewById(R.id.listView1);
        mAdapter = new UserGongGuoAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemLongClickListener(mOnItemLongClickListener);
        mListView.setOnItemClickListener(mOnItemClickListener);
        
        if(mUserGongGuoList!=null && mUserGongGuoList.size()==0){
        	Toast.makeText(this, R.string.empty_user_detaillist, Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }
        
        //弹出选择日期对话框。
        mTextViewTimeRange = (TextView)findViewById(R.id.dayInfo);
        mTextViewTimeRange.setTextSize(MainActivity.TEXT_SIZE-2);
        mTextViewTimeRange.setOnClickListener(mTimeRangeOnClickListener);
        refreshTextViewTimeRange();
    }
    
    OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			UserGongGuo gongguo = mUserGongGuoList.get(position);
			if(gongguo != null){
				UserGongGuo newgongguo = new UserGongGuo();
				newgongguo.count = gongguo.count;
				newgongguo.parent_id = gongguo.parent_id;
				newgongguo.name = gongguo.name;
				newgongguo.parent_name = gongguo.parent_name;
				newgongguo.time = (int) (System.currentTimeMillis()/1000);
				//插入一条记录
				if(mbGongguoconfirm_dialog == true){
					mInsertGongGuoListener.bInsert = true;
					GongGuoListActivity.createAddConfirmDialog(mActivity,null,null,newgongguo,mInsertGongGuoListener);
				}
				else{
					mInsertGongGuoListener.insert(newgongguo);
				}
			}
			
			
		}
    	
    };
    
    /**
     * 统计每一天的功过小计，包括功过总和，功总和，过总和。
     */
    public void setListDayInfo(){
        if(mUserGongGuoList != null && mUserGongGuoList.size()>0){
            int len = mUserGongGuoList.size();
            UserGongGuo gongguoFirst = mUserGongGuoList.get(0);
            gongguoFirst.setFirstDay();
            int firstDayStartS = TimeDate.getCurrentDayStartTimeS(gongguoFirst.time, TimeDate.MODE_CURRENT);
            UserGongGuo gongguo;
//            if(len == 1)
            gongguoFirst.todayInfo = TimeDate.intTime2TodayInfo(mActivity, firstDayStartS, mWeekdayArray);
            
            int temp = 0;
            for(int i=1; i<len; i++){
                gongguo = mUserGongGuoList.get(i);
                gongguo.isFirst = false;
                if(firstDayStartS < gongguo.time){//当天
                	temp = gongguo.count*gongguo.times;
                	gongguoFirst.todayCount+= temp;
                	if(gongguo.count>0){
                		gongguoFirst.todayGong += temp;
                	}
                	else{
                		gongguoFirst.todayGuo += temp;
                	}
                }
                else{//第二天
                    gongguoFirst = gongguo;
                    gongguoFirst.setFirstDay();
                    firstDayStartS = TimeDate.getCurrentDayStartTimeS(gongguoFirst.time, TimeDate.MODE_CURRENT);
                    gongguoFirst.todayInfo = TimeDate.intTime2TodayInfo(mActivity, firstDayStartS, mWeekdayArray);
                }
            }
        }
    }
    
    public void refreshTextViewTimeRange()
    {
        mTextViewTimeRange.setText(TimeDate.getTimeRangeDateString(mActivity,mTimeRange,mTimeRangeIndex));
//        mTextViewTimeRange.invalidate();
    }
    
    public void getList(TimeRange range){
        
        SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
        
        switch(mType){
        case TYPE_GONG:
            break;
        case TYPE_GUO:
            break;
        case TYPE_ALL:
            mUserGongGuoList = mSQLiteHelper.getUserGongGuoListByRange(db,range);
            break;
        }
        db.close();
        setListDayInfo();
        refreshTotalGongGuoByList();
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }
    
    public void refreshTotalGongGuoByList(){
    	mGong = 0;
    	mGuo = 0;
    	for(UserGongGuo data : mUserGongGuoList){
    		if(data.count > 0){
    			mGong += data.count*data.times;
    		}
    		else{
    			mGuo += data.count*data.times;
    		}
    	}
    	
    	mTextViewGong.setText(strGong+" "+mGong);
    	mTextViewGuo.setText(strGuo+" "+mGuo);
    	
    	int total = mGong+mGuo;
    	mTextViewTotal.setText(strTotal+" "+total);
    	
    	setGongGuoColor(mTextViewGong, mGong);
    	setGongGuoColor(mTextViewGuo, mGuo);
    	setGongGuoColor(mTextViewTotal, total);
    }
    
    OnClickListener mTimeRangeOnClickListener = new OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setTitle(R.string.date_range);
			builder.setItems(R.array.list_date_range, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
				    mTimeRangeIndex = which;
				    Settings.setTimeRangeIndex(mActivity, which);
				    mTimeRange = TimeDate.getTimeRangeByIndex(null,which,TimeDate.MODE_CURRENT);
				    getList(mTimeRange);
					refreshTextViewTimeRange();
				}
			});
			builder.create().show();
		}
    	
    };
    
    public void backClick(View view)
    {
    	finish();
    }
    
    @Override
	protected void onResume() {
    	mbGongguoconfirm_dialog = sp.getBoolean(Settings.gongguoconfirm_dialog, false);
		super.onResume();
		MobclickAgent.onResume(this);
	}
    
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
    
    OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener(){

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  

	            public void onCreateContextMenu(ContextMenu menu, View v,  
	                            ContextMenuInfo menuInfo) {  
	            		menu.add(0,0,0,"编辑");
	                    menu.add(0,1,0,"删除");   
	            }
	    });
		return false;
		}
    	
    };	
    
    InsertGongGuoListener mInsertGongGuoListener = new InsertGongGuoListener(){

		@Override
		public boolean insert(GongGuoBase base, GongGuoDetail detail,
				UserGongGuo gongguo) {
			
			return false;
		}

		@Override
		public boolean insert(UserGongGuo gongguo) {
			boolean bGong = true;
			if(gongguo.count < 0)
				bGong = false;
			GongGuoListActivity.insertOneItem(mActivity,bGong,mSQLiteHelper,gongguo);
			
			getList(mTimeRange);
			refreshTextViewTimeRange();
			return false;
		}

		@Override
		public boolean update(UserGongGuo oldGongGuo, UserGongGuo newGongGuo) {
			
			int rc = 0;
			if(oldGongGuo.time == newGongGuo.time && oldGongGuo.times == newGongGuo.times
					&& oldGongGuo.comment.equals(newGongGuo.comment))
			{
				Toast.makeText(mActivity, mActivity.getString(R.string.save_ok), Toast.LENGTH_SHORT).show();
				return true;
			}
			
			SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
			if(oldGongGuo.count > 0)
				rc = mSQLiteHelper.updateUserGONGTable(db, oldGongGuo, newGongGuo);
			else
				rc = mSQLiteHelper.updateUserGUOTable(db, oldGongGuo, newGongGuo);
			db.close();
			
			if(rc > 0){
				Toast.makeText(mActivity, mActivity.getString(R.string.save_ok), Toast.LENGTH_SHORT).show();
				getList(mTimeRange);
				refreshTextViewTimeRange();
				return true;
			}{
				Toast.makeText(mActivity, mActivity.getString(R.string.save_fail), Toast.LENGTH_SHORT).show();
				return false;
			}
		}
    	
    };

	// 长按菜单响应函数  
	public boolean onContextItemSelected(MenuItem item) {  

	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item  
	    .getMenuInfo();  
		
//	int id = (int) info.id;// 这里的info.id对应的就是数据库中_id的值  
	int id = (int) info.position;
	Log.i(""," info.id:"+ info.id+" info.position:"+info.position);
	
	switch(item.getItemId()) { 
		case 0:  //编辑
			UserGongGuo gongguo = mUserGongGuoList.get(id);
			if(gongguo != null){
				mInsertGongGuoListener.bInsert = false;
				GongGuoListActivity.createAddConfirmDialog(mActivity, null, null, gongguo, mInsertGongGuoListener);
			}
			break;
		case 1:  //删除
			gongguo = mUserGongGuoList.get(id);
			
			if(gongguo != null){
				boolean rc = false;
				SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
				if(gongguo.count>0){//功
					rc = mSQLiteHelper.deleteUserGongItemById(db, gongguo.id);
				}
				else{//过
					rc = mSQLiteHelper.deleteUserGuoItemById(db, gongguo.id);
				}
				boolean isFirst = gongguo.isFirst;
				mUserGongGuoList.remove(id);
				if(isFirst)
				    setListDayInfo();
				refreshTotalGongGuoByList();
				mAdapter.notifyDataSetChanged();
				db.close();
				
				if(rc == true){
					Toast.makeText(mActivity, gongguo.parent_name+" "+gongguo.name+" "+getString(R.string.deleteok), Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(mActivity, gongguo.parent_name+" "+gongguo.name+" "+getString(R.string.deletefail), Toast.LENGTH_LONG).show();
				}
			}
			break;
	default:  
	break;  
	}  
	
	return super.onContextItemSelected(item);  

}  
	
	public static int getGuoColor()
	{
		return getGongGuoColor(-1);
	}
	
	public static int getGongColor()
	{
		return getGongGuoColor(1);
	}
	
	private static int getGongGuoColor(int count)
	{
		if(count > 0){
			if(MainActivity.COLOR_SWAP){
				return (COM.COLOR_GUO);
			}
			else{
				return(COM.COLOR_GONG);
			}
		}
		else{
			if(MainActivity.COLOR_SWAP)
				return(COM.COLOR_GONG);
			else
				return(COM.COLOR_GUO);
		}
	}
	
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
    
    public void getBundles(){
    	Intent intent = getIntent();
    	if(intent != null){
    		mType = intent.getIntExtra(COM.INTENT_TYPE, TYPE_ALL);
    	}
    }


    public class UserGongGuoAdapter extends BaseAdapter{
    	LayoutInflater mInflater; 
    	Activity mActivity;
    	
    	public UserGongGuoAdapter(Activity activity){
    		mActivity = activity;
    		mInflater = LayoutInflater.from(mActivity);
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
				holder.titleDate = (TextView) view.findViewById(R.id.titleDate);
				holder.titleCount = (TextView) view.findViewById(R.id.titleCount);
				holder.name = (TextView) view.findViewById(R.id.TextItemName);
				holder.date = (TextView) view.findViewById(R.id.TextItemDate);
				holder.times = (TextView) view.findViewById(R.id.TextViewGuoTitle);
				holder.comment = (TextView) view.findViewById(R.id.TextViewComments);
				
				holder.name.setTextSize(MainActivity.TEXT_SIZE);
				holder.date.setTextSize(MainActivity.TEXT_SIZE);
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
		    TextView titleDate;//每天的日期
		    TextView titleCount; //每天功过统计
			TextView name;
			TextView date;
			TextView times;
			TextView comment;
			int position;
		}
    	
    }


    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.btnRight:
                mTimeRange = TimeDate.getTimeRangeByIndex(mTimeRange,mTimeRangeIndex,TimeDate.MODE_TO_NEXT);
                getList(mTimeRange);
                refreshTextViewTimeRange();
                break;
            case R.id.btnLeft:
                mTimeRange = TimeDate.getTimeRangeByIndex(mTimeRange,mTimeRangeIndex,TimeDate.MODE_TO_PRE);
                getList(mTimeRange);
                refreshTextViewTimeRange();
                break;
        }
    }
    
    public ChartData getChartData(boolean isWave)
    {
    	ChartData chartData = new ChartData();
    	
    	ArrayList<Double> gong = new ArrayList<Double>();
    	ArrayList<Double> guo = new ArrayList<Double>();
    	if(mTimeRangeIndex == 0) //本年 获取12个月每个月的功过值。
    	{
    		//显示有记录月份的数据。
    		int len = mUserGongGuoList.size();
    		UserGongGuo data;
    		String currentMonth = "";
    		String tempMonth = "";
    		int gongMonth = 0, guoMonth = 0;
    		for(int i=len-1; i>=0; i--){
    			data = mUserGongGuoList.get(i);
    			if(data.isFirst){
    				tempMonth = TimeDate.getCurrentMonth(data.time);
    				if(i == len-1){
    					currentMonth = tempMonth;
    				}
        			
        			//当月，继续累加
        			if(tempMonth.equals(currentMonth)){
        				gongMonth += data.todayGong;
        				if(isWave){
        					guoMonth += data.todayGuo;
        				}
        				else{
        					guoMonth -= data.todayGuo;
        				}
        			}//第二月 将前一月的保存，并设置第二月的初始值。
        			else{
        				gong.add((double)gongMonth);
        				guo.add((double)guoMonth);
        				chartData.xlabels.add(currentMonth);
        				currentMonth = tempMonth;
        				gongMonth = data.todayGong;;
        				if(isWave){
        					guoMonth = data.todayGuo;
        				}
        				else{
        					guoMonth = data.todayGuo;
        				}
        			}
        			
        			//最后一个记录时，需要保存。
        			if(i==0){
        				gong.add((double)gongMonth);
        				guo.add((double)guoMonth);
        				chartData.xlabels.add(currentMonth);
        			}
    			}
    		}
    	}
    	else
    	{  //显示每天的结果
    		int len = mUserGongGuoList.size();
    		UserGongGuo data;
    		String xlabel;
    		for(int i=len-1; i>=0; i--){
    			data = mUserGongGuoList.get(i);
    			if(data.isFirst){
    				gong.add((double)data.todayGong);
    				if(isWave){
    					guo.add((double)data.todayGuo);
    				}
    				else{
    					guo.add(-(double)data.todayGuo);
    				}
    				
    				xlabel = TimeDate.getCurrentDay(data.time); 
    				chartData.xlabels.add(xlabel);
    			}
    			
    		}
    	}
    	
    	int size = gong.size();
		double[] dGong = new double[size];
		double[] dGuo = new double[size];
		for(int i=0; i<size; i++){
			dGong[i] = gong.get(i);
			dGuo[i] = guo.get(i);
		}
		chartData.values.add(dGong);
		chartData.values.add(dGuo);
    	
    	return chartData;
    }
    
    public void chartBarClick(View v)
    {
    	ChartData chartData = getChartData(false);
    	List<double[]> values = chartData.values;
    	if(values != null && values.size()>0){
    		BarChart c = new BarChart();
    		String xName;
        	if(mTimeRangeIndex == 0){
        		xName = mActivity.getString(R.string.month);
        	}
        	else{
        		xName = mActivity.getString(R.string.day);
        	}
        	
        	chartData.xName = xName;
        	Intent intent = c.execute(this,chartData);
    		startActivity(intent);
    	}
    	
    }
    
    public void chartWaveClick(View v)
    {
    	ChartData chartData = getChartData(true);
    	List<double[]> values = chartData.values;
    	if(values != null && values.size()>0){
    		SalesComparisonChartWave c = new SalesComparisonChartWave();
//        	SalesStackedBarChart c = new SalesStackedBarChart();
        	String xName;
        	if(mTimeRangeIndex == 0){
        		xName = mActivity.getString(R.string.month);
        	}
        	else{
        		xName = mActivity.getString(R.string.day);
        	}
        	chartData.xName = xName;
        	Intent intent = c.execute(this,chartData);
    		startActivity(intent);
    	}
    }
    
    public void chartQuxianClick(View v)
    {
    	ChartData chartData = getChartData(false);
    	List<double[]> values = chartData.values;
    	if(values != null && values.size()>0){
    		SalesComparisonChartGongGuo c = new SalesComparisonChartGongGuo();
//        	SalesStackedBarChart c = new SalesStackedBarChart();
        	String xName;
        	if(mTimeRangeIndex == 0){
        		xName = mActivity.getString(R.string.month);
        	}
        	else{
        		xName = mActivity.getString(R.string.day);
        	}
        	chartData.xName = xName;
        	Intent intent = c.execute(this,chartData);
    		startActivity(intent);
    	}
    }
    
//    public void test(){
//    	addView(getLocalActivityManager()
//				.startActivity("Picture", new Intent(NewsListActivity.this, PictrueActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//				.getDecorView());
//    }
}
