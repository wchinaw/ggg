package com.cheng.ggg;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.utils.COM;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity implements OnClickListener{
	
	SQLiteHelper mSQLiteHelper;
	TextView textGong, textGuo, textTotal;
	TextView textTips;
	Animation mAlphaAnimation;
	String tipsStr[] = null;
	
	public static final int TEXT_SIZE = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSQLiteHelper = SQLiteHelper.getInstance(this);
        
        ((Button)findViewById(R.id.buttonGong)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonGuo)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonDetail)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonAbout)).setOnClickListener(this);
        
        textGong = (TextView) findViewById(R.id.textMonthGong);
        textGuo = (TextView) findViewById(R.id.textMonthGuo);
        textTotal = (TextView) findViewById(R.id.textMonthTotal);
        
        initTips(true);
//        SQLiteHelper helper = SQLiteHelper.getInstance(this);
//        SQLiteDatabase  db = helper.getReadableDatabase();
//        ArrayList<GongGuoBase> list = helper.getGongBase(db);
//        
//        if(list != null){
//        	int len = list.size();
//        	for(int i=0; i<len; i++){
//        		list.get(i).dump();
//        	}
//        }
//        
//        db.close();
        //������� 
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        //�Զ������쳣�˳���FC��
        MobclickAgent.onError(this);
        //�û�����
        //����������Feedbackģ�飬�������溯�����뷴�����棺
        //UMFeedbackService.openUmengFeedbackSDK(this);
        //�������߻ظ��û������������Ҫ�����û�������Ӧ�ó�������Activity��OnCreate()��������������´���
//        UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
//        FeedbackAgent agent = new FeedbackAgent(this);
//        agent.sync();
        //������һ����������Ϊ��Context���ڶ�������Ϊö�����ͣ���ѡֵΪNotificationType.AlertDialog ��NotificationType.NotificationBar���ֱ��Ӧ���ֲ�ͬ����ʾ��ʽ��

    }
    
    public void initTips(boolean bAnimation){
    	if(textTips == null)
    		textTips = (TextView) findViewById(R.id.textViewTips);
    	if(tipsStr == null)
    		tipsStr = getResources().getStringArray(R.array.TIPS_STR);
    	int len = tipsStr.length;
    	int index = (int)(Math.random()*(len));
    	if(index <0 || index >= len){
    		index = 0;
    	}
    	COM.LOGE("", "index:"+index);
    	COM.LOGE("", "tipsStr["+index+"]:"+tipsStr[index]);
    	textTips.setText(tipsStr[index]);
    	textTips.setTextSize(TEXT_SIZE);
    	textTips.setOnClickListener(this);
    	
    	if(bAnimation){
    		startAlphaAnimation(textTips);
    	}
    }
    
    public void startAlphaAnimation(View view){
    	if(view == null)
    		return;
    	
    	if(mAlphaAnimation == null){
    		mAlphaAnimation = new android.view.animation.AlphaAnimation(0.1f, 1.0f);
    		mAlphaAnimation.setDuration(1000);
    	}
    	
    	view.startAnimation(mAlphaAnimation);
    }
    
    
    @Override
	protected void onResume() {
    	refreshGongGuoInfo();
		super.onResume();
		MobclickAgent.onResume(this);
	}

    


	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}



	public void refreshGongGuoInfo(){
    	SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
    	int monthGong = mSQLiteHelper.getUserGongCount(db);
    	int monthGuo = mSQLiteHelper.getUserGuoCount(db);
    	int monthTotal = monthGong+monthGuo;
    	
    	db.close();
    	
    	setTextViewColorAndCount(textGong,monthGong);
    	setTextViewColorAndCount(textGuo,monthGuo);
    	setTextViewColorAndCount(textTotal,monthTotal);
    }
	
	public void setTextViewColorAndCount(TextView view, int count){
		if(view == null)
			return;
		
		view.setText(count+"");
		if(count > 0){
			view.setTextColor(Color.RED);
		}
		else if(count < 0){
			view.setTextColor(Color.GREEN);
		}
		else {
			view.setTextColor(Color.WHITE);
		}
	}
    
    public void gotoGongGuoActivity(boolean bGong){
    	Intent intent = new Intent(this,GongGuoListActivity.class);
    	intent.putExtra(COM.INTENT_GONG, bGong);
    	startActivity(intent);
    }
    
    private void gotoUserGongGuoListActivity(int type){
    	Intent intent = new Intent(this,UserGongGuoListActivity.class);
    	intent.putExtra(COM.INTENT_TYPE, type);
    	startActivity(intent);
    }

    private void gotoAboutActivity(){
    	Intent intent = new Intent(this,AboutActivity.class);
    	startActivity(intent);
    }
    
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonGong: 
			gotoGongGuoActivity(true);
			break;
		case R.id.buttonGuo:
			gotoGongGuoActivity(false);
			break;
		case R.id.buttonDetail:
			gotoUserGongGuoListActivity(UserGongGuoListActivity.TYPE_ALL);
			break;
		case R.id.buttonAbout:
			gotoAboutActivity();
			break;
		case R.id.textViewTips:
			initTips(true);
			break;
		}
	}


}
