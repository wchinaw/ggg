package com.cheng.ggg;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.DialogAPI;
import com.cheng.ggg.utils.Settings;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity implements OnClickListener{
	
	SQLiteHelper mSQLiteHelper;
	TextView textGong, textGuo, textTotal;
	TextView textTips;
	RelativeLayout mLayoutBackground;
	Animation mAlphaAnimation;
	String tipsStr[] = null;
	
	public static final int TEXT_SIZE = 20;
	public boolean bCheckPasswordOK = false;
	final int REQUEST_SELECT_PIC = 1;
	
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
    String mHomeImagePath;
    
    public static MainActivity mActivity;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mSQLiteHelper = SQLiteHelper.getInstance(this);
        
        ((RelativeLayout)findViewById(R.id.layoutContent)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonGong)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonGuo)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonDetail)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonAbout)).setOnClickListener(this);
        
        textGong = (TextView) findViewById(R.id.textMonthGong);
        textGuo = (TextView) findViewById(R.id.textMonthGuo);
        textTotal = (TextView) findViewById(R.id.textMonthTotal);
        mHomeImagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+COM.HOMG_IMG;
        mLayoutBackground = (RelativeLayout)findViewById(R.id.layoutBackground);
//        String uriStr = Settings.getPic(this);
//        Uri uri = Uri.parse(uriStr);
//        if(!uri.equals("")){
//        	setPicUri(uri,false);
//        }
        Drawable drawable = Drawable.createFromPath(mHomeImagePath);
        if(drawable != null)
        	mLayoutBackground.setBackgroundDrawable(drawable);
        initTips(true);
//        SQLiteHelper helper = SQLiteHelper.getInstance(this);
//        SQLiteDatabase  db = helper.getReadableDatabase();
//        ArrayList<GongGuoBase> list = helper.getGongBase(db);
//        
//        if(list != null){
//        	int len = list.size();
//        	for(int i=0; i<len; i++){
//        		list.get(i).dump();
//        	}
//        }
//        
//        db.close();
        //升级检测 
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        //自动捕获异常退出（FC）
        MobclickAgent.onError(this);
        //用户反馈
        //代码中启用Feedback模块，调用下面函数进入反馈界面：
        //UMFeedbackService.openUmengFeedbackSDK(this);
        //当开发者回复用户反馈后，如果需要提醒用户，请在应用程序的入口Activity的OnCreate()方法中下添加以下代码
//        UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
//        FeedbackAgent agent = new FeedbackAgent(this);
//        agent.sync();
        //方法第一个参数类型为：Context，第二个参数为枚举类型，可选值为NotificationType.AlertDialog 或NotificationType.NotificationBar，分别对应两种不同的提示方式：

    }
    
    public void initTips(boolean bAnimation){
    	if(textTips == null)
    		textTips = (TextView) findViewById(R.id.textViewTips);
    	if(tipsStr == null){
    		String userDefineTips = Settings.getUserdefineTips(this);
    		if("".equals(userDefineTips)){
    			tipsStr = getResources().getStringArray(R.array.TIPS_STR);
    		}
    		else{
    			tipsStr = userDefineTips.split("\n");
    		}
    	}
    	
    	
    	
    	int len = tipsStr.length;
    	int index = (int)(Math.random()*(len));
    	if(index <0 || index >= len){
    		index = 0;
    	}
    	COM.LOGE("", "index:"+index);
    	COM.LOGE("", "tipsStr["+index+"]:"+tipsStr[index]);
    	textTips.setText(tipsStr[index]);
    	textTips.setTextSize(TEXT_SIZE);
    	textTips.setOnClickListener(this);
    	
    	if(bAnimation){
    		startAlphaAnimation(textTips);
    	}
    }
    
    public void startAlphaAnimation(View view){
    	if(view == null)
    		return;
    	
    	if(mAlphaAnimation == null){
    		mAlphaAnimation = new android.view.animation.AlphaAnimation(0.1f, 1.0f);
    		mAlphaAnimation.setDuration(1000);
    	}
    	
    	view.startAnimation(mAlphaAnimation);
    }
    
    
    @Override
	protected void onResume() {
    	boolean isEnablePassword = Settings.getIsEnablePassword(this);
    	if(!bCheckPasswordOK && isEnablePassword){
    		String password = Settings.getPassword(this);
        	if("".equals(password)){
        		DialogAPI.showSetPasswordDialog(this);
        	}
        	else{
        		DialogAPI.showCheckPasswordDialog(this);
        	}
    	}
    	else{
    		refreshGongGuoInfo();
    	}
		super.onResume();
		MobclickAgent.onResume(this);
	}

    


	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		logout();
		super.onDestroy();
	}
	
	public void login(){
		bCheckPasswordOK = true;
		refreshGongGuoInfo();
	}
	
	public void logout(){
		bCheckPasswordOK = false;
	}

	public void refreshGongGuoInfo(){
    	SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
    	int monthGong = mSQLiteHelper.getUserGongCount(db);
    	int monthGuo = mSQLiteHelper.getUserGuoCount(db);
    	int monthTotal = monthGong+monthGuo;
    	
    	db.close();
    	
    	setTextViewColorAndCount(textGong,monthGong);
    	setTextViewColorAndCount(textGuo,monthGuo);
    	setTextViewColorAndCount(textTotal,monthTotal);
    }
	
	public void setTextViewColorAndCount(TextView view, int count){
		if(view == null)
			return;
		
		view.setText(count+"");
		if(count > 0){
			view.setTextColor(Color.RED);
		}
		else if(count < 0){
			view.setTextColor(Color.GREEN);
		}
		else {
			view.setTextColor(Color.WHITE);
		}
	}
    
    public void gotoGongGuoActivity(boolean bGong){
    	Intent intent = new Intent(this,GongGuoListActivity.class);
    	intent.putExtra(COM.INTENT_GONG, bGong);
    	startActivity(intent);
    }
    
    private void gotoUserGongGuoListActivity(int type){
    	Intent intent = new Intent(this,UserGongGuoListActivity.class);
    	intent.putExtra(COM.INTENT_TYPE, type);
    	startActivity(intent);
    }

    private void gotoAboutActivity(){
    	Intent intent = new Intent(this,AboutActivity.class);
    	startActivity(intent);
    }
    
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonGong: 
			gotoGongGuoActivity(true);
			break;
		case R.id.buttonGuo:
			gotoGongGuoActivity(false);
			break;
		case R.id.buttonDetail:
			gotoUserGongGuoListActivity(UserGongGuoListActivity.TYPE_ALL);
			break;
		case R.id.buttonAbout:
			gotoAboutActivity();
			break;
		case R.id.textViewTips:
			initTips(true);
			break;
		case R.id.layoutContent:
//			selectPic();
			showDialog();
			break;
		}
	}
	
	public void selectPic(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		intent.setType("image/*");  
//	  	intent.putExtra("crop", true);  
		intent.putExtra("noFaceDetection", true);
	  	intent.putExtra("crop", "circle");
	   intent.putExtra("return-data", true);  
	  startActivityForResult(intent, REQUEST_SELECT_PIC);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode == REQUEST_SELECT_PIC && data != null){
//			Uri uri = data.getData(); 
//			if(uri != null)
//				setPicUri(uri,true);
//		}
		switch (requestCode) {
        case PHOTO_REQUEST_TAKEPHOTO:
            startPhotoZoom(Uri.fromFile(tempFile), 200);
            break;

        case PHOTO_REQUEST_GALLERY:
            if (data != null)
                startPhotoZoom(data.getData(), 200);
            break;

        case PHOTO_REQUEST_CUT:
            if (data != null) 
                setPicToView(data);
            break;
        }
		super.onActivityResult(requestCode, resultCode, data);
	}
	
//	public void setPicUri(Uri uri, boolean saveUri){
//		Drawable yourDrawable;
//		try {
//		    InputStream inputStream = getContentResolver().openInputStream(uri);
//		    yourDrawable = Drawable.createFromStream(inputStream, uri.toString() );
//		    if(saveUri)
//		    	Settings.setPic(this, uri.toString());
//		} catch (FileNotFoundException e) {
//		    yourDrawable = getResources().getDrawable(R.drawable.b);
//		}
//		mLayoutBackground.setBackgroundDrawable(yourDrawable);
//	}
	
	 //提示对话框方法
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.homepage_background_setting)
                .setPositiveButton(R.string.takephoto, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        // 调用系统的拍照功能
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                    }
                })
                .setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();
    }
    
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1.5);
//
//        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 480);
        intent.putExtra("noFaceDetection", true); 
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            mLayoutBackground.setBackgroundDrawable(drawable);
            
            try {
                FileOutputStream out = new FileOutputStream(mHomeImagePath);
                photo.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
         } catch (Exception e) {
                e.printStackTrace();
         }
        }
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
 

}
