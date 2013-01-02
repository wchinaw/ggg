package com.cheng.ggg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.umeng.fb.UMFeedbackService;

public class AboutActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		((Button)findViewById(R.id.button1)).setOnClickListener(this);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:
			UMFeedbackService.openUmengFeedbackSDK(this);
			break;
		}
		
	}

}
