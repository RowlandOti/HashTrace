package com.rowland.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.rowland.utility.EDbDateLimit;
import com.rowland.utility.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Rowland
 *
 */
public class TweetHashTracerContract {

	// The "Content authority" is a name for the entire content provider,
	// similar to the relationship between a domain name and its website. A convenient string
	// to use for the content authority is the package name for the app,
	// which is guaranteed to be unique on the device.
	public static final String CONTENT_AUTHORITY = "com.rowland.hashtrace.app";

	// Use CONTENT_AUTHORITY to create the base of all URI's which apps will use
	// to contact the content provider.
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	// Possible paths (appended to base content URI for possible URI's)
	// For instance, content://com.example.android.sunshine.app/weather/
	// is a valid path for looking at weather data.
	// content://com.example.android.hashtrace.app/givemeroot/ will fail,
	// as the ContentProvider hasn't been given any information on what to do
	// with "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
	public static final String PATH_HASHTAG = "hashtag";
	public static final String PATH_TWEET = "tweet";
	public static final String PATH_TWEETFAV = "tweetfav";

	// Format used for storing dates in the database. ALso used for converting
	// those strings back into date objects for comparison/processing.
	public static final String DATE_FORMAT = "yyyyMMddHHmmss";

	/**
	 * Converts Date class to a string representation, used for easy comparison
	 * and database lookup.
	 *
	 * @param date
	 *            The input date
	 * @param eDbDateLimit
	 * @return a DB-friendly representation of the date, using the format
	 *         defined in DATE_FORMAT.
	 */
	public static String getDbDateString(Date date, EDbDateLimit eDbDateLimit)
	{
		// Because the API returns a unix timestamp (measured in seconds),
		// it must be converted to milliseconds in order to be converted to
		// valid date.

		String dbDate = Utility.getDbDateLimit(date, eDbDateLimit);

		Log.d("DATECHECK", dbDate+"");
		return dbDate;
	}


	/**
	 * Converts a dateText to a long Unix time representation
	 *
	 * @param dateText the input date string
	 *
	 * @return the Date object
	 */
	public static Date getDateFromDb(String dateText)
	{
		SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
		try {
			return dbDateFormat.parse(dateText);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* Inner class that defines the table contents of the location table */
	public static final class HashTagEntry implements BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HASHTAG).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_HASHTAG;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"+ CONTENT_AUTHORITY + "/" + PATH_HASHTAG;

		// Table name
		public static final String TABLE_NAME = "hashtag";

		// The hashtag setting string is what will be sent to twitter API
		// as the hashtag query.
		public static final String COLUMN_HASHTAG_SETTING = "hashtag_setting";

		// Human readable hashtag string, provided by the API. Because for
		// styling,
		// "HashTag" is more recognizable than "#HashTag".
		public static final String COLUMN_HASHTAG_NAME = "hashtag_name";

		public static Uri buildHashTagUri(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}

	/* Inner class that defines the table contents of the weather table */
	public static final class TweetEntry implements BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TWEET).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"+ CONTENT_AUTHORITY + "/" + PATH_TWEET;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"+ CONTENT_AUTHORITY + "/" + PATH_TWEET;

		public static final String TABLE_NAME = "tweet";

		// Column with the foreign key into the hashtag table.
		public static final String COLUMN_HASHTAG_KEY = "hashtag_id";
		// Weather id as returned by API, to identify the icon to be used
		public static final String COLUMN_TWEET_ID = "tweet_id";
		// message of the tweet, as provided by API.
		public static final String COLUMN_TWEET_TEXT = "tweet_text";
		// date of posting the message
		public static final String COLUMN_TWEET_TEXT_DATE = "tweet_text_date";
		// No of retweets
		public static final String COLUMN_TWEET_TEXT_RETWEET_COUNT = "tweet_text_retweet_count";
		//No of favourites
		public static final String COLUMN_TWEET_TEXT_FAVOURITE_COUNT = "tweet_text_favourite_count";
		//No of contributors
		public static final String COLUMN_TWEET_TEXT_MENTIONS_COUNT = "tweet_text_mentions_count";
		// username of the tweet user, as provided by API.
		public static final String COLUMN_TWEET_USERNAME = "tweet_username";
		// profile image url
		public static final String COLUMN_TWEET_USERNAME_IMAGE_URL = "tweet_username_image_url";
        // User location
		public static final String COLUMN_TWEET_USERNAME_LOCATION = "tweet_username_location";
		// User Description
		public static final String COLUMN_TWEET_USERNAME_DESCRIPTION = "tweet_username_description";
        // Is Tweet Favourite
		public static final String COLUMN_TWEET_FAVOURITED_STATE = "tweet_favoured_state";


		public static Uri buildTweetUri(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static Uri buildTweetHashTag(String hashTagSetting)
		{
			return CONTENT_URI.buildUpon().appendPath(hashTagSetting).build();
		}

		public static Uri buildTweetHashTagWithStartDate(String hashTagSetting, String startDate)
		{
			return CONTENT_URI.buildUpon().appendPath(hashTagSetting).appendQueryParameter(COLUMN_TWEET_TEXT_DATE, startDate).build();
		}

		public static Uri buildTweetHashTagWithDate(String hashTagSetting, String date)
		{
			return CONTENT_URI.buildUpon().appendPath(hashTagSetting).appendPath(date).build();
		}
		public static Uri buildTweetHashTagWithIDUri(String mTweetID)
		{
			return CONTENT_URI.buildUpon().appendPath(mTweetID).build();
		}

		public static String getHashTagSettingFromUri(Uri uri)
		{
			return uri.getPathSegments().get(1);
		}

		public static String getDateFromUri(Uri uri)
		{
			return uri.getPathSegments().get(2);
		}

		public static String getStartDateFromUri(Uri uri)
		{
			return uri.getQueryParameter(COLUMN_TWEET_TEXT_DATE);
		}
		public static String getIdFromUri(Uri uri)
		{
			return uri.getPathSegments().get(1);
		}
	}

	/* Inner class that defines the table contents of the location table */
	public static final class TweetFavEntry implements BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TWEETFAV).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TWEETFAV;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"+ CONTENT_AUTHORITY + "/" + PATH_TWEETFAV;

		// Table name
		public static final String TABLE_NAME = "tweetfav";

		// Column with the foreign key into the hashtag table.
		public static final String COLUMN_HASHTAG_KEY = "hashtag_id";
		// Weather id as returned by API, to identify the icon to be used
		public static final String COLUMN_TWEETFAV_ID = "tweetfav_id";
		// message of the tweet, as provided by API.
		public static final String COLUMN_TWEETFAV_TEXT = "tweetfav_text";
		// date of posting the message
		public static final String COLUMN_TWEETFAV_TEXT_DATE = "tweetfav_text_date";
		// No of retweets
		public static final String COLUMN_TWEETFAV_TEXT_RETWEET_COUNT = "tweetfav_text_retweet_count";
		//No of favourites
		public static final String COLUMN_TWEETFAV_TEXT_FAVOURITE_COUNT = "tweetfav_text_favourite_count";
		//No of contributors
		public static final String COLUMN_TWEETFAV_TEXT_MENTIONS_COUNT = "tweet_textfav_mentions_count";
		// username of the tweet user, as provided by API.
		public static final String COLUMN_TWEETFAV_USERNAME = "tweetfav_username";
		// profile image url
		public static final String COLUMN_TWEETFAV_USERNAME_IMAGE_URL = "tweetfav_username_image_url";
		// User location
		public static final String COLUMN_TWEETFAV_USERNAME_LOCATION = "tweetfav_username_location";
		// User Description
		public static final String COLUMN_TWEETFAV_USERNAME_DESCRIPTION = "tweetfav_username_description";


		public static Uri buildTweetFavUri(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static Uri buildTweetFavHashTagWithStartDate(String hashTagSetting, String startDate)
		{
			return CONTENT_URI.buildUpon().appendPath(hashTagSetting).appendQueryParameter(COLUMN_TWEETFAV_TEXT_DATE, startDate).build();
		}

		public static Uri buildTweetFavHashTagWithDate(String hashTagSetting, String date)
		{
			return CONTENT_URI.buildUpon().appendPath(hashTagSetting).appendPath(date).build();
		}

		public static String getHashTagSettingFromUri(Uri uri)
		{
			return uri.getPathSegments().get(1);
		}

		public static String getDateFromUri(Uri uri)
		{
			return uri.getPathSegments().get(2);
		}

		public static String getStartDateFromUri(Uri uri)
		{
			return uri.getQueryParameter(COLUMN_TWEETFAV_TEXT_DATE);
		}
	}

}
