package com.cheng.ggg.views.chart;

import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.cheng.ggg.MainActivity;
import com.cheng.ggg.UserGongGuoListActivity;
import com.cheng.ggg.types.ChartData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

public class BarChart {
	private static final int SERIES_NR = 2;
	private List<double[]> mValues;
	private double[] gong;
	private double[] guo;
	private String mxName;
	private List<String> xlabels;
//	double[] gong = (new double[] { 10,20,30,20,100,60,10 });
//    double[] guo = (new double[] { 30,20,10,80,50, 30, 65});
	private XYMultipleSeriesDataset getBarDemoDataset() {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    
	      CategorySeries series1 = new CategorySeries("功");
	      CategorySeries series2 = new CategorySeries("过");
	      for (int k = 0; k < gong.length; k++) {
	    	  series1.add(gong[k]);
	    	  series2.add(guo[k]);
	      }
	      dataset.addSeries(series1.toXYSeries());
	      dataset.addSeries(series2.toXYSeries());
	    return dataset;
	  }
	
	public XYMultipleSeriesRenderer getBarDemoRenderer() {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    
	    renderer.setMargins(new int[]{MainActivity.TEXT_SIZE+25,MainActivity.TEXT_SIZE+20,MainActivity.TEXT_SIZE+20,20});
	    SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	    r.setColor(UserGongGuoListActivity.getGongColor());
	    r.setDisplayChartValues(true);
	    renderer.addSeriesRenderer(r);
	    r = new SimpleSeriesRenderer();
	    r.setDisplayChartValues(true);
	    r.setColor(UserGongGuoListActivity.getGuoColor());
	    renderer.addSeriesRenderer(r);
	    
	    double yMin = 0,yMax = 0;
	    
	    yMin = gong[0];
	    yMax = guo[0];
	    for(int i=0; i < gong.length; i++){
	    	
	    	
	    	if(yMin > gong[i]){
	    		yMin  = gong[i];
	    	}
	    	if(yMin > guo[i]){
	    		yMin  = guo[i];
	    	}
	    	
	    	if(yMax < gong[i]){
	    		yMax  = gong[i];
	    	}
	    	if(yMax < guo[i]){
	    		yMax  = guo[i];
	    	}
	    }
	    
	    Log.e("","yMin:"+yMin+" yMax:"+yMax);
	    
	    yMin = (yMin<0) ? yMin*1.2 : yMin*0.9;
	    yMax = (yMax>0) ? yMax*1.1 : yMax*0.9;
	    
	    renderer.setChartTitle("功过统计");
	    renderer.setXTitle(mxName);
	    renderer.setYTitle("功过数");
	    
	    renderer.setBarSpacing(0.2);
	    renderer.setXAxisMin(0.6);
	    renderer.setXAxisMax(gong.length+0.5);
	    renderer.setYAxisMin(yMin);
	    renderer.setYAxisMax(yMax);
	    
	    renderer.setXLabels(0);
	    int len = xlabels.size();
	    for(int i=1; i<=len; i++){
	    	renderer.addXTextLabel(i, xlabels.get(i-1));
	    }
	    
	    
	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
	    renderer.getSeriesRendererAt(0).setChartValuesTextSize(MainActivity.TEXT_SIZE+10);
	    renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
	    renderer.getSeriesRendererAt(1).setChartValuesTextSize(MainActivity.TEXT_SIZE+10);
	    renderer.setAxisTitleTextSize(MainActivity.TEXT_SIZE+10);
	    renderer.setChartTitleTextSize(MainActivity.TEXT_SIZE+10);
	    renderer.setLabelsTextSize(MainActivity.TEXT_SIZE+10);
	    renderer.setLegendTextSize(MainActivity.TEXT_SIZE+10);
	    renderer.setZoomButtonsVisible(true);
	    return renderer;
	  }
	
	private void setChartSettings(XYMultipleSeriesRenderer renderer) {
	    
	  }
	
	public Intent execute(Context context, ChartData chartData){
		mValues = chartData.values;
		gong = mValues.get(0);
		guo = mValues.get(1);
		mxName = chartData.xName;
		xlabels = chartData.xlabels;
		
		XYMultipleSeriesRenderer renderer = getBarDemoRenderer();
	      setChartSettings(renderer);
	      Intent intent = ChartFactory.getBarChartIntent(context, getBarDemoDataset(), renderer, Type.DEFAULT);
	      return intent;
	}
}
