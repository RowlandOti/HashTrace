package com.rowland.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * When application code requests a new instance of this database from the
 * helper, the helper first checks to see if the database file exists. If there
 * is no such file, it creates it, using the name passed as the second argument
 * to the constructor (a name that, by convention, ends with the suffix .db).
 * <p>
 * Next, it calls <b>onCreate</b>, one of its three template methods. The
 * subclass implementation of this method is responsible for completely creating
 * the necessary schema in the newly created database file.
 * </p>
 * An application should always use the database’s helper object to obtain an
 * instance of a database. By doing so, it guarantees that the instance it holds
 * is complete, initialized, and ready for use.
 *
 * * @author Rowland
 */
public class TweetHashTracerDbHelper extends SQLiteOpenHelper {

	// You can have multiple db.execSQL(...) statements in each apply or revert methods.
	// Now when you need to modify your SQLite database schema,
	// simply add a new Patch to the PATCHES array. Your application will take care of the rest
	static final TweetPatch[] PATCHES = new TweetPatch[]
			{ new TweetPatch() {
				public void apply(SQLiteDatabase db) {
					db.execSQL(SQL_CREATE_HASHTAG_TABLE);
					db.execSQL(SQL_CREATE_TWEET_TABLE);
					db.execSQL(SQL_CREATE_TWEETFAV_TABLE);
				}

				public void revert(SQLiteDatabase db) {
					db.execSQL(SQL_DROP_HASHTAG_TABLE);
					db.execSQL(SQL_DROP_TWEET_TABLE);
					db.execSQL(SQL_DROP_TWEETFAV_TABLE);
				}
				//Create new sql and implement methods below for future migrations
			},  new TweetPatch() {
				public void apply(SQLiteDatabase db) { }
				public void revert(SQLiteDatabase db) { }
			} };

	// If you change the database schema, you must increment the database
	// version. This is done automatically by incrementing it to the TweetPatch.length
	private static final int DATABASE_VERSION = PATCHES.length;

	public static final String DATABASE_NAME = "hashtrace.db";

	// Notice the PATCHES.length variable first in the constructor as DATABASE_VERSION
	// That is the key to this whole operation
	public TweetHashTracerDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase)
	{
		for (int i = 0; i < PATCHES.length; i++)
		{
			PATCHES[i].apply(sqLiteDatabase);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
	{
		for (int i = oldVersion; i < newVersion; i++)
		{
			PATCHES[i].apply(sqLiteDatabase);
		}
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		for (int i = oldVersion; i > newVersion; i++)
		{
			PATCHES[i-1].revert(db);
		}
	}
}
