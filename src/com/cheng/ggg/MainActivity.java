package com.cheng.ggg;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.utils.COM;

public class MainActivity extends Activity implements OnClickListener{
	
	SQLiteHelper mSQLiteHelper;
	TextView textGong, textGuo, textTotal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mSQLiteHelper = SQLiteHelper.getInstance(this);
        
        ((Button)findViewById(R.id.buttonGong)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonGuo)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonDetail)).setOnClickListener(this);
        
        textGong = (TextView) findViewById(R.id.textMonthGong);
        textGuo = (TextView) findViewById(R.id.textMonthGuo);
        textTotal = (TextView) findViewById(R.id.textMonthTotal);
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
    }
    
    
    
    @Override
	protected void onResume() {
    	refreshGongGuoInfo();
		super.onResume();
	}



	public void refreshGongGuoInfo(){
    	SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
    	int monthGong = mSQLiteHelper.getUserGongCount(db);
    	int monthGuo = mSQLiteHelper.getUserGuoCount(db);
    	int monthTotal = monthGong+monthGuo;
    	
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
		}
	}


}
