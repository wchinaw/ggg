package com.cheng.ggg.types;

import java.util.ArrayList;

import com.cheng.ggg.utils.COM;

/**功过基本类型，包含功过Detail.*/
public class GongGuoBase {
	final String TAG = "GongGuoBase";
	
	public int id;
	public String name;
	public int count;
	public ArrayList<GongGuoDetail> mList;
	
	//用户功过累计
	public int userCount;
	
	public void addList(ArrayList<GongGuoDetail> list){
		if(mList == null)
			mList = new ArrayList<GongGuoDetail>();
		
		if(list != null){
			int count = list.size();
			for(int i=0; i<count; i++){
				mList.add(list.get(i));
			}
		}
	}
	
	public void initUserCount(){
		if(mList != null){
			int len = mList.size();
			userCount = 0;
			for(int i=0; i<len; i++){
				userCount += mList.get(i).userCount;
			}
		}
	}
	
	public void dump(){
		COM.LOGE(TAG, "id: "+id);
		COM.LOGE(TAG, "name: "+name);
		COM.LOGE(TAG, "count: "+count);
		
		if(mList != null){
			int len = mList.size();
			COM.LOGE(TAG, "mList len: "+len);
			for(int i=0; i<len; i++){
				mList.get(i).dump();
			}
		}
		else
			COM.LOGE(TAG, "mList: "+mList);
	}
}
