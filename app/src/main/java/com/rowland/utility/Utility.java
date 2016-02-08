package com.rowland.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;

import com.rowland.data.provider.TweetHashTracerContract;
import com.rowland.hashtrace.R;
import com.rowland.objects.Tweet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Rowland
 *
 */
public class Utility {

	// Format used for storing dates in the database. ALso used for converting
	// those strings back into date objects for comparison/processing.
	public static final String DATE_FORMAT = "yyyyMMddHHmmss";

	public static String getPreferredHashTag(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getString(context.getString(R.string.pref_hashtag_key),context.getString(R.string.pref_hashtag_default));
	}

	static String formatDate(String dateString)
	{
		Date date = TweetHashTracerContract.getDateFromDb(dateString);

		return DateFormat.getDateInstance().format(date);
	}

	public static String getDbDateLimit(Date date, EDbDateLimit DbDatelimit)
	{
		String FORMAT = "yyyyMMddHHmmss";
		SimpleDateFormat sdf;
		String dbDate;

		switch (DbDatelimit) {
		case DATE_FORMAT_NOW_LIMIT:
		{
			FORMAT = "yyyyMMddHHmmss";
			sdf = new SimpleDateFormat(FORMAT);
			dbDate = sdf.format(date);
			dbDate = dbDate + "";
			break;
		}
		case DATE_FORMAT_MINUTE_LIMIT:
		{
			FORMAT = "yyyyMMddHHmm";
			sdf = new SimpleDateFormat(FORMAT);
			dbDate = sdf.format(date);
			dbDate = dbDate + "00";
			break;
		}
		case DATE_FORMAT_HOUR_LIMIT:
		{
			FORMAT = "yyyyMMddHH";
			sdf = new SimpleDateFormat(FORMAT);
			dbDate = sdf.format(date);
			dbDate = dbDate + "0000";
			break;
		}
		case DATE_FORMAT_DAY_LIMIT:
		{
			FORMAT = "yyyyMMdd";
			sdf = new SimpleDateFormat(FORMAT);
			dbDate = sdf.format(date);
			dbDate = dbDate + "000000";
			break;
		}

		default:
			sdf = new SimpleDateFormat(FORMAT);
			dbDate = sdf.format(date);
			break;
		}
		return dbDate;

	}

	public static String getTimeAgo(String str_date)
	{
		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
		Date date = new Date();

		try
		{
			date = formatter.parse(str_date);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		long timeStamp = date.getTime();

		// Converting timestamp into "x ago" format
		String timeAgo = (String) DateUtils.getRelativeTimeSpanString(timeStamp,System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

		return timeAgo;
	}



	public static int getSmallIconResourceForTweetNotification(int tweetId) {
		// Return the appropriate notification icon
		return R.drawable.ic_launcher;
	}


	public static Bitmap getLargeIconResourceForTweetNotification(String user_name_image_url, Context context)
	{
		// Bitmap bitmap = getBitmap(user_name_image_url, context);
		Bitmap bitmap = getBitmapFromURL(user_name_image_url);
		return bitmap;
	}

/*	private static Bitmap getBitmap(String url, Context context) {

		File cacheDir = null;
		// Find the dir to save cached images
		String sdState = android.os.Environment.getExternalStorageState();

		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, "data/tweethashtrace");
		} else
			cacheDir = context.getCacheDir();

		if (!cacheDir.exists())
			cacheDir.mkdirs();

		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);

		// Is the bitmap in our cache?
		Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());

		if (bitmap != null)

			return bitmap;

		// Nope, have to download it
		try {
			bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
			// save bitmap to cache for later
			writeFile(bitmap, f);

			return bitmap;

		} catch (Exception ex) {
			ex.printStackTrace();

			return null;
		}
	}

	private static void writeFile(Bitmap bmp, File f)
	{
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
			}
		}
	}*/

	private static Bitmap getBitmapFromURL(String strURL)
	{
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();

			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**This method is used to convert dp values to pixel values.*/
	public static int convertDpToPixel(float dp, DisplayMetrics displayMetrics)
	{
		DisplayMetrics metrics = displayMetrics;
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	public static Twitter getTwitter()
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
		cb.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);
		cb.setOAuthAccessToken(Constants.TWITTER_ACCESS_TOKEN);
		cb.setOAuthAccessTokenSecret(Constants.TWITTER_TOKEN_SECRET).setHttpConnectionTimeout(100000);
		//cb.setUseSSL(true);

		/*
		 * if() { cb.setHttpProxyHost(httpProxyHost);
		 * cb.setHttpProxyPort(httpProxyPort);
		 * cb.setHttpProxyUser(httpProxyUser);
		 * cb.setHttpProxyPassword(httpProxyPassword);
		 *
		 * }
		 */

		return new TwitterFactory(cb.build()).getInstance();
	}

	public static Tweet createTweet(Status status)
	{
		User user = status.getUser();

		long tweet_id = status.getId();

		String tweet_text = status.getText();

		Date tweet_text_date = status.getCreatedAt();

		int tweet_retweet_count = status.getRetweetCount();

		int tweet_favourite_count = status.getFavoriteCount();

		int tweet_mentions_count = status.getUserMentionEntities().length;

		String user_name = user.getScreenName();

		String user_image_url = user.getBiggerProfileImageURL();

		String user_location = user.getLocation();

		String user_description = user.getDescription();

		Tweet tweet = new Tweet(tweet_id, tweet_text, tweet_text_date, tweet_retweet_count,tweet_favourite_count,tweet_mentions_count, user_name, user_image_url, user_location, user_description);

		return tweet;
	}
    /**
	 * Returns true if network is available or about to become available
	 * */
	public static boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

}
