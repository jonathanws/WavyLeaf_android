package com.towson.wavyleaf;

import static android.provider.BaseColumns._ID;
import static com.towson.wavyleaf.DatabaseConstants.ITEM_NAME;
import static com.towson.wavyleaf.DatabaseConstants.TABLE_NAME;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is based off of PA4 from the Android Development class
 * Not sure if this is the same number in the class now, but you get the idea.
 */

public class DatabaseListJSONData extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "points.db";
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Create a helper object for the points database
	 */
	public DatabaseListJSONData(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// TABLE_NAME comes from DatabaseConstants.java
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
				"CREATE TABLE " + TABLE_NAME + 
				" (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT NOT NULL);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}
