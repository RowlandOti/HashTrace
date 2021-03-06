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

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rowland.hashtrace.HashTraceApplication;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.HashTagEntry;
import com.rowland.hashtrace.data.provider.TweetHashTracerContract.TweetEntry;
import com.rowland.hashtrace.R;
import com.rowland.hashtrace.utility.EDbDateLimit;
import com.rowland.hashtrace.utility.IterableCursor;
import com.rowland.hashtrace.utility.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Graph fragment for displaying the graphs
 *
 * @author Rowland
 *
 */
public class GraphFragment extends Fragment implements LoaderCallbacks<Cursor> {

	public static final String LOG_TAG = GraphFragment.class.getSimpleName();
	// These indices are tied to TWEET_COLUMNS and must match for projection
	public static final int COL_ID = 0;
	public static final int COL_HASHTAG_KEY = 1;
	public static final int COL_TWEET_ID = 2;
	public static final int COL_TWEET_TEXT_DATE = 3;
	public static final int COL_TWEET_TEXT_RETWEET_COUNT = 4;
	public static final int COL_TWEET_TEXT_FAVOURITE_COUNT = 5;
	public static final int COL_TWEET_TEXT_MENTIONS_COUNT = 6;
	public static final int COL_HASHTAG_NAME = 7;
	private static final int TWEETGRAPH_LOADER = 2;
	// Specify the columns we need for projection.
	private static final String[] TWEET_GRAPH_COLUMNS = {
		// In this case the id needs to be fully qualified with a table
		// name, since the content provider joins the hastag & tweet tables in the background (both have an _id column)
		// On the one hand, that's annoying. On the other, you can search
		// the tweet table using the hashtag set by the user, which is only in the Hashtag table. So the convenience is worth it.
		    TweetEntry.TABLE_NAME + "." +

			TweetEntry._ID,                      			//0
			TweetEntry.COLUMN_HASHTAG_KEY,					//1
			TweetEntry.COLUMN_TWEET_ID,						//2
			TweetEntry.COLUMN_TWEET_TEXT_DATE,	            //3
			TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT,     //4
			TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT,   //5
			TweetEntry.COLUMN_TWEET_TEXT_MENTIONS_COUNT,    //6
			HashTagEntry.COLUMN_HASHTAG_NAME 				//7
	};
	private ColumnChartView mColumnChartView;
	private Tracker mTracker;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Add this line in order for this fragment to handle menu events.
		setHasOptionsMenu(true);
		// Obtain the shared Tracker instance.
		HashTraceApplication application = (HashTraceApplication) getActivity().getApplication();
		mTracker = application.getDefaultTracker();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_column_chart, container, false);

		mColumnChartView = (ColumnChartView) rootView.findViewById(R.id.chart);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		getLoaderManager().initLoader(TWEETGRAPH_LOADER, null, this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		// Clear old menu.
		// menu.clear();
		// Inflate new menu.
		inflater.inflate(R.menu.menu_graphfragment, menu);
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
	public void onResume() {
		super.onResume();
		mTracker.setScreenName(LOG_TAG);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// fragment only uses one loader, so we don't care about checking the id.

		// and filter the query to return tweets only for dates after or including today and after.
		String startDate = TweetHashTracerContract.getDbDateString(new Date(),EDbDateLimit.DATE_FORMAT_DAY_LIMIT);

		// Sort order: Ascending, by date.
		String sortOrder = TweetEntry.COLUMN_TWEET_TEXT_DATE + " DESC";

		String mHashTag = Utility.getPreferredHashTag(getActivity());
		Uri tweetForHashTagUri = TweetEntry.buildTweetHashTagWithStartDate(mHashTag, startDate);

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), tweetForHashTagUri, TWEET_GRAPH_COLUMNS, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		List<Column> columnsList = new ArrayList<Column>();
		List<SubcolumnValue> subColumnValues;

		for (Cursor tweet : new IterableCursor(data))
		{
	        int tweet_id = tweet.getInt(GraphFragment.COL_ID);
	        int tweet_retweet_count = tweet.getInt(GraphFragment.COL_TWEET_TEXT_RETWEET_COUNT);
	        int tweet_favourite_count = tweet.getInt(GraphFragment.COL_TWEET_TEXT_FAVOURITE_COUNT);
	        int tweet_mentions_count = tweet.getInt(GraphFragment.COL_TWEET_TEXT_MENTIONS_COUNT);

	        subColumnValues = new ArrayList<SubcolumnValue>();

	        subColumnValues.add(new SubcolumnValue(tweet_retweet_count, ChartUtils.COLOR_VIOLET));
	        subColumnValues.add(new SubcolumnValue(tweet_favourite_count, ChartUtils.COLOR_ORANGE));
	        subColumnValues.add(new SubcolumnValue(tweet_mentions_count, ChartUtils.COLOR_GREEN));

	        Column column = new Column(subColumnValues);
			column.setHasLabels(true);
			column.setHasLabelsOnlyForSelected(false);
			columnsList.add(column);
	    }

		ColumnChartData columnChartData = new ColumnChartData(columnsList);
		// Set stacked flag.
		columnChartData.setStacked(true);

		Axis axisX = new Axis();
		Axis axisY = new Axis().setHasLines(true);
		axisX.setName("Tweet");
		axisY.setName("Retweets | Favourites | Mentions");
		columnChartData.setAxisXBottom(axisX);
		columnChartData.setAxisYLeft(axisY);

		mColumnChartView.setValueSelectionEnabled(true);
		mColumnChartView.setColumnChartData(columnChartData);
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{

	}


}
