package com.cheng.ggg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.InsertGongGuoListener;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.DialogAPI;
import com.cheng.ggg.utils.Settings;
import com.cheng.ggg.utils.TimeDate;
import com.umeng.analytics.MobclickAgent;

public class GongGuoListActivity  extends Activity {
	
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
    
    Button recordButton;//快速记录按钮。记过或记功
    Button buttonGraphic; 
    
    /**用户可修改当前时间，并将其作为 功过时间保存 在添加确认功过对话框中进行修改。*/
//    Calendar mCalendarSave;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mThis = this;
        setContentView(R.layout.activity_gongguo_list);
        getBundles();
        sp = PreferenceManager.getDefaultSharedPreferences(this); 
        mRs = getResources();
        
        recordButton = (Button)findViewById(R.id.recordButton);
        recordButton.setTextSize(MainActivity.TEXT_SIZE-2);
        
        buttonGraphic = (Button)findViewById(R.id.buttonGraphic);
        buttonGraphic.setTextSize(MainActivity.TEXT_SIZE-2);
        
        mListView = (ExpandableListView) findViewById(R.id.list);
//        mListView.setOnItemClickListener(mOnItemClickListener);
//        mListView.setOnChildClickListener(mOnChildClickListener);
//        mListView.setOnLongClickListener(mOnLongClickListenerGroup);
        mListView.setGroupIndicator(getResources().getDrawable(R.drawable.list_expand_btn));
        
        if(mbUserDefine){
        	mListView.setOnCreateContextMenuListener(mContextMenuListener);
        	buttonGraphic.setVisibility(View.GONE);
        }
        else{
        	mListView.setOnChildClickListener(mOnChildClickListener);
        	mListView.setOnCreateContextMenuListener(mContextMenuListener);
        }
        
//        mListView.setBackgroundResource(R.drawable.c);
//        mListView.setCacheColorHint(0xFFFFFFFF);
        // Set up our adapter
        mAdapter = new MyExpandableListAdapter(this);
//        mListView.setListAdapter(mAdapter);
        mListView.setAdapter(mAdapter);
                
//        registerForContextMenu(getExpandableListView());
        mSQLiteHelper = SQLiteHelper.getInstance(this);
        
        SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
        
        getBundles();
        
        if(mbGong){
        	mGongGuoBaseList = mSQLiteHelper.getGongBase(db);
        	
        	if(mbUserDefine)
        		recordButton.setText(R.string.userdefine_guo);
        	else
        		recordButton.setText(R.string.record_guo);
        }
        else{ 
        	mGongGuoBaseList = mSQLiteHelper.getGuoBase(db);
        	
        	if(mbUserDefine)
        		recordButton.setText(R.string.userdefine_gong);
        	else
        		recordButton.setText(R.string.record_gong);
        }
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
    
    
    /**
     * 
     * @param activity
     * @param bInsert
     * @param base
     * @param detail
     * @param time
     * @param mInsertClick 里面的bInsert确定了是插入还是编辑模式
     * @param gongguo  编辑模式才有的值
     */
	public static void createAddConfirmDialog(final Activity activity, final GongGuoBase base,
			final GongGuoDetail detail,
			final UserGongGuo gongguo, final InsertGongGuoListener mInsertClick){
		
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(gongguo.time*1000L);
		View loadingDialog = View.inflate(activity,R.layout.dialog_add_confirm, null);
		final EditText editTimes = (EditText)loadingDialog.findViewById(R.id.editText1);
		final EditText editTextComment = (EditText)loadingDialog.findViewById(R.id.editTextComment);
		
		if(!mInsertClick.bInsert){
			editTimes.setText(gongguo.times+"");
			editTextComment.setText(gongguo.comment);
		}
		
		//日期时间控件
		final DatePicker datePicker = (DatePicker)loadingDialog.findViewById(R.id.datePicker1);
		final TimePicker timePicker = (TimePicker)loadingDialog.findViewById(R.id.timePicker1);
		datePicker.setVisibility(View.GONE);
		timePicker.setVisibility(View.GONE);
		
		//日期时间显示字符
		final TextView textViewDate = (TextView)loadingDialog.findViewById(R.id.textViewDate);
		textViewDate.setText(TimeDate.intTime2Date1(activity, gongguo.time));
		textViewDate.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				int year = calendar.get(Calendar.YEAR);
				int monthOfYear = calendar.get(Calendar.MONTH);
				int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); 
				datePicker.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener(){

					public void onDateChanged(DatePicker arg0, int year, int monthOfYear,
	                        int dayOfMonth) {
						Log.e("","onDateChanged year:"+year+" monthOfYear:"+monthOfYear+" dayOfMonth:"+dayOfMonth);
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, monthOfYear);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						textViewDate.setText(TimeDate.intTime2Date1(activity, calendar));
					}
				});
				
				datePicker.setVisibility(View.VISIBLE);
				timePicker.setVisibility(View.GONE);
			}
		});
		
		final TextView textViewTime = (TextView)loadingDialog.findViewById(R.id.textViewTime);
		textViewTime.setText(TimeDate.intTime2Date2(activity, gongguo.time));
		textViewTime.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				timePicker.setIs24HourView(true);
				int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
				int currentMinute = calendar.get(Calendar.MINUTE);
				timePicker.setCurrentHour(currentHour);
				timePicker.setCurrentMinute(currentMinute);
				
				timePicker.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.GONE);
			}
		});
		
		
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener(){

			public void onTimeChanged(TimePicker arg0, int hourOfDay, int minute) {
				//刷新time显示，并修改mTimeSave变量
				Log.e("","onTimeChanged hourOfDay:"+hourOfDay+" minute:"+minute);
				calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				calendar.set(Calendar.MINUTE, minute);
				textViewTime.setText(TimeDate.intTime2Date2(activity, calendar));
			}
		});
		
		TextView textView = (TextView)loadingDialog.findViewById(R.id.textViewGongTitle);
		textView.setText(gongguo.parent_name+" "+gongguo.name);
		
		int titleId = R.string.gonggongadd_confirm;
		if(!mInsertClick.bInsert)
			titleId = R.string.gonggongmodify_confirm;
		
		AlertDialog dialog =  new AlertDialog.Builder(activity)
        .setTitle(titleId)
//        .setMessage(base.name+" "+detail.name)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	int saveTime = (int) (calendar.getTimeInMillis()/1000);
            	int times = COM.parseInt(editTimes.getText().toString());
            	if(mInsertClick.bInsert){
            		gongguo.time = saveTime;
            		gongguo.times = times;
            		gongguo.comment = editTextComment.getText().toString();
            		if(base != null && detail != null)
            			mInsertClick.insert(base,detail,gongguo);//仅用于记功，记过 列表
            		else{
            			mInsertClick.insert(gongguo);
            		}
            	}
            	else{
            		UserGongGuo newGongGuo = new UserGongGuo();
            		newGongGuo.time = saveTime;
            		newGongGuo.times = times;
            		newGongGuo.comment = editTextComment.getText().toString();
            		mInsertClick.update(gongguo,newGongGuo);
            	}
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        })
        .setView(loadingDialog)
        .create();
		
		dialog.show();
	}
    
//    public void insertOneItem(GongGuoBase base,GongGuoDetail detail,int time, int times,String comment){
//    	SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
//		if(mbGong){
//			mSQLiteHelper.insertUserGONGTable(db, detail.id,base.name, detail.name, detail.count,time,times,comment);
//		}
//		else{
//			mSQLiteHelper.insertUserGUOTable(db, detail.id, base.name,detail.name, detail.count,time,times,comment);
//		}
//		
//		detail.userCount+=times;
//		base.userCount+=times;
//		mListView.invalidateViews();
////		mThis.finish();
//		
//		Toast.makeText(mThis, base.name+" "+detail.name+" "+mThis.getString(R.string.addok), Toast.LENGTH_SHORT).show();
//		db.close();
//			
//    }
	
	InsertGongGuoListener mInsertGongGuoListener = new InsertGongGuoListener(){

		@Override
		public boolean insert(GongGuoBase base, GongGuoDetail detail, UserGongGuo gongguo) {
			insertOneItem(mThis,mbGong,mSQLiteHelper,gongguo);
			detail.userCount+=gongguo.times;
			base.userCount+=gongguo.times;
			mListView.invalidateViews();
			return true;
		}

		@Override
		public boolean update(UserGongGuo oldGongGuo, UserGongGuo newGongGuo) {
			// TODO Auto-generated method stub
			return false;
		}
//
//		@Override
//		public boolean insert(GongGuoBase base, GongGuoDetail detail,
//				int time, int times, String comment) {
//			insertOneItem(mThis,mbGong,mSQLiteHelper,base,detail,time,times,comment);
//			detail.userCount+=times;
//			base.userCount+=times;
//			mListView.invalidateViews();
//			return true;
//		}
//

		@Override
		public boolean insert(UserGongGuo gongguo) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	
  public static void insertOneItem(Activity activity ,boolean bGong, SQLiteHelper sqliteHelper ,UserGongGuo gongguo){
	SQLiteDatabase db = sqliteHelper.getWritableDatabase();
	if(bGong){
		sqliteHelper.insertUserGONGTable(db, gongguo.parent_id,gongguo.parent_name, gongguo.name, gongguo.count,gongguo.time,gongguo.times,gongguo.comment);
	}
	else{
		sqliteHelper.insertUserGUOTable(db, gongguo.parent_id,gongguo.parent_name, gongguo.name, gongguo.count,gongguo.time,gongguo.times,gongguo.comment);
	}
	Toast.makeText(activity, gongguo.parent_name+" "+gongguo.name+" "+activity.getString(R.string.addok), Toast.LENGTH_SHORT).show();
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
				
				
				UserGongGuo gongguo = new UserGongGuo();
				gongguo.parent_id = detail.id+"";
				gongguo.parent_name = base.name;
				gongguo.name = detail.name;
				gongguo.count = detail.count;
//				gongguo.comment =  在dialog中获取
				gongguo.time = (int) (System.currentTimeMillis()/1000);
				mInsertGongGuoListener.bInsert = true;
				//插入一条记录
				if(mbGongguoconfirm_dialog == true){
					createAddConfirmDialog(mThis,base,detail,gongguo,mInsertGongGuoListener);
				}
				else{
					mInsertGongGuoListener.insert(gongguo);
				}
			}
			
			
			return false;
		}
		
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
			
			holder.txtName.setTextSize(MainActivity.TEXT_SIZE);
			holder.txtCount.setTextSize(MainActivity.TEXT_SIZE);
			
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
			holder.button = (Button) view.findViewById(R.id.btnRight);
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
			holder.button = (Button) view.findViewById(R.id.btnRight);	
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
	
	public void backClick(View view)
	{
		finish();
	}
	
	public void recordClick(View view)
	{
		if(mbUserDefine){
			AboutActivity.gotoUserDefineGongGuoActivity(this,!mbGong);
		}
		else{
			MainActivity.gotoGongGuoActivity(this, !mbGong);
		}
		
		finish();
	}
	
	public void detailClick(View view)
	{
		MainActivity.gotoUserGongGuoListActivity(this, UserGongGuoListActivity.TYPE_ALL);
		finish();
	}
			
}
