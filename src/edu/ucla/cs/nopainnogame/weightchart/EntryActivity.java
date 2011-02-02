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
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import edu.ucla.cs.nopainnogame.R;

public class EntryActivity extends Activity {
    private boolean mStone;
    private long mId;
    private GregorianCalendar mDateTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.entry);
	String weightUnit = PreferenceManager.getDefaultSharedPreferences(this).getString("weight_unit", null);
	mStone = "st".equals(weightUnit);
	((TextView)findViewById(R.id.weight_unit_major)).setText(mStone ? R.string.st : "lb".equals(weightUnit) ? R.string.lb : R.string.kg);

	if (!mStone) {
	    findViewById(R.id.weight_minor).setVisibility(View.GONE);
	    findViewById(R.id.weight_unit_minor).setVisibility(View.GONE);
	}

	((Button)findViewById(R.id.date)).setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
		    showDialog(0);
		}
	    });
        ((Button)findViewById(R.id.time)).setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
		    showDialog(1);
		}
	    });
        ((Button)findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
		    saveAndFinish();
		}
	    });

	if (savedInstanceState != null) {
	    mId = savedInstanceState.getLong("id");
	    mDateTime = (GregorianCalendar)savedInstanceState.getSerializable("datetime");

	    if (mDateTime != null) {
		findViewById(R.id.set_manually).setVisibility(View.GONE);
		initDateTime();
		return;
	    }
	} else {
	    mId = getIntent().getLongExtra("edu.ucla.cs.nopainnogame.Id", 0);
	    
	    if (mId != 0) {
		Database database = new Database(EntryActivity.this);
		Cursor cursor = database.query("SELECT weight, created_at FROM weight WHERE _id = " + mId);

		try {
		    if (cursor.moveToNext()) {
			int weight = cursor.getInt(0);

			if (mStone) {
			    ((EditText)findViewById(R.id.weight_major)).setText("" + weight / 140);
			    ((EditText)findViewById(R.id.weight_minor)).setText("" + weight % 140 / 10.);
			} else {
			    ((EditText)findViewById(R.id.weight_major)).setText("" + weight / 10.);
			}

			mDateTime = new GregorianCalendar();
			mDateTime.setTimeInMillis(1000L * cursor.getInt(1));
			findViewById(R.id.set_manually).setVisibility(View.GONE);
			initDateTime();
			return;
		    }
		} finally {
		    cursor.close();
		    database.close();
		}

		mId = 0;
	    }
	}

	((Button)findViewById(R.id.set_manually)).setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
		    mDateTime = new GregorianCalendar();
		    mDateTime.set(GregorianCalendar.SECOND, 0);
		    findViewById(R.id.set_manually).setVisibility(View.GONE);
		    findViewById(R.id.date_time).setVisibility(View.VISIBLE);
		    initDateTime();
		}
	    });
	findViewById(R.id.date_time).setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("id", mId);
        outState.putSerializable("datetime", mDateTime);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	if (id == 0) {
	    return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
		    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mDateTime.set(GregorianCalendar.YEAR, year);
			mDateTime.set(GregorianCalendar.MONTH, monthOfYear);
			mDateTime.set(GregorianCalendar.DATE, dayOfMonth);
			((Button)findViewById(R.id.date)).setText(DateFormat.getDateFormat(EntryActivity.this).format(mDateTime.getTime()));
		    }
		}, mDateTime.get(GregorianCalendar.YEAR), mDateTime.get(GregorianCalendar.MONTH), mDateTime.get(GregorianCalendar.DATE));
	}
	if (id == 1) {
	    return new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
		    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mDateTime.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
			mDateTime.set(GregorianCalendar.MINUTE, minute);
			((Button)findViewById(R.id.time)).setText(DateFormat.getTimeFormat(EntryActivity.this).format(mDateTime.getTime()));
		    }
		}, mDateTime.get(GregorianCalendar.HOUR), mDateTime.get(GregorianCalendar.MINUTE), DateFormat.is24HourFormat(this));
	}
	return null;
    }

    private void initDateTime() {
	Date date = mDateTime.getTime();
	((Button)findViewById(R.id.date)).setText(DateFormat.getDateFormat(this).format(date));
	((Button)findViewById(R.id.time)).setText(DateFormat.getTimeFormat(this).format(date));
    }

    private void saveAndFinish() {
	int weight;

	try {
	    weight = (int)(10 * Float.parseFloat(((EditText)findViewById(R.id.weight_major)).getText().toString()) + .5);

	    if (mStone) {
		weight = 14 * weight + (int)(10 * Float.parseFloat(((EditText)findViewById(R.id.weight_minor)).getText().toString()) + .5);
	    }
	} catch (NumberFormatException e) {
	    Toast.makeText(EntryActivity.this, R.string.invalid_weight, Toast.LENGTH_SHORT).show();
	    return;
	}

	long createdAt = (mDateTime == null ? System.currentTimeMillis() : mDateTime.getTime().getTime()) / 1000;
	Database database = new Database(EntryActivity.this);

	if (mId == 0) {
	    database.exec("INSERT INTO weight (weight, created_at) VALUES (?, ?)", new Object[] { weight, createdAt });
	} else {
	    database.exec("UPDATE weight SET weight = ?, created_at = ? WHERE _id = ?", new Object[] { weight, createdAt, mId });
	}

	database.close();
	Toast.makeText(EntryActivity.this, mId == 0 ? R.string.weight_added : R.string.entry_edited, Toast.LENGTH_SHORT).show();
	finish();
    }
}
