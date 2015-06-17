package com.rowland.fragments;

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

import com.rowland.data.TweetHashTracerContract;
import com.rowland.data.TweetHashTracerContract.HashTagEntry;
import com.rowland.data.TweetHashTracerContract.TweetEntry;
import com.rowland.hashtrace.R;
import com.rowland.utility.EDbDateLimit;
import com.rowland.utility.IterableCursor;
import com.rowland.utility.Utility;

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
		//menu.clear();
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
