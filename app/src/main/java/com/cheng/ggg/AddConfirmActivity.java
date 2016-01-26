package com.cheng.ggg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.InsertGongGuoListener;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.TimeDate;
import com.cheng.ggg.views.GWidget;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.TimeZone;

public class AddConfirmActivity extends Activity{
	final String TAG = "AlarmActivity";
	Activity mActivity;
	GongGuoBase base;
	GongGuoDetail detail;
	UserGongGuo gongguo;
	Calendar calendar;
	EditText editTimes;
	EditText editTextComment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_confirm);
		mActivity = this;

		getBundles();

		calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(gongguo.time * 1000L);

		GongGuoBase base = new GongGuoBase();
		base.name = gongguo.parent_name;
		base.count = gongguo.count;

		GongGuoDetail detail = new GongGuoDetail();
		detail.name = gongguo.name;
		detail.id = COM.parseInt(gongguo.parent_id);
		detail.count = gongguo.count;



		editTimes = (EditText)findViewById(R.id.editText1);
		editTextComment = (EditText)findViewById(R.id.editTextComment);

		if(!mInsertClick.bInsert){
			editTimes.setText(gongguo.times+"");
			editTextComment.setText(gongguo.comment);
		}

		//日期时间控件
		final DatePicker datePicker = (DatePicker)findViewById(R.id.datePicker1);
		final TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker1);
		datePicker.setVisibility(View.GONE);
		timePicker.setVisibility(View.GONE);

		//日期时间显示字符
		final TextView textViewDate = (TextView)findViewById(R.id.textViewDate);
		textViewDate.setText(TimeDate.intTime2Date1(mActivity, gongguo.time));
		textViewDate.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				int year = calendar.get(Calendar.YEAR);
				int monthOfYear = calendar.get(Calendar.MONTH);
				int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
				datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener(){

					public void onDateChanged(DatePicker arg0, int year, int monthOfYear,
											  int dayOfMonth) {
						Log.e("", "onDateChanged year:" + year + " monthOfYear:" + monthOfYear + " dayOfMonth:" + dayOfMonth);
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, monthOfYear);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						textViewDate.setText(TimeDate.intTime2Date1(mActivity, calendar));
					}
				});

				datePicker.setVisibility(View.VISIBLE);
				timePicker.setVisibility(View.GONE);
			}
		});

		final TextView textViewTime = (TextView)findViewById(R.id.textViewTime);
		textViewTime.setText(TimeDate.intTime2Date2(mActivity, gongguo.time));
		textViewTime.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				timePicker.setIs24HourView(true);
				int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
				int currentMinute = calendar.get(Calendar.MINUTE);
				timePicker.setCurrentHour(currentHour);
				timePicker.setCurrentMinute(currentMinute);

				timePicker.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.GONE);
			}
		});


		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){

			public void onTimeChanged(TimePicker arg0, int hourOfDay, int minute) {
				//刷新time显示，并修改mTimeSave变量
				Log.e("","onTimeChanged hourOfDay:"+hourOfDay+" minute:"+minute);
				calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				calendar.set(Calendar.MINUTE, minute);
				textViewTime.setText(TimeDate.intTime2Date2(mActivity, calendar));
			}
		});

		TextView textView = (TextView)findViewById(R.id.textViewGongTitle);
		textView.setText(gongguo.parent_name+" "+gongguo.name);

		int titleId = R.string.gonggongadd_confirm;
		if(!mInsertClick.bInsert)
			titleId = R.string.gonggongmodify_confirm;
		((TextView)findViewById(R.id.textViewTitle)).setText(titleId);
	}

	public void getBundles(){
		Intent intent = getIntent();
		gongguo = (UserGongGuo) intent.getSerializableExtra(COM.INTENT_GONGGUO);
	}

	public static void startActivity(Context activity, UserGongGuo gongguo){
		Intent intent = new Intent(activity,AddConfirmActivity.class);
		intent.putExtra(COM.INTENT_GONGGUO,gongguo);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
	}

	InsertGongGuoListener mInsertClick = new InsertGongGuoListener(){

		@Override
		public boolean insert(GongGuoBase base, GongGuoDetail detail, UserGongGuo gongguo) {
			return false;
		}

		@Override
		public boolean update(UserGongGuo oldGongGuo, UserGongGuo newGongGuo) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean insert(UserGongGuo gongguo) {
			boolean mbGong = gongguo.count>0?true:false;
			GongGuoListActivity.insertOneItem(mActivity,mbGong,SQLiteHelper.getInstance(mActivity),gongguo);
			GWidget.updateBroadcast(mActivity);
			return true;
		}
	};

	public void onOKClick(View v){
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(gongguo.time*1000L);
		int saveTime = (int) (calendar.getTimeInMillis() / 1000);
		int times = COM.parseInt(editTimes.getText().toString());
		if (mInsertClick.bInsert) {
			gongguo.time = saveTime;
			gongguo.times = times;
			gongguo.comment = editTextComment.getText().toString();
			if (base != null && detail != null)
				mInsertClick.insert(base, detail, gongguo);//仅用于记功，记过 列表
			else {
				mInsertClick.insert(gongguo);
			}
		} else {
			UserGongGuo newGongGuo = new UserGongGuo();
			newGongGuo.time = saveTime;
			newGongGuo.times = times;
			newGongGuo.comment = editTextComment.getText().toString();
			mInsertClick.update(gongguo, newGongGuo);
		}
		finish();
	}

	public void onCancelClick(View v){
		finish();
	}

//	public static void createAddConfirmDialog(final Context activity, final GongGuoBase base,
//											  final GongGuoDetail detail,
//											  final UserGongGuo gongguo, final InsertGongGuoListener mInsertClick){
//
//
//		final Calendar calendar = Calendar.getInstance();
//		calendar.setTimeZone(TimeZone.getDefault());
//		calendar.setTimeInMillis(gongguo.time*1000L);
//		View loadingDialog = View.inflate(activity,R.layout.dialog_add_confirm, null);
//		final EditText editTimes = (EditText)loadingDialog.findViewById(R.id.editText1);
//		final EditText editTextComment = (EditText)loadingDialog.findViewById(R.id.editTextComment);
//
//		if(!mInsertClick.bInsert){
//			editTimes.setText(gongguo.times+"");
//			editTextComment.setText(gongguo.comment);
//		}
//
//		//日期时间控件
//		final DatePicker datePicker = (DatePicker)loadingDialog.findViewById(R.id.datePicker1);
//		final TimePicker timePicker = (TimePicker)loadingDialog.findViewById(R.id.timePicker1);
//		datePicker.setVisibility(View.GONE);
//		timePicker.setVisibility(View.GONE);
//
//		//日期时间显示字符
//		final TextView textViewDate = (TextView)loadingDialog.findViewById(R.id.textViewDate);
//		textViewDate.setText(TimeDate.intTime2Date1(activity, gongguo.time));
//		textViewDate.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View arg0) {
//				int year = calendar.get(Calendar.YEAR);
//				int monthOfYear = calendar.get(Calendar.MONTH);
//				int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//				datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener(){
//
//					public void onDateChanged(DatePicker arg0, int year, int monthOfYear,
//											  int dayOfMonth) {
//						Log.e("", "onDateChanged year:" + year + " monthOfYear:" + monthOfYear + " dayOfMonth:" + dayOfMonth);
//						calendar.set(Calendar.YEAR, year);
//						calendar.set(Calendar.MONTH, monthOfYear);
//						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//						textViewDate.setText(TimeDate.intTime2Date1(activity, calendar));
//					}
//				});
//
//				datePicker.setVisibility(View.VISIBLE);
//				timePicker.setVisibility(View.GONE);
//			}
//		});
//
//		final TextView textViewTime = (TextView)loadingDialog.findViewById(R.id.textViewTime);
//		textViewTime.setText(TimeDate.intTime2Date2(activity, gongguo.time));
//		textViewTime.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View arg0) {
//				timePicker.setIs24HourView(true);
//				int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
//				int currentMinute = calendar.get(Calendar.MINUTE);
//				timePicker.setCurrentHour(currentHour);
//				timePicker.setCurrentMinute(currentMinute);
//
//				timePicker.setVisibility(View.VISIBLE);
//				datePicker.setVisibility(View.GONE);
//			}
//		});
//
//
//		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
//
//			public void onTimeChanged(TimePicker arg0, int hourOfDay, int minute) {
//				//刷新time显示，并修改mTimeSave变量
//				Log.e("","onTimeChanged hourOfDay:"+hourOfDay+" minute:"+minute);
//				calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
//				calendar.set(Calendar.MINUTE, minute);
//				textViewTime.setText(TimeDate.intTime2Date2(activity, calendar));
//			}
//		});
//
//		TextView textView = (TextView)loadingDialog.findViewById(R.id.textViewGongTitle);
//		textView.setText(gongguo.parent_name+" "+gongguo.name);
//
//		int titleId = R.string.gonggongadd_confirm;
//		if(!mInsertClick.bInsert)
//			titleId = R.string.gonggongmodify_confirm;
//
//		AlertDialog dialog =  new AlertDialog.Builder(activity)
//				.setTitle(titleId)
////        .setMessage(base.name+" "+detail.name)
//				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						int saveTime = (int) (calendar.getTimeInMillis() / 1000);
//						int times = COM.parseInt(editTimes.getText().toString());
//						if (mInsertClick.bInsert) {
//							gongguo.time = saveTime;
//							gongguo.times = times;
//							gongguo.comment = editTextComment.getText().toString();
//							if (base != null && detail != null)
//								mInsertClick.insert(base, detail, gongguo);//仅用于记功，记过 列表
//							else {
//								mInsertClick.insert(gongguo);
//							}
//						} else {
//							UserGongGuo newGongGuo = new UserGongGuo();
//							newGongGuo.time = saveTime;
//							newGongGuo.times = times;
//							newGongGuo.comment = editTextComment.getText().toString();
//							mInsertClick.update(gongguo, newGongGuo);
//						}
//					}
//				})
//				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//					}
//				})
//				.setView(loadingDialog)
//				.create();
//
//		dialog.show();
//	}


	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
