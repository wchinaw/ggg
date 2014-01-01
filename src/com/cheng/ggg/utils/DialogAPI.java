package com.cheng.ggg.utils;

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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.GongGuoListActivity;
import com.cheng.ggg.MainActivity;
import com.cheng.ggg.R;
import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.umeng.analytics.MobclickAgent;

public class DialogAPI {
	
//	public interface DialogListener{
//		public void onAllSelect();
//		public void onMaleSelect();
//		public void onFemaleSelect();
//		public void onCancel();//��back��
//	}
	
	//ɾ������
		public static void showDeleteItemDialog(final GongGuoListActivity activity,final GongGuoDetail detail,final int groupPos, final int childPos, final boolean bGong
				){
			
			if(activity == null ||detail == null){
				COM.LOGE("alertDialog", "ERR activity ="+activity+" detail ="+detail);
				return;//(new Dialog(activity,R.style.CustomDialogStyle));
			}
			final GongGuoBase base = activity.mGongGuoBaseList.get(groupPos);
//			String strAdd = activity.getResources().getString(R.string.delete);
			
			View loadingDialog = View.inflate(activity,R.layout.dialog_delete_item, null);
			TextView txt = (TextView)loadingDialog.findViewById(R.id.textView1);
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
		
		//�༭�޸��Զ��幦��
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
							//���ж����ݿ����Ƿ�����ͬ������ͬ����count�����ݣ�����У�����ӡ����ޣ�����ӡ�
							
							SQLiteHelper sqlite = SQLiteHelper.getInstance(activity);
							SQLiteDatabase db = sqlite.getWritableDatabase();
							
							//���ݱ����޴˼�¼���ɲ��롣
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
							else{//�Ѵ��ڣ���ʾ�û����Ѵ��ڴ˹�����
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
	
	//����Զ��幦��
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
						//���ж����ݿ����Ƿ�����ͬ������ͬ����count�����ݣ�����У�����ӡ����ޣ�����ӡ�
						
						SQLiteHelper sqlite = SQLiteHelper.getInstance(activity);
						SQLiteDatabase db = sqlite.getWritableDatabase();
						
						//���ݱ����޴˼�¼���ɲ��롣
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
						else{//�Ѵ��ڣ���ʾ�û����Ѵ��ڴ˹�����
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
	
	//����Զ��幦��
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
							//���ж����ݿ����Ƿ�����ͬ������ͬ����count�����ݣ�����У�����ӡ����ޣ�����ӡ�

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
	
	//ɾ������
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
		checkbox1.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton arg0, boolean bNewChecked) {
				if (bNewChecked) {    
				    // ��ʾ����    
					edit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);   
					edit2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);  
				    }   
				else {    
				    // ��������    
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
				    // ��ʾ����    
					edit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);     
				    }   
				else {    
				    // ��������    
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
				.setNegativeButton(R.string.cancel, new OnClickListener(){

					public void onClick(DialogInterface arg0, int arg1) {
						activity.finish();
					}
					
				})
				.setOnCancelListener(new OnCancelListener(){

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
     * �������
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
}
