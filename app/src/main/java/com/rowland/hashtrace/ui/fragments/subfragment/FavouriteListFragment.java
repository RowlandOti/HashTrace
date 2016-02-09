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
import com.rowland.hashtrace.ui.adapters.TweetFavListAdapter;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.HashTagEntry;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.TweetEntry;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.TweetFavEntry;
import com.rowland.hashtrace.R;
import com.rowland.hashtrace.utility.EDbDateLimit;
import com.rowland.hashtrace.utility.Utility;

import java.util.Date;

/**
 * TweetFavouriteListFragment for displaying users favourite tweets
 * over a period
 *
 * @author Rowland
 *
 */
public class FavouriteListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	// These indices are tied to TWEET_COLUMNS and must match for projection
	public static final int COL_ID = 0;
	public static final int COL_HASHTAG_KEY = 1;
	public static final int COL_TWEETFAV_ID = 2;
	public static final int COL_TWEETFAV_TEXT = 3;
	public static final int COL_TWEETFAV_TEXT_DATE = 4;
	public static final int COL_TWEETFAV_TEXT_RETWEET_COUNT = 5;
	public static final int COL_TWEETFAV_TEXT_FAVOURITE_COUNT = 6;
	public static final int COL_TWEETFAV_TEXT_MENTIONS_COUNT = 7;
	public static final int COL_TWEETFAV_USERNAME = 8;
	public static final int COL_TWEETFAV_USERNAME_IMAGE_URL = 9;
	public static final int COL_TWEETFAV_USERNAME_LOCATION = 10;
	public static final int COL_HASHTAG_NAME = 11;
	private static final String SELECTED_KEY = "selected_position";
	private static final int TWEETFAVLIST_LOADER = 1;
	private static final String[] TWEETFAV_COLUMNS = {
		// In this case the id needs to be fully qualified with a table
		// name, since the content provider joins the hastag & tweet tables in the background (both have an _id column)
		// On the one hand, that's annoying. On the other, you can search
		// the tweet table using the hashtag set by the user, which is only in the Hashtag table. So the convenience is worth it.
		TweetFavEntry.TABLE_NAME + "." +

		TweetFavEntry._ID,                      			 //0
		TweetFavEntry.COLUMN_HASHTAG_KEY,					 //1
		TweetFavEntry.COLUMN_TWEETFAV_ID,					 //2
		TweetFavEntry.COLUMN_TWEETFAV_TEXT,					 //3
		TweetFavEntry.COLUMN_TWEETFAV_TEXT_DATE,	         //4
		TweetFavEntry.COLUMN_TWEETFAV_TEXT_RETWEET_COUNT,    //5
		TweetFavEntry.COLUMN_TWEETFAV_TEXT_FAVOURITE_COUNT,  //6
		TweetFavEntry.COLUMN_TWEETFAV_TEXT_MENTIONS_COUNT,//7
		TweetFavEntry.COLUMN_TWEETFAV_USERNAME,				 //8
		TweetFavEntry.COLUMN_TWEETFAV_USERNAME_IMAGE_URL,	 //9
	    TweetFavEntry.COLUMN_TWEETFAV_USERNAME_LOCATION,	 //10
		HashTagEntry.COLUMN_HASHTAG_NAME 				     //11
	};
	private SwipeMenuListView mListView;
	private TweetFavListAdapter mTweetFavListAdapter;
	private SwipeMenuCreator creator;
	private int mPosition = ListView.INVALID_POSITION;
	private String mHashTag;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Add this line in order for this fragment to handle menu events.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// The CursorAdapter will take data from a source and use it to populate the ListView it's attached to.
		mTweetFavListAdapter = new TweetFavListAdapter(getActivity(), null, 0);
		creator = new SwipeMenuCreator() {

		    @Override
		    public void create(SwipeMenu menu)
		    {
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
		        favourItem.setIcon(R.drawable.selector_swipemenuitem_discard);
		        // set item title
		        favourItem.setTitle("Delete");
		        // set item title fontsize
		        favourItem.setTitleSize(18);
		        // set item title font color
		        favourItem.setTitleColor(Color.WHITE);
		        // add to menu
		        menu.addMenuItem(favourItem);
		    }
		};

		View rootView = inflater.inflate(R.layout.fragment_tweetfav_list, container, false);

		mListView = (SwipeMenuListView) rootView.findViewById(android.R.id.list);
		mListView.setAdapter(mTweetFavListAdapter);
		mListView.setMenuCreator(creator);
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id)
			{
				// Do the onItemLongClick action
				mListView.smoothOpenMenu(position);

				return true;
			}
		});
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
			{
				switch (index) {
				case 0:
					// Share
					shareTweet();
					break;
				case 1:
					// Favourite
					DeleteTweet();
					break;
				}
				// false : close the menu; true : not close the menu
				return false;
			}
		});
		mListView.setCloseInterpolator(new BounceInterpolator());

		// If there's instance state, mine it for useful information.
		// The end-goal here is that the user never knows that turning their
		// device sideways does crazy lifecycle related things. It should
		// feel like some stuff stretched out, or magically appeared to take
		// advantage of room, but data or place in the app was never actually *lost*.
		if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
		{
			// The listview probably hasn't even been populated yet. Actually
			// perform the
			// swapout in onLoadFinished.
			mPosition = savedInstanceState.getInt(SELECTED_KEY);
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		getLoaderManager().initLoader(TWEETFAVLIST_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		// Clear old menu.
		menu.clear();
		// Inflate new menu.
		inflater.inflate(R.menu.menu_favlistfragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_overflow:
			{
				return true;
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// When tablets rotate, the currently selected list item needs to be saved.
		// When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
		// so check for that before storing.
		if (mPosition != ListView.INVALID_POSITION)
		{
			outState.putInt(SELECTED_KEY, mPosition);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onListItemClick(ListView lv, View view, int position, long rowID)
	{
		super.onListItemClick(lv, view, position, rowID);
		// Do the onItemClick action
		Cursor cursor = mTweetFavListAdapter.getCursor();
		if (cursor != null && cursor.moveToPosition(position))
		{
			((onFavouriteItemSelectedCallback) getActivity()).onFavouriteItemSelected((int) rowID);
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
		String startDate = TweetHashTracerContract.getDbDateString(new Date(),EDbDateLimit.DATE_FORMAT_DAY_LIMIT);

		// Sort order: Ascending, by date.
		String sortOrder = TweetFavEntry.COLUMN_TWEETFAV_TEXT_DATE + " DESC";

		mHashTag = Utility.getPreferredHashTag(getActivity());
		Uri tweetForHashTagUri = TweetFavEntry.buildTweetFavHashTagWithStartDate(mHashTag, startDate);

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), tweetForHashTagUri, TWEETFAV_COLUMNS, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mTweetFavListAdapter.swapCursor(data);
		if (mPosition != ListView.INVALID_POSITION)
		{
			// If we don't need to restart the loader, and there's a desired
			// position to restore to, do so now.
			mListView.smoothScrollToPosition(mPosition);
		}
		updateEmptyView();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		mTweetFavListAdapter.swapCursor(null);
	}

	private void shareTweet()
	{
		Cursor cursor = mTweetFavListAdapter.getCursor();
		String tweet_text = cursor.getString(COL_TWEETFAV_TEXT);

		Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, tweet_text);
        startActivity(Intent.createChooser(share, "Share Tweet"));
	}

	private void DeleteTweet()
	{
		Cursor cursor = mTweetFavListAdapter.getCursor();
		String tweet_text = cursor.getString(COL_TWEETFAV_TEXT);

		ContentValues tweetValues = new ContentValues();
		tweetValues.put(TweetEntry.COLUMN_TWEET_FAVOURITED_STATE, 0);

		String whereClause = TweetEntry.COLUMN_TWEET_TEXT + " = ?";
		String whereClauseFav = TweetFavEntry.COLUMN_TWEETFAV_TEXT + " = ?";
		String[] selectionArgs = new String[]{String.valueOf(tweet_text)};

		getActivity().getApplicationContext().getContentResolver().delete(TweetFavEntry.CONTENT_URI, whereClauseFav, selectionArgs);
		getActivity().getApplicationContext().getContentResolver().update(TweetEntry.CONTENT_URI, tweetValues, whereClause, selectionArgs);
		mTweetFavListAdapter.notifyDataSetChanged();
		getLoaderManager().restartLoader(TWEETFAVLIST_LOADER, null, this);
	}

	public void updateEmptyView()
	{
		TextView emptyTextView = (TextView) getView().findViewById(R.id.empty_text_view);

		if(mTweetFavListAdapter.getCount() == 0)
		{
			if (null != emptyTextView)
			{
				emptyTextView.setVisibility(View.VISIBLE);
				//If cursor is empty why do we have an invalid position
				int message = R.string.empty_tweet_list_fav;

				emptyTextView.setText(message);
			}
		}
		else
		{
			if (null != emptyTextView)
			{
				emptyTextView.setVisibility(View.GONE);
			}
		}
	}

	public interface onFavouriteItemSelectedCallback
	{
		/**
		 * TweetItemFragmentCallback for when an item has been selected.
		 */
		void onFavouriteItemSelected(int date);
	}

}
