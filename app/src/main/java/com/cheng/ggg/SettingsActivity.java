package com.cheng.ggg;

import com.cheng.ggg.utils.DialogAPI;
import com.cheng.ggg.utils.Settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this); 
		sp.registerOnSharedPreferenceChangeListener(this);
//        boolean gongguoconfirm_dialog = sp.getBoolean(Settings.gongguoconfirm_dialog, false);
//        Log.i("","gongguoconfirm_dialog :"+gongguoconfirm_dialog);
        
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if(sharedPreferences == null || key == null){
			return;
		}
		
		if(Settings.is_enable_password.equals(key)){
			Settings.setPassword(this, "");
			boolean isEnablePassword = sharedPreferences.getBoolean(Settings.is_enable_password, false);
			if(isEnablePassword){
				DialogAPI.showSetPasswordDialog(this);
			}
		}
	}

   
}