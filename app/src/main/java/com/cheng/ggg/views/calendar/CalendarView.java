package com.cheng.ggg.views.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.cheng.ggg.R;
import com.cheng.ggg.UserGongGuoListActivity;
import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.TimeRange;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.Settings;
import com.cheng.ggg.utils.TimeDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 日历控件 功能：获得点选的日期区间
 * 
 */
public class CalendarView extends View {
	private final static String TAG = "anCalendar";
	private Date selectedStartDate;
	private Date selectedEndDate;
	private Date curDate; // 当前日历显示的月
	private Date today; // 今天的日期文字显示红色
	private Date downDate; // 手指按下状态时临时日期
	private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
	private int downIndex; // 按下的格子索引
	private Calendar calendar;
	private Surface surface;
	private int[] date = new int[42]; // 日历显示数字
	private int curStartIndex, curEndIndex; // 当前显示的日历起始的索引
	private boolean completed = false; // 为false表示只选择了开始日期，true表示结束日期也选择了
	private boolean isSelectMore = false;

//	/**需要显示的习惯列表 从数据库中获取,数据库获取时就需要进行本月习惯过滤.*/
//	private ArrayList<Integer> habitTimeList;
//	private ArrayList<Integer> habitIndexList;

//	@param mGongGuoBase 功过Base
//	* @param mGongGuoDetail 功过Detail
//	* @param mIsDetail TRUE: 是否只显示对应的Detail专用列表  FALSE: 显示一段时间所有功过列表
//	* @param mbGong 是否是功
//	* @param range 时间范围
	Bitmap bitmapGong;
	Bitmap bitmapGuo;
	GongGuoBase mGongGuoBase;
	GongGuoDetail mGongGuoDetail;
	boolean mIsDetail;
	boolean mIsGong;
	boolean mIsTotal; //所有统计
	ArrayList<IndexItem> timeList;
	/**获取列表*/
//	ArrayList<UserGongGuo> mUserGongGuoList;
	//给控件设置监听事件
	private OnItemClickListener onItemClickListener;
	CalendarActivity mContext;
	
	public CalendarView(Context context) {
		super(context);
		mContext = (CalendarActivity) context;
		init();
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = (CalendarActivity) context;
		init();
	}

	private void init() {
//		bitmapTest = BitmapFactory.decodeResource(getResources(),R.drawable.g);

		curDate = selectedStartDate = selectedEndDate = today = new Date();
		calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		surface = new Surface();
		surface.density = getResources().getDisplayMetrics().density;
		setBackgroundColor(surface.bgColor);
//		setOnTouchListener(this);
	}

	public void setGongGuoData(GongGuoBase base, GongGuoDetail detail, boolean isDetail ,boolean isGong, boolean istotal){
		mGongGuoBase = base;
		mGongGuoDetail = detail;
		mIsGong = isGong;
		mIsDetail = isDetail;
		mIsTotal = istotal;

		boolean COLOR_SWAP = Settings.getIsColorSwap(mContext);
		int bitmapId = R.drawable.check_red;
		if(COLOR_SWAP)
			bitmapId = R.drawable.check_green;
		bitmapGong = BitmapFactory.decodeResource(getResources(), bitmapId);

		bitmapId = R.drawable.x_green;
		if(COLOR_SWAP)
			bitmapId = R.drawable.x_red;
		bitmapGuo = BitmapFactory.decodeResource(getResources(), bitmapId);

//		if(istotal == false){
//			if(mIsGong){
//				int bitmapId = R.drawable.check_red;
//				if(COLOR_SWAP)
//					bitmapId = R.drawable.check_green;
//				bitmapGong = BitmapFactory.decodeResource(getResources(), bitmapId);
//				BitmapFactory.Options ops = new BitmapFactory.Options();
////			ops.inSampleSize = 4;
////			ops.outHeight = 25;
////			ops.outWidth = 25;
////			bitmapTest = BitmapFactory.decodeResource(getResources(), R.drawable.gy,ops);
//			}
//			else{
////			BitmapFactory.Options ops = new BitmapFactory.Options();
////			ops.inSampleSize = 4;
////			ops.outHeight = 25;
////			ops.outWidth = 25;
//				int bitmapId = R.drawable.x_green;
//				if(COLOR_SWAP)
//					bitmapId = R.drawable.x_red;
//				bitmapGuo = BitmapFactory.decodeResource(getResources(), bitmapId);
////			bitmapTest = BitmapFactory.decodeResource(getResources(), R.drawable.x_yellow);
//			}
//		}
	}

	public int getFirstDateTime(){
		if(showFirstDate!=null)
			return (int)(showFirstDate.getTime()/1000);
		else
			return 0;
	}

	public int getLastDateTime(){
		if(showLastDate!=null)
			return (int)(showLastDate.getTime()/1000);
		else
			return 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		surface.width = getResources().getDisplayMetrics().widthPixels;
		surface.height = (int) (getResources().getDisplayMetrics().heightPixels*2/5);
//		if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.EXACTLY) {
//			surface.width = View.MeasureSpec.getSize(widthMeasureSpec);
//		}
//		if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
//			surface.height = View.MeasureSpec.getSize(heightMeasureSpec);
//		}
		widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width,
				View.MeasureSpec.EXACTLY);
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height,
				View.MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		Log.d(TAG, "[onLayout] changed:"
				+ (changed ? "new size" : "not change") + " left:" + left
				+ " top:" + top + " right:" + right + " bottom:" + bottom);
		if (changed) {
			surface.init();
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw");
		// 画框
		canvas.drawPath(surface.boxPath, surface.borderPaint);
		// 年月
		//String monthText = getYearAndmonth();
		//float textWidth = surface.monthPaint.measureText(monthText);
		//canvas.drawText(monthText, (surface.width - textWidth) / 2f,
		//		surface.monthHeight * 3 / 4f, surface.monthPaint);
		// 上一月/下一月
		//canvas.drawPath(surface.preMonthBtnPath, surface.monthChangeBtnPaint);
		//canvas.drawPath(surface.nextMonthBtnPath, surface.monthChangeBtnPaint);
		// 星期
		float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 4f;
		// 星期背景
//		surface.cellBgPaint.setColor(surface.textColor);
//		canvas.drawRect(surface.weekHeight, surface.width, surface.weekHeight, surface.width, surface.cellBgPaint);
		for (int i = 0; i < surface.weekText.length; i++) {
			float weekTextX = i
					* surface.cellWidth
					+ (surface.cellWidth - surface.weekPaint
							.measureText(surface.weekText[i])) / 2f;
			canvas.drawText(surface.weekText[i], weekTextX, weekTextY,
					surface.weekPaint);
		}
		
		// 计算日期
		calculateDate();
		// 按下状态，选择状态背景色
		//
		// FIXME: 16-1-15 测试多个同时显示
//		drawHabit(canvas);
		drawHabit(canvas);
//		drawDownOrSelectedBg(canvas);
		// write date number
		// today index
		int todayIndex = -1;
		calendar.setTime(curDate);
		String curYearAndMonth = calendar.get(Calendar.YEAR) + ""
				+ calendar.get(Calendar.MONTH);
		calendar.setTime(today);
		String todayYearAndMonth = calendar.get(Calendar.YEAR) + ""
				+ calendar.get(Calendar.MONTH);
		if (curYearAndMonth.equals(todayYearAndMonth)) {
			int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
			todayIndex = curStartIndex + todayNumber - 1;
		}
		for (int i = 0; i < 42; i++) {
			int color = surface.textColor;
			if (isLastMonth(i)) {
				color = surface.borderColor;
			} else if (isNextMonth(i)) {
				color = surface.borderColor;
			}
			if (todayIndex != -1 && i == todayIndex) {
				color = surface.todayNumberColor;
			}
			drawCellText(canvas, i, date[i] + "", color);
		}
		super.onDraw(canvas);
	}

	public void calculateDate() {
		calendar.setTime(curDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
		Log.d(TAG, "day in week:" + dayInWeek);
		int monthStart = dayInWeek;
		if (monthStart == 1) {
			monthStart = 8;
		}
		monthStart -= 1;  //以日为开头-1，以星期一为开头-2
		curStartIndex = monthStart;
		date[monthStart] = 1;
		// last month
		if (monthStart > 0) {
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i = monthStart - 1; i >= 0; i--) {
				date[i] = dayInmonth;
				dayInmonth--;
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[0]);
		}
		showFirstDate = calendar.getTime();
		// this month
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		// Log.d(TAG, "m:" + calendar.get(Calendar.MONTH) + " d:" +
		// calendar.get(Calendar.DAY_OF_MONTH));
		int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
		for (int i = 1; i < monthDay; i++) {
			date[monthStart + i] = i + 1;
		}
		curEndIndex = monthStart + monthDay;
		// next month
		for (int i = monthStart + monthDay; i < 42; i++) {
			date[i] = i - (monthStart + monthDay) + 1;
		}
		if (curEndIndex < 42) {
			// 显示了下一月的
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[41]);
		showLastDate = calendar.getTime();
	}

	/**
	 * 
	 * @param canvas
	 * @param index
	 * @param text
	 */
	private void drawCellText(Canvas canvas, int index, String text, int color) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.datePaint.setColor(color);
		float cellY = surface.monthHeight + surface.weekHeight + (y - 1)
				* surface.cellHeight + surface.cellHeight * 3 / 4f;
		float cellX = (surface.cellWidth * (x - 1))
				+ (surface.cellWidth - surface.datePaint.measureText(text))
				/ 2f;
		canvas.drawText(text, cellX, cellY, surface.datePaint);
	}

	/**
	 * 
	 * @param canvas
	 * @param index
	 * @param color
	 */
	private void drawCellBg(Canvas canvas, IndexItem index, int color) {
		int x = getXByIndex(index.index);
		int y = getYByIndex(index.index);

		float left = surface.cellWidth * (x - 1) + surface.borderWidth;
		float top = surface.monthHeight + surface.weekHeight + (y - 1)
				* surface.cellHeight + surface.borderWidth;
//		if(color == surface.cellDownColor){
//			surface.cellBgPaint.setColor(color);
//			canvas.drawRect(left, top, left + surface.cellWidth
//					- surface.borderWidth, top + surface.cellHeight
//					- surface.borderWidth, surface.cellBgPaint);
//		}
//		else {
//			Rect rectsrc = new Rect((int)left, (int)top, (int)(left + surface.cellWidth
//					- surface.borderWidth), (int)(top + surface.cellHeight
//					- surface.borderWidth));
//			Rect destsrc = new Rect((int)left, (int)top, (int)(left + surface.cellWidth
//					- surface.borderWidth), (int)(top + surface.cellHeight
//					- surface.borderWidth));
//			canvas.drawBitmap(bitmapTest, rectsrc, destsrc, surface.cellBgPaint);
			float r = left + surface.cellWidth
					- surface.borderWidth;
			float b = top + surface.cellHeight
					- surface.borderWidth;

			if(index.count > 0){
				//			Bitmap bitmap = COM.BitmapToSmallBitmap(bitmapTest,w,h);
				float bitw = bitmapGong.getWidth();
				float bith = bitmapGong.getHeight();

				float leftoffset = left+((r-left)-bitw)/2;
				float topoffset = top+((b-top)-bith)/2;

				canvas.drawBitmap(bitmapGong, leftoffset, topoffset, surface.cellBgPaint);
			}
			else{
				float bitw = bitmapGuo.getWidth();
				float bith = bitmapGuo.getHeight();

				float leftoffset = left+((r-left)-bitw)/2;
				float topoffset = top+((b-top)-bith)/2;

				canvas.drawBitmap(bitmapGuo, leftoffset, topoffset, surface.cellBgPaint);
			}

//		}
	}

	public class IndexItem{
		int index;
		int count;
	}

	public ArrayList<UserGongGuo> getUserGongGuoList(){

		calculateDate();

		TimeRange range = new TimeRange();
		range.mStartTimeS = getFirstDateTime();
		range.mEndTimeS = getLastDateTime();

		SQLiteHelper helper = SQLiteHelper.getInstance(mContext);
		if(mIsTotal){
			mContext.mAdapter.setList(helper.getUserGongGuoListByRange(helper.getReadableDatabase(), range));//获取所有的功过
		}
		else{
			mContext.mAdapter.setList(helper.getUserGongGuoTypeListByRange(helper.getReadableDatabase(), mIsGong, mIsDetail, mGongGuoBase, mGongGuoDetail, range));
		}
		mContext.mAdapter.setList(UserGongGuoListActivity.setListDayInfo(mContext,mContext.mAdapter.getList(), mContext.mWeekdayArray));

		//将每天的第一条保存下来供日历显示
		int len = mContext.mAdapter.getList().size();
		ArrayList<UserGongGuo> list = new ArrayList<UserGongGuo>();
		for(int i=0; i<len; i++){
			UserGongGuo item = mContext.mAdapter.getList().get(i);
			if(item.isFirst == true && item.todayCount != 0){
				list.add(item);
			}
		}

		//创建日历能识别的数据结构进行显示
		if(timeList != null)
			timeList.clear();
		else
			timeList = new ArrayList<IndexItem>();
		IndexItem item;
		for(UserGongGuo gongguo : list){
			item = new IndexItem();
			item.index = findSelectedIndex(curStartIndex, curEndIndex, calendar,gongguo.time);
			if(mIsTotal)
				item.count = gongguo.todayCount;
			else
				item.count = gongguo.count*gongguo.times;
			timeList.add(item);
		}

		return mContext.mAdapter.getList();
	}

	/**在日历上绘制功过明细 几种模式
	 * 1.total 显示功过合并统计,如果当天 功大于过,则显示勾, 如果过大于功,则显示叉.
	 * 2.detail与isgong配合 true: 就是具体的功过事项 如 讚一人善 false :就是base 如 准一功 准一过
	*/
	private void drawHabit(Canvas canvas){
		for(IndexItem index : timeList){
			if(index.index != -1)
				drawCellBg(canvas, index, surface.cellSelectedColor);
		}
	}

//	private void drawHabit(Canvas canvas){
//		Date date = calendar.getTime();
//		date.setDate(18);
////		calendar.set(Calendar.DAY_OF_MONTH,0,18);
////		drawDownOrSelectedBg(canvas);
//		int[] section = new int[] { -1, -1 };
//		int selectIndex = findSelectedIndex(curStartIndex, curEndIndex, calendar,date);
//		if(selectIndex != -1)
//			drawCellBg(canvas, selectIndex, surface.cellSelectedColor);
////		calendar.setTime(curDate);
////		calendar.set(Calendar.DAY_OF_MONTH, 1, 18);
////		curDate = calendar.getTime();
////		drawDownOrSelectedBg(canvas);
//	}

//	private void drawDownOrSelectedBg(Canvas canvas) {
//		// down and not up
//		if (downDate != null) {
//			drawCellBg(canvas, downIndex, surface.cellDownColor);
////			drawCellBg(canvas, downIndex+1, surface.cellDownColor);
//		}
//		// selected bg color
//		if (!selectedEndDate.before(showFirstDate)
//				&& !selectedStartDate.after(showLastDate)) {
//			int[] section = new int[] { -1, -1 };
//			calendar.setTime(curDate);
//			calendar.add(Calendar.MONTH, -1);
//			findSelectedIndex(0, curStartIndex, calendar, section);
//			if (section[1] == -1) {
//				calendar.setTime(curDate);
//				findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
//			}
//			if (section[1] == -1) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, 1);
//				findSelectedIndex(curEndIndex, 42, calendar, section);
//			}
//			if (section[0] == -1) {
//				section[0] = 0;
//			}
//			if (section[1] == -1) {
//				section[1] = 41;
//			}
//			for (int i = section[0]; i <= section[1]; i++) {
//				drawCellBg(canvas, i, surface.cellSelectedColor);
//			}
//		}
//	}

	private void findSelectedIndex(int startIndex, int endIndex,
			Calendar calendar, int[] section) {
		for (int i = startIndex; i < endIndex; i++) {
			calendar.set(Calendar.DAY_OF_MONTH, date[i]);
			Date temp = calendar.getTime();
			// Log.d(TAG, "temp:" + temp.toLocaleString());
			if (temp.compareTo(selectedStartDate) == 0) {
				section[0] = i;
			}
			if (temp.compareTo(selectedEndDate) == 0) {
				section[1] = i;
				return;
			}
		}
	}

	private int findSelectedIndex(int startIndex, int endIndex,
								  Calendar calendar, int time) {
		Date habitDate = new Date(time*1000L);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(habitDate);

		int year1 = calendar1.get(Calendar.YEAR);
		int month1 = calendar1.get(Calendar.MONTH);
		int day1 = calendar1.get(Calendar.DAY_OF_MONTH);

		int year,month,day;// dayOfyear;
		int len = date.length;
		long timeSave = calendar.getTimeInMillis();
		Calendar ctemp = Calendar.getInstance();
		ctemp.setTimeInMillis(timeSave);
		for (int i = 0; i < len; i++) {
			ctemp.setTimeInMillis(timeSave);
			month = ctemp.get(Calendar.MONTH);
			if(i<startIndex){
				ctemp.set(Calendar.MONTH,month-2);
			}
			else if(i>=endIndex){
				ctemp.set(Calendar.MONTH,month);
			}
			else{
				ctemp.set(Calendar.MONTH,month-1);
			}

			ctemp.set(Calendar.DAY_OF_MONTH, date[i]);
			year = ctemp.get(Calendar.YEAR);
			month = ctemp.get(Calendar.MONTH);
			day = ctemp.get(Calendar.DAY_OF_MONTH);
			if (day == day1 && month == month1 && year == year1) {
				return i;
			}
		}
		return -1;
	}

	private int findSelectedIndex(int startIndex, int endIndex,
								   Calendar calendar, Date selectDate) {
		for (int i = startIndex; i < endIndex; i++) {
			calendar.set(Calendar.DAY_OF_MONTH, date[i]);
			Date temp = calendar.getTime();
			if (temp.compareTo(selectDate) == 0) {
				return i;
			}
		}
		return -1;
	}

	public Date getSelectedStartDate() {
		return selectedStartDate;
	}

	public Date getSelectedEndDate() {
		return selectedEndDate;
	}

	private boolean isLastMonth(int i) {
		if (i < curStartIndex) {
			return true;
		}
		return false;
	}

	private boolean isNextMonth(int i) {
		if (i >= curEndIndex) {
			return true;
		}
		return false;
	}

	private int getXByIndex(int i) {
		return i % 7 + 1; // 1 2 3 4 5 6 7
	}

	private int getYByIndex(int i) {
		return i / 7 + 1; // 1 2 3 4 5 6
	}

	// 获得当前应该显示的年月
	public String getYearAndmonth() {
		calendar.setTime(curDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year + "-" + month;
	}
	
	//上一月
	public String clickLeftMonth(){
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, -1);
		curDate = calendar.getTime();
		invalidate();
		return getYearAndmonth();
	}
	//下一月
	public String clickRightMonth(){
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		curDate = calendar.getTime();
		invalidate();
		return getYearAndmonth();
	}
	
	//设置日历时间
	public void setCalendarData(Date date){
		calendar.setTime(date);
		invalidate();
	}
	
	//获取日历时间
	public void getCalendatData(){
		calendar.getTime();	
	}
	
	//设置是否多选
	public boolean isSelectMore() {
		return isSelectMore;
	}

	public void setSelectMore(boolean isSelectMore) {
		this.isSelectMore = isSelectMore;
	}

	private void setSelectedDateByCoor(float x, float y) {
		// change month
//		if (y < surface.monthHeight) {
//			// pre month
//			if (x < surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, -1);
//				curDate = calendar.getTime();
//			}
//			// next month
//			else if (x > surface.width - surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, 1);
//				curDate = calendar.getTime();
//			}
//		}
		// cell click down
		if (y > surface.monthHeight + surface.weekHeight) {
			int m = (int) (Math.floor(x / surface.cellWidth) + 1);
			int n = (int) (Math
					.floor((y - (surface.monthHeight + surface.weekHeight))
							/ Float.valueOf(surface.cellHeight)) + 1);
			downIndex = (n - 1) * 7 + m - 1;
			Log.d(TAG, "downIndex:" + downIndex);
			calendar.setTime(curDate);
			if (isLastMonth(downIndex)) {
				calendar.add(Calendar.MONTH, -1);
			} else if (isNextMonth(downIndex)) {
				calendar.add(Calendar.MONTH, 1);
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
			downDate = calendar.getTime();
		}
		invalidate();
	}

//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			setSelectedDateByCoor(event.getX(), event.getY());
//			break;
//		case MotionEvent.ACTION_UP:
//			if (downDate != null) {
//				if(isSelectMore){
//					if (!completed) {
//						if (downDate.before(selectedStartDate)) {
//							selectedEndDate = selectedStartDate;
//							selectedStartDate = downDate;
//						} else {
//							selectedEndDate = downDate;
//						}
//						completed = true;
//						//响应监听事件
//						onItemClickListener.OnItemClick(selectedStartDate,selectedEndDate,downDate);
//					} else {
//						selectedStartDate = selectedEndDate = downDate;
//						completed = false;
//					}
//				}else{
//					selectedStartDate = selectedEndDate = downDate;
//					//响应监听事件
//					onItemClickListener.OnItemClick(selectedStartDate,selectedEndDate,downDate);
//				}
//				invalidate();
//			}
//
//			break;
//		}
//		return true;
//	}
	
	//给控件设置监听事件
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener =  onItemClickListener;
	}
	//监听接口
	public interface OnItemClickListener {
		void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate);
	}

	/**
	 * 
	 * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
	 */
	private class Surface {
		public float density;
		public int width; // 整个控件的宽度
		public int height; // 整个控件的高度
		public float monthHeight; // 显示月的高度
		//public float monthChangeWidth; // 上一月、下一月按钮宽度
		public float weekHeight; // 显示星期的高度
		public float cellWidth; // 日期方框宽度
		public float cellHeight; // 日期方框高度	
		public float borderWidth;
		public int bgColor = Color.parseColor("#FFFFFF");
//		public int bgColor = Color.parseColor("#333333");
		private int textColor = Color.BLACK;
//		private int textColor = Color.parseColor("#EEEEEE");
		//private int textColorUnimportant = Color.parseColor("#666666");
		private int btnColor = Color.parseColor("#666666");
		private int borderColor = Color.parseColor("#CCCCCC");
		public int todayNumberColor = Color.BLUE;
		public int cellDownColor = Color.parseColor("#CCFFFF");
		public int cellSelectedColor = Color.parseColor("#99CCFF");
		public Paint borderPaint;
		public Paint monthPaint;
		public Paint weekPaint;
		public Paint datePaint;
		public Paint monthChangeBtnPaint;
		public Paint cellBgPaint;
		public Path boxPath; // 边框路径
		//public Path preMonthBtnPath; // 上一月按钮三角形
		//public Path nextMonthBtnPath; // 下一月按钮三角形
//		public String[] weekText = { "Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		public String[] weekText = { "日","一","二", "三", "四", "五", "六"};
		//public String[] monthText = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		   
		public void init() {
			float temp = height / 7f;
			monthHeight = 0;//(float) ((temp + temp * 0.3f) * 0.6);
			//monthChangeWidth = monthHeight * 1.5f;
			weekHeight = (float) ((temp + temp * 0.3f) * 0.7);
			cellHeight = (height - monthHeight - weekHeight) / 6f;
			cellWidth = width / 7f;
			borderPaint = new Paint();
			borderPaint.setColor(borderColor);
			borderPaint.setStyle(Paint.Style.STROKE);
			borderWidth = (float) (0.5 * density);
			// Log.d(TAG, "borderwidth:" + borderWidth);
			borderWidth = borderWidth < 1 ? 1 : borderWidth;
			borderPaint.setStrokeWidth(borderWidth);
			monthPaint = new Paint();
			monthPaint.setColor(textColor);
			monthPaint.setAntiAlias(true);
			float textSize = cellHeight * 0.4f;
			Log.d(TAG, "text size:" + textSize);
			monthPaint.setTextSize(textSize);
			monthPaint.setTypeface(Typeface.DEFAULT_BOLD);
			weekPaint = new Paint();
			weekPaint.setColor(textColor);
			weekPaint.setAntiAlias(true);
			float weekTextSize = weekHeight * 0.6f;
			weekPaint.setTextSize(weekTextSize);
			weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
			datePaint = new Paint();
			datePaint.setColor(textColor);
			datePaint.setAntiAlias(true);
			float cellTextSize = cellHeight * 0.5f;
			datePaint.setTextSize(cellTextSize);
			datePaint.setTypeface(Typeface.DEFAULT_BOLD);
			boxPath = new Path();
			//boxPath.addRect(0, 0, width, height, Direction.CW);
			//boxPath.moveTo(0, monthHeight);
			boxPath.rLineTo(width, 0);
			boxPath.moveTo(0, monthHeight + weekHeight);
			boxPath.rLineTo(width, 0);
			for (int i = 1; i < 6; i++) {
				boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
				boxPath.rLineTo(width, 0);
				boxPath.moveTo(i * cellWidth, monthHeight);
				boxPath.rLineTo(0, height - monthHeight);
			}
			boxPath.moveTo(6 * cellWidth, monthHeight);
			boxPath.rLineTo(0, height - monthHeight);
			//preMonthBtnPath = new Path();
			//int btnHeight = (int) (monthHeight * 0.6f);
			//preMonthBtnPath.moveTo(monthChangeWidth / 2f, monthHeight / 2f);
			//preMonthBtnPath.rLineTo(btnHeight / 2f, -btnHeight / 2f);
			//preMonthBtnPath.rLineTo(0, btnHeight);
			//preMonthBtnPath.close();
			//nextMonthBtnPath = new Path();
			//nextMonthBtnPath.moveTo(width - monthChangeWidth / 2f,
			//		monthHeight / 2f);
			//nextMonthBtnPath.rLineTo(-btnHeight / 2f, -btnHeight / 2f);
			//nextMonthBtnPath.rLineTo(0, btnHeight);
			//nextMonthBtnPath.close();
			monthChangeBtnPaint = new Paint();
			monthChangeBtnPaint.setAntiAlias(true);
			monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			monthChangeBtnPaint.setColor(btnColor);
			cellBgPaint = new Paint();
			cellBgPaint.setAntiAlias(true);
			cellBgPaint.setStyle(Paint.Style.FILL);
			cellBgPaint.setColor(cellSelectedColor);
		}
	}
}
