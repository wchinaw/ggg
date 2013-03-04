package com.cheng.ggg;

import java.util.ArrayList;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
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
import com.cheng.ggg.utils.DialogAPI;
import com.umeng.analytics.MobclickAgent;

public class GongGuoListActivity  extends ExpandableListActivity {
	
	final String TAG = "GongGuoListActivity";
    ExpandableListAdapter mAdapter;
    SQLiteHelper mSQLiteHelper;
    boolean mbGong = false;
    /**用户自定义模式*/
    boolean mbUserDefine = false;
    public ArrayList<GongGuoBase> mGongGuoBaseList;
    GongGuoListActivity mThis;
    public ExpandableListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThis = this;
        getBundles();
        
        mListView = getExpandableListView();
        
//        mListView.setOnItemClickListener(mOnItemClickListener);
//        mListView.setOnChildClickListener(mOnChildClickListener);
//        mListView.setOnLongClickListener(mOnLongClickListener);
        mListView.setGroupIndicator(getResources().getDrawable(R.drawable.list_expand_btn));
        
        if(mbUserDefine)
        	mListView.setOnCreateContextMenuListener(mContextMenuListener);
        else
        	mListView.setOnChildClickListener(mOnChildClickListener);
        
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
    
//    public void removeItem(int groupPos, int childPos){
//    	if(mGongGuoBaseList != null){
//    		GongGuoBase base
//    	}
//    }
    
    OnCreateContextMenuListener mContextMenuListener = new OnCreateContextMenuListener(){

		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

			 int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		        //点击的是子列表
			 int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
			 GongGuoBase base = (GongGuoBase)mAdapter.getGroup(groupPos);
			 base.dump();
			 
		        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {            
		            
		            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
//		            Toast.makeText(mThis, "hahatest for child.", Toast.LENGTH_SHORT).show();
		            //相应显示dialog吧~
		            
		            GongGuoDetail detail = (GongGuoDetail) mAdapter.getChild(groupPos,childPos);
		            detail.dump();
		            
//		            menu.add(0,0,0,"删除"); 
		            DialogAPI.showDeleteItemDialog(mThis, detail,groupPos, childPos, mbGong);
		        } 
		        //点击的是组列表
		        else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
//		            Toast.makeText(mThis, "hahatest for group.", Toast.LENGTH_SHORT).show();
		            //相应显示dialog吧~
		            
		            DialogAPI.showAddItemDialog(mThis, base.name, base,mbGong);
		        }
		}
    	
    };
    
    
    
    @Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		MobclickAgent.onResume(this);
	}


	//用于添加自定义功过。
//	OnLongClickListener mOnLongClickListenerGroup = new OnLongClickListener(){
//
//		public boolean onLongClick(View arg0) {
//			GongGuoBase base = (GongGuoBase) arg0.getTag();
//			COM.LOGE(TAG, "arg0:"+arg0.toString());
//			return false;
//		}
//    	
//    };
    
    public void getBundles(){
    	Intent intent = getIntent();
    	if(intent != null){
    		mbGong = intent.getBooleanExtra(COM.INTENT_GONG, false);
    		mbUserDefine = intent.getBooleanExtra(COM.INTENT_USERDEFINE, false);
    	}
    	
    	
    	
    	String strTitle;
    	if(mbUserDefine){
    		String strGongGuo;
    		if(mbGong){
    			strGongGuo = getResources().getString(R.string.gong);
    		}
    		else{
    			strGongGuo = getResources().getString(R.string.guo);
    		}
    		strTitle = getResources().getString(R.string.user_define);
    		strTitle = strTitle+strGongGuo;
    	}
    	else{
    		if(mbGong){
    			strTitle = getResources().getString(R.string.record_gong);
    		}
    		else{
    			strTitle = getResources().getString(R.string.record_guo);
    		}
    	}
    	
    	setTitle(strTitle);
    	
    	
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
				if(childPosition < base.mList.size())
					return base.mList.get(childPosition);
				else
					return null;
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
				if(groupPosition < mGongGuoBaseList.size())
					return mGongGuoBaseList.get(groupPosition);
				else
					return null;
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
                    ViewGroup.LayoutParams.FILL_PARENT, 64);

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
//	         textView.setOnClickListener(l)
//	         textView.setOnLongClickListener(mOnLongClickListenerGroup);
	         textView.setTag(base);
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
		
//		OnClickListener mGroupClick = new OnClickListener(){
//
//			public void onClick(View v) {
//				int pos = (Integer) v.getTag();
////				mListView.setSelectedGroup(pos);
//				Toast.makeText(mThis, "mGroupClick "+pos, Toast.LENGTH_SHORT).show();
//			}
//			
//		};
		
		
		
//		OnClickListener mChildClick = new OnClickListener(){
//
//			public void onClick(View v) {
//				GroupChild g = (GroupChild) v.getTag();
//				
//				if(g != null){
//					SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
//					
//					int time = (int) (System.currentTimeMillis()/1000);
//					
//					GongGuoBase base = (GongGuoBase)getGroup(g.groupPosition);
//					
//					GongGuoDetail detail = (GongGuoDetail) getChild(g.groupPosition,g.childPosition);
//					
//					if(mbGong){
//						mSQLiteHelper.insertUserGONGTable(db, detail.id,base.name, detail.name, detail.count,time);
//					}
//					else{
//						mSQLiteHelper.insertUserGUOTable(db, detail.id, base.name,detail.name, detail.count,time);
//					}
////					mThis.finish();
//					
//					Toast.makeText(mThis, base.name+" "+detail.name+" "+mThis.getString(R.string.addok), Toast.LENGTH_SHORT).show();
//						
//				}
//				
//				
////				mListView.setSelectedChild(groupPosition, childPosition, shouldExpandGroup)
////				Toast.makeText(mContext, "mChildClick "+g.groupPosition+" "+g.childPosition, Toast.LENGTH_SHORT).show();
//			}
//			
//		};
		
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
