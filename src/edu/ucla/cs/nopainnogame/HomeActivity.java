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
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

public class HomeActivity extends Activity {
	
	public static String user;
	public static boolean loggedin = false;
	public final int LOG_IN = 1;
	public boolean LOG_LAUNCH = false;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  savedInstanceState.putBoolean("loginBoolean", loggedin);
	  savedInstanceState.putBoolean("loginLaunchBoolean", LOG_LAUNCH);
	  super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  loggedin = savedInstanceState.getBoolean("loginBoolean");
	  LOG_LAUNCH = savedInstanceState.getBoolean("loginLaunchBoolean");
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        if(!loggedin && !LOG_LAUNCH){
        	callLoginActivity();
        	LOG_LAUNCH = true;
        } else {
        	createLayout();
        }
    }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode) {
		case LOG_IN:
			user = data.getStringExtra("user");
			loggedin = true;
			createLayout(); 
			break;
		default:
			break;
		}
	}
	
	void createLayout(){
		int tvTime = getTvTime(user);
		ScrollView sv = new ScrollView(this);
		TableLayout tl = new TableLayout(HomeActivity.this);
		
		ImageView iv = new ImageView(HomeActivity.this);
		iv.setImageResource(R.drawable.npng);
		
		Button loginButton = new Button(this);
        loginButton.setOnClickListener(loginListener);
        loginButton.setText("Change User");
        
		
        TextView tv1 = new TextView(HomeActivity.this);
        TextView tv2 = new TextView(HomeActivity.this);
        TextView tv3 = new TextView(HomeActivity.this);
        TextView tv4 = new TextView(HomeActivity.this);
        tv1.setGravity(1);
        tv2.setGravity(1);
        tv3.setGravity(1);
        tv4.setGravity(1);
        tv1.setTextAppearance(getApplicationContext(), R.style.LargeStyle);
        tv2.setTextAppearance(getApplicationContext(), R.style.MedStyle);
        tv3.setTextAppearance(getApplicationContext(), R.style.NumStyle);
        tv4.setTextAppearance(getApplicationContext(), R.style.MedStyle);
        
        tv1.setText("\nWelcome, "+user+"!");
        tv2.setText("\nYou have\n");
        tv3.setText(Integer.toString(tvTime));
        tv4.setText("\nminutes of TV Time\n\n");
        
        tl.addView(iv);
        tl.addView(tv1);
        tl.addView(tv2);
        tl.addView(tv3);
        tl.addView(tv4);
        tl.addView(loginButton);
        sv.addView(tl);
        setContentView(sv);
	}
	
	private OnClickListener loginListener = new OnClickListener() {
    	public void onClick(View v) {
    		callLoginActivity();
    	}
    };
    
    public void callLoginActivity(){
    	Intent loginIntent = new Intent(HomeActivity.this, edu.ucla.cs.nopainnogame.LogInActivity.class);
        startActivityForResult(loginIntent, LOG_IN);
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
        
    public static String getName(){
    	if(loggedin){
    		return user;
    	} else {
    		return "";
    	}
    }   
}
