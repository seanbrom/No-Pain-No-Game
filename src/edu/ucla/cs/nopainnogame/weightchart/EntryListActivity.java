// This file is part of Weight Chart.
// Copyright 2010 Fredrik Portstrom
//
// Weight Chart is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Weight Chart is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Weight Chart. If not, see
// <http://www.gnu.org/licenses/>.

package edu.ucla.cs.nopainnogame.weightchart;

import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import edu.ucla.cs.nopainnogame.R;

public class EntryListActivity extends ListActivity {
    private int mWeightUnit;
    private double mBmiFactor;
    private Database mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.entries);
        registerForContextMenu(getListView());
	mDatabase = new Database(this);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	mDatabase.close();
    }

    @Override
    protected void onResume() {
	super.onResume();
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	String weightUnit = preferences.getString("weight_unit", null);
	mWeightUnit = "lb".equals(weightUnit) ? 1 : "st".equals(weightUnit) ? 2 : 0;
	int height = preferences.getInt("height", 0);
	mBmiFactor = height > 0 ? (mWeightUnit > 0 ? .045359237 : .1) / ("ft".equals(preferences.getString("height_unit", null)) ? 0.00064516 : .0001) / height / height : 0;
	fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.entries, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (new FileCommands(this, mDatabase) {
		@Override
		protected void fillData() {
		    EntryListActivity.this.fillData();
		}
	    }.onOptionsItemSelected(item.getItemId())) {
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
	openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	getMenuInflater().inflate(R.menu.entry, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	int id = item.getItemId();
        if (id == R.id.edit) {
	    Intent intent = new Intent(this, EntryActivity.class);
	    intent.putExtra("edu.ucla.cs.nopainnogame.Id", ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id);
	    startActivity(intent);
	} else if (id == R.id.delete) {
	    mDatabase.exec("DELETE FROM weight WHERE _id = ?", new Object[] { ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id });
	    fillData();
	    return true;
        }
        return super.onContextItemSelected(item);
    }

    private void fillData() {
	Cursor cursor = mDatabase.query("SELECT _id, weight, created_at FROM weight ORDER BY created_at DESC");
	startManagingCursor(cursor);
	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, new String[] { "_id", "weight", "created_at" }, new int[] { R.id.weight, R.id.created_at, R.id.bmi });
	adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		    if (columnIndex == 0) {
			int weight = cursor.getInt(1);
			((TextView)view).setText(mWeightUnit < 2 ? String.format(getString(mWeightUnit < 1 ? R.string.weight_kg : R.string.weight_lb), .1 * weight) : String.format(getString(R.string.weight_st), weight / 140, weight % 140 * .1));
		    } else if (columnIndex == 1) {
			Date date = new Date(1000L * cursor.getInt(2));
			((TextView)view).setText(DateFormat.getDateFormat(EntryListActivity.this).format(date) + " " + DateFormat.getTimeFormat(EntryListActivity.this).format(date));
		    } else if (mBmiFactor > 0) {
			((TextView)view).setText(String.format(getString(R.string.bmi_value), mBmiFactor * cursor.getInt(1)));
		    } else {
			view.setVisibility(View.GONE);
		    }
		    return true;
		}
	    });
	setListAdapter(adapter);
    }
}
