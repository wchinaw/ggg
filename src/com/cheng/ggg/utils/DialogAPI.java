package com.cheng.ggg.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.GongGuoListActivity;
import com.cheng.ggg.R;
import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;

public class DialogAPI {
	
//	public interface DialogListener{
//		public void onAllSelect();
//		public void onMaleSelect();
//		public void onFemaleSelect();
//		public void onCancel();//按back键
//	}
	
	//删除功过
		public static void showDeleteItemDialog(final GongGuoListActivity activity,final GongGuoDetail detail,final int groupPos, final int childPos, final boolean bGong
				){
			
			if(activity == null ||detail == null){
				COM.LOGE("alertDialog", "ERR activity ="+activity+" detail ="+detail);
				return;//(new Dialog(activity,R.style.CustomDialogStyle));
			}
			final GongGuoBase base = activity.mGongGuoBaseList.get(groupPos);
			String strAdd = activity.getResources().getString(R.string.delete);
			
			View loadingDialog = View.inflate(activity,R.layout.dialog_delete_item, null);
			TextView txt = (TextView)loadingDialog.findViewById(R.id.textView1);
			txt.setText(detail.name);
			
			Dialog alert = new AlertDialog.Builder(activity)
				.setPositiveButton(R.string.ok , new OnClickListener(){

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
									activity.mListView.invalidateViews();
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
					.setView(loadingDialog)
					.setTitle(strAdd+" "+base.name)
					.create();
			
				alert.show();
				
		}
	
	//添加自定义功过
	public static void showAddItemDialog(final GongGuoListActivity activity,String title,final GongGuoBase base,final boolean bGong){
		
		if(activity == null){
			COM.LOGE("alertDialog", "ERR activity == null!");
			return;//(new Dialog(activity,R.style.CustomDialogStyle));
		}
		
		String strAdd = activity.getResources().getString(R.string.add);
		
		View loadingDialog = View.inflate(activity,R.layout.dialog_add_item, null);
		final EditText edit = (EditText)loadingDialog.findViewById(R.id.editText1);
		
		Dialog alert = new AlertDialog.Builder(activity)
			.setPositiveButton(R.string.ok , new OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					String txt = edit.getText().toString();
					if(txt == null || txt.equals("")){
						Toast.makeText(activity, R.string.input_empty, Toast.LENGTH_SHORT).show();
					}
					else{
						SQLiteHelper sqlite = SQLiteHelper.getInstance(activity);
						 SQLiteDatabase db = sqlite.getWritableDatabase();
						sqlite.insertUserDefineGONGGUOTable(db, 
								bGong, txt, base.count);
						
						GongGuoDetail detail = new GongGuoDetail();
						detail.bUserdefine = true;
						detail.count = base.count;
						detail.name = txt;
						detail.id = sqlite.getUserDefineGongGuoId(db, txt, base.count);
						base.mList.add(0, detail);
						activity.mListView.invalidateViews();
					}
						Toast.makeText(activity, R.string.add_ok, Toast.LENGTH_SHORT).show();

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
}
