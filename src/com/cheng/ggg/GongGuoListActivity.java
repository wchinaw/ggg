package com.cheng.ggg;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.DialogAPI;
import com.cheng.ggg.utils.Settings;
import com.umeng.analytics.MobclickAgent;

public class GongGuoListActivity  extends ExpandableListActivity {
	
	final String TAG = "GongGuoListActivity";
	public BaseExpandableListAdapter mAdapter;
    SQLiteHelper mSQLiteHelper;
    boolean mbGong = false;
    /**用户自定义模式*/
    boolean mbUserDefine = false;
    public ArrayList<GongGuoBase> mGongGuoBaseList;
    GongGuoListActivity mThis;
    public ExpandableListView mListView;
    Resources mRs;
    boolean mbGongguoconfirm_dialog = false;
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThis = this;
        getBundles();
        sp = PreferenceManager.getDefaultSharedPreferences(this); 
        mListView = getExpandableListView();
        mRs = getResources();
        
//        mListView.setOnItemClickListener(mOnItemClickListener);
//        mListView.setOnChildClickListener(mOnChildClickListener);
//        mListView.setOnLongClickListener(mOnLongClickListenerGroup);
        mListView.setGroupIndicator(getResources().getDrawable(R.drawable.list_expand_btn));
        
        if(mbUserDefine)
        	mListView.setOnCreateContextMenuListener(mContextMenuListener);
        else{
        	mListView.setOnChildClickListener(mOnChildClickListener);
        	mListView.setOnCreateContextMenuListener(mContextMenuListener);
        }
        
//        mListView.setBackgroundResource(R.drawable.c);
//        mListView.setCacheColorHint(0xFFFFFFFF);
        // Set up our adapter
        mAdapter = new MyExpandableListAdapter(this);
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
//			 base.dump();
			 
		        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {            
		            
		            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
//		            Toast.makeText(mThis, "hahatest for child.", Toast.LENGTH_SHORT).show();
		            //相应显示dialog吧~
		            
		            GongGuoDetail detail = (GongGuoDetail) mAdapter.getChild(groupPos,childPos);
//		            detail.dump();
		            
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
		mbGongguoconfirm_dialog = sp.getBoolean(Settings.gongguoconfirm_dialog, false);
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
    
	public void createAddConfirmDialog(final GongGuoBase base,final  GongGuoDetail detail,
			final  int time){
		AlertDialog dialog =  new AlertDialog.Builder(this)
        .setTitle(R.string.gonggongadd_confirm)
        .setMessage(base.name+" "+detail.name)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	insertOneItem(base,detail,time);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        })
        .create();
		
		dialog.show();
	}
    
    public void insertOneItem(GongGuoBase base,GongGuoDetail detail,int time){
    	SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		if(mbGong){
			mSQLiteHelper.insertUserGONGTable(db, detail.id,base.name, detail.name, detail.count,time);
		}
		else{
			mSQLiteHelper.insertUserGUOTable(db, detail.id, base.name,detail.name, detail.count,time);
		}
		
		detail.userCount++;
		base.userCount++;
		mListView.invalidateViews();
//		mThis.finish();
		
		Toast.makeText(mThis, base.name+" "+detail.name+" "+mThis.getString(R.string.addok), Toast.LENGTH_SHORT).show();
		db.close();
			
    }
    
    public OnChildClickListener mOnChildClickListener = new OnChildClickListener(){

		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			GroupChildHolder g = (GroupChildHolder) v.getTag();
			
			if(g != null){
				int time = (int) (System.currentTimeMillis()/1000);
				GongGuoBase base = (GongGuoBase)mAdapter.getGroup(g.groupPosition);
				GongGuoDetail detail = (GongGuoDetail) mAdapter.getChild(g.groupPosition,g.childPosition);
				
				//插入一条记录
				if(mbGongguoconfirm_dialog == true){
					createAddConfirmDialog(base,detail,time);
				}
				else{
					insertOneItem(base,detail,time);
				}
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

		LayoutInflater mInflater; 
    	Activity mActivity;
    	
    	public MyExpandableListAdapter(Activity activity){
    		mActivity = activity;
    		mInflater = LayoutInflater.from(mActivity);
    	}
		
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
			
			GongGuoDetail detail = (GongGuoDetail) getChild(groupPosition, childPosition);
			if(detail == null)
				return null;
			
			View textView = getGenericChildView(detail,groupPosition,childPosition);
			
			GroupChildHolder holder = (GroupChildHolder) textView.getTag();
			
			holder.txtName.setText(detail.name);
			holder.txtCount.setText(detail.userCount+"");
			
			holder.childPosition = childPosition;
			holder.groupPosition = groupPosition;
//            textView.setOnClickListener(mChildClick);
//            textView.setTag(new GroupChild(groupPosition, childPosition));
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
		
		public View getGenericGroupView(GongGuoBase base){
			return getGroupViewByStyle(base, R.style.listItem20dp);
		}
		
		public View getGenericChildView(GongGuoDetail detail,int groupPosition, int childPosition){
			return getGenericViewByStyle(detail,groupPosition, childPosition,R.style.listItem18dp);
		}
		
		public View getGroupViewByStyle(final GongGuoBase base, int styleId){
			GroupHolder holder = new GroupHolder();
            View view = mInflater.inflate(R.layout.listview_group, null);
			holder.txtName = (TextView) view.findViewById(R.id.TextItemName);
			holder.txtCount = (TextView) view.findViewById(R.id.TextItemCount);	
			holder.button = (Button) view.findViewById(R.id.button1);
            holder.txtName.setTextAppearance(mThis, styleId);
            holder.txtCount.setTextAppearance(mThis, styleId);
            
            holder.txtName.setTextSize(MainActivity.TEXT_SIZE);
            holder.txtCount.setTextSize(MainActivity.TEXT_SIZE);
            holder.button.setTextSize(MainActivity.TEXT_SIZE);
            
            if(mbUserDefine){
            	holder.button.setVisibility(View.VISIBLE);
            	holder.button.setText(R.string.add);
            	holder.button.setOnClickListener(new OnClickListener(){
            		
					public void onClick(View arg0) {
						DialogAPI.showAddItemDialog(mThis, base.name, base, mbGong);
					}
            	});
            	holder.txtCount.setVisibility(View.GONE);
            }
            else{
            	holder.button.setVisibility(View.GONE);
            	holder.txtCount.setVisibility(View.VISIBLE);
            	holder.txtCount.setTextAppearance(mThis, styleId);
            }
            
            view.setTag(holder);
            return view;
		}
		
		public View getGenericViewByStyle(final GongGuoDetail detail,final int groupPosition, final int childPosition, int styleId) {
            GroupChildHolder holder = new GroupChildHolder();
            View view = mInflater.inflate(R.layout.listview_detail, null);
			holder.txtName = (TextView) view.findViewById(R.id.TextItemName);
			holder.txtCount = (TextView) view.findViewById(R.id.TextItemCount);
			holder.button = (Button) view.findViewById(R.id.button1);	
            holder.txtName.setTextAppearance(mThis, styleId);
            
            holder.txtName.setTextSize(MainActivity.TEXT_SIZE);
            holder.txtCount.setTextSize(MainActivity.TEXT_SIZE);
            holder.button.setTextSize(MainActivity.TEXT_SIZE);
            
            if(mbUserDefine){
            	holder.button.setVisibility(View.VISIBLE);
            	holder.button.setText(R.string.delete);
            	holder.button.setOnClickListener(new OnClickListener(){
            		
					public void onClick(View arg0) {
						DialogAPI.showDeleteItemDialog(mThis, detail, groupPosition, childPosition, mbGong);
					}
            	});
            	holder.txtCount.setVisibility(View.GONE);
            }
            else{
            	holder.button.setVisibility(View.GONE);
            	holder.txtCount.setVisibility(View.VISIBLE);
            	holder.txtCount.setTextAppearance(mThis, styleId);
            }
            
            view.setTag(holder);
            return view;
        }

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			 GongGuoBase base = (GongGuoBase)getGroup(groupPosition);
			 
			 View textView = getGenericGroupView(base);
			 
			 GroupHolder holder = (GroupHolder) textView.getTag();
			 
			 holder.txtName.setText(base.name);
			 holder.txtCount.setText(base.userCount+"");
			 holder.groupPosition = groupPosition;
			 
	            holder.txtCount.setTextSize(MainActivity.TEXT_SIZE);
	            holder.button.setTextSize(MainActivity.TEXT_SIZE);

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
	
	class GroupChildHolder{
		int groupPosition;
		int childPosition;
		TextView txtName;
		TextView txtCount;
		Button button;
		
		public GroupChildHolder(){
			
		}
		
		public GroupChildHolder(int groupPos, int childPos){
			groupPosition = groupPos;
			childPosition = childPos;
		}
	}
	
	class GroupHolder{
		int groupPosition;
		TextView txtName;
		TextView txtCount;
		Button button;
		
		public GroupHolder(){
			
		}
		
		public GroupHolder(int groupPos){
			groupPosition = groupPos;
		}
	}

}
