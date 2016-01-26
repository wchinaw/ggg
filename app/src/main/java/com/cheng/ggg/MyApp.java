package com.cheng.ggg;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by loadling1 on 15/11/17.
 */
public class MyApp extends Application {
    static MyApp mContext;

    private int mServerTime;
    private int mLocalTime;
    private String mVersion = null;
//	private SQLiteHelper mSQLiteHelper;

//	public void setUserSetting(UserSetting set){
//		mUserSetting = set;
//	}

    public void setServerTime(int sys){
        if(sys > 0){
            mServerTime = sys;
            mLocalTime = (int) (System.currentTimeMillis()/1000);
        }
    }

    @Override

    public void onCreate() {
        mContext = this;
        super.onCreate();
    }

    public int getCurrentTime(){
        return (int)(System.currentTimeMillis()/1000)-mLocalTime+mServerTime;
    }

    public static MyApp getContext()
    {
        return mContext;
    }

    public static MyApp getInstance()
    {
        if(mContext == null){
            mContext = new MyApp();
        }

        return mContext;
    }

    public String getVersionCode(){
        if(mVersion == null){
            try {
                PackageInfo pi= getPackageManager().getPackageInfo(getPackageName(), 0);
                mVersion = pi.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "";
            }
            if(mVersion != null){
                mVersion = mVersion.replace(".", "_");
            }
            mVersion = "android_"+mVersion;
        }
        return mVersion;
    }

//
//    public UserSetting getUserSetting(int myUid){
//        return SQLiteHelper.getInstance(mContext).getUserSetting(myUid);
//		if(mUserSetting == null && myUid != 0){
//			mUserSetting = SQLiteHelper.getInstance(mContext).getUserSetting(myUid);
//		}
//
//		if(mUserSetting == null){
//			mUserSetting = new UserSetting();
//		}
//
//		return mUserSetting;
//    }
}