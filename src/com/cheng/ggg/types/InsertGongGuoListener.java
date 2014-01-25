package com.cheng.ggg.types;

/**����һ��������¼�Ļص��� ���ڲ��빦���Ի���*/
public abstract class InsertGongGuoListener {
	public boolean bInsert = true;
	
	/**
	 * ����bInsert����ȷ���ǲ��뻹�Ǹ��¡�
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
