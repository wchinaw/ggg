package com.cheng.ggg.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cheng.ggg.R;
import com.cheng.ggg.types.GongGuoBase;
import com.cheng.ggg.types.GongGuoDetail;
import com.cheng.ggg.types.UserGongGuo;
import com.cheng.ggg.utils.COM;

public class SQLiteHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	public boolean isAdd=true;
	
	public Context mContext = null;
	
	public static SQLiteHelper mSQLitehelper;
	
	public static SQLiteHelper getInstance(Context context){
		if(mSQLitehelper == null){
			mSQLitehelper = new SQLiteHelper(context, COM.DBNAME);
		}
		return mSQLitehelper;
	}
	
	
	// 在SQLiteOpenHelper的子类当中，必须有该构造函数
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
			"name text,"+  //名称
			"count integer"+ //功的数量 百功 十功等
			")"
			;
	public String CREATE_TABLE_GUO_BASE = "create table "+guo_base_table+"("+
			"id integer,"+
			"name text,"+  //名称
			"count integer"+ //功的数量 百功 十功等
			")"
			;
	
	public String gong_detail_table = "gong_detail";
	public String CREATE_TABLE_GONG_DETAIL = "create table "+gong_detail_table+"("+
			"id integer,"+
			"name text,"+  //名称
			"count integer"+ //功的数量 百功 十功等
			")"
			;
	
	public String guo_detail_table = "guo_detail";
	public String CREATE_TABLE_GUO_DETAIL = "create table "+guo_detail_table+"("+
			"id integer,"+
			"name text,"+  //名称
			"count integer"+ //功的数量 百过 十过等
			")"
			;
	
	public String user_gong_table = "user_gong";
	public String CREATE_TABLE_USER_GONG = "create table "+user_gong_table+"("+
			"id integer primary key autoincrement,"+
			"parent_id integer,"+
			"parent_name text,"+  //父名称
			"name text,"+  //名称
			"count integer,"+ //功的数量 百功 十功等
			"time  integer"+
			")"
			;
	
	public String user_guo_table = "user_guo";
	public String CREATE_TABLE_USER_GUO = "create table "+user_guo_table+"("+
			"id integer primary key autoincrement,"+
			"parent_id integer,"+
			"parent_name text,"+  //父名称
			"name text,"+  //名称
			"count integer,"+ //功的数量 百功 十功等
			"time  integer"+
			")"
			;
	
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
		
		createUserTables(db);
	}
	
	public void createUserTables(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_USER_GONG);
		db.execSQL(CREATE_TABLE_USER_GUO);
	}
	
	public void initGONGGUOBaseTable(SQLiteDatabase db, boolean bGong){
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
		String str = "insert into "+ table_name +" values('"+id+"','"+value+"','"+count+"')";
		try{
			db.execSQL(str);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void initGONGGUOTable(SQLiteDatabase db,boolean bGong){
		
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
	
	public void insertGONGGUOTable(SQLiteDatabase db,String tableName,int id, String value, String count){
//		int intCount = Integer.parseInt(count);
//		db.insert(table, nullColumnHack, values)
		String str = "insert into "+ tableName +" values('"+id+"','"+value+"','"+count+"')";
		try{
			db.execSQL(str);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**在用户功过表中插入数据*/
	public void insertUserGONGGUOTable(SQLiteDatabase db,String table_name,int parent_id,String parent_name, String name, int count, int time){
//		int intCount = Integer.parseInt(count);
//		db.insert(table, nullColumnHack, values)
		String str = "insert into "+ table_name +" values(null,'"+parent_id+"','"+parent_name+"','"+name+"','"+count+"','"+time+"')";
		try{
			db.execSQL(str);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**在用户功表中插入数据*/
	public void insertUserGONGTable(SQLiteDatabase db,int parent_id,String parent_name, String name, int count, int time){
		insertUserGONGGUOTable(db,user_gong_table,parent_id,parent_name,name,count,time);
	}
	
	/**在用户过表中插入数据*/
	public void insertUserGUOTable(SQLiteDatabase db,int parent_id, String parent_name, String name, int count, int time){
		insertUserGONGGUOTable(db,user_guo_table,parent_id,parent_name,name,count,time);
	}
	
	public ArrayList<GongGuoBase> getGongBase(SQLiteDatabase db){
		return getGongGuoBase(db,gong_base_table, gong_detail_table);
	}
	
	public ArrayList<GongGuoBase> getGuoBase(SQLiteDatabase db){
		return getGongGuoBase(db,guo_base_table, guo_detail_table);
	}
		

	public ArrayList<GongGuoBase> getGongGuoBase(SQLiteDatabase db, String baseTableName, String detailTableName){
		ArrayList<GongGuoBase> baseList = new ArrayList<GongGuoBase>();
    	
		String sql="select * from "+baseTableName+";";
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
					
					base.mList = getGongGuoDetail(db,detailTableName,base.count);
					
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
	
	public ArrayList<GongGuoDetail> getGongGuoDetail(SQLiteDatabase db, String detailTableName, int count){
		ArrayList<GongGuoDetail> detailList = new ArrayList<GongGuoDetail>();
		
		String sql="select * from "+detailTableName+" where count="+count+";";
    	Cursor cursor=null;
    	try{
    		cursor=db.rawQuery(sql, null);
    		if(cursor != null){
    			while(cursor.moveToNext()){
    				GongGuoDetail detail = GongGuoDetail.getFromCursor(cursor);
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
	
	public int getUserGongGuoCount(SQLiteDatabase db, String tableName){
		String sql = "select SUM(count) from "+tableName;
		Cursor cursor = db.rawQuery( sql, null);
		
        if(cursor.moveToFirst()) {
    	   return cursor.getInt(0);
        }

		return 0;
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
	
	
	/**获取用户功过表中所有列表,按时间逆序排序*/
	public ArrayList<UserGongGuo> getUserGongGuoListAll(SQLiteDatabase db){
		ArrayList<UserGongGuo> detailList = new ArrayList<UserGongGuo>();
		
		String sql="select * from "+user_gong_table+" union all select * from "+user_guo_table+" order by time desc;";
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
	
	/**获取用户功过表中列表*/
	public ArrayList<UserGongGuo> getUserGongGuoListByTableName(SQLiteDatabase db, String detailTableName){
		ArrayList<UserGongGuo> detailList = new ArrayList<UserGongGuo>();
		
		String sql="select * from "+detailTableName+" order by time desc;";
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
	
	/**删除数据库表中的数据*/
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
	
	public boolean deleteUserGongItemById(SQLiteDatabase db, int id){
		boolean rc = true;
		rc = deleteItemById(db, user_gong_table, id);
		return rc;
	}
	
	public boolean deleteUserGuoItemById(SQLiteDatabase db, int id){
		boolean rc = true;
		rc = deleteItemById(db, user_guo_table, id);
		return rc;
	}
	
	/**进行数据库版本升级时需要做的工作*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
//		String col="";
//		String in_col="";
//		int num=0;
//		String numstr="select * from test";
//		try{
//			Log.w("", "sql:"+numstr);
//			Cursor cs=db.rawQuery(numstr, null);
//			cs.moveToNext();
//			num=cs.getColumnCount();
//			Log.w("", "num:"+num);
//			cs.close();
//			}catch(SQLException e){
//				e.printStackTrace();
//			}
//		if(isAdd){
//			num++;
//		}
//		else 
//			num--;
//		if(num<=1||num>8){
//			return;
//		}
//		String sql="ALTER TABLE test RENAME TO _temp_test; ";
//		try{
//			Log.w("", "sql:"+sql);
//			db.execSQL(sql);
//			}catch(SQLException e){
//				e.printStackTrace();
//			}
//		
//		for(int i=1;i<num;i++){
//			if(i<num-1){
//				col+="c"+i+" NVARCHAR(20)";
//				in_col+="c"+i+"";
//				col+=",";
//				if(i<num-2){
//					in_col+=",";
//				}
//			}
//			else {
//				col+="c"+i+" NVARCHAR(20)";
//				if(!isAdd){
//					if(num>2){
//						in_col+=",";
//					}
//					in_col+="c"+i+"";
//				}
//			}
//		}
//		sql="create table test(" +
//		"cid INTEGER PRIMARY KEY," +
//		col +
//		");";
//		try{
//			Log.w("", "sql:"+sql);
//			db.execSQL(sql);
//			}catch(SQLException e){
//				e.printStackTrace();
//			}
//		if(isAdd)
//			sql="insert into test select cid,"+in_col+",'0' from _temp_test;";
//		else 
//			sql="insert into test select cid,"+in_col+" from _temp_test;";
//		try{
//			Log.w("", "sql:"+sql);
//			db.execSQL(sql);
//			}catch(SQLException e){
//				e.printStackTrace();
//			}
//		sql="drop table  _temp_test;";
//		try{
//			Log.w("", "sql:"+sql);
//			db.execSQL(sql);
//			}catch(SQLException e){
//				e.printStackTrace();
//			}
	}
}
