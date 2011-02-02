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

import android.app.Activity;
import android.os.Bundle;
import edu.ucla.cs.nopainnogame.R;

public class LegendActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.legend);
    }
}
