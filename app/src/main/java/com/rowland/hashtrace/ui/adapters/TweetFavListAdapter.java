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

package com.rowland.hashtrace.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rowland.hashtrace.R;
import com.rowland.hashtrace.ui.fragments.subfragment.FavouriteListFragment;
import com.rowland.hashtrace.utility.ImageManager;
import com.rowland.hashtrace.utility.Utility;

/**
 * This adapter for the TweetFavouriteFragment
 *
 * @author Rowland
 *
 */
public class TweetFavListAdapter extends CursorAdapter {

	public ImageManager imageManager;

	public TweetFavListAdapter(Context context, Cursor c, int flags)
	{
		super(context, c, flags);

		imageManager = new ImageManager(context);
	}

	/**
	 * Cache of the children views for a tweet list item.
	 */
	public static class ViewHolder {

		public final TextView user_name;
		public final TextView tweet_text;
		public final TextView tweet_text_date;
		public final TextView tweet_text_retweet_count;
		public final ImageView image;
		public final ProgressBar progress;
		public final TextView tweet_favourite;

		public ViewHolder(View view)
		{
			user_name = (TextView) view.findViewById(R.id.user_name);
			tweet_text = (TextView) view.findViewById(R.id.tweet_text);
			tweet_text_date = (TextView) view.findViewById(R.id.tweet_text_date);
			tweet_text_retweet_count = (TextView) view.findViewById(R.id.tweet_text_retweet_count);
			image = (ImageView) view.findViewById(R.id.profile_pic);
			progress = (ProgressBar) view.findViewById(R.id.progress_bar);
			tweet_favourite = (TextView) view.findViewById(R.id.tweet_fav);
		}

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		ViewHolder viewHolder = (ViewHolder) view.getTag();

		// Read tweet_text from cursor Find TextView and set tweet text on it
		String tweet_text = cursor.getString(FavouriteListFragment.COL_TWEETFAV_TEXT);
		String tweet_text_trim = tweet_text.trim().replaceAll("\\s+", " ");
		viewHolder.tweet_text.setText(tweet_text_trim);

		// Read tweet_text_date from cursor Find TextView and set tweet text on it
		String tweet_text_date = Utility.getTimeAgo(cursor.getString(FavouriteListFragment.COL_TWEETFAV_TEXT_DATE));
		viewHolder.tweet_text_date.setText(tweet_text_date);

		// Read tweet_text_date from cursor Find TextView and set tweet text on it
		String tweet_text_retweet_count = cursor.getString(FavouriteListFragment.COL_TWEETFAV_TEXT_RETWEET_COUNT);
		viewHolder.tweet_text_retweet_count.setText(tweet_text_retweet_count + " Retweets");

		// Read user_name from cursor
		String username = cursor.getString(FavouriteListFragment.COL_TWEETFAV_USERNAME);
		viewHolder.user_name.setText(username);

		// Read tweet_image url from cursor and set Profile picture of the owner of tweet
		String image_url = cursor.getString(FavouriteListFragment.COL_TWEETFAV_USERNAME_IMAGE_URL);
		viewHolder.image.setTag(image_url);
		imageManager.displayImage(image_url, (Activity) context, viewHolder.image, viewHolder.progress);

		// Assert favourite status
		viewHolder.tweet_favourite.setText("Favoured");

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		int layoutId = -1;

		layoutId = R.layout.listitemfav;

		View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

		ViewHolder viewHolder = new ViewHolder(view);
		view.setTag(viewHolder);

		return view;
	}

}
