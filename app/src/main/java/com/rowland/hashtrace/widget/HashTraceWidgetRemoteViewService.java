/*
 *     Copyright (c) 2016 Oti Rowland
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *
 */

package com.rowland.hashtrace.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rowland.hashtrace.R;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract;
import com.rowland.hashtrace.utility.EDbDateLimit;
import com.rowland.hashtrace.utility.Utility;

import java.util.Date;

/**
 * Created by Rowland on 2/16/2016.
 */
public class HashTraceWidgetRemoteViewService extends android.widget.RemoteViewsService {

    // Logging Identifier for class
    public final String LOG_TAG = HashTraceWidgetRemoteViewService.class.getSimpleName();

    // Specify the columns we need.
    private static final String[] TWEET_COLUMNS = {
            //
            TweetHashTracerContract.TweetEntry.TABLE_NAME + "." +
                    TweetHashTracerContract.TweetEntry._ID,                                //0
            TweetHashTracerContract.TweetEntry.COLUMN_HASHTAG_KEY,                    //1
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_ID,                        //2
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT,                    //3
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_DATE,                //4
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT,     //5
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT,   //6
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_MENTIONS_COUNT,//7
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_USERNAME,                //8
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL,        //9
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_USERNAME_LOCATION,        //10
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_USERNAME_DESCRIPTION,    //11
            TweetHashTracerContract.TweetEntry.COLUMN_TWEET_FAVOURITED_STATE,        //12
            TweetHashTracerContract.HashTagEntry.COLUMN_HASHTAG_NAME                //13
    };

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // Clear calling identity
                final long idToken = Binder.clearCallingIdentity();

                // To only show current and future dates, get the String representation for today,
                // and filter the query to return tweets only for dates after or including today.
                String startDate = TweetHashTracerContract.getDbDateString(new Date(), EDbDateLimit.DATE_FORMAT_DAY_LIMIT);
                // Sort order: Ascending, by date.
                String sortOrder = TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_DATE + " DESC";
                String mHashTag = Utility.getPreferredHashTag(getApplicationContext());
                Uri tweetForHashTagUri = TweetHashTracerContract.TweetEntry.buildTweetHashTagWithStartDate(mHashTag, startDate);
                // Lets acquire the scores data
                data = getContentResolver().query(tweetForHashTagUri, TWEET_COLUMNS, null, null, sortOrder);
                // Restore calling identity for calls to use our process and permission
                Binder.restoreCallingIdentity(idToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                Log.v(LOG_TAG, "Widget Data Size: " + data.getCount());
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                // Data must not be null to continue
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                final RemoteViews views = new RemoteViews(getPackageName(), R.layout.wdgt_tweets_list_item);

                // Read tweet_text from cursor Find TextView and set tweet text on it
                String tweet_text = data.getString(data.getColumnIndex(TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT));
                String tweet_text_trim = tweet_text.trim().replaceAll("\\s+", " ");
                views.setTextViewText(R.id.tweet_text, tweet_text_trim);

                // Read tweet_text_date from cursor Find TextView and set tweet text on it
                String tweet_text_date = Utility.getTimeAgo(data.getString(data.getColumnIndex(TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_DATE)));
                views.setTextViewText(R.id.tweet_text_date, tweet_text_date);

                // Read tweet_text_date from cursor Find TextView and set tweet text on it
                String tweet_text_retweet_count = data.getString(data.getColumnIndex(TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT));
                views.setTextViewText(R.id.tweet_text_retweet_count, tweet_text_retweet_count);

                // Read tweet_text_date from cursor Find TextView and set tweet text on it
                String tweet_text_favourite_count = data.getString(data.getColumnIndex(TweetHashTracerContract.TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT));
                views.setTextViewText(R.id.tweet_text_favourite_count, tweet_text_favourite_count);

                // Read user_name from cursor
                String username = data.getString(data.getColumnIndex(TweetHashTracerContract.TweetEntry.COLUMN_TWEET_USERNAME));
                views.setTextViewText(R.id.user_name, username);
                // Set the Content Descriptions
                views.setContentDescription(R.id.tweet_text, tweet_text_trim);
                views.setContentDescription(R.id.tweet_text_date, tweet_text_date);
                views.setContentDescription(R.id.tweet_text_retweet_count, tweet_text_retweet_count);
                views.setContentDescription(R.id.tweet_text_favourite_count, tweet_text_favourite_count);
                views.setContentDescription(R.id.user_name, username);
                views.setContentDescription(R.id.tweet_fav, "Favoured");
                views.setContentDescription(R.id.icon_retweet, tweet_text_retweet_count);
                views.setContentDescription(R.id.icon_favourite, tweet_text_favourite_count);
                views.setContentDescription(R.id.profile_pic, username);

                // Read tweet_image url from cursor and set Profile picture of the owner of tweet
                final String image_url = data.getString(data.getColumnIndex(TweetHashTracerContract.TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL));

                // Create global configuration and initialize ImageLoader with this config
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(config);

                // Load image, decode it to Bitmap and return Bitmap to callback
                // ToDo: I am suprised callback is never called. Why?
                imageLoader.loadImage(image_url, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                        // Do whatever you want with Bitmap
                        if (loadedBitmap != null) {
                            views.setImageViewBitmap(R.id.profile_pic, loadedBitmap);
                            views.setViewVisibility(R.id.progress_bar, View.GONE);
                        }
                    }
                });


                views.setImageViewResource(R.id.icon_favourite, R.drawable.ic_action_favorites);
                views.setImageViewResource(R.id.icon_retweet, R.drawable.ic_action_retweet);

                if (data.getString(data.getColumnIndex(TweetHashTracerContract.TweetEntry.COLUMN_TWEET_FAVOURITED_STATE)).equals("1")) {
                    views.setViewVisibility(R.id.tweet_fav, View.VISIBLE);
                    // Assert favourite status
                    views.setTextViewText(R.id.tweet_fav, "Favoured");
                } else {
                    views.setViewVisibility(R.id.tweet_fav, View.GONE);
                }

                Log.v(LOG_TAG, "Widget Data Name: " + username);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.wdgt_tweets_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(data.getColumnIndex(TweetHashTracerContract.TweetEntry._ID));
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
