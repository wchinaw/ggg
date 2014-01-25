package com.cheng.ggg.types;

/**插入一个功过记录的回调， 用于插入功过对话框*/
public abstract class InsertGongGuoListener {
	public boolean bInsert = true;
	
	/**
	 * 根据bInsert变量确定是插入还是更新。
	 * @param base
	 * @param detail
	 * @param time
	 * @param times
	 * @param comment
	 * @return
	 */
	public boolean insertAuto(UserGongGuo oldGongGuo, UserGongGuo newGongGuo){
		boolean rc = true;
		
		if(bInsert)
			rc = insert(newGongGuo);
		else
			rc = update(oldGongGuo,newGongGuo);
		
		return rc;
	}
	
	public abstract boolean insert(GongGuoBase base, GongGuoDetail detail, UserGongGuo gongguo);
	public abstract boolean insert(UserGongGuo gongguo);
	public abstract boolean update(UserGongGuo oldGongGuo, UserGongGuo newGongGuo);
}
