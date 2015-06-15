package com.rowland.data;

import android.database.sqlite.SQLiteDatabase;

import com.rowland.data.TweetHashTracerContract.HashTagEntry;
import com.rowland.data.TweetHashTracerContract.TweetEntry;
import com.rowland.data.TweetHashTracerContract.TweetFavEntry;

/**
 * This class will represent a patchset of changes to be applied on
 *  the old database during OnUpgrade() or OnDownGrade().
 *
 * @author Rowland
 *
 */
public class TweetPatch {

	static	final String SQL_DROP_HASHTAG_TABLE = "DROP TABLE IF EXISTS "+ HashTagEntry.TABLE_NAME;
	static	final String SQL_DROP_TWEET_TABLE = "DROP TABLE IF EXISTS "+ TweetEntry.TABLE_NAME;
	static	final String SQL_DROP_TWEETFAV_TABLE = "DROP TABLE IF EXISTS "+ TweetFavEntry.TABLE_NAME;

	// Create a table to hold hashtags. A hashtag consists of the string
	// supplied in the hashtag setting.
	static	final String SQL_CREATE_HASHTAG_TABLE = "CREATE TABLE "
			+ HashTagEntry.TABLE_NAME + " (" + HashTagEntry._ID
			+ " INTEGER PRIMARY KEY," + HashTagEntry.COLUMN_HASHTAG_SETTING
			+ " TEXT UNIQUE NOT NULL, " + HashTagEntry.COLUMN_HASHTAG_NAME
			+ " TEXT NOT NULL, " + "UNIQUE ("
			+ HashTagEntry.COLUMN_HASHTAG_SETTING + ") ON CONFLICT IGNORE"
			+ " );";

	static	final String SQL_CREATE_TWEET_TABLE = "CREATE TABLE "
			+ TweetEntry.TABLE_NAME
			+ " ("
			+
			// Why AutoIncrement here, and not above?
			// Unique keys will be auto-generated in either case. But for
			// weather
			// forecasting, it's reasonable to assume the user will want
			// information
			// for a certain date and all dates *following*, so the forecast
			// data
			// should be sorted accordingly.
			TweetEntry._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+
			// the ID of the hashtag entry associated with this tweet data
			  TweetEntry.COLUMN_HASHTAG_KEY + " INTEGER NOT NULL, "
			+ TweetEntry.COLUMN_TWEET_ID + " INTEGER NOT NULL,"
			+ TweetEntry.COLUMN_TWEET_TEXT + " TEXT NOT NULL, "
			+ TweetEntry.COLUMN_TWEET_TEXT_DATE + " TEXT NOT NULL, "
			+ TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT + " INTEGER NOT NULL,"
			+ TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT + " INTEGER NOT NULL,"
			+ TweetEntry.COLUMN_TWEET_TEXT_MENTIONS_COUNT + " INTEGER NOT NULL,"
			+ TweetEntry.COLUMN_TWEET_USERNAME + " TEXT NOT NULL, "
			+ TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL + " TEXT NOT NULL, "
			+ TweetEntry.COLUMN_TWEET_USERNAME_LOCATION + " TEXT NOT NULL, "
			+ TweetEntry.COLUMN_TWEET_USERNAME_DESCRIPTION + " TEXT NOT NULL, "
			+ TweetEntry.COLUMN_TWEET_FAVOURITED_STATE + " INTEGER NOT NULL default 0, "
			+
			// Set up the hashtag column as a foreign key to location table.
			" FOREIGN KEY (" + TweetEntry.COLUMN_HASHTAG_KEY
			+ ") REFERENCES " + HashTagEntry.TABLE_NAME + " ("
			+ HashTagEntry._ID
			+ "), "
			+
			// To assure the application have just one tweet entry per day
			// per tweet_id, it's created a UNIQUE constraint with REPLACE
			// strategy
			" UNIQUE (" + TweetEntry.COLUMN_TWEET_ID + ", "
			+ TweetEntry.COLUMN_HASHTAG_KEY + ") ON CONFLICT REPLACE);";

	static	final String SQL_CREATE_TWEETFAV_TABLE = "CREATE TABLE "
			+ TweetFavEntry.TABLE_NAME
			+ " ("
			+
			// Why AutoIncrement here, and not above?
			// Unique keys will be auto-generated in either case. But for
			// weather
			// forecasting, it's reasonable to assume the user will want
			// information
			// for a certain date and all dates *following*, so the forecast
			// data
			// should be sorted accordingly.
			TweetFavEntry._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+
			// the ID of the hashtag entry associated with this tweet data
			TweetFavEntry.COLUMN_HASHTAG_KEY + " INTEGER NOT NULL, "
			+ TweetFavEntry.COLUMN_TWEETFAV_ID + " INTEGER NOT NULL,"
			+ TweetFavEntry.COLUMN_TWEETFAV_TEXT + " TEXT NOT NULL, "
			+ TweetFavEntry.COLUMN_TWEETFAV_TEXT_DATE + " TEXT NOT NULL, "
			+ TweetFavEntry.COLUMN_TWEETFAV_TEXT_RETWEET_COUNT + " INTEGER NOT NULL,"
			+ TweetFavEntry.COLUMN_TWEETFAV_TEXT_FAVOURITE_COUNT + " INTEGER NOT NULL,"
			+ TweetFavEntry.COLUMN_TWEETFAV_TEXT_MENTIONS_COUNT + " INTEGER NOT NULL,"
			+ TweetFavEntry.COLUMN_TWEETFAV_USERNAME + " TEXT NOT NULL, "
			+ TweetFavEntry.COLUMN_TWEETFAV_USERNAME_IMAGE_URL + " TEXT NOT NULL, "
			+ TweetFavEntry.COLUMN_TWEETFAV_USERNAME_LOCATION + " TEXT NOT NULL, "
			+ TweetFavEntry.COLUMN_TWEETFAV_USERNAME_DESCRIPTION + " TEXT NOT NULL, "
			+
			// Set up the hashtag column as a foreign key to location table.
			" FOREIGN KEY (" + TweetFavEntry.COLUMN_HASHTAG_KEY
			+ ") REFERENCES " + HashTagEntry.TABLE_NAME + " ("
			+ HashTagEntry._ID
			+ "), "
			+
			// To assure the application have just one tweet entry per day
			// per hashtag, it's created a UNIQUE constraint with REPLACE
			// strategy
			" UNIQUE (" + TweetFavEntry.COLUMN_TWEETFAV_ID + ", "
			+ TweetFavEntry.COLUMN_HASHTAG_KEY + ") ON CONFLICT REPLACE);";



	public void apply(SQLiteDatabase db) {}

	public void revert(SQLiteDatabase db) {}

}
