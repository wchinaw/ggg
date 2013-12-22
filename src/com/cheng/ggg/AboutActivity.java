package com.cheng.ggg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.types.DialogClickListener;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.DialogAPI;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

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
		((Button)findViewById(R.id.buttonReadLFSX)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonReadLFSXBHW)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonfeedback)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonUserDefineGong)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonUserDefineGuo)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonBackup)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonRestore)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonSettings)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonExport)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonImport)).setOnClickListener(this);
		
		
		mActivity = this;
		mSQLiteHelper = SQLiteHelper.getInstance(this);
		mRs = getResources();
		
		String version = getResources().getString(R.string.about_version) + COM.getVersionName(this);
		((TextView)findViewById(R.id.TextView02)).setText(version);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonReadLFSXBHW:
			readLFSX(COM.LFSXBHW_TXT);
			break;
		case R.id.buttonReadLFSX:
			readLFSX(COM.LFSX_TXT);
			break;
		case R.id.buttonfeedback:
			FeedbackAgent agent = new FeedbackAgent(mActivity);
			agent.sync();
		    agent.startFeedbackActivity();
//			UMFeedbackService.openUmengFeedbackSDK(this);
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
		case R.id.buttonExport:
			exportUserDefineGongGuo();
			break;
		case R.id.buttonImport:
			importUserDefineGongGuo();
			break;
		case R.id.buttonSettings:
			gotoSettingsActivity();
			break;			
		}
		
	}
	
	//备份响应事件
	DialogClickListener backUpLisenter = new DialogClickListener(){

		@Override
		public void button1Click(String src, String dest) {
			dobackUp(src,dest);
		}
	};
	
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
			createBackupConfirmDialog(srcPath, destPath,backUpLisenter);
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
	
	//导出自定义功过文件对话框
	public void createExportConfirmDialog(String srcPath,String destPath,DialogClickListener clickLisenter){
		String msg = mRs.getString(R.string.exportfile_path_info)+destPath;
		createConfirmDialog(R.string.backupfile_exists,msg,srcPath,destPath,clickLisenter);
	}
	
	public void createBackupConfirmDialog(String srcPath,String destPath,DialogClickListener clickLisenter){
		String msg = mRs.getString(R.string.backupfile_path_info)+destPath;
		createConfirmDialog(R.string.backupfile_exists,msg,srcPath,destPath,clickLisenter);
	}
		
	public void createConfirmDialog(int title,String msg,final String srcPath,final String destPath,final DialogClickListener clickLisenter){
		
		AlertDialog dialog =  new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	clickLisenter.button1Click(srcPath,destPath);
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
	
	//导出自定义功过
	public void doExportUserDefineGongGuoDetail(String destPath){
		
		boolean rc = mSQLiteHelper.exportUserDefineGongGuoDetailTable(destPath);
		if(rc == true){ //备份成功
			//Toast.makeText(mActivity,R.string.backupok, Toast.LENGTH_SHORT).show();
			createExportOKDialog(destPath);
		}
		else{//备份失败
			createExportFailDialog(destPath);
			Toast.makeText(mActivity,R.string.backupfail, Toast.LENGTH_SHORT).show();
		}
	}
	
	//导出自定义功过项响应事件
		DialogClickListener exportLisenter = new DialogClickListener(){

			@Override
			public void button1Click(String src, String dest) {
				doExportUserDefineGongGuoDetail(dest);
			}
		};
	
	/**导出用户自定义数据到.csv文件中*/
	public void exportUserDefineGongGuo(){
		String  destPath;
		
		destPath = COM.getExportUserGoneGuoFilePath();
		
		File file = new File(destPath);
		if(file.exists()){
			createExportConfirmDialog("", destPath,exportLisenter);
		}
		else{
			doExportUserDefineGongGuoDetail(destPath);
		}
		
	}
	
	public void importUserDefineGongGuo(){
		String srcPath;
		
		srcPath = COM.getExportUserGoneGuoFilePath();
		
		File file = new File(srcPath);
		if(file.exists()){
			createImportConfirmDialog(srcPath);
		}
		else{
			//createNoBackupFileDialog(srcPath, destPath);
			String msg = mRs.getString(R.string.importfile_not_exist)+srcPath;
			DialogAPI.creatInfoDialog(mActivity,R.string.importfail,msg);
			}
		
	}
		
	public void createExportOKDialog(final String destPath){
		String msg = mRs.getString(R.string.exportfile_path_info)+destPath;
		DialogAPI.creatInfoDialog(mActivity,R.string.exportok,msg);
	}
	
	public void createExportFailDialog(final String destPath){
		String msg = mRs.getString(R.string.backupfail_info)+destPath;
		DialogAPI.creatInfoDialog(mActivity,R.string.exportfail,msg);
	}
	
	/**导入自定义功过确认对话框*/
	public void createImportConfirmDialog(final String srcPath){
		String msg = mRs.getString(R.string.importfile_path_info)+srcPath;
		AlertDialog dialog =  new AlertDialog.Builder(this)
        .setTitle(R.string.importconfirm)
        .setMessage(msg)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	//恢复之前 对原文件进行备份
            	boolean rc = false;
				try {
					SQLiteDatabase  db = mSQLiteHelper.getWritableDatabase();
					rc = mSQLiteHelper.importUserDefineGongGuoDetail(db, srcPath);
					db.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            	if(rc == true){
            		DialogAPI.creatInfoDialog(mActivity,R.string.importok,null);
            	}
            	else{
            		//无法进行导入，提醒导入失败
            		Toast.makeText(mActivity,R.string.importfail, Toast.LENGTH_SHORT).show();
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

	
	public void readLFSX(String fileName){
		File file = new File ("/sdcard/"+fileName);
		if(!file.exists()){
			Log.e("","!file.exists()");
			try{
				InputStream is = getAssets().open(fileName);
				inputstreamtofile(is,file);
				
				file = new File ("/sdcard/"+fileName);
				if(file.exists())
					viewTextFile(this,file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			viewTextFile(this,file);
		}
	}

public void inputstreamtofile(InputStream ins,File file) throws IOException{
	OutputStream os = new FileOutputStream(file);
	int bytesRead = 0;
	byte[] buffer = new byte[8192];
	while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
	os.write(buffer, 0, bytesRead);
	}
	os.close();
	ins.close();
	}

//android获取一个用于打开文本文件的intent
public static void viewTextFile(Context context,File file)
{   
  Intent intent = new Intent("android.intent.action.VIEW");
  intent.addCategory("android.intent.category.DEFAULT");
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  Uri uri = Uri.fromFile(file);
  intent.setDataAndType(uri, "text/plain");
  context.startActivity(intent);
}
}
