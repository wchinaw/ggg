package com.cheng.ggg;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;


/**用户功过明细*/
public class UserGongGuoListActivity extends Activity {

	Activity mActivity;
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
        
        mActivity = this;
        
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
        mListView.setOnItemLongClickListener(mOnItemLongClickListener);
    }
    
    OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener(){

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  

	            public void onCreateContextMenu(ContextMenu menu, View v,  
	                            ContextMenuInfo menuInfo) {  
	                    menu.add(0,0,0,"删除");   
	            }
	    });
		return false;
		}
    	
    };
    
      

	// 长按菜单响应函数  
	public boolean onContextItemSelected(MenuItem item) {  

	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item  
	    .getMenuInfo();  
	int id = (int) info.id;// 这里的info.id对应的就是数据库中_id的值  
	
	switch(item.getItemId()) {  
		case 0:  //删除
			UserGongGuo gongguo = mUserGongGuoList.get(id);
			
			if(gongguo != null){
				boolean rc = false;
				SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
				if(gongguo.count>0){//功
					rc = mSQLiteHelper.deleteUserGongItemById(db, gongguo.id);
				}
				else{//过
					rc = mSQLiteHelper.deleteUserGuoItemById(db, gongguo.id);
				}
				mUserGongGuoList.remove(id);
				mAdapter.notifyDataSetChanged();
				db.close();
				
				if(rc == true){
					Toast.makeText(mActivity, R.string.deleteok, Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(mActivity, R.string.deletefail, Toast.LENGTH_LONG).show();
				}
			}
	
	default:  
	break;  
	}  
	
	return super.onContextItemSelected(item);  

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

		public View getView(int position, View view, ViewGroup arg2) {
			
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
			UserGongGuo gongguo = mUserGongGuoList.get(position);
			holder.name.setText(gongguo.parent_name+" "+gongguo.name);
			holder.date.setText(COM.intTime2Date(mActivity, gongguo.time));
			holder.position = position;
			return view;
		}
		
		public class ViewHolder{
			TextView name;
			TextView date;
			int position;
		}
    	
    }
}
