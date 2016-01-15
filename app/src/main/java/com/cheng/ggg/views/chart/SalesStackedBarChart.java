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
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.cheng.ggg.types.ChartData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

/**
 * Sales demo bar chart.
 */
public class SalesStackedBarChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Sales stacked bar chart";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The monthly sales for the last 2 years (stacked bar chart)";
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
    String[] titles = new String[] { "功", "过" };
//    List<double[]> values = new ArrayList<double[]>();
    values = new ArrayList<double[]>();
    values.add(new double[] { 10,20,30,20,100,60,10 });
    values.add(new double[] { 30,20,10,80,50, 30,65});
    int[] colors = new int[] { Color.RED, Color.GREEN };
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
    
    int length = values.get(0).length;
    double yMin = 0,yMax = 0;
    
    yMin = values.get(0)[0];
    yMax = values.get(0)[0];
    for(int i=0; i < length; i++){
    	
    	
    	if(yMin > values.get(1)[i]){
    		yMin  = values.get(1)[i];
    	}
    	
    	if(yMax < values.get(1)[i]){
    		yMax  = values.get(1)[i];
    	}
    }
    
    Log.e("","yMin:"+yMin+" yMax:"+yMax);
    
    yMin = (yMin>0) ? yMin*0.9 : yMin*1.1;
    yMax = (yMax>0) ? yMax*1.1 : yMax*0.9;
    
    setChartSettings(renderer, "功过统计", "月", "功过数量", 1,
    	      12, yMin, yMax, Color.GRAY, Color.LTGRAY);
    
    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
    renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setXLabelsAlign(Align.LEFT);
    renderer.setYLabelsAlign(Align.LEFT);
    renderer.setPanEnabled(true, false);
    // renderer.setZoomEnabled(false);
    renderer.setZoomRate(1.1f);
    renderer.setBarSpacing(0.5f);
    return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
        Type.STACKED);
  }

}
