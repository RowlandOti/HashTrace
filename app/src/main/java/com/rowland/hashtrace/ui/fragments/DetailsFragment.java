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

package com.rowland.hashtrace.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rowland.hashtrace.data.provider.TweetHashTracerContract.HashTagEntry;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.TweetEntry;
import com.rowland.hashtrace.R;
import com.rowland.hashtrace.utility.ImageManager;
import com.rowland.hashtrace.utility.Utility;

public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String DATE_KEY = "tweet_text_date";
    public static final String  ID_KEY = "_id";
    private static final int DETAIL_LOADER = 3;
    private static final String HASHTAG_KEY = "hashtag";
    // Specify the columns we need for projection.
    private static final String[] TWEET_DETAILS_COLUMNS = {

            TweetEntry.TABLE_NAME + "." +
            TweetEntry._ID,                      			//0
            TweetEntry.COLUMN_HASHTAG_KEY,					//1
            TweetEntry.COLUMN_TWEET_ID,						//2
            TweetEntry.COLUMN_TWEET_TEXT,					//3
            TweetEntry.COLUMN_TWEET_TEXT_DATE,	            //4
            TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT,     //5
            TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT,   //6
            TweetEntry.COLUMN_TWEET_TEXT_MENTIONS_COUNT,    //7
            TweetEntry.COLUMN_TWEET_USERNAME,				//8
            TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL,		//9
            TweetEntry.COLUMN_TWEET_USERNAME_LOCATION,		//10
            TweetEntry.COLUMN_TWEET_USERNAME_DESCRIPTION,	//11
            TweetEntry.COLUMN_TWEET_FAVOURITED_STATE,		//12
            HashTagEntry.COLUMN_HASHTAG_NAME 				//13
    };
    private static DetailsFragment fragmentInstance = null;
    private final String LOG_TAG = DetailsFragment.class.getSimpleName();
    public ImageManager imageManager;
    private String mShareText;
    private String mHashTag;
    private int mTweetID;
    private TextView user_name;
    private ImageView user_profile_image;
    private ProgressBar progress;
    private TextView user_profile_description;
    private TextView user_location;
    private TextView tweet_text;
    private TextView tweet_text_date;
    private TextView tweet_text_retweet_count;
    private ImageView tweet_image_retweet;
    private TextView tweet_text_favourite_count;
    private ImageView tweet_image_fav;
    private TextView tweet_favourite;
    private TextView tweet_hash_tag;

    public DetailsFragment()
    {
        imageManager = new ImageManager(getActivity());
        setRetainInstance(true);
    }

    public static DetailsFragment newInstance(Bundle args)
    {
            fragmentInstance = new DetailsFragment();
            if(args != null)
            {
                fragmentInstance.setArguments(args);
            }
            return fragmentInstance;
       // }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments != null)
        {
           // mDateStr = arguments.getString(DetailsFragment.DATE_KEY);
            mTweetID = arguments.getInt(DetailsFragment.ID_KEY, 0);
            Log.w(LOG_TAG, "TWEETID:" +  mTweetID);
        }

        if (savedInstanceState != null)
        {
            mHashTag = savedInstanceState.getString(HASHTAG_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        user_name = (TextView) rootView.findViewById(R.id.user_profile_name);
        user_profile_image = (ImageView) rootView.findViewById(R.id.user_profile_pic);
        user_profile_description = (TextView) rootView.findViewById(R.id.user_profile_description);
        user_location = (TextView) rootView.findViewById(R.id.location_at);
        tweet_text = (TextView) rootView.findViewById(R.id.tweet_text);
        tweet_text_date = (TextView) rootView.findViewById(R.id.tweet_text_date);
        tweet_text_retweet_count = (TextView) rootView.findViewById(R.id.tweet_text_retweet_count);
        tweet_text_favourite_count = (TextView) rootView.findViewById(R.id.tweet_text_favourite_count);
        tweet_image_retweet = (ImageView) rootView.findViewById(R.id.icon_retweet);
        tweet_image_fav = (ImageView) rootView.findViewById(R.id.icon_favourite);
        progress = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        tweet_favourite = (TextView) rootView.findViewById(R.id.tweet_fav);
        tweet_hash_tag = (TextView) rootView.findViewById(R.id.tweet_hashtag);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
        {
            mHashTag = savedInstanceState.getString(HASHTAG_KEY);
        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailsFragment.ID_KEY))
        {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear old menu.
        menu.clear();
        // Inflate new menu.
        inflater.inflate(R.menu.menu_detailsfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.action_settings:
            {
                return true;
            }
            case R.id.action_share:
            {
                shareTweet();
                Log.w(LOG_TAG,"Called me Share");
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString(HASHTAG_KEY, mHashTag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailsFragment.ID_KEY) && mHashTag != null && !mHashTag.equals(Utility.getPreferredHashTag(getActivity())))
        {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        // Sort order:  Ascending, by date.
        String sortOrder = TweetEntry.COLUMN_TWEET_TEXT_DATE + " ASC";

        mHashTag = Utility.getPreferredHashTag(getActivity());
       // Uri tweetForHashTagUri = TweetEntry.buildTweetHashTagWithDate(mHashTag, mDateStr);
        Uri tweetForIdUri = TweetEntry.buildTweetUri(mTweetID);
        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
        CursorLoader cursorLoader = new CursorLoader(getActivity(), tweetForIdUri, TWEET_DETAILS_COLUMNS, null, null,sortOrder);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (data != null && data.moveToFirst())
        {
            // Read date from cursor and update views
            String username = data.getString(data.getColumnIndex(TweetEntry.COLUMN_TWEET_USERNAME));
            user_name.setText(username);

            String userprofile_image_url = data.getString(data.getColumnIndex(TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL));
            user_profile_image.setTag(userprofile_image_url);
            imageManager.displayImage(userprofile_image_url, getActivity(), user_profile_image, progress);

            String userprofile_description = data.getString(data.getColumnIndex(TweetEntry.COLUMN_TWEET_USERNAME_DESCRIPTION)).trim().replaceAll("\\s+", " ");
            user_profile_description.setText(userprofile_description);

            String userlocation = data.getString(data.getColumnIndex(TweetEntry.COLUMN_TWEET_USERNAME_LOCATION));
            user_location.setText(userlocation);

            String tweettext = data.getString(data.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT)).trim().replaceAll("\\s+", " ");
            mShareText = tweettext;
            tweet_text.setText(tweettext);

            String tweettext_date = Utility.getTimeAgo(data.getString(data.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT_DATE)));
            tweet_text_date.setText(tweettext_date);

            int tweettext_retweet_count = data.getInt(data.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT));
            tweet_text_retweet_count.setText(String.valueOf(tweettext_retweet_count));

            int tweettext_favorite_count = data.getInt(data.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT));
            tweet_text_favourite_count.setText(String.valueOf(tweettext_favorite_count));

            int tweetfav_state = data.getInt(data.getColumnIndex(TweetEntry.COLUMN_TWEET_FAVOURITED_STATE));
            if(data.getString(data.getColumnIndex(TweetEntry.COLUMN_TWEET_FAVOURITED_STATE)).equals("1"))
            {
                tweet_favourite.setVisibility(View.VISIBLE);
            }
            else
            {
               tweet_favourite.setVisibility(View.GONE);
            }

            String tweethash_tag = data.getString(data.getColumnIndex(HashTagEntry.COLUMN_HASHTAG_NAME));
            tweet_hash_tag.setText(tweethash_tag);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    private void shareTweet()
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, mShareText);
        startActivity(Intent.createChooser(share, "Share Tweet"));
    }
}