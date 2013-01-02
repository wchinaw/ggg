package com.cheng.ggg;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.utils.COM;

public class GongGuoListActivity  extends ExpandableListActivity {
	
    ExpandableListAdapter mAdapter;
    SQLiteHelper mSQLiteHelper;
    boolean mbGong = false;
    ArrayList<GongGuoBase> mGongGuoBaseList;
    Activity mThis;
    ExpandableListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThis = this;
        mListView = getExpandableListView();
//        mListView.setOnItemClickListener(mOnItemClickListener);
        mListView.setOnChildClickListener(mOnChildClickListener);
        
        mListView.setGroupIndicator(getResources().getDrawable(R.drawable.list_expand_btn));
//        mListView.setBackgroundResource(R.drawable.c);
//        mListView.setCacheColorHint(0xFFFFFFFF);
        // Set up our adapter
        mAdapter = new MyExpandableListAdapter();
        setListAdapter(mAdapter);
//        registerForContextMenu(getExpandableListView());
        mSQLiteHelper = SQLiteHelper.getInstance(this);
        
        SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
        
        getBundles();
        
        if(mbGong)
        	mGongGuoBaseList = mSQLiteHelper.getGongBase(db);
        else 
        	mGongGuoBaseList = mSQLiteHelper.getGuoBase(db);
        db.close();
        
//        if(mGongGuoBaseList != null){
//        	int len = mGongGuoBaseList.size();
//        	for(int i=0; i<len; i++){
//        		mGongGuoBaseList.get(i).dump();
//        	}
//        }
        
    }
    
    public void getBundles(){
    	Intent intent = getIntent();
    	if(intent != null){
    		mbGong = intent.getBooleanExtra(COM.INTENT_GONG, false);
    	}
    }
    
    public OnChildClickListener mOnChildClickListener = new OnChildClickListener(){

		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			GroupChild g = (GroupChild) v.getTag();
			
			if(g != null){
				SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
				
				int time = (int) (System.currentTimeMillis()/1000);
				
				GongGuoBase base = (GongGuoBase)mAdapter.getGroup(g.groupPosition);
				
				GongGuoDetail detail = (GongGuoDetail) mAdapter.getChild(g.groupPosition,g.childPosition);
				
				if(mbGong){
					mSQLiteHelper.insertUserGONGTable(db, detail.id,base.name, detail.name, detail.count,time);
				}
				else{
					mSQLiteHelper.insertUserGUOTable(db, detail.id, base.name,detail.name, detail.count,time);
				}
//				mThis.finish();
				
				Toast.makeText(mThis, base.name+" "+detail.name+" "+mThis.getString(R.string.addok), Toast.LENGTH_SHORT).show();
					
			}
			
			
//			mListView.setSelectedChild(groupPosition, childPosition, shouldExpandGroup)
//			Toast.makeText(mContext, "mChildClick "+g.groupPosition+" "+g.childPosition, Toast.LENGTH_SHORT).show();
		
//			Log.i("mOnItemClickListener","id:"+id+" groupPosition:"+groupPosition+" childPosition:"+childPosition);
			return false;
		}

//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			
//			
//		}
		
	};
	
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		public Object getChild(int groupPosition, int childPosition) {
			GongGuoBase base = (GongGuoBase) getGroup(groupPosition);
			if(base != null && base.mList != null){
				return base.mList.get(childPosition);
			}
			return null;
		}

		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		
		
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericChildView();
			GongGuoDetail detail = (GongGuoDetail) getChild(groupPosition, childPosition);
            textView.setText(detail.name);
//            textView.setOnClickListener(mChildClick);
            textView.setTag(new GroupChild(groupPosition, childPosition));
            return textView;
		}

		public int getChildrenCount(int groupPosition) {
			
			if(mGongGuoBaseList != null){
				GongGuoBase base = mGongGuoBaseList.get(groupPosition);
				if(base != null && base.mList != null)
					return base.mList.size();
			}
			
			return 0;
		}

		public Object getGroup(int groupPosition) {
			if(mGongGuoBaseList != null){
				return mGongGuoBaseList.get(groupPosition);
			}
			return null;
		}

		public int getGroupCount() {
			if(mGongGuoBaseList != null){
				return mGongGuoBaseList.size();
			}
			return 0;
		}

		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public TextView getGenericGroupView(){
			return getGenericViewByStyle(R.style.listItem20dp);
		}
		
		public TextView getGenericChildView(){
			return getGenericViewByStyle(R.style.listItem18dp);
		}
		
		public TextView getGenericViewByStyle(int styleId) {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

            TextView textView = new TextView(GongGuoListActivity.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(80, 0, 0, 0);
            textView.setTextAppearance(mThis, styleId);
            return textView;
        }

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			 TextView textView = getGenericGroupView();
			 GongGuoBase base = (GongGuoBase)getGroup(groupPosition);
	         textView.setText(base.name);
//	         textView.setOnClickListener(mGroupClick);
//	         textView.setTag(groupPosition);
			return textView;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
		OnClickListener mGroupClick = new OnClickListener(){

			public void onClick(View v) {
				int pos = (Integer) v.getTag();
//				mListView.setSelectedGroup(pos);
				Toast.makeText(mThis, "mGroupClick "+pos, Toast.LENGTH_SHORT).show();
			}
			
		};
		
		
		
		OnClickListener mChildClick = new OnClickListener(){

			public void onClick(View v) {
				GroupChild g = (GroupChild) v.getTag();
				
				if(g != null){
					SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
					
					int time = (int) (System.currentTimeMillis()/1000);
					
					GongGuoBase base = (GongGuoBase)getGroup(g.groupPosition);
					
					GongGuoDetail detail = (GongGuoDetail) getChild(g.groupPosition,g.childPosition);
					
					if(mbGong){
						mSQLiteHelper.insertUserGONGTable(db, detail.id,base.name, detail.name, detail.count,time);
					}
					else{
						mSQLiteHelper.insertUserGUOTable(db, detail.id, base.name,detail.name, detail.count,time);
					}
//					mThis.finish();
					
					Toast.makeText(mThis, base.name+" "+detail.name+" "+mThis.getString(R.string.addok), Toast.LENGTH_SHORT).show();
						
				}
				
				
//				mListView.setSelectedChild(groupPosition, childPosition, shouldExpandGroup)
//				Toast.makeText(mContext, "mChildClick "+g.groupPosition+" "+g.childPosition, Toast.LENGTH_SHORT).show();
			}
			
		};
		
	}
	
	class GroupChild{
		int groupPosition;
		int childPosition;
		
		public GroupChild(int groupPos, int childPos){
			groupPosition = groupPos;
			childPosition = childPos;
		}
	}

}
