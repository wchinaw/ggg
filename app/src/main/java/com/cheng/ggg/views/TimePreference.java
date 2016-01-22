package com.cheng.ggg.views;

import java.util.Date;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cheng.ggg.R;
import com.cheng.ggg.utils.AlarmNotification;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.Settings;

public class TimePreference extends DialogPreference {
	private TimePicker mPicker;
	private long mValue;
	Context mContext;
	String TAG = "TimePreference";
	CheckBox mCheckBox;
	
	Spinner mSpinner;
	ArrayAdapter<CharSequence> mArrayAdapter;
	int mListDaysValues[];
	String mListDays[];
	final int ONE_DAY_MS = 24*60*60*1000;
//	final int ONE_DAY_MS = 10*1000; //10秒提醒一次测试
	final int EIGHT_HOURS_MS = 8*60*60*1000;
	final int TW_HOURS_MS = 12*60*60*1000;
		
	public TimePreference(Context context, AttributeSet attrs) {
		super(context, attrs);		
		setDialogLayoutResource(R.layout.time_preference);
		mContext = context;
		
	}
	
	public void setValue(long value) {
		final boolean wasBlocking = shouldDisableDependents();
		
		mValue = value;
		persistLong(value);			
		final boolean isBlocking = shouldDisableDependents(); 
	    if (isBlocking != wasBlocking) {
	        notifyDependencyChange(isBlocking);
	    }
	}
	
	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		mListDaysValues = mContext.getResources().getIntArray(R.array.list_days_value);
		mListDays = mContext.getResources().getStringArray(R.array.list_days);
		
		mSpinner = (Spinner) view.findViewById(R.id.spinner1);
		mArrayAdapter = ArrayAdapter.createFromResource(
        		mContext , R.array.list_days, android.R.layout.simple_spinner_item);
		mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(mArrayAdapter);
		
		mPicker = (TimePicker)view.findViewById(R.id.timePicker_preference);
		if(mPicker != null) {			
			mPicker.setIs24HourView(true);
			long value = mValue;
			Date d = new Date(value);
			mPicker.setCurrentHour(d.getHours());
			mPicker.setCurrentMinute(d.getMinutes());
		}
		
		mCheckBox = (CheckBox)view.findViewById(R.id.checkBox1);
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(
				) {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
//				mPicker.setEnabled(isChecked);
				if(isChecked){
					mPicker.setVisibility(View.VISIBLE);
					mSpinner.setVisibility(View.VISIBLE);
				}
				else{
					mPicker.setVisibility(View.GONE);
					mSpinner.setVisibility(View.GONE);
				}
			}
		});
		boolean isEnableAlarm = Settings.getIsEnableAlarm(mContext);
		mCheckBox.setChecked(isEnableAlarm);
		
		if(isEnableAlarm){
			mPicker.setVisibility(View.VISIBLE);
			mSpinner.setVisibility(View.VISIBLE);
		}
		else{
			mPicker.setVisibility(View.GONE);
			mSpinner.setVisibility(View.GONE);
		}
//		mPicker.setEnabled(isEnableAlarm);
		
		
	}
	
	public long getRepeatTime(String strItem){
		int len = mListDays.length;
		for(int i=0; i< len; i++){
			if(mListDays[i].equals(strItem)){
//				if(i==0){
//					return EIGHT_HOURS_MS;
//				}
//				else if(i==1){
//					return TW_HOURS_MS;
//				}
//				else
				return (mListDaysValues[i]*ONE_DAY_MS);
			}
		}
		
		return ONE_DAY_MS;
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if(positiveResult) {		
			
			String strItem = (String) mSpinner.getSelectedItem();
			long repeatTimeMs = getRepeatTime(strItem);
			
			Settings.setIsEnableAlarm(mContext, mCheckBox.isChecked());
			//设置成功，并且允许提醒，则设置闹钟
			if(mCheckBox.isChecked()){
				Date d = new Date(0, 0, 0, mPicker.getCurrentHour(), mPicker.getCurrentMinute(), 0);			
				long value = d.getTime();
				if(callChangeListener(value)) {
					setValue(value);
					Settings.setRepeatTimeMs(mContext, repeatTimeMs);
					AlarmNotification.setAlarm(mContext,mValue,repeatTimeMs,false,true);
				}
			}
			else{//设置成功，并且不允许提醒，则关闭闹钟
				Date d = new Date(0, 0, 0, mPicker.getCurrentHour(), mPicker.getCurrentMinute(), 0);			
				long value = d.getTime();
				if(callChangeListener(value)) {
					setValue(value);
					AlarmNotification.cancelAlarm(mContext);
				}
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,	Object defaultValue) {
		long value;
		if(restorePersistedValue) value = getPersistedLong(0);
		else {
			value = Long.parseLong(defaultValue.toString());
		}
        setValue(value);
	}
}
