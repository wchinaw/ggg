package com.cheng.ggg.types;

import java.util.ArrayList;
import java.util.List;

/**图标数据*/
public class ChartData {
	/**功过值*/
	public List<double[]> values = new ArrayList<double[]>();
	/**横轴标签*/
	public ArrayList<String> xlabels = new ArrayList<String>();
	public String xName;
}
