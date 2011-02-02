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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import edu.ucla.cs.nopainnogame.R;

public class PreferencesActivity extends PreferenceActivity {
    private HeightDialog mHeightDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.chart_preferences);
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	updateWeightUnit(preferences.getString("weight_unit", null));
	updateHeight();
	mHeightDialog = new HeightDialog(this) {
		protected void done() {
		    removeDialog(2);
		    updateHeight();
		}
	    };
	findPreference("weight_unit").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
		    updateWeightUnit(newValue);
		    return true;
		}
	    });
	findPreference("height").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
		    showDialog(PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this).getInt("height", 0) < 1 ? 1 : 2);
		    return true;
		}
	    });
	
	//TODO: uncomment these lines after demo
	/*findPreference("website").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_url))));
		    return true;
		}
	    });
	findPreference("license").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
		    showDialog(0);
		    return true;
		}
	    });*/
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	if (id == 0) {
	    return createLicenseDialog();
	}
	return mHeightDialog.createDialog(id);
    }

    private Dialog createLicenseDialog() {
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setMessage(R.string.license_description);
	builder.setNeutralButton(R.string.close, null);
	return builder.create();
    }

    private void updateWeightUnit(Object unit) {
	findPreference("weight_unit").setSummary(getResources().getStringArray(R.array.weight_unit_labels)["lb".equals(unit) ? 1 : "st".equals(unit) ? 2 : 0]);
    }

    private void updateHeight() {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);
	int height = preferences.getInt("height", 0);
	String summary = height < 1 ? null : "ft".equals(preferences.getString("height_unit", null)) ? String.format(getString(R.string.height_ft), height / 12, height % 12) : String.format(getString(R.string.height_cm), height);
	findPreference("height").setSummary(summary);
    }
}
