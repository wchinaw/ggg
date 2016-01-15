package com.cheng.ggg;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.ggg.database.SQLiteHelper;
import com.cheng.ggg.receiver.AlarmReceiver;
import com.cheng.ggg.utils.AlarmNotification;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.DialogAPI;
import com.cheng.ggg.utils.Settings;
import com.cheng.ggg.utils.TimeDate;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity implements OnClickListener
  ,OnSharedPreferenceChangeListener{
	
	SQLiteHelper mSQLiteHelper;
	TextView textGong, textGuo, textTotal;
	int mGongCount, mGuoCount;
	TextView textTips;
	RelativeLayout mLayoutBackground;
	Animation mAlphaAnimation;
	String tipsStr[] = null;
	
	public static int TEXT_SIZE = 20;
	/**功过颜色互换*/
	public static boolean COLOR_SWAP = false;
	public boolean bCheckPasswordOK = false;
	final int REQUEST_SELECT_PIC = 1;
	
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    File tempFile;//
    String mHomeImagePath;
    
    public static MainActivity mActivity;
    
    final int BUTTON_COUNTS = 4;
    Button mButtons[] = new Button[BUTTON_COUNTS];
    int buttonIds[] = {R.id.buttonGong, R.id.buttonGuo ,R.id.buttonDetail,R.id.buttonAbout};
    //调整fontSize
    TextView textViewGongTitle,TextViewGuoTitle,TextViewTotalTitle;
    int colors[] = {0xff777777,Color.BLACK,    
    		Color.BLUE, 
    		Color.CYAN ,
    		Color.DKGRAY, 
    		Color.GRAY ,
    		Color.GREEN ,
    		Color.LTGRAY ,
    		Color.MAGENTA ,
    		Color.RED ,
    		Color.WHITE ,
    		Color.YELLOW };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //每次进入之前，检查并创建 ggg文件夹。
        File file = new File(COM.GGG_DIRECTORY_PATH);  
        if (!file.exists()) {  
            try {  
                //按照指定的路径创建文件夹  
                file.mkdirs();  
            } catch (Exception e) {  
                // TODO: handle exception  
            }  
        }  
        
        tempFile = new File(COM.GGG_DIRECTORY_PATH,getPhotoFileName());
        
        
        mActivity = this;
        mSQLiteHelper = SQLiteHelper.getInstance(this);
        TEXT_SIZE = Settings.getFontSize(this);
        COLOR_SWAP = Settings.getIsColorSwap(this);
        
        ((RelativeLayout)findViewById(R.id.layoutContent)).setOnLongClickListener(new OnLongClickListener(){

			public boolean onLongClick(View arg0) {
				showDialog();
				return false;
			}
        	
        });
        
        for(int i=0; i<BUTTON_COUNTS; i++){
        	mButtons[i] = ((Button)findViewById(buttonIds[i]));
        	mButtons[i].setOnClickListener(this);
        }
        
        textViewGongTitle = (TextView) findViewById(R.id.textViewGongTitle);
        TextViewGuoTitle = (TextView) findViewById(R.id.TextViewGuoTitle);
        TextViewTotalTitle = (TextView) findViewById(R.id.TextViewTotalTitle);
        
        textGong = (TextView) findViewById(R.id.textMonthGong);
        textGuo = (TextView) findViewById(R.id.textMonthGuo);
        textTotal = (TextView) findViewById(R.id.textMonthTotal);
        mHomeImagePath = COM.GGG_DIRECTORY_PATH+"/"+COM.HOMG_IMG;
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
       
        TimeDate.test(this);
    }
    
//    BroadcastReceiver mBroadCast = new BroadcastReceiver(){
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			
//		}
//    	
//    };
    
    public void setFontSizeAndColor(){
    	for(int i=0; i< BUTTON_COUNTS; i++){
    		mButtons[i].setTextSize(TEXT_SIZE);
    	}
    	textTips.setTextSize(TEXT_SIZE);
    	textGong.setTextSize(TEXT_SIZE);
    	textGuo.setTextSize(TEXT_SIZE);
        textTotal.setTextSize(TEXT_SIZE);
        
        textViewGongTitle.setTextSize(TEXT_SIZE);
        TextViewGuoTitle.setTextSize(TEXT_SIZE);
        TextViewTotalTitle.setTextSize(TEXT_SIZE);
    }
    
    public void initTips(boolean bAnimation){
    	if(textTips == null)
    		textTips = (TextView) findViewById(R.id.textViewTips);
    	
    	//设置首页字体颜色
    	textTips.setTextColor(Settings.getHomeTextColorIndex(this));
    	
    	if(tipsStr == null){
    		String userDefineTips = Settings.getUserdefineTips(this);
    		userDefineTips = userDefineTips.trim();
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
    	textTips.setOnClickListener(this);
    	textTips.setOnLongClickListener(new OnLongClickListener(){

			public boolean onLongClick(View arg0) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle(R.string.date_range);
				builder.setItems(R.array.list_colors, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						int color = colors[which];
						Settings.setHomeTextColorIndex(mActivity, color);
						textTips.setTextColor(color);
					}
				});
				builder.create().show();
				return false;
			}
    		
    	});
    	
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
    	TEXT_SIZE = Settings.getFontSize(this);
    	COLOR_SWAP = Settings.getIsColorSwap(this);
    	setFontSizeAndColor();
    	
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
		 //清除notification
        AlarmNotification.clearNotification(this);
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
    	mGongCount = mSQLiteHelper.getUserGongCount(db);
    	mGuoCount = mSQLiteHelper.getUserGuoCount(db);
    	int monthTotal = mGongCount+mGuoCount;
    	
    	db.close();
    	
    	setTextViewColorAndCount(textGong,mGongCount);
    	setTextViewColorAndCount(textGuo,mGuoCount);
    	setTextViewColorAndCount(textTotal,monthTotal);
    }
	
	public void setTextViewColorAndCount(TextView view, int count){
		if(view == null)
			return;
		
		view.setText(count+"");
		
		int gongColor = COM.COLOR_GONG;
		int guoColor = COM.COLOR_GUO;
		
		if(COLOR_SWAP){
			gongColor = COM.COLOR_GUO;
			guoColor = COM.COLOR_GONG;
		}
		
		
		if(count > 0){
			view.setTextColor(gongColor);
		}
		else if(count < 0){
			view.setTextColor(guoColor);
		}
		else {
			view.setTextColor(Color.WHITE);
		}
	}
    
    public static void gotoGongGuoActivity(Context context, boolean bGong){
    	Intent intent = new Intent(context,GongGuoListActivity.class);
    	intent.putExtra(COM.INTENT_GONG, bGong);
    	context.startActivity(intent);
    }
    
    public static void gotoUserGongGuoListActivity(Context context,int type){
    	Intent intent = new Intent(context,UserGongGuoListActivity.class);
    	intent.putExtra(COM.INTENT_TYPE, type);
    	context.startActivity(intent);
    }

    private void gotoAboutActivity(){
    	Intent intent = new Intent(this,AboutActivity.class);
    	startActivity(intent);
    }
    
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonGong: 
			gotoGongGuoActivity(this,true);
			break;
		case R.id.buttonGuo:
			gotoGongGuoActivity(this,false);
			break;
		case R.id.buttonDetail:
			if(mGuoCount==0 && mGongCount == 0){
				Toast.makeText(this, R.string.empty_user_detaillist, Toast.LENGTH_LONG).show();
			}
			else
				gotoUserGongGuoListActivity(this,UserGongGuoListActivity.TYPE_ALL);
			break;
		case R.id.buttonAbout:
			gotoAboutActivity();
			break;
		case R.id.textViewTips:
			initTips(true);
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
                })
                .setNeutralButton(R.string.defaultpic, new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface arg0, int arg1) {
						File file = new File(mHomeImagePath);
						if(file.exists()){
							file.delete();
						}
						mLayoutBackground.setBackgroundResource(R.drawable.b);
					}
                	
                })
                .show();
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

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(Settings.tips.equals(key)){
			initTips(false);
		}
	}
 

}
