package com.rowland.hashtrace.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * @author Rowland
 *
 */
public class TweetHashTracerContentProvider extends ContentProvider {

	private static final int TWEET = 100;
	private static final int TWEET_WITH_ID = 101;
	private static final int TWEET_WITH_HASHTAG = 102;
	private static final int TWEET_WITH_HASHTAG_AND_DATE = 103;
	private static final int TWEETFAV = 200;
	private static final int TWEETFAV_WITH_HASHTAG = 201;
	private static final int TWEETFAV_WITH_HASHTAG_AND_DATE = 202;
	private static final int HASHTAG = 300;
	private static final int HASHTAG_ID = 301;
	private static final int TWEET_WITH_HASHTAG_SEARCH = 400;
	// The URI Matcher used by this content provider.
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private static final SQLiteQueryBuilder sTweetByHashTagSettingQueryBuilder;
	private static final SQLiteQueryBuilder sTweetFavByHashTagSettingQueryBuilder;
	private static final String sHashTagSettingSelection = TweetHashTracerContract.HashTagEntry.TABLE_NAME
			+ "."
			+ TweetHashTracerContract.HashTagEntry.COLUMN_HASHTAG_SETTING
			+ " = ? ";
	private static final String sHashTagSettingWithStartDateSelection = TweetHashTracerContract.HashTagEntry.TABLE_NAME
			+ "."
			+ TweetHashTracerContract.HashTagEntry.COLUMN_HASHTAG_SETTING
			+ " = ? AND "
			+ TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_DATE
			+ " >= ? ";
	private static final String sHashTagSettingAndDaySelection = TweetHashTracerContract.HashTagEntry.TABLE_NAME
			+ "."
			+ TweetHashTracerContract.HashTagEntry.COLUMN_HASHTAG_SETTING
			+ " = ? AND "
			+ TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_DATE
			+ " = ? ";
	private static final String sHashTagSettingAndSearchKeySelection = TweetHashTracerContract.HashTagEntry.TABLE_NAME
			+ "."
			+ TweetHashTracerContract.HashTagEntry.COLUMN_HASHTAG_SETTING
			+ " = ? AND "
			+ TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT
			+ " = ? OR "
			+ TweetHashTracerContract.TweetEntry.COLUMN_TWEET_USERNAME
			+ " = ? OR "
			+ TweetHashTracerContract.TweetEntry.COLUMN_TWEET_USERNAME_LOCATION
			+ " = ? ";
	private static final String sHashTagSettingAndIdSelection = TweetHashTracerContract.HashTagEntry.TABLE_NAME
			+ "."
			+ TweetHashTracerContract.HashTagEntry.COLUMN_HASHTAG_SETTING
			+ " = ? AND "
			+ TweetHashTracerContract.TweetEntry._ID
			+ " = ? ";
	private static final String sIdSelection = TweetHashTracerContract.TweetEntry.TABLE_NAME
			+ "."
			+ TweetHashTracerContract.TweetEntry._ID
			+ " = ? ";
	private static final String sHashTagFavSettingWithStartDateSelection = TweetHashTracerContract.HashTagEntry.TABLE_NAME
			+ "."
			+ TweetHashTracerContract.HashTagEntry.COLUMN_HASHTAG_SETTING
			+ " = ? AND "
			+ TweetHashTracerContract.TweetFavEntry.COLUMN_TWEETFAV_TEXT_DATE
			+ " >= ? ";
	private static final String sHashTagFavSettingAndDaySelection = TweetHashTracerContract.HashTagEntry.TABLE_NAME
			+ "."
			+ TweetHashTracerContract.HashTagEntry.COLUMN_HASHTAG_SETTING
			+ " = ? AND "
			+ TweetHashTracerContract.TweetFavEntry.COLUMN_TWEETFAV_TEXT_DATE
			+ " = ? ";

	static {
		sTweetByHashTagSettingQueryBuilder = new SQLiteQueryBuilder();
		sTweetByHashTagSettingQueryBuilder.setTables(
				TweetHashTracerContract.TweetEntry.TABLE_NAME
				+ " INNER JOIN "
				+ TweetHashTracerContract.HashTagEntry.TABLE_NAME
				+ " ON "
				+ TweetHashTracerContract.TweetEntry.TABLE_NAME + "."
				+ TweetHashTracerContract.TweetEntry.COLUMN_HASHTAG_KEY
				+ " = "
				+ TweetHashTracerContract.HashTagEntry.TABLE_NAME + "."
				+ TweetHashTracerContract.HashTagEntry._ID);
	}

	static {
		sTweetFavByHashTagSettingQueryBuilder = new SQLiteQueryBuilder();
		sTweetFavByHashTagSettingQueryBuilder.setTables(
				TweetHashTracerContract.TweetFavEntry.TABLE_NAME
				+ " INNER JOIN "
						+ TweetHashTracerContract.HashTagEntry.TABLE_NAME
				+ " ON "
						+ TweetHashTracerContract.TweetFavEntry.TABLE_NAME + "."
						+ TweetHashTracerContract.TweetFavEntry.COLUMN_HASHTAG_KEY
				+ " = "
						+ TweetHashTracerContract.HashTagEntry.TABLE_NAME + "."
						+ TweetHashTracerContract.HashTagEntry._ID);
	}

	private final String LOG_TAG = TweetHashTracerContentProvider.class.getSimpleName();
	private TweetHashTracerDbHelper mOpenHelper;

	private static UriMatcher buildUriMatcher() {
		// I know what you're thinking. Why create a UriMatcher when you can use
		// regular
		// expressions instead? Because you're not crazy, that's why.

		// All paths added to the UriMatcher have a corresponding code to return
		// when a match is
		// found. The code passed into the constructor represents the code to
		// return for the root
		// URI. It's common to use NO_MATCH as the code for this case.
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = TweetHashTracerContract.CONTENT_AUTHORITY;

		// For each type of URI you want to add, create a corresponding code.
		matcher.addURI(authority, TweetHashTracerContract.PATH_TWEET, TWEET);
		matcher.addURI(authority, TweetHashTracerContract.PATH_TWEET + "/#",TWEET_WITH_ID);
		matcher.addURI(authority, TweetHashTracerContract.PATH_TWEET + "/*",TWEET_WITH_HASHTAG);
		matcher.addURI(authority, TweetHashTracerContract.PATH_TWEET + "/*/*",TWEET_WITH_HASHTAG_AND_DATE);

		matcher.addURI(authority, TweetHashTracerContract.PATH_TWEET_SEARCH + "/*/*", TWEET_WITH_HASHTAG_SEARCH);

		matcher.addURI(authority, TweetHashTracerContract.PATH_TWEETFAV, TWEETFAV);
		matcher.addURI(authority, TweetHashTracerContract.PATH_TWEETFAV + "/*",TWEETFAV_WITH_HASHTAG);
		matcher.addURI(authority, TweetHashTracerContract.PATH_TWEETFAV + "/*/*",TWEETFAV_WITH_HASHTAG_AND_DATE);

		matcher.addURI(authority, TweetHashTracerContract.PATH_HASHTAG, HASHTAG);
		matcher.addURI(authority, TweetHashTracerContract.PATH_HASHTAG + "/#",HASHTAG_ID);

		return matcher;
	}

	private Cursor getTweetByHashTagSetting(Uri uri, String[] projection,String sortOrder)
	{
		String hashTagSetting = TweetHashTracerContract.TweetEntry.getHashTagSettingFromUri(uri);
		String startDate = TweetHashTracerContract.TweetEntry.getStartDateFromUri(uri);

		String[] selectionArgs;
		String selection;

		if (startDate == null)
		{
			selection = sHashTagSettingSelection;
			selectionArgs = new String[] { hashTagSetting };
		}
		else
		{
			selectionArgs = new String[] { hashTagSetting, startDate };
			selection = sHashTagSettingWithStartDateSelection;
		}

		return sTweetByHashTagSettingQueryBuilder.query(
				mOpenHelper.getReadableDatabase(),
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder);
	}

	private Cursor getTweetByHashTagSettingAndDate(Uri uri, String[] projection, String sortOrder)
	{
		String hashTagSetting = TweetHashTracerContract.TweetEntry.getHashTagSettingFromUri(uri);
		String date = TweetHashTracerContract.TweetEntry.getDateFromUri(uri);

		return sTweetByHashTagSettingQueryBuilder.query(
				mOpenHelper.getReadableDatabase(),
				projection,
				sHashTagSettingAndDaySelection,
				new String[] {hashTagSetting, date },
				null,
				null,
				sortOrder);
	}

	private Cursor getTweetById(Uri uri, String[] projection, String sortOrder)
	{
		String id = TweetHashTracerContract.TweetEntry.getIdFromUri(uri);

		String[] selectionArgs = new String[] { id };
		String selection = sIdSelection;

		return sTweetByHashTagSettingQueryBuilder.query(
				mOpenHelper.getReadableDatabase(),
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder);
	}

	private Cursor getTweetByHashTagSettingAndId(Uri uri, String[] projection, String sortOrder)
	{
		String hashTagSetting = TweetHashTracerContract.TweetEntry.getHashTagSettingFromUri(uri);
		String id = TweetHashTracerContract.TweetEntry.getIdFromUri(uri);

		return sTweetByHashTagSettingQueryBuilder.query(
				mOpenHelper.getReadableDatabase(),
				projection,
				sHashTagSettingAndIdSelection,
				new String[] {hashTagSetting, id },
				null,
				null,
				sortOrder);
	}

	private Cursor getTweetByHashTagSettingForSearch(Uri uri, String[] projection, String sortOrder)
	{
		String hashTagSetting = TweetHashTracerContract.TweetEntry.getHashTagSettingFromUri(uri);
		String searchKey = TweetHashTracerContract.TweetEntry.getSearchKeyFromUri(uri);

		Log.w(LOG_TAG,""+hashTagSetting);
		Log.w(LOG_TAG,""+searchKey);

		return sTweetByHashTagSettingQueryBuilder.query(
				mOpenHelper.getReadableDatabase(),
				projection,
				sHashTagSettingAndSearchKeySelection,
				new String[] {hashTagSetting, searchKey, searchKey, searchKey },
				null,
				null,
				sortOrder);
	}

	private Cursor getTweetFavByHashTagSetting(Uri uri, String[] projection, String sortOrder)
	{
		String hashTagSetting = TweetHashTracerContract.TweetFavEntry.getHashTagSettingFromUri(uri);
		String startDate = TweetHashTracerContract.TweetFavEntry.getStartDateFromUri(uri);

		String[] selectionArgs;
		String selection;

		if (startDate == null)
		{
			selection = sHashTagSettingSelection;
			selectionArgs = new String[] { hashTagSetting };
		}
		else
		{
			selectionArgs = new String[] { hashTagSetting, startDate };
			selection = sHashTagFavSettingWithStartDateSelection;
		}

		return sTweetFavByHashTagSettingQueryBuilder.query(
				mOpenHelper.getReadableDatabase(),
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder);
	}

	private Cursor getTweetFavByHashTagSettingAndDate(Uri uri, String[] projection, String sortOrder)
    {
		String hashTraceSetting = TweetHashTracerContract.TweetFavEntry.getHashTagSettingFromUri(uri);
		String date = TweetHashTracerContract.TweetFavEntry.getDateFromUri(uri);

		return sTweetFavByHashTagSettingQueryBuilder.query(
				mOpenHelper.getReadableDatabase(),
				projection,
				sHashTagFavSettingAndDaySelection,
				new String[] {hashTraceSetting, date },
				null,
				null,
				sortOrder);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new TweetHashTracerDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		/*final String DEFAULT_SORT_ORDER = TweetEntry._ID +" ASC";

		if (TextUtils.isEmpty(sortOrder))
		{
			sortOrder = DEFAULT_SORT_ORDER;
		}*/

		// Here's the switch statement that, given a URI, will determine what
		// kind of request it is, and query the database accordingly.
		Cursor retCursor;
		switch (sUriMatcher.match(uri))
		{
		// "tweet/*/*"
		case TWEET_WITH_HASHTAG_AND_DATE:
		{
			retCursor = getTweetByHashTagSettingAndDate(uri, projection, sortOrder);
			break;
		}
	    // "tweet/#"
		case TWEET_WITH_ID:
		{
			retCursor = getTweetById(uri, projection, sortOrder);
			break;
		}
		// "tweet/*"
		case TWEET_WITH_HASHTAG:
		{
			retCursor = getTweetByHashTagSetting(uri, projection, sortOrder);
			break;
		}
		// "search/*/*"
		case TWEET_WITH_HASHTAG_SEARCH:
		{
			retCursor = getTweetByHashTagSettingForSearch(uri, projection, sortOrder);
			Log.w(LOG_TAG,""+retCursor.getCount());
			break;
		}
		// "tweet"
		case TWEET:
		{
			retCursor = mOpenHelper.getReadableDatabase().query(
					TweetHashTracerContract.TweetEntry.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder);
			break;
		}
		// "tweet/*/*"
		case TWEETFAV_WITH_HASHTAG_AND_DATE:
		{
			retCursor = getTweetFavByHashTagSettingAndDate(uri, projection,sortOrder);
			break;
		}

		// "tweet/*"
		case TWEETFAV_WITH_HASHTAG:
		{
			retCursor = getTweetFavByHashTagSetting(uri, projection, sortOrder);
			break;
		}
		// "tweetfav"
		case TWEETFAV:
		{
			retCursor = mOpenHelper.getReadableDatabase().query(
					TweetHashTracerContract.TweetFavEntry.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder);
			break;
		}
		// "hashtag/*"
		case HASHTAG_ID:
		{
			retCursor = mOpenHelper.getReadableDatabase().query(
					TweetHashTracerContract.HashTagEntry.TABLE_NAME,
					projection,
					TweetHashTracerContract.HashTagEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
					null,
					null,
					null,
					sortOrder);
			break;
		}
		// "hashtag"
		case HASHTAG:
		{
			retCursor = mOpenHelper.getReadableDatabase().query(
					TweetHashTracerContract.HashTagEntry.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder);
			break;
		}

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}



	@Override
	public String getType(Uri uri)
	{
		// Use the Uri Matcher to determine what kind of URI this is.
		final int match = sUriMatcher.match(uri);

		switch (match)
		{
			case TWEET_WITH_HASHTAG_AND_DATE:
				return TweetHashTracerContract.TweetEntry.CONTENT_ITEM_TYPE;
			case TWEET_WITH_ID:
				return TweetHashTracerContract.TweetEntry.CONTENT_ITEM_TYPE;
			case TWEET_WITH_HASHTAG:
				return TweetHashTracerContract.TweetEntry.CONTENT_TYPE;
			case TWEET_WITH_HASHTAG_SEARCH:
				return TweetHashTracerContract.TweetEntry.CONTENT_TYPE;
			case TWEET:
				return TweetHashTracerContract.TweetEntry.CONTENT_TYPE;
			case TWEETFAV:
				return TweetHashTracerContract.TweetFavEntry.CONTENT_TYPE;
			case HASHTAG:
				return TweetHashTracerContract.HashTagEntry.CONTENT_TYPE;
			case HASHTAG_ID:
				return TweetHashTracerContract.HashTagEntry.CONTENT_ITEM_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;

		switch (match)
		{
		case TWEET:
		{
			long _id = db.insert(TweetHashTracerContract.TweetEntry.TABLE_NAME,null, values);
			if (_id > 0)
				returnUri = TweetHashTracerContract.TweetEntry.buildTweetUri(_id);
			else
				throw new android.database.SQLException("Failed to insert row into " + uri);
			break;
		}
		case TWEETFAV:
		{
			long _id = db.insert(TweetHashTracerContract.TweetFavEntry.TABLE_NAME,null, values);
			if (_id > 0)
				returnUri = TweetHashTracerContract.TweetFavEntry.buildTweetFavUri(_id);
			else
				throw new android.database.SQLException("Failed to insert row into " + uri);
			break;
		}
		case HASHTAG:
		{
			long _id = db.insert(TweetHashTracerContract.HashTagEntry.TABLE_NAME, null,values);
			if (_id > 0)
				returnUri = TweetHashTracerContract.HashTagEntry.buildHashTagUri(_id);
			else
				throw new android.database.SQLException("Failed to insert row into " + uri);
			break;
		}
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsDeleted;
		switch (match)
		{
		case TWEET:
			rowsDeleted = db.delete(TweetHashTracerContract.TweetEntry.TABLE_NAME, selection,selectionArgs);
			break;
		case TWEETFAV:
			rowsDeleted = db.delete(TweetHashTracerContract.TweetFavEntry.TABLE_NAME, selection,selectionArgs);
			break;
		case HASHTAG:
			rowsDeleted = db.delete(TweetHashTracerContract.HashTagEntry.TABLE_NAME, selection,selectionArgs);
			break;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		// Because a null deletes all rows
		if (selection == null || rowsDeleted != 0)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs)
	{
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsUpdated;

		switch (match)
		{
		case TWEET:
			rowsUpdated = db.update(TweetHashTracerContract.TweetEntry.TABLE_NAME, values,selection, selectionArgs);
			break;
		case TWEETFAV:
			rowsUpdated = db.update(TweetHashTracerContract.TweetFavEntry.TABLE_NAME, values,selection, selectionArgs);
			break;
		case HASHTAG:
			rowsUpdated = db.update(TweetHashTracerContract.HashTagEntry.TABLE_NAME, values,selection, selectionArgs);
			break;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		if (rowsUpdated != 0)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsUpdated;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values)
	{
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);

		switch (match)
		{
		case TWEET:
			db.beginTransaction();
			int returnCount = 0;

			try {
				for (ContentValues value : values)
				{
					long _id = db.insert(TweetHashTracerContract.TweetEntry.TABLE_NAME,null, value);
					if (_id != -1)
					{
						returnCount++;
					}
				}
				db.setTransactionSuccessful();
			}
			finally
			{
				db.endTransaction();
			}
			getContext().getContentResolver().notifyChange(uri, null);

			return returnCount;
		default:
			return super.bulkInsert(uri, values);
		}
	}
}
