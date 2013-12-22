package com.cheng.ggg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ViewTxtActivity extends Activity {
	Activity mActivity;
	final static String TAG = "ViewTxtActivity";
	TextView textView;
	String content;
	int scrollY = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_txt);
		mActivity = this;
		
		textView = (TextView)findViewById(R.id.textViewContent);
		
		Intent intent = getIntent();
		Uri uri = intent.getData();
		Log.e("","copyOfMainActivity uri getPath:"+uri.getPath()+" path:"+uri.toString());
		File file = new File(uri.getPath());
		if(file.exists()){
			try {
				textView.setText(readFile(uri.getPath()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String readFile(String fileName) throws IOException{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName),"GBK");
		String lineStr="";
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader(isr);
		//解析输入文件每一行
		while((lineStr=br.readLine())!=null){
			sb.append(lineStr);
		}
		br.close();
		isr.close();
		return sb.toString();
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
