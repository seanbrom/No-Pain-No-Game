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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WatchActivity extends Activity {
	
	public static String user;
	public static int tv_time;
	public boolean viewing;
	boolean isLoggedIn;
    TextView timeLeftView;
    MyCount counter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_layout);
        
        viewing = false;
        setData();
        if(!isLoggedIn){
        	Toast.makeText(WatchActivity.this, "Please log in at the Home tab.", Toast.LENGTH_SHORT).show();
        }
        
        Button startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(startListener);
        Button stopButton = (Button)findViewById(R.id.stopButton);
        stopButton.setOnClickListener(stopListener);
        timeLeftView = (TextView) findViewById(R.id.timeLeft);
        timeLeftView.setText(Integer.toString(tv_time));
    }
    
    @Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  savedInstanceState.putBoolean("viewingBoolean", viewing);
	  super.onSaveInstanceState(savedInstanceState);
	  if(counter != null){
		  counter.cancel();
	  }
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  viewing = savedInstanceState.getBoolean("viewingBoolean");
	  if(viewing){
		  counter = new MyCount((getTvTime(user)*1000), 1000);
		  counter.start();
	  }
	}
    
    @Override
    protected void onResume() {
        super.onResume();
        setData();
        timeLeftView.setText(Integer.toString(tv_time));
    }
    
    public void setData(){
    	user = HomeActivity.getName();
    	if(!user.equals("")){
	    	isLoggedIn = true;
        }
        else {
        	isLoggedIn = false;
        }
	    if(isLoggedIn){
	    	tv_time = getTvTime(user);
    	}
    }
    
    public int getTvTime(String userName){
    	String filename = userName + "_tvtime";
    	int tvTime = -1;
    	FileInputStream fis;
		try {
			fis = openFileInput(filename);
			tvTime = fis.read();
        	fis.close();
		} catch (FileNotFoundException fe) {
			System.err.println("Error: User file not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tvTime;
    }
    
    public void setTvTime(int newTime, String userName){
    	String filename = userName + "_tvtime";
    	FileOutputStream fos;
		try {
			fos = openFileOutput(filename, Context.MODE_WORLD_WRITEABLE);
			fos.write(newTime);
        	fos.close();
		} catch (FileNotFoundException fe) {
			System.err.println("Error: User file not found.");
			fe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private OnClickListener startListener = new OnClickListener() {
    	public void onClick(View v) {
    		//action when start button is pushed 
    		startRoutine();
    	}
    };
    
    private OnClickListener stopListener = new OnClickListener() {
    	public void onClick(View v) {
    		//action when stop button is pushed    		
    		stopRoutine();
    	}
    };

    
    private void startRoutine(){
    	if(!viewing && isLoggedIn && tv_time > 0){
    			viewing = true;
	    		counter = new MyCount((tv_time*1000), 1000);
	    		HttpClient httpclient = new DefaultHttpClient();    		
	    		HttpPost startPost = new HttpPost("http://npng.dyndns.org/start/"+(Integer.toString(tv_time)+5));
	    		//in the above line, add 5 to the tv time to provide a time buffer to allow phone to turn outlet off
	    		//if the phone doesn't, the server will for redundancy
	    		try {
					HttpResponse startResponse = httpclient.execute(startPost);
					int statusCode = startResponse.getStatusLine().getStatusCode();
					if(statusCode == 200){
						//Success
						counter.start();
						Toast.makeText(WatchActivity.this, "Your TV time has started", Toast.LENGTH_SHORT).show();
					} else {
						//TODO: did not work, alert the user
						Toast.makeText(WatchActivity.this, "Unable to reach automation hardware.", Toast.LENGTH_SHORT).show();
						viewing = false;
					}	
				} catch (ClientProtocolException e) {
					System.out.println("START PROTOCOL EXCEPTION");
					Toast.makeText(WatchActivity.this, "Unable to reach web server.", Toast.LENGTH_SHORT).show();
					viewing = false;
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("START IO EXCEPTION");
					Toast.makeText(WatchActivity.this, "Unable to reach web server.", Toast.LENGTH_SHORT).show();
					viewing = false;
					e.printStackTrace();
				}
    		} else if(viewing && isLoggedIn){
    			Toast.makeText(WatchActivity.this, "You are currently watching TV.", Toast.LENGTH_SHORT).show();
    		} else if(isLoggedIn && tv_time == 0){
    			Toast.makeText(WatchActivity.this, "You do not have enough TV Time.", Toast.LENGTH_SHORT).show();
    		} else if(!isLoggedIn){
    			Toast.makeText(WatchActivity.this, "Please log in at the Home tab.", Toast.LENGTH_SHORT).show();
    		} else {
    			System.out.print("Fell through if/else");
    		}
    }
    
    private void stopRoutine(){
    	if(viewing && isLoggedIn){
    			viewing = false;
    			counter.cancel();
	    		
	    		HttpClient httpclient = new DefaultHttpClient();
	    		HttpPost stopPost = new HttpPost("http://npng.dyndns.org/stop/"+Integer.toString(tv_time));
	    		try {
					HttpResponse stopResponse = httpclient.execute(stopPost);
					int statusCode = stopResponse.getStatusLine().getStatusCode();
					//String statusReason = stopResponse.getStatusLine().getReasonPhrase();
					//int time_left = Integer.parseInt(statusReason);			
					if(statusCode == 200){
						//Success
						//tv_time = time_left;
						//setTvTime(time_left, user);
		    			Toast.makeText(WatchActivity.this, "Your TV Time has been stopped", Toast.LENGTH_SHORT).show();
					} else {
						//did not work, alert the user
						Toast.makeText(WatchActivity.this, "Unable to reach automation hardware.", Toast.LENGTH_SHORT).show();
					}
				} catch (ClientProtocolException e) {
					Toast.makeText(WatchActivity.this, "Unable to reach web server.", Toast.LENGTH_SHORT).show();
					viewing = false;
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(WatchActivity.this, "Unable to reach web server.", Toast.LENGTH_SHORT).show();
					viewing = false;
					e.printStackTrace();
				}
    		} else if(!viewing && isLoggedIn){
    			Toast.makeText(WatchActivity.this, "You are not currently watching TV.", Toast.LENGTH_SHORT).show();
    		} else if(!isLoggedIn){
    			Toast.makeText(WatchActivity.this, "Please log in at the Home tab.", Toast.LENGTH_SHORT).show();
    		} else {
    			System.out.print("Fell through if/else");
    		}
    }
    
    public class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			timeLeftView.setText("Out of Time");
			stopRoutine();
			setTvTime(0, user);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			timeLeftView.setText(Integer.toString((int)millisUntilFinished/1000));
			setTvTime((int)millisUntilFinished/1000, user);
		}
    	
    }
    
}
