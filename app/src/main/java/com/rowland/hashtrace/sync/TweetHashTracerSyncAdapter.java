/*
 *
 *  * Copyright (c) 2016 Oti Rowland
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.rowland.hashtrace.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.rowland.hashtrace.data.provider.TweetHashTracerContract;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.HashTagEntry;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.TweetEntry;
import com.rowland.hashtrace.ui.activities.MainActivity;
import com.rowland.hashtrace.R;
import com.rowland.hashtrace.models.Tweet;
import com.rowland.hashtrace.utility.EDbDateLimit;
import com.rowland.hashtrace.utility.Utility;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * @author Rowland
 *
 */
public class TweetHashTracerSyncAdapter extends AbstractThreadedSyncAdapter {

	// Interval at which to sync with the tweet, in milliseconds.
	// 60 seconds (1 minute) * 180 = 3 hours
	public static final int SYNC_INTERVAL = 60 * 180;
	public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
	public static final int HASHTAG_STATUS_OK = 0;
	public static final int HASHTAG_SERVER_DOWN = 1;
	public static final int HASHTAG_STATUS_SERVER_INVALID = 2;
	public static final int HASHTAG_STATUS_UNKNOWN = 3;
	private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 6;
	private static final int TWEET_NOTIFICATION_ID = 3004;
	private static final String[] NOTIFY_TWEET_PROJECTION = new String[] {
			TweetEntry.COLUMN_TWEET_ID,
			TweetEntry.COLUMN_TWEET_TEXT,
			TweetEntry.COLUMN_TWEET_TEXT_DATE,
			TweetEntry.COLUMN_TWEET_USERNAME,
			TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL };
	// these indices must match the projection
	private static final int INDEX_TWEET_ID = 0;
	private static final int INDEX_TWEET_TEXT = 1;
	private static final int INDEX_TWEET_TEXT_DATE = 2;
	private static final int INDEX_TWEET_USER_NAME = 3;
	private static final int INDEX_TWEET_USER_NAME_IMAGE_URL = 4;
	public final String LOG_TAG = TweetHashTracerSyncAdapter.class.getSimpleName();
	private Twitter mTwitter;

	public TweetHashTracerSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	/**
	 * Helper method to schedule the sync adapter periodic execution
	 */
	public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
		Account account = getSyncAccount(context);
		String authority = context.getString(R.string.content_authority);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// we can enable inexact timers in our periodic sync
			SyncRequest request = new SyncRequest.Builder().syncPeriodic(syncInterval, flexTime).setSyncAdapter(account, authority).build();
			ContentResolver.requestSync(request);
		} else {
			ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
		}
	}

	/**
	 * Helper method to have the sync adapter sync immediately
	 *
	 * @param context The context used to access the account service
	 */
	public static void syncImmediately(Context context) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
	}

	/**
	 * Helper method to get the fake account to be used with SyncAdapter, or
	 * make a new one if the fake account doesn't exist yet. If we make a new
	 * account, we call the onAccountCreated method so we can initialize things.
	 *
	 * @param context The context used to access the account service
	 * @return a fake account.
	 */
	public static Account getSyncAccount(Context context) {
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

		// Create the account type and default account
		Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

		// If the password doesn't exist, the account doesn't exist
		if (null == accountManager.getPassword(newAccount)) {

			/*
			 * Add the account and account type, no password or user data If
			 * successful, return the Account object, otherwise report an error.
			 */
			if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
				return null;
			}
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call
			 * ContentResolver.setIsSyncable(account, AUTHORITY, 1) here.
			 */

			onAccountCreated(newAccount, context);
		}

		return newAccount;
	}

	private static void onAccountCreated(Account newAccount, Context context) {
		/*
		 * Since we've created an account
		 */
		TweetHashTracerSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

		/*
		 * Without calling setSyncAutomatically, our periodic sync will not be
		 * enabled.
		 */
		ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

		/*
		 * Finally, let's do a sync to get things started
		 */
		syncImmediately(context);
	}

	public static void initializeSyncAdapter(Context context) {
		getSyncAccount(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
	{
		Log.d(LOG_TAG, "Starting sync");
		// Getting the hashtag to send to the API
		String hashTagQuery = Utility.getPreferredHashTag(getContext());

		long hashTagId = addHashTag(hashTagQuery, hashTagQuery);

		ArrayList<Tweet> tweetArrayList = new ArrayList<Tweet>();
		List<Status> statuses = new ArrayList<Status>();

		try {

			mTwitter = Utility.getTwitter();

			statuses = mTwitter.search(new Query(hashTagQuery)).getTweets();

			for (Status s : statuses)
			{
				Tweet tweet = Utility.createTweet(s);
				tweetArrayList.add(tweet);
			}

		}

		catch (TwitterException e)

		{
			e.printStackTrace();
		}

		try {

		// Insert the new tweet information into the database
		Vector<ContentValues> cVVector = new Vector<ContentValues>(tweetArrayList.size());

		for (int i = 0; i < tweetArrayList.size(); i++)
		{
			// These are the values that will be collected.
			Tweet tweet = tweetArrayList.get(i);
			long tweet_id = tweet.getTweetId();
			String tweet_text = tweet.getTweetText();
			Date tweet_text_date = tweet.getTweetTextDate();
			int tweet_text_retweet_count = tweet.getTweetRetweetCount();
			int tweet_text_favourite_count = tweet.getTweetFavouriteCount();
			int tweet_text_mentions_count = tweet.getTweetMentionsCount();
			String user_name = tweet.getUserName();
			String user_image_url = tweet.getUserImageUrl();
			String user_cover_url = tweet.getUserCoverUrl();
			String user_location = tweet.getUserLocation();
			String user_description = tweet.getUserDescription();

			ContentValues tweetValues = new ContentValues();

			tweetValues.put(TweetEntry.COLUMN_HASHTAG_KEY, hashTagId);
			tweetValues.put(TweetEntry.COLUMN_TWEET_TEXT_DATE, TweetHashTracerContract.getDbDateString(tweet_text_date, EDbDateLimit.DATE_FORMAT_NOW_LIMIT));
			tweetValues.put(TweetEntry.COLUMN_TWEET_ID, tweet_id);
			tweetValues.put(TweetEntry.COLUMN_TWEET_TEXT, tweet_text);
			tweetValues.put(TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT, tweet_text_retweet_count);
			tweetValues.put(TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT, tweet_text_favourite_count);
			tweetValues.put(TweetEntry.COLUMN_TWEET_TEXT_MENTIONS_COUNT, tweet_text_mentions_count);
			tweetValues.put(TweetEntry.COLUMN_TWEET_USERNAME, user_name);
			tweetValues.put(TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL, user_image_url);
			tweetValues.put(TweetEntry.COLUMN_TWEET_USERNAME_COVER_URL, user_cover_url);
			tweetValues.put(TweetEntry.COLUMN_TWEET_USERNAME_LOCATION, user_location);
			tweetValues.put(TweetEntry.COLUMN_TWEET_USERNAME_DESCRIPTION, user_description);

			cVVector.add(tweetValues);
		}

		if (cVVector.size() > 0)
		{
			ContentValues[] cvArray = new ContentValues[cVVector.size()];
			cVVector.toArray(cvArray);
			int insertNo = getContext().getContentResolver().bulkInsert(TweetEntry.CONTENT_URI, cvArray);

			// Get's a calendar object with the current time.
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -3); // Signifies 3 days ago's date
			String previousDate = TweetHashTracerContract.getDbDateString(cal.getTime(),EDbDateLimit.DATE_FORMAT_NOW_LIMIT);
			getContext().getContentResolver().delete(TweetEntry.CONTENT_URI, TweetEntry.COLUMN_TWEET_TEXT_DATE + " <= ?", new String[] { previousDate });

			notifyWeather();

		}
		Log.d(LOG_TAG, "FetchTweetTask Complete. " + cVVector.size() + " Inserted");


		} catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
		return;
	}

	/**
	 * Helper method to handle insertion of a new hashtag in the tweet database.
	 *
	 * @param hashTagSetting
	 *            The hashtag string used to request updates from the server.
	 * @return the row ID of the added location.
	 */
	private long addHashTag(String hashTagSetting, String hashTagName)
	{
		long hashTagId;

		Log.v(LOG_TAG, "inserting " + hashTagSetting + ", with name: " + hashTagName);

		// First, check if the location with this city name exists in the db
		Cursor hashTagCursor = getContext().getContentResolver().query(
				TweetHashTracerContract.HashTagEntry.CONTENT_URI,
				new String[]{HashTagEntry._ID},
				HashTagEntry.COLUMN_HASHTAG_SETTING + " = ?",
				new String[]{hashTagSetting}, null);

		if (hashTagCursor.moveToFirst())
		{
			int hashTagIdIndex = hashTagCursor.getColumnIndex(HashTagEntry._ID);
			hashTagId = hashTagCursor.getLong(hashTagIdIndex);
		}
		else
		{
			// Now that the content provider is set up, inserting rows of data
			// is pretty simple.
			// First create a ContentValues object to hold the data you want to
			// insert.
			ContentValues hashTagValues = new ContentValues();

			// Then add the data, along with the corresponding name of the data
			// type,
			// so the content provider knows what kind of value is being
			// inserted.
			hashTagValues.put(HashTagEntry.COLUMN_HASHTAG_NAME, hashTagName);
			hashTagValues.put(HashTagEntry.COLUMN_HASHTAG_SETTING, hashTagSetting);

			// Finally, insert location data into the database.
			Uri insertedUri = getContext().getContentResolver().insert(TweetHashTracerContract.HashTagEntry.CONTENT_URI, hashTagValues);

			// The resulting URI contains the ID for the row. Extract the
			// locationId from the Uri.
			hashTagId = ContentUris.parseId(insertedUri);
		}

		// Wait, that worked? Yes!
		return hashTagId;
	}

	private void notifyWeather()
	{
		Context context = getContext();
		// checking the last update and notify if its the first of the day
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
		boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

		if (displayNotifications)
		{
			String lastNotificationKey = context.getString(R.string.pref_last_notification);
			long lastSync = prefs.getLong(lastNotificationKey, 0);

			if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS)
			{
				// Last sync was more than 1 day ago, let's send a notification with the tweet.
				String hashTagQuery = Utility.getPreferredHashTag(context);

				// Sort order:  Ascending, by date.
				//String sortOrder = TweetEntry.COLUMN_TWEET_TEXT_DATE + " ASC";

				Uri tweetUri = TweetEntry.buildTweetHashTagWithDate(hashTagQuery,TweetHashTracerContract.getDbDateString(new Date(), EDbDateLimit.DATE_FORMAT_MINUTE_LIMIT));

				// we'll query our contentProvider, as always
				Cursor cursor = context.getContentResolver().query(tweetUri,NOTIFY_TWEET_PROJECTION, null, null, null);

				if (cursor != null && cursor.moveToFirst())
				{
					int tweetId = cursor.getInt(INDEX_TWEET_ID);
					String tweet_text = cursor.getString(INDEX_TWEET_TEXT);
					String tweet_text_date = cursor.getString(INDEX_TWEET_TEXT_DATE);
					String user_name = cursor.getString(INDEX_TWEET_USER_NAME);
					String user_name_image_url = cursor.getString(INDEX_TWEET_USER_NAME_IMAGE_URL);

					int smallIconId = Utility.getSmallIconResourceForTweetNotification(tweetId);
					Bitmap largeIcon = Utility.getLargeIconResourceForTweetNotification(user_name_image_url, context);

					int notificationCount = 0;
					String title = context.getString(R.string.app_name);

					// Define the text of the tweet
					String contentText = String.format(context.getString(R.string.format_notification),tweet_text);

					// NotificationCompatBuilder is a very convenient way to backward-compatible
					// notifications. Just throw in some data.
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							getContext()).setSmallIcon(smallIconId)
							.setContentTitle(title).setContentText(contentText)
							.setLargeIcon(largeIcon)
							.setNumber(++notificationCount)
							.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
							.setAutoCancel(true);

					// Make something interesting happen when the user clicks on
					// the notification. In this case, opening the app is sufficient.
					Intent resultIntent = new Intent(context, MainActivity.class);

					// The stack builder object will contain an artificial back stack for the started Activity.
					// This ensures that navigating backward from the Activity leads out of your application to the Home screen.
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
					stackBuilder.addNextIntent(resultIntent);
					PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent(resultPendingIntent);

					NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
					// TWEET_NOTIFICATION_ID allows you to update the
					// notification later on.
					mNotificationManager.notify(TWEET_NOTIFICATION_ID,mBuilder.build());

					// refreshing last sync
					SharedPreferences.Editor editor = prefs.edit();
					editor.putLong(lastNotificationKey,System.currentTimeMillis());
					editor.commit();
				}

			}
		}

	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({HASHTAG_STATUS_OK, HASHTAG_SERVER_DOWN, HASHTAG_STATUS_SERVER_INVALID, HASHTAG_STATUS_UNKNOWN})
	public @interface HashTagStatus {}

}
