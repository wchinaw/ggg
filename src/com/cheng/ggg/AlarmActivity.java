package com.cheng.ggg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.DialogClickListener;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.DialogAPI;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

public class AlarmActivity extends Activity implements OnClickListener {
	final String TAG = "AlarmActivity";
	SQLiteHelper mSQLiteHelper;
	Activity mActivity;
	Resources mRs;
	
	//mode
	final int MODE_BACKUP = 0;
	final int MODE_RESTORE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_alarm);
		TextView msg = (TextView)findViewById(R.id.dayInfo);
		msg.setText("请记得每天按时记录功过，养成今日事今日毕的好习惯。也可以在设置里取消每天定时提醒。");
		mActivity = this;
	}
	
	public void ok(View view){
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void cancel(View view){
		finish();
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
