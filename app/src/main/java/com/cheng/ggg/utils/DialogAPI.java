package com.cheng.ggg.utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.GongGuoListActivity;
import com.cheng.ggg.MainActivity;
import com.cheng.ggg.R;
import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.UserGongGuo;
import com.umeng.analytics.MobclickAgent;

public class DialogAPI {
	
//	public interface DialogListener{
//		public void onAllSelect();
//		public void onMaleSelect();
//		public void onFemaleSelect();
//		public void onCancel();//按back键
//	}

	//删除首页自定义快捷键
	public static void showDeleteHotGongGuoItemDialog(final MainActivity activity,final ArrayList<UserGongGuo> list ,final int i){

		if(activity == null ||list == null){
			COM.LOGE("alertDialog", "ERR activity ="+activity+" detail ="+list);
			return;//(new Dialog(activity,R.style.CustomDialogStyle));
		}
//			String strAdd = activity.getResources().getString(R.string.delete);
		if(i>=0 && i<list.size()){
			final UserGongGuo gongguo = list.get(i);

			View loadingDialog = View.inflate(activity,R.layout.dialog_delete_item, null);
			TextView txt = (TextView)loadingDialog.findViewById(R.id.textViewGongTitle);
			txt.setText(gongguo.name);

			Dialog alert = new AlertDialog.Builder(activity)
					.setPositiveButton(R.string.delete, new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							UserGongGuo gongguo = list.get(i);
							String info = gongguo.name + " " + activity.getString(R.string.delete_ok);
							list.remove(i);
							Settings.setHomeHotGongGuo(activity, list);

							Toast.makeText(activity, info, Toast.LENGTH_SHORT).show();
							activity.mGridAdapter.notifyDataSetChanged();
						}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {

						}

					})
					.setView(loadingDialog)
					.setTitle(gongguo.parent_name+" "+activity.getString(R.string.hotgongguo))
					.create();

			alert.show();
		}
	}
	
	//删除功过
		public static void showDeleteItemDialog(final GongGuoListActivity activity,final GongGuoDetail detail,final int groupPos, final int childPos, final boolean bGong
				){
			
			if(activity == null ||detail == null){
				COM.LOGE("alertDialog", "ERR activity ="+activity+" detail ="+detail);
				return;//(new Dialog(activity,R.style.CustomDialogStyle));
			}
			final GongGuoBase base = activity.mGongGuoBaseList.get(groupPos);
//			String strAdd = activity.getResources().getString(R.string.delete);
			
			View loadingDialog = View.inflate(activity,R.layout.dialog_delete_item, null);
			TextView txt = (TextView)loadingDialog.findViewById(R.id.textViewGongTitle);
			txt.setText(detail.name);
			
			Dialog alert = new AlertDialog.Builder(activity)
				.setPositiveButton(R.string.delete , new OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
							SQLiteHelper sqlite = SQLiteHelper.getInstance(activity);
							SQLiteDatabase db = sqlite.getWritableDatabase();
							boolean bOK = false;
							if(detail.bUserdefine){
								if(bGong)
									bOK = sqlite.deleteUserDefineGongItemById(db,detail.id);
								else
									bOK = sqlite.deleteUserDefineGuoItemById(db,detail.id);
							}
							else{
								if(bGong)
									bOK = sqlite.deleteGongDetailById(db,detail.id);
								else
									bOK = sqlite.deleteGuoDetailById(db,detail.id);
							}
							if(bOK){
								
								if(base.mList != null){
									COM.LOGE("", "childPos:"+childPos);
									base.mList.remove(childPos);
//									activity.mListView.invalidateViews();
									activity.mAdapter.notifyDataSetChanged();
								}
								
								Toast.makeText(activity, R.string.delete_ok, Toast.LENGTH_SHORT).show();
							}
							else{
								Toast.makeText(activity, R.string.delete_fail, Toast.LENGTH_SHORT).show();
							}
					}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener(){

					public void onClick(DialogInterface arg0, int arg1) {
						
					}
					
				})
					.setNeutralButton(R.string.edit, new OnClickListener(){
						public void onClick(DialogInterface arg0, int arg1) {
							showEditItemDialog(activity,base.name,groupPos,childPos);
						}
					})
					.setView(loadingDialog)
					.setTitle(base.name)
					.create();
			
				alert.show();
				
		}
		
		public static void showAddItemDialog(final GongGuoListActivity activity,String title,final GongGuoBase base,final boolean bGong){
			showAddItemDialog(activity,title, base,bGong, "");
		}
		
		//编辑修改自定义功过
		public static void showEditItemDialog(final GongGuoListActivity activity,final String title,final int groupPos,final int childPos){
			
			if(activity == null){
				COM.LOGE("alertDialog", "ERR activity == null!");
				return;//(new Dialog(activity,R.style.CustomDialogStyle));
			}
			
			String strAdd = activity.getResources().getString(R.string.edit);
			final GongGuoBase base = activity.mGongGuoBaseList.get(groupPos);
			final GongGuoDetail detail = base.mList.get(childPos);
			
			View loadingDialog = View.inflate(activity,R.layout.dialog_add_item, null);
			final EditText edit = (EditText)loadingDialog.findViewById(R.id.editText1);
			edit.setText(detail.name);
			
			Dialog alert = new AlertDialog.Builder(activity)
				.setPositiveButton(R.string.ok , new OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						String txt = edit.getText().toString();
						if(txt == null || txt.equals("")){
							Toast.makeText(activity, R.string.input_empty, Toast.LENGTH_SHORT).show();
						}
						else{
							//先判断数据库中是否有相同名称相同功过count的数据，如果有，则不添加。若无，则添加。
							
							SQLiteHelper sqlite = SQLiteHelper.getInstance(activity);
							SQLiteDatabase db = sqlite.getWritableDatabase();
							
							//数据表中无此记录，可插入。
							if(!sqlite.haveSameGongGuoItem(db,txt,detail.count,detail.bUserdefine)){
								String oldValue = detail.name;
								detail.name = txt;
								sqlite.updateGongGuoDetail(db, oldValue, detail);
//								activity.mListView.invalidateViews();
								activity.mAdapter.notifyDataSetChanged();
								
								String event_id = "userdefine";
								if(detail.count > 0){
									event_id +=detail.count+"gong";
								}
								else{
									event_id +=(-detail.count)+"guo";
								}
								
								COM.LOGE("", "event_id :"+event_id+" name:"+detail.name);
								
								MobclickAgent.onEvent(activity, event_id, detail.name);
								Toast.makeText(activity, R.string.edit_ok, Toast.LENGTH_SHORT).show();
							}
							else{//已存在，提示用户，已存在此功过项
								Toast.makeText(activity, R.string.add_exists, Toast.LENGTH_SHORT).show();
								showEditItemDialog(activity,title,groupPos,childPos);
							}
							
						}
					}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener(){

						public void onClick(DialogInterface arg0, int arg1) {
							
						}
						
					})
					.setView(loadingDialog)
					.setTitle(strAdd+" "+title)
					.create();
			
				alert.show();
				openKeyboard(activity);
//				((InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(edit, 0);
//				edit.requestFocus();
//				alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	
	//添加自定义功过
	public static void showAddItemDialog(final GongGuoListActivity activity,final String title,final GongGuoBase base,final boolean bGong, String edittextStr){
		
		if(activity == null){
			COM.LOGE("alertDialog", "ERR activity == null!");
			return;//(new Dialog(activity,R.style.CustomDialogStyle));
		}
		
		String strAdd = activity.getResources().getString(R.string.add);
		
		View loadingDialog = View.inflate(activity,R.layout.dialog_add_item, null);
		final EditText edit = (EditText)loadingDialog.findViewById(R.id.editText1);
		edit.setText(edittextStr);
		
		Dialog alert = new AlertDialog.Builder(activity)
			.setPositiveButton(R.string.ok , new OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					String txt = edit.getText().toString();
					if(txt == null || txt.equals("")){
						Toast.makeText(activity, R.string.input_empty, Toast.LENGTH_SHORT).show();
					}
					else{
						//先判断数据库中是否有相同名称相同功过count的数据，如果有，则不添加。若无，则添加。
						
						SQLiteHelper sqlite = SQLiteHelper.getInstance(activity);
						SQLiteDatabase db = sqlite.getWritableDatabase();
						
						//数据表中无此记录，可插入。
						if(!sqlite.haveSameGongGuoItem(db,txt,base.count)){
							GongGuoDetail detail = new GongGuoDetail();
							detail.bUserdefine = true;
							detail.count = base.count;
							detail.name = txt;
//							detail.id = sqlite.getUserDefineGongGuoId(db, txt, base.count);
							detail.id = sqlite.insertUserDefineGONGGUOTable(db, 
									bGong, txt, base.count);
							base.mList.add(0, detail);
//							activity.mListView.invalidateViews();
							activity.mAdapter.notifyDataSetChanged();
							
							String event_id = "usrdefine";
							if(detail.count > 0){
								event_id +=detail.count+"gong";
							}
							else{
								event_id +=(-detail.count)+"guo";
							}
							
							COM.LOGE("", "event_id :"+event_id+" name:"+detail.name);
							
							MobclickAgent.onEvent(activity, event_id, detail.name);
							Toast.makeText(activity, R.string.add_ok, Toast.LENGTH_SHORT).show();
						}
						else{//已存在，提示用户，已存在此功过项
							Toast.makeText(activity, R.string.add_exists, Toast.LENGTH_SHORT).show();
							showAddItemDialog(activity,title, base,bGong, txt);
						}
						
					}
				}
				})
				.setNegativeButton(R.string.cancel, new OnClickListener(){

					public void onClick(DialogInterface arg0, int arg1) {
						
					}
					
				})
				.setView(loadingDialog)
				.setTitle(strAdd+" "+title)
				.create();
		
			alert.show();
			openKeyboard(activity);
//			((InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(edit, 0);
//			edit.requestFocus();
//			alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}
	
	//添加自定义功过
		public static void showAddConfirmDialog(final GongGuoListActivity activity,final String title,final GongGuoBase base,final boolean bGong, String edittextStr){
			
			if(activity == null){
				COM.LOGE("alertDialog", "ERR activity == null!");
				return;//(new Dialog(activity,R.style.CustomDialogStyle));
			}
			
			String strAdd = activity.getResources().getString(R.string.add);
			
			View loadingDialog = View.inflate(activity,R.layout.dialog_add_item, null);
			final EditText edit = (EditText)loadingDialog.findViewById(R.id.editText1);
			edit.setText(edittextStr);
			
			Dialog alert = new AlertDialog.Builder(activity)
				.setPositiveButton(R.string.ok , new OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						String txt = edit.getText().toString();
						if(txt == null || txt.equals("")){
							Toast.makeText(activity, R.string.input_empty, Toast.LENGTH_SHORT).show();
						}
						else{
							//先判断数据库中是否有相同名称相同功过count的数据，如果有，则不添加。若无，则添加。

						}
					}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener(){

						public void onClick(DialogInterface arg0, int arg1) {
							
						}
						
					})
					.setView(loadingDialog)
					.setTitle(strAdd+" "+title)
					.create();
			
				alert.show();
				openKeyboard(activity);
//				((InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(edit, 0);
//				edit.requestFocus();
//				alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	
	//删除功过
//			public static void showConfirmDialog(){
//				
//				AlertDialog dialog = new AlertDialog()
//					
//			}
	
	public static void creatInfoDialog(Activity activity, int title, String msg){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title);
		if(msg != null){
			builder.setMessage(msg);
		}

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	
            }
        });
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public static void showSetPasswordDialog(final Activity activity){
		
		if(activity == null){
			COM.LOGE("showSetPasswordDialog", "ERR activity == null!");//
			return;//(new Dialog(activity,R.style.CustomDialogStyle));
		}
		
		View loadingDialog = View.inflate(activity,R.layout.dialog_enter_password_item, null);
		final EditText edit = (EditText)loadingDialog.findViewById(R.id.editPassword);
		final EditText edit2 = (EditText)loadingDialog.findViewById(R.id.editPassword2);
		final CheckBox checkbox1 = (CheckBox)loadingDialog.findViewById(R.id.checkBox1);
		checkbox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton arg0, boolean bNewChecked) {
				if (bNewChecked) {
					// 显示密码
					edit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					edit2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					// 隐藏密码
					edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					edit2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}

		});
		
		Dialog alert = new AlertDialog.Builder(activity)
			.setPositiveButton(R.string.ok , new OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					String txt = edit.getText().toString();
					String txt2 = edit2.getText().toString();
					if(txt == null || txt.equals("") || txt2 == null || txt2.equals("") || !txt.equals(txt2)){
						Toast.makeText(activity, R.string.input_password_empty, Toast.LENGTH_SHORT).show();
					}
					else{
						Settings.setPassword(activity, txt);
						Toast.makeText(activity, R.string.set_password_ok, Toast.LENGTH_SHORT).show();
						MainActivity.mActivity.login();
					}
				}
				})
				.setNegativeButton(R.string.cancel, new OnClickListener(){

					public void onClick(DialogInterface arg0, int arg1) {
						
					}
					
				})
				.setView(loadingDialog)
				.setTitle(R.string.set_password)
				.create();
		
			alert.show();
			openKeyboard(activity);
	}
	
	public static void showCheckPasswordDialog(final MainActivity activity){
		
		if(activity == null){
			COM.LOGE("showSetPasswordDialog", "ERR activity == null!");
			return;//(new Dialog(activity,R.style.CustomDialogStyle));
		}
		
		View loadingDialog = View.inflate(activity,R.layout.dialog_enter_password_item, null);
		final EditText edit = (EditText)loadingDialog.findViewById(R.id.editPassword);
		final EditText edit2 = (EditText)loadingDialog.findViewById(R.id.editPassword2);
		final CheckBox checkbox1 = (CheckBox)loadingDialog.findViewById(R.id.checkBox1);
		edit2.setVisibility(View.GONE);
		
		checkbox1.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton arg0, boolean bNewChecked) {
				if (bNewChecked) {    
				    // 显示密码    
					edit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);     
				    }   
				else {    
				    // 隐藏密码    
					edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);    
				}   
			}
			
		});
		
		Dialog alert = new AlertDialog.Builder(activity)
			.setPositiveButton(R.string.ok , new OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					String txt = edit.getText().toString();
					String password = Settings.getPassword(activity);
					if(txt == null || txt.equals("") || !txt.equals(password)){
						Toast.makeText(activity, R.string.login_password_error, Toast.LENGTH_SHORT).show();
						showCheckPasswordDialog(activity);
					}
					else{
						activity.login();
					}
				}
				})
				.setNegativeButton(R.string.cancel, new OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						activity.finish();
					}

				})
				.setOnCancelListener(new OnCancelListener() {

					public void onCancel(DialogInterface arg0) {
						activity.finish();
					}

				})
				.setView(loadingDialog)
				.setTitle(R.string.login_password)
				.create();
		
			alert.show();
			openKeyboard(activity);
	}
	
	/**
     * 打开软键盘
     */
    private static void openKeyboard(final Activity activity) {

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
				@Override
				public void run() {
					InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

				}
			}, 800);
    }

	public interface ConfirmDialog3ItemsListener{
		public void onItem1(Object obj);
		public void onItem2(Object obj);
		public void onItem3(Object obj);
	}

	public static Dialog mDialogHozTwoButton;
	public static void hideDialogHozTwoButton()
	{
		if(mDialogHozTwoButton != null){
			mDialogHozTwoButton.dismiss();
			mDialogHozTwoButton = null;
		}
	}

	public static Dialog DialogHozTwoButton(final Activity activity,final Object obj, final ConfirmDialog3ItemsListener listener,
											int one ,int two){

		if(activity == null){
			COM.LOGE("alertDialog", "ERR activity == null!");
			return null;//(new Dialog(activity,R.style.CustomDialogStyle));
		}

		mDialogHozTwoButton = new Dialog(activity,R.style.ThemeActivity);
		mDialogHozTwoButton.requestWindowFeature(Window.FEATURE_NO_TITLE);

// 		View loadingDialog = View.inflate(activity,R.layout.confirm_dialog, null);
		View loadingDialog = View.inflate(activity,R.layout.mydiary_more, null);

		TextView topText = (TextView) loadingDialog.findViewById(R.id.mydiary_more_share);
//		LinearLayout lout_1 = (LinearLayout) loadingDialog.findViewById(R.id.lout_1);

		TextView bottomText = (TextView) loadingDialog.findViewById(R.id.mydiary_more_change);
		RelativeLayout rl = (RelativeLayout) loadingDialog.findViewById(R.id.dialog_twobtn);

//				lout_1.setBackgroundResource(R.color.chat_bg);
				topText.setBackgroundResource(R.drawable.dialogtopclick_selector);
				bottomText.setBackgroundResource(R.drawable.dialogtopclick_selector);
//			int	color = activity.getResources().getColor(R.color.yellow);
//		topText.setTextColor(color);
//		bottomText.setTextColor(color);
		topText.setText(one);
		bottomText.setText(two);

// 		ImageView imgTop = (ImageView) loadingDialog.findViewById(R.id.imageView1);
// 		ImageView imgBottom = (ImageView) loadingDialog.findViewById(R.id.imageView2);






		topText.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onItem1(obj);
				hideDialogHozTwoButton();
			}

		});


		bottomText.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.onItem2(obj);
				hideDialogHozTwoButton();
			}

		});

		rl.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
//				listener.onItem3(obj);
				hideDialogHozTwoButton();

			}

		});


		Window window = mDialogHozTwoButton.getWindow();

		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = (int) (dm.widthPixels);



//		Log.i("test", "弹出dialog的长宽"+screenWidth);

		WindowManager.LayoutParams params0 = window.getAttributes();
		params0.width = screenWidth;


		window.setAttributes(params0);
		mDialogHozTwoButton.setContentView(loadingDialog);
		mDialogHozTwoButton.show();
		return mDialogHozTwoButton;
	}
}
