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

package com.rowland.hashtrace.ui.fragments.subfragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeMenuListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.rowland.hashtrace.R;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.HashTagEntry;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.TweetEntry;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.TweetFavEntry;
import com.rowland.hashtrace.sync.TweetHashTracerSyncAdapter;
import com.rowland.hashtrace.ui.adapters.TweetListAdapter;
import com.rowland.hashtrace.utility.EDbDateLimit;
import com.rowland.hashtrace.utility.Utility;

import java.util.Date;

/**
 * The Fragment for displaying the retrieved tweets
 *
 * @author Rowland
 */
public class TweetListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = TweetListFragment.class.getSimpleName();
    // These indices are tied to TWEET_COLUMNS and must match for projection
    public static final int COL_ID = 0;
    public static final int COL_HASHTAG_KEY = 1;
    public static final int COL_TWEET_ID = 2;
    public static final int COL_TWEET_TEXT = 3;
    public static final int COL_TWEET_TEXT_DATE = 4;
    public static final int COL_TWEET_TEXT_RETWEET_COUNT = 5;
    public static final int COL_TWEET_TEXT_FAVOURITE_COUNT = 6;
    public static final int COL_TWEET_TEXT_MENTIONS_COUNT = 7;
    public static final int COL_TWEET_USERNAME = 8;
    public static final int COL_TWEET_USERNAME_IMAGE_URL = 9;
    public static final int COL_TWEET_USERNAME_LOCATION = 10;
    public static final int COL_TWEET_USERNAME_DESCRIPTION = 11;
    public static final int COL_TWEET_TWEET_FAVOURITED_STATE = 12;
    public static final int COL_HASHTAG_NAME = 13;
    private static final String SELECTED_KEY = "selected_position";
    private static final int TWEETLIST_LOADER = 0;
    //TODO: migrate all projections to some Constants.java class
    // For the tweet view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] TWEET_COLUMNS = {
            // In this case the id needs to be fully qualified with a table
            // name, since the content provider joins the hastag & tweet tables in the background (both have an _id column)
            // On the one hand, that's annoying. On the other, you can search
            // the tweet table using the hashtag set by the user, which is only in the Hashtag table. So the convenience is worth it.
            TweetEntry.TABLE_NAME + "." +

                    TweetEntry._ID,                                //0
            TweetEntry.COLUMN_HASHTAG_KEY,                    //1
            TweetEntry.COLUMN_TWEET_ID,                        //2
            TweetEntry.COLUMN_TWEET_TEXT,                    //3
            TweetEntry.COLUMN_TWEET_TEXT_DATE,                //4
            TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT,     //5
            TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT,   //6
            TweetEntry.COLUMN_TWEET_TEXT_MENTIONS_COUNT,//7
            TweetEntry.COLUMN_TWEET_USERNAME,                //8
            TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL,        //9
            TweetEntry.COLUMN_TWEET_USERNAME_LOCATION,        //10
            TweetEntry.COLUMN_TWEET_USERNAME_DESCRIPTION,    //11
            TweetEntry.COLUMN_TWEET_FAVOURITED_STATE,        //12
            HashTagEntry.COLUMN_HASHTAG_NAME                //13
    };
    private PullToRefreshSwipeMenuListView mPullToRefreshListView;
    private TweetListAdapter mTweetListAdapter;
    private SwipeMenuCreator creator;
    private String mHashTag;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private Menu optionsMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear old menu.
        // menu.clear();
        // Inflate new menu.
        inflater.inflate(R.menu.tweet_list_fragment, menu);
        this.optionsMenu = menu;

		/*SearchManager SManager =  (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
		SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
		mSearchView.setSearchableInfo(SManager.getSearchableInfo(new ComponentName(getActivity().getApplicationContext(), SearchActivity.class)));
		mSearchView.setIconifiedByDefault(true);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                MenuItemCompat.setActionView(item, R.layout.layout_progress);
                //MenuItemCompat.expandActionView(item);
                updateTweet();
                //MenuItemCompat.collapseActionView(item);
                //MenuItemCompat.setActionView(item, null);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu.findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.layout_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The ArrayAdapter will take data from a source and use it to populate the ListView it's attached to.
        mTweetListAdapter = new TweetListAdapter(getActivity(), null, 0);
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "share" item
                SwipeMenuItem shareItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                shareItem.setBackground(new ColorDrawable(Color.rgb(211, 214, 219)));
                // set item width
                shareItem.setWidth(Utility.convertDpToPixel(100, getResources().getDisplayMetrics()));
                // set icon resource
                shareItem.setIcon(R.drawable.selector_swipemenuitem_share);
                // set item title
                shareItem.setTitle("Share");
                // set item title fontsize
                shareItem.setTitleSize(18);
                // set item title font color
                shareItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(shareItem);

                // create "favour" item
                SwipeMenuItem favourItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                favourItem.setBackground(new ColorDrawable(Color.rgb(211, 214, 219)));
                // set item width
                favourItem.setWidth(Utility.convertDpToPixel(100, getResources().getDisplayMetrics()));
                // set icon resource
                favourItem.setIcon(R.drawable.selector_swipemenuitem_favorite);
                // set item title
                favourItem.setTitle("Like");
                // set item title fontsize
                favourItem.setTitleSize(18);
                // set item title font color
                favourItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(favourItem);
            }
        };

        View rootView = inflater.inflate(R.layout.fragment_tweet_list, container, false);

        mListView = (SwipeMenuListView) rootView.findViewById(android.R.id.list);


        ViewGroup parent = (ViewGroup) mListView.getParent();
        int lvIndex = parent.indexOfChild(mListView);
        if (mListView.getParent() != null) {
            parent.removeViewAt(lvIndex);
        }

        mListView.setVisibility(View.GONE);
        mPullToRefreshListView = new PullToRefreshSwipeMenuListView(getActivity());
        mPullToRefreshListView.setLayoutParams(mListView.getLayoutParams());
        parent.addView(mPullToRefreshListView, lvIndex, mListView.getLayoutParams());
        View emptyView = rootView.findViewById(R.id.empty_text_view);
        mPullToRefreshListView.setEmptyView(emptyView);
        mPullToRefreshListView.setAdapter(mTweetListAdapter);
        mPullToRefreshListView.setMenuCreator(creator);
        mPullToRefreshListView.setVerticalFadingEdgeEnabled(true);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<SwipeMenuListView>() {

            @Override
            public void onRefresh(final PullToRefreshBase<SwipeMenuListView> refreshView) {
                // Do work to refresh the list here.
                updateTweet();
                // Call onRefreshComplete when the list has been refreshed.
                refreshView.onRefreshComplete();
                //mPullToRefreshListView.onRefreshComplete();
            }
        });
        //This somehow refuses to work , I really have no idea why
	/*	mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId)
			{

				Cursor cursor = mTweetListAdapter.getCursor();
				if (cursor != null && cursor.moveToPosition(position))
				{
					((onTweetItemSelectedCallback) getActivity()).onTweetItemSelected(cursor.getString(COL_TWEET_TEXT_DATE));
					Log.d("ROWSELECT", "" + rowId );

				}
				mPosition = position;
			}
		});*/
        mPullToRefreshListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long rowId) {
                // Do the onItemLongClick action
                mPullToRefreshListView.smoothOpenMenu(position);

                return true;
            }
        });
        mPullToRefreshListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // Share
                        shareTweet();
                        break;
                    case 1:
                        // Favourite
                        favouriteTweet();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<SwipeMenuListView> soundListener = new SoundPullEventListener<SwipeMenuListView>(getActivity());
        soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event_mp3);
        soundListener.addSoundEvent(State.RESET, R.raw.reset_sound_mp3);
        soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound_mp3);
        mPullToRefreshListView.setOnPullEventListener(soundListener);
        mPullToRefreshListView.setCloseInterpolator(new BounceInterpolator());

        // If there's instance state, mine it for useful information. The end-goal here is that
        // the user never knows that turning their device sideways does crazy lifecycle related things.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet. Actually
            // perform the swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TWEETLIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHashTag != null && !mHashTag.equals(Utility.getPreferredHashTag(getActivity()))) {
            getLoaderManager().restartLoader(TWEETLIST_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ListView lv, View view, int position, long rowID) {
        super.onListItemClick(lv, view, position, rowID);
        // Do the onItemClick action
        Cursor cursor = mTweetListAdapter.getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
            //((onTweetItemSelectedCallback) getActivity()).onTweetItemSelected((cursor.getInt(COL_ID)));
            ((onTweetItemSelectedCallback) getActivity()).onTweetItemSelected((int) rowID);
            //mPullToRefreshListView.getRefreshableView().setItemChecked(position, true);;
        }
        mPosition = position;
        Log.d("ROWSELECT", "" + rowID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created. This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return tweets only for dates after or including today.
        String startDate = TweetHashTracerContract.getDbDateString(new Date(), EDbDateLimit.DATE_FORMAT_DAY_LIMIT);

        // Sort order: Ascending, by date.
        String sortOrder = TweetEntry.COLUMN_TWEET_TEXT_DATE + " DESC";

        mHashTag = Utility.getPreferredHashTag(getActivity());
        Uri tweetForHashTagUri = TweetEntry.buildTweetHashTagWithStartDate(mHashTag, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity().getApplicationContext(), tweetForHashTagUri, TWEET_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTweetListAdapter.swapCursor(data);

        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired
            // position to restore to, do so now.
            mPullToRefreshListView.getRefreshableView().smoothScrollToPosition(mPosition);
        }
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTweetListAdapter.swapCursor(null);
    }

    private void updateTweet() {
        TweetHashTracerSyncAdapter.syncImmediately(getActivity());
    }

    private void shareTweet() {
        Cursor cursor = mTweetListAdapter.getCursor();
        String tweet_text = cursor.getString(COL_TWEET_TEXT);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, tweet_text);
        startActivity(Intent.createChooser(share, "Share Tweet"));
    }

    private void favouriteTweet() {
        Cursor cursor = mTweetListAdapter.getCursor();

        int _id = cursor.getInt(cursor.getColumnIndex(TweetEntry._ID));
        int hash_tag_id = cursor.getInt(COL_HASHTAG_KEY);
        long tweet_id = cursor.getLong(COL_TWEET_ID);
        String tweet_text = cursor.getString(COL_TWEET_TEXT);
        String tweet_text_date = cursor.getString(COL_TWEET_TEXT_DATE);
        String tweet_text_retweet_count = cursor.getString(COL_TWEET_TEXT_RETWEET_COUNT);
        String tweet_text_favourite_count = cursor.getString(COL_TWEET_TEXT_FAVOURITE_COUNT);
        String tweet_text_mentions_count = cursor.getString(COL_TWEET_TEXT_MENTIONS_COUNT);
        String tweet_user_name = cursor.getString(COL_TWEET_USERNAME);
        String tweet_user_name_image_url = cursor.getString(COL_TWEET_USERNAME_IMAGE_URL);
        String tweet_user_name_location = cursor.getString(COL_TWEET_USERNAME_LOCATION);
        String tweet_user_name_description = cursor.getString(COL_TWEET_USERNAME_DESCRIPTION);

        ContentValues tweetFavValues = new ContentValues();
        ContentValues tweetValues = new ContentValues();

        tweetFavValues.put(TweetFavEntry.COLUMN_HASHTAG_KEY, hash_tag_id);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_TEXT_DATE, tweet_text_date);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_ID, tweet_id);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_TEXT, tweet_text);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_TEXT_RETWEET_COUNT, tweet_text_retweet_count);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_TEXT_FAVOURITE_COUNT, tweet_text_favourite_count);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_TEXT_MENTIONS_COUNT, tweet_text_mentions_count);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_USERNAME, tweet_user_name);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_USERNAME_IMAGE_URL, tweet_user_name_image_url);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_USERNAME_LOCATION, tweet_user_name_location);
        tweetFavValues.put(TweetFavEntry.COLUMN_TWEETFAV_USERNAME_DESCRIPTION, tweet_user_name_location);


        tweetValues.put(TweetEntry.COLUMN_TWEET_FAVOURITED_STATE, 1);

        String whereClause = TweetEntry._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(_id)};


        getActivity().getApplicationContext().getContentResolver().insert(TweetFavEntry.CONTENT_URI, tweetFavValues);
        getActivity().getApplicationContext().getContentResolver().update(TweetEntry.CONTENT_URI, tweetValues, whereClause, selectionArgs);
        mTweetListAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(TWEETLIST_LOADER, null, this);
    }

    public void updateEmptyView() {
        TextView emptyTextView = (TextView) getView().findViewById(R.id.empty_text_view);

        if (mTweetListAdapter.getCount() == 0) {
            if (null != emptyTextView) {
                emptyTextView.setVisibility(View.VISIBLE);
                //If cursor is empty why do we have an invalid position
                int message = R.string.empty_tweet_list;
                if (!Utility.isNetworkAvailable(getActivity())) {
                    message = R.string.empty_tweet_list_no_network;
                }
                emptyTextView.setText(message);
            }
        } else {
            if (null != emptyTextView) {
                emptyTextView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface onTweetItemSelectedCallback {
        /**
         * TweetItemFragmentCallback for when an item has been selected.
         */
        void onTweetItemSelected(int date);
    }

	/*public void updateViewItem(View targetView)
	{
		int start = mPullToRefreshListView.getFirstVisiblePosition();
		int end = mPullToRefreshListView.getLastVisiblePosition();

		for(int i=start; i<=end; i++)
		{
			if(targetView == mPullToRefreshListView.getItemAtPosition(i))
			{
				View view = mPullToRefreshListView.getChildAtPosition(i-start);
				mPullToRefreshListView.getAdapter().getView(i, view, mPullToRefreshListView);
				//view.findViewById(R.id.tweet_favourite);
				break;
			}
		}
	}*/
}
