/*
 *  No Pain No Game - Android App
 *  Copyright 2011 Sean Bromage
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  <http://www.gnu.org/licenses/>.
 */

package edu.ucla.cs.nopainnogame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ProgressActivity extends Activity {
	
	int DATA_POINTS = 7;
	int CHART_TYPE = 0;
	float[] drawValues;
	float[] fileValues;
	String[] dates;
	String[] dateValues;
	public static String user;
    boolean isLoggedIn;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  savedInstanceState.putInt("dataPoints", DATA_POINTS);
	  savedInstanceState.putInt("chartType", CHART_TYPE);
	  super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  DATA_POINTS = savedInstanceState.getInt("dataPoints");
	  CHART_TYPE = savedInstanceState.getInt("chartType");
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        setData();
        createView();
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.progress, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    
		    //Select type of chart
		    case R.id.step_chart:
		        CHART_TYPE = 0;
		        System.out.println("Chart type: "+CHART_TYPE);
		        createView();
		        return true;
		    case R.id.calorie_chart:
		        CHART_TYPE = 1;
		    	System.out.println("Chart type: "+CHART_TYPE);        
		        createView();
		        return true;
		    case R.id.tv_chart:
		        CHART_TYPE = 2;
		        System.out.println("Chart type: "+CHART_TYPE);
		        createView();
		        return true;
		    case R.id.weight_graph:
		        CHART_TYPE = 3;
		        System.out.println("Chart type: "+CHART_TYPE);
		        createView();
		        return true;
		        
		    //Select Number of Days    
		    case R.id.view_7_days:
		        DATA_POINTS = 7;
		        System.out.println("Data points: "+DATA_POINTS);
		        createView();
		        return true;
		    case R.id.view_14_days:
		    	DATA_POINTS = 14;
		    	System.out.println("Data points: "+DATA_POINTS);
		    	createView();
		        return true;
		    case R.id.view_21_days:
		    	DATA_POINTS = 21;
		    	System.out.println("Data points: "+DATA_POINTS);
		    	createView();
		        return true;
		    case R.id.view_30_days:
		    	DATA_POINTS = 30;
		    	System.out.println("Data points: "+DATA_POINTS);
		    	createView();
		        return true;
		        
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void setData(){
    	user = HomeActivity.getName();
    	if(!user.equals("")){
	    	isLoggedIn = true;
        } else {
        	isLoggedIn = false;
        }
    }
	
	public void createView(){
		int i = 0;
		String chartTitle = "";
		
		switch(CHART_TYPE){
			case 0:
				i = getFileData(user, "_steps.txt");
				System.out.println("Get Steps file data");
				chartTitle = "Steps Taken Per Day";
				break;
			case 1:
				i = getFileData(user, "_calories.txt");
				System.out.println("Get Calories file data");
				chartTitle = "Calories Per Day";
				break;
			case 2:
				i = getFileData(user, "_timewatched.txt");
				System.out.println("Get TV file data");
				chartTitle = "Minutes Watched Per Day";
				break;
			case 3:
				//TODO: weight chart
				break;
			default:
				i = getFileData(user, "_steps.txt");
				System.out.println("Get Default file data");
				chartTitle = "Steps Taken Daily";
				break;
		}
		
		float max = 0.0f;
		if(i>0){ //ensure the data file is not empty
			//reverse the order of values and find the maximum
	        drawValues = new float[i];
	        int h = i-1;
	        for(int j=0; j<i; j++){
	        	drawValues[h] = fileValues[j];
	        	if(fileValues[j]>max){
	        		max = fileValues[j];
	        	}
	        	h--;
	        }
	        
	        //reverse the order of dates and strip off the year
	        dateValues = new String[i];
	        StringTokenizer st;
	        int r = i-1;
	        for(int s=0; s<i; s++){
	        	st = new StringTokenizer(dates[s],"/");
	        	dateValues[r] = st.nextToken()+"/"+st.nextToken();
	        	r--;
	        }
		}
        
		String[] verlabels = new String[] { Integer.toString((int)max), Integer.toString(((int)max)/2), "0" };
		GraphView graphView = new GraphView(this, drawValues, chartTitle, dateValues, verlabels, GraphView.BAR);
		setContentView(graphView);
	}
	
	public int getFileData(String userName, String fileSuffix){
		int count = 0;
		fileValues = new float[DATA_POINTS];
		dates = new String[DATA_POINTS];
		boolean first = true;
		try {
			InputStream instream = openFileInput(userName+fileSuffix);
			if(!instream.equals(null)){
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				String prevDate = "";
				String currentDate = "";
				Float prevVal = 0.0f;
				Float currentVal = 0.0f;
				while ((line = buffreader.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(line);
					currentDate = st.nextElement().toString();
					//System.out.println(currentDate);
					currentVal = Float.parseFloat(st.nextElement().toString());
					//System.out.println(currentVal);
					
					if(currentDate.equals(prevDate)){
						prevVal = prevVal + currentVal;
						prevDate = currentDate;
					} else {
						if(!first){
							//add prevVal to fileValues
							fileValues[count] = prevVal;
							dates[count] = prevDate;
							count++;
							prevDate = currentDate;
							prevVal = currentVal;
							if(count == DATA_POINTS-1){
								break;
							}
						} else {
							first = false;
							prevDate = currentDate;
							prevVal = currentVal;
						}
					}
				}
				//add prevVal to fileValues
				fileValues[count] = prevVal;
				dates[count] = prevDate;
				if(!first){
					count++;
				}
				System.out.println("Count: "+count);
			}
			instream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count; 
    }
	
	/*public void callWeightChartActivity(){
		Intent loginIntent = new Intent(ProgressActivity.this, edu.ucla.cs.nopainnogame.weightchart.ChartActivity.class);
        
	}*/
}
