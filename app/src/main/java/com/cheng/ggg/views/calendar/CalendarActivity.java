package com.cheng.ggg.views.calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.GongGuoListActivity;
import com.cheng.ggg.MainActivity;
import com.cheng.ggg.R;
import com.cheng.ggg.UserGongGuoAdapter;
import com.cheng.ggg.UserGongGuoListActivity;
import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.InsertGongGuoListener;
import com.cheng.ggg.types.TimeRange;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.TimeDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalendarActivity extends Activity {
	private CalendarView calendar;
	private ImageButton calendarLeft;
	private TextView calendarCenter;
	private ImageButton calendarRight;
	private SimpleDateFormat format;

	public String[] mWeekdayArray;
	/**获取列表*/
//	ArrayList<UserGongGuo> mUserGongGuoList;


	ListView mListView;
	UserGongGuoAdapter mAdapter;
	SQLiteHelper mSQLiteHelper;
	CalendarActivity mActivity;

	boolean istotal;
	boolean isdetail;
	boolean isgong;
	GongGuoBase base;
	GongGuoDetail detail;

	String strTotal,strGong,strGuo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		mActivity = this;
		mSQLiteHelper = SQLiteHelper.getInstance(this);

		mWeekdayArray = getResources().getStringArray(R.array.list_weekday);
		format = new SimpleDateFormat("yyyy-MM-dd");
		//获取日历控件对象
		calendar = (CalendarView)findViewById(R.id.calendar);
		calendar.setSelectMore(false); //单选

		strTotal = getString(R.string.total);
		strGong = getString(R.string.gong);
		strGuo = getString(R.string.guo);

		getBundles();
		
		calendarLeft = (ImageButton)findViewById(R.id.calendarLeft);
		calendarCenter = (TextView)findViewById(R.id.calendarCenter);
		calendarRight = (ImageButton)findViewById(R.id.calendarRight);
		try {
			//设置日历日期
			Date date = format.parse("2015-01-01");
			calendar.setCalendarData(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mAdapter = new UserGongGuoAdapter(this);
		mAdapter.setList(calendar.getUserGongGuoList());
		refreshTotalGongGuoByList();
		
		//获取日历中年月 ya[0]为年，ya[1]为月（格式大家可以自行在日历控件中改）
		String[] ya = calendar.getYearAndmonth().split("-");
		calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
		calendarLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//点击上一月 同样返回年月 
				String leftYearAndmonth = calendar.clickLeftMonth();
				String[] ya = leftYearAndmonth.split("-");
				calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
				getList();
			}
		});

		calendarRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//点击下一月
				String rightYearAndmonth = calendar.clickRightMonth();
				String[] ya = rightYearAndmonth.split("-");
				calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
				getList();
			}
		});
		
		//设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
		calendar.setOnItemClickListener(new CalendarView.OnItemClickListener() {

			@Override
			public void OnItemClick(Date selectedStartDate,
									Date selectedEndDate, Date downDate) {
				if (calendar.isSelectMore()) {
					Toast.makeText(getApplicationContext(), format.format(selectedStartDate) + "到" + format.format(selectedEndDate), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), format.format(downDate), Toast.LENGTH_SHORT).show();
				}
			}
		});

		mListView = (ListView) findViewById(R.id.listView);

		mListView.setAdapter(mAdapter);
		mListView.setOnItemLongClickListener(mOnItemLongClickListener);
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	public void getList(){
		if(calendar != null){
			calendar.getUserGongGuoList();
			calendar.invalidate();
		}
		if(mAdapter != null)
			mAdapter.notifyDataSetChanged();
		refreshTotalGongGuoByList();
	}

	AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener(){

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
			mListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

				public void onCreateContextMenu(ContextMenu menu, View v,
												ContextMenu.ContextMenuInfo menuInfo) {
					menu.add(0,0,0,"编辑");
					menu.add(0,1,0,"删除");
				}
			});
			return false;
		}

	};

	public void refreshTotalGongGuoByList(){
		int mGong = 0;
		int mGuo = 0;
		ArrayList<UserGongGuo> mUserGongGuoList = mAdapter.getList();
		for(UserGongGuo data : mUserGongGuoList){
			if(data.count > 0){
				mGong += data.count*data.times;
			}
			else{
				mGuo += data.count*data.times;
			}
		}

		String title;
		if(istotal){
			title = getString(R.string.calendar_title_all);
			setTitle(title+" "+strGong+" "+mGong+" "+strGuo+" "+mGuo+" "+strTotal+" "+(mGong+mGuo));
		}
		else if(isdetail && detail != null){ //具体的功过
			title = detail.name;
			setTitle(title+" "+strTotal+" "+(mGong+mGuo));
		}
		else if(base != null){ //大类别  一功 十功等
			title = base.name;
			setTitle(title+" "+strTotal+" "+(mGong+mGuo));
		}
		else{ //所有的功  过 统计
			if(isgong) {
				title = getString(R.string.calendar_title_gong);
				setTitle(title+" "+strTotal+" "+mGong);
			}
			else {
				title = getString(R.string.calendar_title_guo);
				setTitle(title+" "+strTotal+" "+mGuo);
			}
		}
	}

	AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
								long arg3) {
			UserGongGuo gongguo = mAdapter.getList().get(position);
			if(gongguo != null){
				UserGongGuo newgongguo = new UserGongGuo();
				newgongguo.count = gongguo.count;
				newgongguo.parent_id = gongguo.parent_id;
				newgongguo.name = gongguo.name;
				newgongguo.parent_name = gongguo.parent_name;
				newgongguo.time = (int) (System.currentTimeMillis()/1000);
				//插入一条记录
//				if(mbGongguoconfirm_dialog == true){
				mInsertGongGuoListener.bInsert = true;
				GongGuoListActivity.createAddConfirmDialog(mActivity, null, null, newgongguo, mInsertGongGuoListener);
//				}
//				else{
//					mInsertGongGuoListener.insert(newgongguo);
//				}
			}


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
			if (gongguo.count < 0)
				bGong = false;
			GongGuoListActivity.insertOneItem(mActivity, bGong, mSQLiteHelper, gongguo);
			getList();
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
				getList();
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
		Log.i("", " info.id:" + info.id + " info.position:" + info.position);

		switch(item.getItemId()) {
			case 0:  //编辑
				UserGongGuo gongguo = mAdapter.getList().get(id);
				if(gongguo != null){
					mInsertGongGuoListener.bInsert = false;
					GongGuoListActivity.createAddConfirmDialog(mActivity, null, null, gongguo, mInsertGongGuoListener);
				}
				break;
			case 1:  //删除
				gongguo = mAdapter.getList().get(id);

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
					mAdapter.getList().remove(id);
					if(isFirst) {
						calendar.getUserGongGuoList();
						calendar.invalidate();
					}
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

	public void getBundles(){
		Intent intent = getIntent();
		base = (GongGuoBase) intent.getSerializableExtra(COM.INTENT_GONGGUOBASE);
		detail = (GongGuoDetail) intent.getSerializableExtra(COM.INTENT_GONGGUODETAIL);
		isdetail = intent.getBooleanExtra(COM.INTENT_ISDETAIL,true);
		isgong = intent.getBooleanExtra(COM.INTENT_GONG, true);
		istotal = intent.getBooleanExtra(COM.INTENT_ISTOTAL, false);
		calendar.setGongGuoData(base, detail, isdetail, isgong, istotal);
		if(istotal){
			setTitle(R.string.calendar_title_all);
		}
		else if(isdetail && detail != null){ //具体的功过
			setTitle(detail.name);
		}
		else if(base != null){ //大类别  一功 十功等
			setTitle(base.name);
		}
		else{ //所有的功  过 统计
			if(isgong)
				setTitle(R.string.calendar_title_gong);
			else
				setTitle(R.string.calendar_title_guo);
		}
	}

	/**
	 *
	 * @param base 功过Base
	 * @param detail 功过Detail
	 * @param isDetail TRUE: 是否只显示对应的Detail专用列表  FALSE: 显示一段时间所有功过列表
	 * @param mbGong 是否是功
	 * @param range 时间范围
	 */
	public static void startActvitiyForGongGuoDate(Activity activity, GongGuoBase base, GongGuoDetail detail,boolean isDetail, boolean mbGong){
		startActvitiyForGongGuoDate(activity,base,detail,isDetail,mbGong,false);
	}

	/**
	 *
	 * @param base 功过Base
	 * @param detail 功过Detail
	 * @param isDetail TRUE: 是否只显示对应的Detail专用列表  FALSE: 显示一段时间所有功过列表
	 * @param mbGong 是否是功
	 * @param range 时间范围
	 */
	public static void startActvitiyForGongGuoDate(Activity activity, GongGuoBase base, GongGuoDetail detail,boolean isDetail, boolean mbGong, boolean istotal){
		if(activity == null)
			return;

		Intent intent = new Intent(activity, CalendarActivity.class);
		intent.putExtra(COM.INTENT_GONGGUOBASE,base);
		intent.putExtra(COM.INTENT_GONGGUODETAIL,detail);
		intent.putExtra(COM.INTENT_ISDETAIL,isDetail);
		intent.putExtra(COM.INTENT_GONG,mbGong);
		intent.putExtra(COM.INTENT_ISTOTAL,istotal);
		activity.startActivity(intent);
	}

//	public class UserGongGuoAdapter extends BaseAdapter {
//		LayoutInflater mInflater;
//		Activity mActivity;
//
//		public UserGongGuoAdapter(Activity activity){
//			mActivity = activity;
//			mInflater = LayoutInflater.from(mActivity);
//		}
//
//		public int getCount() {
//			if(mUserGongGuoList != null)
//				return mUserGongGuoList.size();
//			return 0;
//		}
//
//		public Object getItem(int arg0) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		public long getItemId(int arg0) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		public View getView(int position, View view, ViewGroup arg2) {
//
//			ViewHolder holder;
//			if(view == null){
//				holder = new ViewHolder();
//				view = mInflater.inflate(R.layout.listview_user_detail, null);
//				holder.layoutDayInfo = (RelativeLayout) view.findViewById(R.id.layoutDayInfo);
//				holder.layoutTimesCalendar = view.findViewById(R.id.layoutTimesCalendar);
//				holder.layoutTimesCalendar.setOnClickListener(mCalendarClickListener);
//				holder.layoutTimesCalendar.setTag(holder);
//
//				holder.titleDate = (TextView) view.findViewById(R.id.titleDate);
//				holder.titleCount = (TextView) view.findViewById(R.id.titleCount);
//				holder.name = (TextView) view.findViewById(R.id.TextItemName);
//				holder.date = (TextView) view.findViewById(R.id.TextItemDate);
//				holder.times = (TextView) view.findViewById(R.id.TextViewGuoTitle);
//				holder.comment = (TextView) view.findViewById(R.id.TextViewComments);
//
//				holder.name.setTextSize(MainActivity.TEXT_SIZE);
//				holder.date.setTextSize(MainActivity.TEXT_SIZE-5);
//				holder.times.setTextSize(MainActivity.TEXT_SIZE);
//				holder.comment.setTextSize(MainActivity.TEXT_SIZE-3);
//
//				view.setTag(holder);
//			}
//			else{
//				holder = (ViewHolder) view.getTag();
//			}
//			UserGongGuo gongguo = mUserGongGuoList.get(position);
//
//			if(gongguo.isFirst){
//				holder.layoutDayInfo.setVisibility(View.VISIBLE);
//				holder.titleDate.setText(gongguo.todayInfo);
//				holder.titleCount.setText(strTotal+" "+gongguo.todayCount);
//			}
//			else{
//				holder.layoutDayInfo.setVisibility(View.GONE);
//			}
//
//			holder.name.setText(gongguo.parent_name+" "+gongguo.name);
//			holder.date.setText(TimeDate.intTime2HourMinute(mActivity, gongguo.time));
//			if(gongguo.times>1)
//				holder.times.setText(gongguo.times+strTimes);
//			else
//				holder.times.setText("");
//			if(gongguo.comment == null || gongguo.comment.equals("")){
//				holder.comment.setVisibility(View.GONE);
//			}
//			else{
//				holder.comment.setText("  "+gongguo.comment);//strComment+
//				holder.comment.setVisibility(View.VISIBLE);
//			}
//
//			holder.position = position;
//			setGongGuoColor(holder.name,gongguo.count);
//			setGongGuoColor(holder.titleCount,gongguo.todayCount);
//
//			return view;
//		}
//
//		OnClickListener mCalendarClickListener = new OnClickListener() {
//			public void onClick(View view) {
//				ViewHolder holder = (ViewHolder) view.getTag();
//				if(holder != null){
//					UserGongGuo gongguo = mUserGongGuoList.get(holder.position);
//					if(gongguo != null){
//						GongGuoBase base = new GongGuoBase();
//						base.name = gongguo.parent_name;
//						base.count = gongguo.count;
//
//						GongGuoDetail detail = new GongGuoDetail();
//						detail.id = COM.parseInt(gongguo.parent_id);
//						detail.name = gongguo.name;
//						CalendarActivity.startActvitiyForGongGuoDate(mActivity,base,detail,true,(gongguo.count>0)?true:false); // 单一具体的功过
//					}
//				}
//			}
//
//		};
//
//		public class ViewHolder{
//			RelativeLayout layoutDayInfo; //每日信息
//			View layoutTimesCalendar;//点击之后进入日历界面
//			TextView titleDate;//每天的日期
//			TextView titleCount; //每天功过统计
//			TextView name;
//			TextView date;
//			TextView times;
//			TextView comment;
//			int position;
//		}
//
//	}

//	public static void setGongGuoColor(TextView textView,int count)
//	{
//		if(count > 0){
//			if(MainActivity.COLOR_SWAP){
//				textView.setTextColor(COM.COLOR_GUO);
//			}
//			else{
//				textView.setTextColor(COM.COLOR_GONG);
//			}
//		}
//		else{
//			if(MainActivity.COLOR_SWAP)
//				textView.setTextColor(COM.COLOR_GONG);
//			else
//				textView.setTextColor(COM.COLOR_GUO);
//		}
//	}
}
