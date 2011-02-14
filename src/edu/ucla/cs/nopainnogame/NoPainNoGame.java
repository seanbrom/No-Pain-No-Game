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

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class NoPainNoGame extends TabActivity {
	
	public static String username = "";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, HomeActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("home").setIndicator("Home",
                          res.getDrawable(R.drawable.ic_tab_home))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, PedometerActivity.class);
        spec = tabHost.newTabSpec("pedometer").setIndicator("Pedometer",
                          res.getDrawable(R.drawable.ic_tab_pedometer))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, LearnActivity.class);
        spec = tabHost.newTabSpec("learn").setIndicator("Learn",
                          res.getDrawable(R.drawable.ic_tab_info))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, WatchActivity.class);
        spec = tabHost.newTabSpec("watch").setIndicator("Watch TV",
                          res.getDrawable(R.drawable.ic_tab_watch))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, ProgressActivity.class);
        spec = tabHost.newTabSpec("progress").setIndicator("Progress",
                          res.getDrawable(R.drawable.ic_tab_progress))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}