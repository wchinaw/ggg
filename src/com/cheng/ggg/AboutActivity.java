package com.cheng.ggg;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.DialogAPI;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

public class AboutActivity extends Activity implements OnClickListener {
	final String TAG = "AboutActivity";
	SQLiteHelper mSQLiteHelper;
	Activity mActivity;
	Resources mRs;
	
	//mode
	final int MODE_BACKUP = 0;
	final int MODE_RESTORE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		((Button)findViewById(R.id.button1)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonUserDefineGong)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonUserDefineGuo)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonBackup)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonRestore)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonSettings)).setOnClickListener(this);
		
		mActivity = this;
		mSQLiteHelper = SQLiteHelper.getInstance(this);
		mRs = getResources();
		
		String version = getResources().getString(R.string.about_version) + COM.getVersionName(this);
		((TextView)findViewById(R.id.TextView02)).setText(version);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:
			UMFeedbackService.openUmengFeedbackSDK(this);
			break;
		case R.id.buttonUserDefineGong:
			gotoUserDefineGongGuoActivity(true);
			break;
		case R.id.buttonUserDefineGuo:
			gotoUserDefineGongGuoActivity(false);
			break;
		case R.id.buttonBackup:
			backUp();
			break;
		case R.id.buttonRestore:
			restore();
			break;
		case R.id.buttonSettings:
			gotoSettingsActivity();
			break;			
		}
		
	}
	
	public void dobackUp(String srcPath, String destPath){
		int rc = COM.copyFile(srcPath,destPath);
		if(rc == 0){ //备份成功
			//Toast.makeText(mActivity,R.string.backupok, Toast.LENGTH_SHORT).show();
			createBackupOKDialog(srcPath,destPath);
		}
		else{//备份失败
			createBackupFailDialog(srcPath,destPath);
			Toast.makeText(mActivity,R.string.backupfail, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void backUp(){
		String srcPath, destPath;
		SQLiteDatabase  db = mSQLiteHelper.getReadableDatabase();
		srcPath = db.getPath();
		destPath = COM.getBackupFilePath();
		
		File file = new File(destPath);
		if(file.exists()){
			createBackupConfirmDialog(srcPath, destPath);
		}
		else{
			dobackUp(srcPath, destPath);
		}
		
	}
	
	public void restore(){
		String srcPath, destPath;
		SQLiteDatabase  db = mSQLiteHelper.getReadableDatabase();
		destPath = db.getPath();
		srcPath = COM.getBackupFilePath();
		
		File file = new File(srcPath);
		if(file.exists()){
			createRestoreConfirmDialog(srcPath, destPath);
		}
		else{
			//createNoBackupFileDialog(srcPath, destPath);
			String msg = mRs.getString(R.string.restorefile_not_exist)+srcPath;
			DialogAPI.creatInfoDialog(mActivity,R.string.restorefail,msg);
			}
		
	}
		
	public void createBackupConfirmDialog(final String srcPath,final String destPath){
		String msg = mRs.getString(R.string.backupfile_path_info)+destPath;
		AlertDialog dialog =  new AlertDialog.Builder(this)
        .setTitle(R.string.backupfile_exists)
        .setMessage(msg)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	
            	dobackUp(srcPath,destPath);
            	
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        })
        .create();
		
		dialog.show();
	}
	
	public void createBackupOKDialog(final String srcPath,final String destPath){
		String msg = mRs.getString(R.string.backupfile_path_info)+destPath;
		DialogAPI.creatInfoDialog(mActivity,R.string.backupok,msg);
	}
	
	public void createBackupFailDialog(final String srcPath,final String destPath){
		String msg = mRs.getString(R.string.backupfail_info)+destPath;
		DialogAPI.creatInfoDialog(mActivity,R.string.backupfail,msg);
	}
	
	
	public void createRestoreConfirmDialog(final String srcPath,final String destPath){
		String msg = mRs.getString(R.string.backupfile_path_info)+srcPath;
		AlertDialog dialog =  new AlertDialog.Builder(this)
        .setTitle(R.string.restoreconfirm)
        .setMessage(msg)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	//恢复之前 对原文件进行备份
            	int rc = COM.copyFile(destPath,destPath+COM.BACKUP_FOR_RESTORE_EXT);
            	if(rc == 0){
            		rc = COM.copyFile(srcPath,destPath);
        			if(rc == 0){ //恢复成功
        				DialogAPI.creatInfoDialog(mActivity,R.string.restoreok,null);
        				//Toast.makeText(mActivity,R.string.restoreok, Toast.LENGTH_SHORT).show();
        			}
        			else{//恢复失败 将原文件替换为原来的.
        				rc = COM.copyFile(destPath+COM.BACKUP_FOR_RESTORE_EXT,destPath);
        				if( rc == 0){
        					DialogAPI.creatInfoDialog(mActivity,R.string.restorefail,
        						mRs.getString(R.string.restorefail_info));
        				}
        				else{
        					DialogAPI.creatInfoDialog(mActivity,R.string.restorefail,
            						null);
        				}
        				//Toast.makeText(mActivity,R.string.restorefail, Toast.LENGTH_SHORT).show();
        			}
            	}
            	else{
            		//无法进行备份，提醒恢复失败
            		Toast.makeText(mActivity,R.string.restorefail_backupextfail, Toast.LENGTH_SHORT).show();
            	}
            	
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        })
        .create();
		
		dialog.show();
	}
	
	
	public void gotoUserDefineGongGuoActivity(boolean bGong){
    	Intent intent = new Intent(this,GongGuoListActivity.class);
    	intent.putExtra(COM.INTENT_GONG, bGong);
    	intent.putExtra(COM.INTENT_USERDEFINE, true);
    	startActivity(intent);
    }
	
	public void gotoSettingsActivity(){
    	Intent intent = new Intent(this,SettingsActivity.class);
    	startActivity(intent);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

    


	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
