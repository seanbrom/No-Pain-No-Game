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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.tts.TTS;

import edu.ucla.cs.nopainnogame.pedometer.PedometerSettings;
import edu.ucla.cs.nopainnogame.pedometer.Settings;
import edu.ucla.cs.nopainnogame.pedometer.StepService;


@SuppressWarnings("deprecation")
public class PedometerActivity extends Activity {
	private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    
    private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    TextView mDesiredPaceView;
    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;
    private float mDesiredPaceOrSpeed;
    private int mMaintain;
    private boolean mIsMetric;
    //private float mMaintainInc;
    
    public static String user;
	public static int tv_time;
    boolean isLoggedIn;
    
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStepValue = 0;
        mPaceValue = 0;

        setContentView(R.layout.pedometer_layout);
        startStepService();
        setData();
        if(!isLoggedIn){
        	Toast.makeText(PedometerActivity.this, "Please log in at the Home tab.", Toast.LENGTH_SHORT).show();
        }
        
        Button submitButton = (Button)findViewById(R.id.submitButton);
        submitButton.setOnClickListener(submitListener);
    }
    
    public void setData(){
    	user = HomeActivity.getName();
    	if(!user.equals("")){
	    	isLoggedIn = true;
        } else {
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
			fos = openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(newTime);
        	fos.close();
		} catch (FileNotFoundException fe) {
			System.err.println("Error: User file not found.");
			fe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void setFileData(String userName, String fileSuffix, String today, int value){
    	String filename = userName + fileSuffix;
    	FileOutputStream fos;
    	InputStream is;
    	int numBytes = 0;
    	//String test = "";//for debugging
		try {
			is = openFileInput(filename);
			numBytes = is.available();
			InputStreamReader ir = new InputStreamReader(is);
			char[] buf = new char [numBytes];
			ir.read(buf);
			fos = openFileOutput(filename, Context.MODE_PRIVATE);
			String data = today+" "+value+"\n";
			data = data+String.valueOf(buf);
			fos.write(data.getBytes());
			//fos.write(test.getBytes());//for debugging
			fos.flush();
			ir.close();
			is.close();			
        	fos.close();	
		} catch (FileNotFoundException fe) {
			System.err.println("Error: User file not found.");
			fe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void getSteps(String userName){
		try {
			InputStream instream = openFileInput(userName+"_steps.txt");
			if(!instream.equals(null)){
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				while ((line = buffreader.readLine()) != null) {
					System.out.println(line);
				}
			}
			instream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    
    private OnClickListener submitListener = new OnClickListener() {
    	public void onClick(View v) {
    		if(isLoggedIn){
	    		//log distance/steps with date
    			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    			Date date = new Date();
    			String today = dateFormat.format(date);
    			setFileData(user, "_steps.txt", today, mStepValue);
    			setFileData(user, "_calories.txt", today, mCaloriesValue);
    			//getSteps(user);
	    		
	    		//calculate TV time from calories/steps
	    		int newTvTime = mCaloriesValue; //TODO: change this later
	    		
	    		//update TV time file
	    		tv_time = tv_time + newTvTime;
	    		setTvTime(tv_time, user);    			
	    		
	    		//clear step values on pedometer screen
	    		resetValues(true);
	    		Toast.makeText(PedometerActivity.this, "You now have "+tv_time+" minutes of TV Time.", Toast.LENGTH_LONG).show();
    		} else {
    			System.err.println("User not logged in.");
    		}
    	}
    };
 

    @Override
    protected void onResume() {
        super.onResume();
        setData();
       
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        
        if (mSettings.getBoolean("speak", false)) {
            ensureTtsInstalled();
        }
        
        if (mIsRunning) {
            bindStepService();
        }
        mStepValueView     = (TextView) findViewById(R.id.step_value);
        mPaceValueView     = (TextView) findViewById(R.id.pace_value);
        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
        mSpeedValueView    = (TextView) findViewById(R.id.speed_value);
        mCaloriesValueView = (TextView) findViewById(R.id.calories_value);
        mDesiredPaceView   = (TextView) findViewById(R.id.desired_pace_value);

        mIsMetric = mPedometerSettings.isMetric();
        ((TextView) findViewById(R.id.distance_units)).setText(getString(
                mIsMetric
                ? R.string.kilometers
                : R.string.miles
        ));
        
        ((TextView) findViewById(R.id.speed_units)).setText(getString(
                mIsMetric
                ? R.string.kilometers_per_hour
                : R.string.miles_per_hour
        ));
        
        mMaintain = mPedometerSettings.getMaintainOption();
        /*((LinearLayout) this.findViewById(R.id.desired_pace_control)).setVisibility(
                mMaintain != PedometerSettings.M_NONE
                ? View.VISIBLE
                : View.GONE
            );
        if (mMaintain == PedometerSettings.M_PACE) {
            mMaintainInc = 5f;
            mDesiredPaceOrSpeed = (float)mPedometerSettings.getDesiredPace();
        }
        else 
        if (mMaintain == PedometerSettings.M_SPEED) {
            mDesiredPaceOrSpeed = mPedometerSettings.getDesiredSpeed();
            mMaintainInc = 0.1f;
        }
        Button button1 = (Button) findViewById(R.id.button_desired_pace_lower);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDesiredPaceOrSpeed -= mMaintainInc;
                mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
                displayDesiredPaceOrSpeed();
                setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
            }
        });
        Button button2 = (Button) findViewById(R.id.button_desired_pace_raise);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDesiredPaceOrSpeed += mMaintainInc;
                mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
                displayDesiredPaceOrSpeed();
                setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
            }
        });
        if (mMaintain != PedometerSettings.M_NONE) {
            ((TextView) findViewById(R.id.desired_pace_label)).setText(
                    mMaintain == PedometerSettings.M_PACE
                    ? R.string.desired_pace
                    : R.string.desired_speed
            );
        }
        
        
        displayDesiredPaceOrSpeed();*/
    }
    
    /*private void displayDesiredPaceOrSpeed() {
        if (mMaintain == PedometerSettings.M_PACE) {
            mDesiredPaceView.setText("" + (int)mDesiredPaceOrSpeed);
        }
        else {
            mDesiredPaceView.setText("" + mDesiredPaceOrSpeed);
        }
    }*/
    
    @Override
    protected void onPause() {
        if (mIsRunning) {
            unbindStepService();
        }
        super.onPause();
        savePaceSetting();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
    
    /*private void setDesiredPaceOrSpeed(float desiredPaceOrSpeed) {
        if (mService != null) {
            if (mMaintain == PedometerSettings.M_PACE) {
                mService.setDesiredPace((int)desiredPaceOrSpeed);
            }
            else
            if (mMaintain == PedometerSettings.M_SPEED) {
                mService.setDesiredSpeed(desiredPaceOrSpeed);
            }
        }
    }*/
    
    private void savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
    }

    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        mIsRunning = true;
        startService(new Intent(PedometerActivity.this,
                StepService.class));
    }
    
    private void bindStepService() {
    	getApplicationContext().bindService(new Intent(PedometerActivity.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindStepService() {
    	getApplicationContext().unbindService(mConnection);
    }
    
    private void stopStepService() {
        mIsRunning = false;
        if (mService != null) {
            stopService(new Intent(PedometerActivity.this, StepService.class));
        }
    }
    
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            mStepValueView.setText("0");
            mPaceValueView.setText("0");
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;
    
    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsRunning) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
            .setIcon(android.R.drawable.ic_media_pause)
            .setShortcut('1', 'p');
        }
        else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
            .setIcon(android.R.drawable.ic_media_play)
            .setShortcut('1', 'p');
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        .setShortcut('2', 'r');
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
        .setIcon(android.R.drawable.ic_menu_preferences)
        .setShortcut('8', 's')
        .setIntent(new Intent(this, Settings.class));
        menu.add(0, MENU_QUIT, 0, R.string.quit)
        .setIcon(android.R.drawable.ic_lock_power_off)
        .setShortcut('9', 'q');
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                return true;
            case MENU_RESET:
                resetValues(true);
                return true;
            case MENU_QUIT:
                resetValues(false);
                stopStepService();
                finish();
                return true;
        }
        return false;
    }
 
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) { 
                        mPaceValueView.setText("0");
                    }
                    else {
                        mPaceValueView.setText("" + (int)mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) { 
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) { 
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
    private void ensureTtsInstalled() {
        TTS t = new TTS(this, null, true);
        t.shutdown();
    }
    
}