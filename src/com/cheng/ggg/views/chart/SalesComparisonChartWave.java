/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cheng.ggg.views.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import com.cheng.ggg.MainActivity;
import com.cheng.ggg.UserGongGuoListActivity;
import com.cheng.ggg.types.ChartData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

/**
 * 功过曲线，包括功过合计
 */
public class SalesComparisonChartWave extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Sales comparison";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "Monthly sales advance for 2 years (interpolated line and area charts)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context, ChartData chartData ) {
	  List<double[]> values = chartData.values;
	  String xName = chartData.xName;
	  List<String> xlabels = chartData.xlabels;
	  String[] titles = new String[] { "功", "过",
      "合计" };
//  List<double[]> values = new ArrayList<double[]>();
//	  values = new ArrayList<double[]>();
//  values.add(new double[] { 10,20,30,20,100,60,10 });
//  values.add(new double[] { -30,-20,-10,-80,-50, -30,-65});
//  values.add(new double[] { 30,20,10,80,50, 30, 65});
  

  
  int length = values.get(0).length;
  double[] diff = new double[length];
  for (int i = 0; i < length; i++) {
    diff[i] = values.get(0)[i] + values.get(1)[i];
  }
  values.add(diff);
  
  double yMin = 0,yMax = 0;
  
  yMin = values.get(0)[0];
  yMax = values.get(0)[0];
  for(int i=0; i < length; i++){
  	
  	
  	if(yMin > values.get(0)[i]){
  		yMin  = values.get(0)[i];
  	}
  	if(yMin > values.get(1)[i]){
  		yMin  = values.get(1)[i];
  	}
  	
  	if(yMax < values.get(0)[i]){
  		yMax  = values.get(0)[i];
  	}
  	if(yMax < values.get(1)[i]){
  		yMax  = values.get(1)[i];
  	}
  }
  
  Log.e("","yMin:"+yMin+" yMax:"+yMax);
  
  yMin = (yMin<0) ? yMin*1.1 : yMin*0.9;
  yMax = (yMax>0) ? yMax*1.1 : yMax*0.9;
  
  int[] colors = new int[] { UserGongGuoListActivity.getGongColor(),
		  UserGongGuoListActivity.getGuoColor(), Color.RED | Color.GREEN };
  
  PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.CIRCLE, PointStyle.CIRCLE };
  XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
  setChartSettings(renderer, "功过统计", xName, "功过数量", 0.9,
		  length+0.1, yMin, yMax, Color.GRAY, Color.LTGRAY);
    
  renderer.setXLabels(0);
  int len = xlabels.size();
  for(int i=1; i<=len; i++){
  	renderer.addXTextLabel(i, xlabels.get(i-1));
  }
  
  
  renderer.setYLabels(10);
  renderer.setDisplayValues(true);
  renderer.setMargins(new int[]{MainActivity.TEXT_SIZE+25,MainActivity.TEXT_SIZE+20,MainActivity.TEXT_SIZE+20,20});
  renderer.setChartTitleTextSize(MainActivity.TEXT_SIZE+10);
  renderer.setTextTypeface("sans_serif", Typeface.BOLD);
  renderer.setLabelsTextSize(MainActivity.TEXT_SIZE+10);
  renderer.setAxisTitleTextSize(MainActivity.TEXT_SIZE+10);
  renderer.setLegendTextSize(MainActivity.TEXT_SIZE+10);
  renderer.setShowGrid(true);
  renderer.setZoomButtonsVisible(true);
  
  length = renderer.getSeriesRendererCount();

  for (int i = 0; i < length; i++) {
    XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
    if (i == length - 1) {
      FillOutsideLine fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
      fill.setColor(Color.GREEN | Color.RED);
      seriesRenderer.addFillOutsideLine(fill);
    }
    seriesRenderer.setFillPoints(true);
    seriesRenderer.setLineWidth(3);//(2.5f);
    seriesRenderer.setDisplayChartValues(true);
    seriesRenderer.setChartValuesTextSize(MainActivity.TEXT_SIZE+10);
  }
  //曲线
  return ChartFactory.getCubicLineChartIntent(context, buildBarDataset(titles, values), renderer,
      0.5f);
  //直线
//  return ChartFactory.getCubicLineChartIntent(context, buildBarDataset(titles, values), renderer,
//	      0f);
}

}
