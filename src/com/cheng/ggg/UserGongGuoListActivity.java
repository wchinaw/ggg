package com.cheng.ggg;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;


/**用户功过明细*/
public class UserGongGuoListActivity extends Activity {

	public static final int TYPE_GONG = 0; 
	public static final int TYPE_GUO = 1; 
	public static final int TYPE_ALL = 2; 
	ListView mListView;
	UserGongGuoAdapter mAdapter;
	SQLiteHelper mSQLiteHelper;
	ArrayList<UserGongGuo> mUserGongGuoList;
	int mType = TYPE_ALL;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        getBundles();
        
        mSQLiteHelper = SQLiteHelper.getInstance(this);
        SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
        
        switch(mType){
        case TYPE_GONG:
        	break;
        case TYPE_GUO:
        	break;
        case TYPE_ALL:
        	mUserGongGuoList = mSQLiteHelper.getUserGongGuoListAll(db);
        	break;
        }
        
        db.close();
        
        mListView = (ListView) findViewById(R.id.listView1);
        mAdapter = new UserGongGuoAdapter(this);
        mListView.setAdapter(mAdapter);
    }
    
    public void getBundles(){
    	Intent intent = getIntent();
    	if(intent != null){
    		mType = intent.getIntExtra(COM.INTENT_TYPE, TYPE_ALL);
    	}
    }


    public class UserGongGuoAdapter extends BaseAdapter{
    	LayoutInflater mInflater; 
    	Activity mActivity;
    	
    	public UserGongGuoAdapter(Activity activity){
    		mActivity = activity;
    		mInflater = LayoutInflater.from(mActivity);
    	}
    	
		public int getCount() {
			if(mUserGongGuoList != null)
				return mUserGongGuoList.size();
			return 0;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int arg0, View view, ViewGroup arg2) {
			
			ViewHolder holder;
			if(view == null){
				holder = new ViewHolder();
				view = mInflater.inflate(R.layout.listview_detail, null);
				holder.name = (TextView) view.findViewById(R.id.TextItemName);
				holder.date = (TextView) view.findViewById(R.id.TextItemDate);
				view.setTag(holder);
			}
			else{
				holder = (ViewHolder) view.getTag();
			}
			UserGongGuo gongguo = mUserGongGuoList.get(arg0);
			holder.name.setText(gongguo.parent_name+" "+gongguo.name);
			holder.date.setText(COM.intTime2Date(mActivity, gongguo.time));
			
			return view;
		}
		
		public class ViewHolder{
			TextView name;
			TextView date;
		}
    	
    }
}
