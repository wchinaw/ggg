package com.cheng.ggg;

import com.cheng.ggg.utils.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;


public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this); 
        boolean gongguoconfirm_dialog = sp.getBoolean(Settings.gongguoconfirm_dialog, false);
        Log.i("","gongguoconfirm_dialog :"+gongguoconfirm_dialog);
        
	}

   
}