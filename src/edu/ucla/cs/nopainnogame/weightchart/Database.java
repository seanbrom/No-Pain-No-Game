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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {
    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mHelper;

    public Database(Context context) {
	mHelper = new SQLiteOpenHelper(context, "data", null, 1) {
		@Override
		public void onCreate(SQLiteDatabase db) {
		    db.execSQL("CREATE TABLE weight (_id INTEGER PRIMARY KEY AUTOINCREMENT, weight, created_at)");
		    db.execSQL("CREATE INDEX weight_created_at ON weight (created_at)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	    };
	mDatabase = mHelper.getWritableDatabase();
    }

    public void close() {
	mHelper.close();
    }

    public void beginTransaction() {
	mDatabase.beginTransaction();
    }

    public void endTransaction() {
	mDatabase.endTransaction();
    }

    public void setTransactionSuccessful() {
	mDatabase.setTransactionSuccessful();
    }

    public void exec(String query) {
	mDatabase.execSQL(query);
    }

    public void exec(String query, Object[] values) {
	mDatabase.execSQL(query, values);
    }

    public Cursor query(String query) {
	return mDatabase.rawQuery(query, null);
    }
}
