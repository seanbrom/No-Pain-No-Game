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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class LearnActivity extends Activity {
	
	public static String user;
	public static int tv_time;
	public static int current_file;
	public static int max_file = 3;
    boolean isLoggedIn;
    public boolean isViewing = false;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.learn_layout);
        
        setData();
        if(!isLoggedIn){
        	Toast.makeText(LearnActivity.this, "Please log in at the Home tab.", Toast.LENGTH_SHORT).show();
        }
        
        System.out.println("isViewing: "+isViewing);
        if(!isViewing){
        	createWebView();
        	isViewing = true;
        }
              
        Button learnButton = (Button)findViewById(R.id.learnButton);
        learnButton.setOnClickListener(learnListener);
    }
    
    @Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  savedInstanceState.putBoolean("viewingBoolean", isViewing);
	  super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  isViewing = savedInstanceState.getBoolean("viewingBoolean");
	  System.out.println("Restore boolean: " + isViewing);
	}
    
    @Override
    protected void onResume() {
        super.onResume();
        setData();
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
    
    private OnClickListener learnListener = new OnClickListener() {
    	public void onClick(View v) {
    		//action when fetch button is pushed
    		createWebView();
    		tv_time = (tv_time+5);
    		setTvTime(tv_time, user);
    		Toast.makeText(LearnActivity.this, "You now have "+tv_time+" minutes of TV time.", Toast.LENGTH_SHORT).show();
    	}
    };
    
    private void createWebView(){
    	WebView webView = (WebView) findViewById(R.id.LearnWebView);       
        
        webView.getSettings().setJavaScriptEnabled(true);
        final Activity activity = LearnActivity.this;
        webView.setWebChromeClient(new WebChromeClient() {
          public void onProgressChanged(WebView view, int progress) {
            activity.setProgress(progress * 1000);
          }
        });
        webView.setWebViewClient(new WebViewClient() {
          public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
          }
        });
        
        if(isLoggedIn){
        	current_file = getCurrentFile(user);
        	webView.loadUrl("http://npng.dyndns.org/learn/"+current_file);
        	if(current_file < max_file){
        		current_file = (current_file+1);
        		setCurrentFile(current_file, user);	
        	} else {
        		setCurrentFile(1, user);
        	}
        } else {
        	webView.loadUrl("http://npng.dyndns.org/learn/1");
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
    
    public int getCurrentFile(String userName){
    	String filename = userName + "_currentfile";
    	int currentfile = 1;
    	FileInputStream fis;
		try {
			fis = openFileInput(filename);
			currentfile = fis.read();
        	fis.close();
		} catch (FileNotFoundException fe) {
			System.err.println("Error: User file not found.");
			createCurrentFile(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return currentfile;
    }
    
    public void setCurrentFile(int newFileNum, String userName){
    	String filename = userName + "_currentfile";
    	FileOutputStream fos;
		try {
			fos = openFileOutput(filename, Context.MODE_WORLD_WRITEABLE);
			fos.write(newFileNum);
        	fos.close();
		} catch (FileNotFoundException fe) {
			System.err.println("Error: User file not found.");
			createCurrentFile(user);
			fe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void createCurrentFile(String userName){
    	//Create Current_File persistent file
		String fileNum_filename = userName.toString() + "_currentfile";
    	int fileNum = 1;
    	FileOutputStream fos;
    	try {
			fos = openFileOutput(fileNum_filename, Context.MODE_WORLD_WRITEABLE);
			fos.write(fileNum);
        	fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}