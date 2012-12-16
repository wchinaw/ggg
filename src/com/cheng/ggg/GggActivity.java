package com.cheng.ggg;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.cheng.ggg.database.SQLiteHelper;

public class GggActivity extends TabActivity {
	private TabHost mTabHost;
	private TabHost.TabSpec mTabs[] = new TabHost.TabSpec[5];
	private int TABID_GONG = 0;
	private final String TAB_GONG = "TAB_GONG";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTabHost = getTabHost();
        
        SQLiteHelper helper=new SQLiteHelper(this, "ggg");
        helper.getWritableDatabase();
		
//		mTabs[TABID_GONG] = mTabHost.newTabSpec(TAB_GONG).setIndicator(TAB_GONG).setContent(
//				new Intent(this, GongActivity.class));
		
		mTabHost.addTab(mTabs[TABID_GONG]);
		
    }
}