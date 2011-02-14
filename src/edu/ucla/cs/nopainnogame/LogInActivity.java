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
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class LogInActivity extends Activity {
	
	public static String user;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        
        final EditText edittext = (EditText) findViewById(R.id.editInput);
        edittext.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) { // Perform action on key press
                    //Check to see if the user exists
                	Editable userName = edittext.getText();
                	user = userName.toString();
                	do_login(userName);
                	backHome();
                	return true;
                }
                return false;
            }
        });   
       
    }
	
	private void do_login(Editable userName){
    	String filename = userName.toString() + "_tvtime";
    	FileInputStream fis;
		try {
			fis = openFileInput(filename);
        	fis.close();
		} catch (FileNotFoundException e) {
			System.err.println("The username does not exist, creating one now...");
			do_create(userName);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void do_create(Editable userName){
    	//Create TV_Time persistent file
    	String tv_filename = userName.toString() + "_tvtime";
    	int time = 0;
    	FileOutputStream fos;
    	try {
			fos = openFileOutput(tv_filename, Context.MODE_PRIVATE);
			fos.write(time);
			System.out.println("A user account has been created for "+userName.toString());
        	fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Create Num_Steps persistent file
		String steps_filename = userName.toString() + "_steps.txt";
		String data = "";
    	try {
			fos = openFileOutput(steps_filename, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
        	fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Create Num_Calories persistent file
		String calories_filename = userName.toString() + "_calories.txt";
    	try {
			fos = openFileOutput(calories_filename, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
        	fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Create Time_Watched persistent file
		String time_filename = userName.toString() + "_timewatched.txt";
    	try {
			fos = openFileOutput(time_filename, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
        	fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Create Current_File persistent file
		String fileNum_filename = userName.toString() + "_currentfile";
    	int fileNum = 1;
    	try {
			fos = openFileOutput(fileNum_filename, Context.MODE_PRIVATE);
			fos.write(fileNum);
        	fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
    
    public void backHome(){
    	Intent data = new Intent();
    	data.putExtra("user", user);
    	setResult(RESULT_OK, data);
    	finish();
    }

}
