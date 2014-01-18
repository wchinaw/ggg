package com.cheng.ggg.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cheng.ggg.R;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.TimeRange;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;
import com.cheng.ggg.utils.Settings;

public class SQLiteHelper extends SQLiteOpenHelper {
//	private static final int VERSION = 2;
	private static final int VERSION = 3;//���version 2 ����һ���������������ֶΣ������ڷ���n��֮��ģ���
	public boolean isAdd=true;
	
	public Context mContext = null;
	
	public static SQLiteHelper mSQLitehelper;
	
	public static SQLiteHelper getInstance(Context context){
		if(mSQLitehelper == null){
			mSQLitehelper = new SQLiteHelper(context, COM.DBNAME);
		}
		return mSQLitehelper;
	}
	
	
	// ��SQLiteOpenHelper�����൱�У������иù��캯��
	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	public SQLiteHelper(Context context, String name) {
		this(context, name, VERSION);
		mContext = context;
	}

	public SQLiteHelper(Context context, String name, int version) {
		this(context, name, null, version);
		mContext = context;
	}
	public SQLiteHelper(Context context, String name, int version,boolean isAdd) {
		this(context, name, null, version);
		this.isAdd=isAdd;
		mContext = context;
	}
	
	public String gong_base_table = "gong_base";
	public String guo_base_table = "guo_base";
	
	public String CREATE_TABLE_GONG_BASE = "create table "+gong_base_table+"("+
			"id integer,"+
			"name text,"+  //����
			"count integer"+ //�������� �ٹ� ʮ����
			")"
			;
	public String CREATE_TABLE_GUO_BASE = "create table "+guo_base_table+"("+
			"id integer,"+
			"name text,"+  //����
			"count integer"+ //�������� �ٹ� ʮ����
			")"
			;
	
	public String gong_detail_table = "gong_detail";
	public String CREATE_TABLE_GONG_DETAIL = "create table "+gong_detail_table+"("+
			"id integer,"+
			"name text,"+  //����
			"count integer"+ //�������� �ٹ� ʮ����
			")"
			;
	
	/**�û��Զ��幦*/
	public String userdefine_gong_detail_table = "userdefine_gong_detail";
	public String CREATE_TABLE_USERDEFINE_GONG_DETAIL = "create table "+userdefine_gong_detail_table+"("+
			"id integer primary key autoincrement,"+
			"name text,"+  //����
			"count integer"+ //�������� �ٹ� ʮ����
			")"
			;
	
	/**�û��Զ����*/
	public String userdefine_guo_detail_table = "userdefine_guo_detail";
	public String CREATE_TABLE_USERDEFINE_GUO_DETAIL = "create table "+userdefine_guo_detail_table+"("+
			"id integer primary key autoincrement,"+
			"name text,"+  //����
			"count integer"+ //�������� �ٹ� ʮ����
			")"
			;
	
	public String guo_detail_table = "guo_detail";
	public String CREATE_TABLE_GUO_DETAIL = "create table "+guo_detail_table+"("+
			"id integer,"+
			"name text,"+  //����
			"count integer"+ //�������� �ٹ� ʮ����
			")"
			;
	
	String user_gong_guo_common = "("+
			"id integer primary key autoincrement,"+
			"parent_id integer,"+
			"parent_name text,"+  //������
			"name text,"+  //����
			"count integer,"+ //�������� �ٹ� ʮ����
			"time  integer,"+
			"times integer,"+ //����  ��Version =2 �� VERSION = 3 ���� �ֶ�
			"comment text"+ //�Ա��μ�¼������˵��   ��Version =2 �� VERSION = 3 ���� �ֶ�
			")"
			;
	
	public String user_gong_table = "user_gong";
	public String CREATE_TABLE_USER_GONG = "create table "+user_gong_table+user_gong_guo_common;
			
	
	public String user_guo_table = "user_guo";
	public String CREATE_TABLE_USER_GUO = "create table "+user_guo_table+user_gong_guo_common;
	
	public void onCreate(SQLiteDatabase db) {
//		String sql="create table test(" +
//		"cid INTEGER PRIMARY KEY," +
//		"c1 NVARCHAR(20)," +
//		"c2 NVARCHAR(20),"+
//		"c3 NVARCHAR(20)," +
//		"c4 NVARCHAR(20))" +
//		";";
		initGONGGUOBaseTable(db,true);
		initGONGGUOBaseTable(db,false);
		initGONGGUOTable(db,true);
		initGONGGUOTable(db,false);
		createUserDefineTables(db);
		createUserTables(db);
		
		//Ĭ�ϵ���ȷ�϶Ի���
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		Editor editor = sp.edit();
		editor.putBoolean(Settings.gongguoconfirm_dialog, true);
		editor.commit();
	}
	
	public void createUserTables(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_USER_GONG);
		db.execSQL(CREATE_TABLE_USER_GUO);
	}
	
	/**�����û��Զ����*/
	void createUserDefineTables(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_USERDEFINE_GONG_DETAIL);
		db.execSQL(CREATE_TABLE_USERDEFINE_GUO_DETAIL);
	}
	
	void initGONGGUOBaseTable(SQLiteDatabase db, boolean bGong){
		String createTable;
		String tableName;
		int strID,valueID;
		if(bGong){
			createTable = CREATE_TABLE_GONG_BASE;
			strID = R.array.GONG_STR;
			valueID = R.array.GONG_VALUES;
			tableName = gong_base_table;
		}
		else{
			createTable = CREATE_TABLE_GUO_BASE;
			strID = R.array.GUO_STR;
			valueID = R.array.GUO_VALUES;
			tableName = guo_base_table;
		}
		
		try{
			db.execSQL(createTable);
			if(mContext != null){
				String baseStr[] = mContext.getResources().getStringArray(strID);
				String baseValue[] = mContext.getResources().getStringArray(valueID);
				
				if(baseStr != null && baseValue != null){
					int len = baseStr.length;
					if(len > baseValue.length)
						len = baseValue.length;
					
					for(int i=0; i<len; i++){
						insertGONGGUOBaseTable(db,tableName,i, baseStr[i],baseValue[i]);
					}
				}
				
			}
			else{
				Log.e("ERR","ERR mContext == null");
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void insertGONGGUOBaseTable(SQLiteDatabase db,String table_name,int id, String value, String count){
//		int intCount = Integer.parseInt(count);
//		db.insert(table, nullColumnHack, values)
		String str = "insert into "+ table_name +" values('"+id+"','"+getReplacedString(value)+"','"+count+"')";
		try{
			db.execSQL(str);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	void initGONGGUOTable(SQLiteDatabase db,boolean bGong){
		
		String createTable;
		String tableName;
		int strID,valueID;
		if(bGong){
			createTable = CREATE_TABLE_GONG_DETAIL;
			strID = R.array.GONG_DETAIL;
			valueID = R.array.GONG_VALUES;
			tableName = gong_detail_table;
		}
		else{
			createTable = CREATE_TABLE_GUO_DETAIL;
			strID = R.array.GUO_DETAIL;
			valueID = R.array.GUO_VALUES;
			tableName = guo_detail_table;
		}
		
		try{
			db.execSQL(createTable);
			if(mContext != null){
				String detailStr[] = mContext.getResources().getStringArray(strID);
				String detailValue[] = mContext.getResources().getStringArray(valueID);
				
				if(detailStr != null && detailValue != null){
					int len = detailStr.length;
					if(len > detailValue.length)
						len = detailValue.length;
					
					for(int i=0; i<len; i++){
						String detailArray[]=detailStr[i].split("\\,");
						for(int j=0; j<detailArray.length; j++){
							insertGONGGUOTable(db,tableName,j,detailArray[j],detailValue[i]);
						}
					}
				}
				
			}
			else{
				Log.e("ERR","ERR mContext == null");
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**����һ���û��Զ��幦��*/
	public int insertUserDefineGONGTable(SQLiteDatabase db,String value, int count){
		return insertUserDefineGONGGUOTable(db,userdefine_gong_detail_table,value,count);
	}
	
	/**����һ���û��Զ������*/
	public int insertUserDefineGUOTable(SQLiteDatabase db,String value, int count){
		return insertUserDefineGONGGUOTable(db,userdefine_guo_detail_table,value,count);
	}
	
	public int insertUserDefineGONGGUOTable(SQLiteDatabase db,boolean bGong, String value, int count){
		if(bGong){
			return insertUserDefineGONGTable(db,value,count);
		}
		else{
			return insertUserDefineGUOTable(db,value,count);
		}
	}
	
	public String getReplacedString(String value){
		value = value.replace("'", "\"");
		return value;
	}
	
	/**����һ���û��Զ��幦����*/
	public int insertUserDefineGONGGUOTable(SQLiteDatabase db,String tableName, String value, int count){
		ContentValues content = new ContentValues();
		content.put("name", value);
		content.put("count", count);
		return (int) db.insert(tableName, null, content);
//		String str = "insert into "+ tableName +" values(null,'"+getReplacedString(value)+"','"+count+"')";
//		try{
//			db.execSQL(str);
//		}catch(SQLException e){
//			e.printStackTrace();
//		}
	}
	
	public void insertGONGGUOTable(SQLiteDatabase db,String tableName,int id, String value, String count){
//		int intCount = Integer.parseInt(count);
//		db.insert(table, nullColumnHack, values)
		String str = "insert into "+ tableName +" values('"+id+"','"+getReplacedString(value)+"','"+count+"')";
		try{
			db.execSQL(str);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**���û��������в�������*/
	public void insertUserGONGGUOTable(SQLiteDatabase db,String table_name,int parent_id,String parent_name, String name, int count, int time, int times,String comment){
//		int intCount = Integer.parseInt(count);
//		db.insert(table, nullColumnHack, values)
		String str = "insert into "+ table_name +" values(null,'"+parent_id+"','"+parent_name+"','"+name+"','"+count+"','"+time+"','"+times+"','"+getReplacedString(comment)+"')";
		try{
			db.execSQL(str);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
//	/**���û������в�������*/
//	public void insertUserGONGTable(SQLiteDatabase db,int parent_id,String parent_name, String name, int count, int time){
//		insertUserGONGGUOTable(db,user_gong_table,parent_id,parent_name,name,count,time,1);
//	}
//	
//	/**���û������в�������*/
//	public void insertUserGUOTable(SQLiteDatabase db,int parent_id, String parent_name, String name, int count, int time){
//		insertUserGONGGUOTable(db,user_guo_table,parent_id,parent_name,name,count,time,1);
//	}
	
	/**���û������в�������*/
	public void insertUserGONGTable(SQLiteDatabase db,int parent_id,String parent_name, String name, int count, int time, int times,String comment){
		insertUserGONGGUOTable(db,user_gong_table,parent_id,parent_name,name,count,time,times,comment);
	}
	
	/**���û������в�������*/
	public void insertUserGUOTable(SQLiteDatabase db,int parent_id, String parent_name, String name, int count, int time, int times,String comment){
		insertUserGONGGUOTable(db,user_guo_table,parent_id,parent_name,name,count,time,times,comment);
	}
	
	/**�����Զ��幦��֮ǰ���Ƚ��Ƿ�����ͬ���ƣ���ͬcount�Ĺ����   ��Ԥ���幦�����Զ��幦���в�ѯ*/
	public boolean haveSameGongGuoItem(SQLiteDatabase db, String name, int count) {
		boolean rc = haveSameGongGuoItem(db, name, count, true);
		rc &= haveSameGongGuoItem(db, name, count, false);
		return rc;
	}
	
	/**�����Զ��幦��֮ǰ���Ƚ��Ƿ�����ͬ���ƣ���ͬcount�Ĺ����   ��Ԥ���幦�������Զ��幦���в�ѯ*/
	public boolean haveSameGongGuoItem(SQLiteDatabase db, String name, int count, boolean bUserDefineTable){
		
		int total = 0;
		String tableName;
		if(count > 0)
		{
			if(bUserDefineTable)
				tableName = userdefine_gong_detail_table;
			else
				tableName = gong_detail_table;
		}
		else
		{
			if(bUserDefineTable)
				tableName = userdefine_guo_detail_table;
			else
				tableName = guo_detail_table;
		}
		
		String sql = "select * from "+tableName +" where name = '"+getReplacedString(name)+"' and count = '"+count+"'";
		Cursor cursor = db.rawQuery( sql, null);
		
		if(cursor!=null){
			if(cursor.moveToFirst()) 
				total = cursor.getCount();
        	cursor.close();
        }
		
		if(total > 0){
			return true;
		}
		else 
			return false;

	}
	
	/**�����Զ��幦��������Ҫ�ı���û��Ѽ�¼�Ĺ���*/
	public int updateGongGuo(SQLiteDatabase db, String oldValue, GongGuoDetail detail){
		String tableName;
		if(detail.count>0)
			tableName = user_gong_table;
		else
			tableName = user_guo_table;
		
		ContentValues cv = new ContentValues();
        cv.put("name", detail.name);
		
		return db.update(tableName, cv, "name=?",new String[]{oldValue});
	}
	
	/**�����Զ��幦������*/
	public int updateGongGuoDetail(SQLiteDatabase db, String oldValue, GongGuoDetail detail){
		String tableName;
		int count = 0;
		if(detail.bUserdefine){
			if(detail.count>0)
				tableName = userdefine_gong_detail_table;
			else
				tableName = userdefine_guo_detail_table;
		}
		else{
			if(detail.count>0)
				tableName = gong_detail_table;
			else
				tableName = guo_detail_table;
		}
		
		
		ContentValues cv = new ContentValues();
        cv.put("name", detail.name);
        
        count = db.update(tableName, cv, "id=? and name=?",new String[]{detail.id+"",oldValue});
        count += updateGongGuo(db,oldValue,detail);
		
		return count;
	}
	
	/**��ȡ���б�*/
	public ArrayList<GongGuoBase> getGongBase(SQLiteDatabase db){
		return getGongGuoBase(db,gong_base_table, userdefine_gong_detail_table, gong_detail_table);
	}
	
	/**��ȡ���б�*/
	public ArrayList<GongGuoBase> getGuoBase(SQLiteDatabase db){
		return getGongGuoBase(db,guo_base_table, userdefine_guo_detail_table, guo_detail_table);
	}
		
	//�����б� �������ܻ��� �� �ٹ���ʮ�� Ȼ���������������
	public ArrayList<GongGuoBase> getGongGuoBase(SQLiteDatabase db, String baseTableName,String userDefineDetailTableName, String detailTableName){
		ArrayList<GongGuoBase> baseList = new ArrayList<GongGuoBase>();
    	
		String sql="select * from "+baseTableName+" order by id desc";
    	Cursor cursor=null;
    	try{
    		cursor=db.rawQuery(sql, null);
    		if(cursor != null){
//    			int count = cursor.getCount();
//    			COM.LOGE("", "GongGuoBase count: "+count);
//    			if(count > 0){
    			while(cursor.moveToNext()){
    				
    				GongGuoBase base;
//    				for(int i=0; i<count; i++){
					base = new GongGuoBase();
//    					COM.LOGE("", "GongGuoBase i: "+i);
					base.id = cursor.getInt(0);
					base.name = cursor.getString(1);
					base.count = cursor.getInt(2);
					
					base.mList = getGongGuoDetail(db,userDefineDetailTableName,base.count,true);
					base.addList(getGongGuoDetail(db,detailTableName,base.count,false));
					base.initUserCount();
					baseList.add(base);
    			}
    			
    			cursor.close();
    		}
    		
    	}catch(SQLException e){
    		e.printStackTrace();
    		cursor=null;
    		return null;
    	}
		
		return baseList;
	}
	
	public ArrayList<GongGuoDetail> getGongGuoDetail(SQLiteDatabase db, String detailTableName, int count, boolean bUserDefine){
		ArrayList<GongGuoDetail> detailList = new ArrayList<GongGuoDetail>();
		
		String sql="select * from "+detailTableName+" where count="+count;
		
		String userGongGuoTableName;
		if(count > 0)
			userGongGuoTableName = user_gong_table;
		else
			userGongGuoTableName = user_guo_table;
		
    	Cursor cursor=null;
    	try{
    		cursor=db.rawQuery(sql, null);
    		if(cursor != null){
    			while(cursor.moveToNext()){
    				GongGuoDetail detail = GongGuoDetail.getFromCursor(cursor);
    				
    				detail.bUserdefine = bUserDefine;
    				detail.userCount = getUserGongGuoCountByName(db,userGongGuoTableName,detail.name,count);
    				detailList.add(detail);
    			}
    			cursor.close();
    		}
			}catch(SQLException e){
	    		e.printStackTrace();
	    		cursor=null;
	    		return null;
	    	}
    			
		return detailList;
	}
	
//	public int getUserDefineGongGuoId(SQLiteDatabase db,String value, int count){
//		int id = -1;
//		String tableName = "";
//		if(count > 0)
//			tableName = userdefine_gong_detail_table;
//		else
//			tableName = userdefine_guo_detail_table;
//		
//		String sql = "select id from "+tableName+" where name = '"+getReplacedString(value)+"'";
//		Cursor cursor = db.rawQuery( sql, null);
//		
////        if(cursor!=null && cursor.moveToFirst()) {
////        	id = cursor.getInt(0);
////        	cursor.close();
////        }
//		if(cursor!=null) {
//			if( cursor.moveToFirst()){
//				id = cursor.getInt(0);
//			}
//        	cursor.close();
//        }
//		return id;
//	}
	
	public int getUserGongGuoCountByName(SQLiteDatabase db, String tableName, String name, int count){
		int total = 0;
		String sql = "select SUM(times) from "+tableName +" where name = '"+name+"' and count = '"+count+"'";
		Cursor cursor = db.rawQuery( sql, null);
		
//        if(cursor!=null && cursor.moveToFirst()) {
//        	total = cursor.getCount();
//        	cursor.close();
//        }
		if(cursor!=null){
			if(cursor.moveToFirst()) {
				total = cursor.getInt(0);//cursor.getCount();
//				if(total < 0)
//					total = -total;
			}
        	cursor.close();
        }

		return total;
	}
	
	public int getUserGongGuoCount(SQLiteDatabase db, String tableName){
		int count = 0;
		String sql = "select SUM(count*times) from "+tableName;
		Cursor cursor = db.rawQuery( sql, null);
		
//        if(cursor!=null && cursor.moveToFirst()) {
//        	count = cursor.getInt(0);
//        	cursor.close();
//        }
		if(cursor!=null) {
			if(cursor.moveToFirst())
				count = cursor.getInt(0);
        	cursor.close();
        }

		return count;
	}
	
	public int getUserGongCount(SQLiteDatabase db){
		int gongCount = getUserGongGuoCount(db,user_gong_table);
		if(gongCount < 0)
			gongCount = 0;
		return gongCount;
	}
	
	public int getUserGuoCount(SQLiteDatabase db){
		int guoCount =  getUserGongGuoCount(db,user_guo_table);
//		if(guoCount > 0)
//			guoCount = 0;
		return guoCount;
	}
	
	/**
	 * ��ȡ�û��������������б�,��ʱ�䷶Χ��ȡ ������ʱ����������
	 * ChenGang
	 * 2014-1-18
	 * @param db
	 * @param startTimeS ��ʼʱ��(���ڵ�����)
	 * @param endTimeS   ��ֹʱ��(ҪС����)
	 * @return ArrayList<UserGongGuo>
	 */
    public ArrayList<UserGongGuo> getUserGongGuoListByRange(SQLiteDatabase db,TimeRange range ){
        String where = " where time>="+range.mStartTimeS+" and time<"+range.mEndTimeS;
        String sql = " select * from " + user_gong_table + where
                   + " union all"
                   + " select * from "+user_guo_table + where
                   + " order by time desc";
        return getUserGongGuoList(db,sql);
    }
	
	/**��ȡ�û��������������б�,��ʱ����������*/
	public ArrayList<UserGongGuo> getUserGongGuoListAll(SQLiteDatabase db){
	    String sql="select * from "+user_gong_table+" union all select * from "+user_guo_table+" order by time desc";
	    return getUserGongGuoList(db,sql);
	}
	
	/**��ȡ�û����������б�*/
	public ArrayList<UserGongGuo> getUserGongGuoList(SQLiteDatabase db,String sql){
		ArrayList<UserGongGuo> detailList = new ArrayList<UserGongGuo>();
    	Cursor cursor=null;
    	try{
    		cursor=db.rawQuery(sql, null);
    		if(cursor != null){
    			while(cursor.moveToNext()){
    				UserGongGuo detail = UserGongGuo.getFromCursor(cursor);
    				detailList.add(detail);
    			}
    			cursor.close();
    		}
			}catch(SQLException e){
	    		e.printStackTrace();
	    		cursor=null;
	    		return null;
	    	}
    			
		return detailList;
	}
	
	/**�����û��Զ��幦��������*/
	public boolean exportUserDefineGongGuoDetailTable(String fileName){
		boolean rc = true;
		try {
			File file = new File(fileName);
			if(file.exists())
				file.delete();
			FileOutputStream os = new FileOutputStream(fileName);
			SQLiteDatabase  db = getReadableDatabase();
			rc &= exportUserDefineGongGuoDetailTable(db,userdefine_gong_detail_table,os);
			rc &= exportUserDefineGongGuoDetailTable(db,userdefine_guo_detail_table,os);
			db.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rc = false;
		} catch (IOException e) {
			e.printStackTrace();
			rc = false;
		}
		
		
		return rc;
	}
	/**�����û��Զ��幦��������
	 * @throws IOException */
	boolean exportUserDefineGongGuoDetailTable(SQLiteDatabase  db,String detailTableName,FileOutputStream os) throws IOException{
		String sql="select * from "+detailTableName;
    	Cursor cursor=null;
    	try{
    		cursor=db.rawQuery(sql, null);
    		if(cursor != null){
    			String strLine = "";
    			while(cursor.moveToNext()){
    				strLine = cursor.getString(2);
    				strLine = strLine + "," + cursor.getString(1)+"\n";
    				os.write(strLine.getBytes());
    			}
    			cursor.close();
    		}
			}catch(SQLException e){
	    		e.printStackTrace();
	    		cursor=null;
	    		return false;
	    	}
    			
		return true;
	}
	
	/**�����û��Զ��幦����*/
	public boolean importUserDefineGongGuoDetail(SQLiteDatabase  db,String fileName) throws IOException{
		if(fileName == null || db == null)
			return false;
		InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName),"GBK");
		String lineStr="";
		int count = 0;			
		String strCount;
		
		int index = 0;
		BufferedReader br = new BufferedReader(isr);
		//���������ļ�ÿһ��
		while((lineStr=br.readLine())!=null){
			index = lineStr.indexOf(",");
			if(index != -1){
				strCount = lineStr.substring(0, index);
				lineStr = lineStr.substring(index+1);
				count = COM.parseInt(strCount);
				
				if(!haveSameGongGuoItem(db, getReplacedString(lineStr), count)){
					if(count < 0){//��
						insertUserDefineGUOTable(db, lineStr, count);
					}
					else{//��
						insertUserDefineGONGTable(db, lineStr, count);
					}
				}
				
			}
		}
		br.close();
		isr.close();
		return true;
	}
	
	/**��ȡ�û����������б�*/
	public ArrayList<UserGongGuo> getUserGongGuoListByTableName(SQLiteDatabase db, String detailTableName){
		ArrayList<UserGongGuo> detailList = new ArrayList<UserGongGuo>();
		
		String sql="select * from "+detailTableName+" order by time desc";
    	Cursor cursor=null;
    	try{
    		cursor=db.rawQuery(sql, null);
    		if(cursor != null){
    			while(cursor.moveToNext()){
    				UserGongGuo detail = UserGongGuo.getFromCursor(cursor);
    				detailList.add(detail);
    			}
    			cursor.close();
    		}
			}catch(SQLException e){
	    		e.printStackTrace();
	    		cursor=null;
	    		return null;
	    	}
    			
		return detailList;
	}
	
	public ArrayList<UserGongGuo> getUserGongList(SQLiteDatabase db){
		return getUserGongGuoListByTableName(db,user_gong_table);
	}
	
	public ArrayList<UserGongGuo> getUserGuoList(SQLiteDatabase db){
		return getUserGongGuoListByTableName(db,user_guo_table);
	}
	
	/**ɾ�����ݿ���е�����*/
	public boolean deleteItemById(SQLiteDatabase db, String tableName, int id){
		boolean rc = true;
		String sql="delete from "+tableName+" where id="+id+"";
    	try{
    		db.execSQL(sql);
    	}catch(SQLException e){
    		e.printStackTrace();
    		rc = false;
    	}
		return rc;
	}
	
	
	/**�û��Զ������ɾ��*/
	public boolean deleteUserDefineGongItemById(SQLiteDatabase db, int id){
		boolean rc = true;
		rc = deleteItemById(db, userdefine_gong_detail_table, id);
		return rc;
	}
	
	/**�û��Զ������ɾ��*/
	public boolean deleteUserDefineGuoItemById(SQLiteDatabase db, int id){
		boolean rc = true;
		rc = deleteItemById(db, userdefine_guo_detail_table, id);
		return rc;
	}
	
	/**Ԥ�����˷���ѵ����ɾ��*/
	public boolean deleteGongDetailById(SQLiteDatabase db, int id){
		boolean rc = true;
		rc = deleteItemById(db, gong_detail_table, id);
		return rc;
	}
	
	/**Ԥ�����˷���ѵ����ɾ��*/
	public boolean deleteGuoDetailById(SQLiteDatabase db, int id){
		boolean rc = true;
		rc = deleteItemById(db, guo_detail_table, id);
		return rc;
	}
	
	/**�û�������¼*/
	public boolean deleteUserGongItemById(SQLiteDatabase db, int id){
		boolean rc = true;
		rc = deleteItemById(db, user_gong_table, id);
		return rc;
	}
	/**�û�������¼*/
	public boolean deleteUserGuoItemById(SQLiteDatabase db, int id){
		boolean rc = true;
		rc = deleteItemById(db, user_guo_table, id);
		return rc;
	}
	
	void addColumn(SQLiteDatabase db,String tableName,String columnName,String type){
		db.execSQL("ALTER TABLE "+tableName+" ADD new TEXT");
	}
	
	/**�������ݿ�汾����ʱ��Ҫ���Ĺ���*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		COM.LOGE("onUpgrade", "oldVersion: "+oldVersion+" newVersion: "+newVersion);
		if(oldVersion == 1)
			createUserDefineTables(db);
		else if(oldVersion == 2){
			//������������һ�������������ֶ�
			db.execSQL("ALTER TABLE "+user_gong_table+" ADD times integer");
			db.execSQL("ALTER TABLE "+user_guo_table+" ADD times integer");
			db.execSQL("ALTER TABLE "+user_gong_table+" ADD comment TEXT");
			db.execSQL("ALTER TABLE "+user_guo_table+" ADD comment TEXT");
		}
	}
}
