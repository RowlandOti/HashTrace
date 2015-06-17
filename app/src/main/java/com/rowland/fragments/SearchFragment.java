package com.rowland.fragments;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.rowland.adapters.TweetListAdapter;
import com.rowland.data.TweetHashTracerContract;
import com.rowland.data.TweetHashTracerContract.HashTagEntry;
import com.rowland.data.TweetHashTracerContract.TweetEntry;
import com.rowland.data.TweetHashTracerContract.TweetFavEntry;
import com.rowland.hashtrace.R;
import com.rowland.utility.Utility;

public class SearchFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SEARCH_LOADER = 4;
    private static final String SELECTED_KEY = "selected_position";
    // For the tweet view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] TWEET_COLUMNS = {
            // In this case the id needs to be fully qualified with a table
            // name, since the content provider joins the hastag & tweet tables in the background (both have an _id column)
            // On the one hand, that's annoying. On the other, you can search
            // the tweet table using the hashtag set by the user, which is only in the Hashtag table. So the convenience is worth it.
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
    private static SearchFragment fragmentInstance = null;
    private final String LOG_TAG = SearchFragment.class.getSimpleName();
    private SwipeMenuListView mListView;
    private TweetListAdapter mTweetListAdapter;
    private SwipeMenuCreator creator;
    private int mPosition = ListView.INVALID_POSITION;
    private String mQuery;

    public SearchFragment()
    {
        setRetainInstance(true);
    }

    public static SearchFragment newInstance(Bundle args)
    {
        fragmentInstance = new SearchFragment();
        if (args != null)
        {
            fragmentInstance.setArguments(args);
        }
        return fragmentInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            mQuery = arguments.getString(SearchManager.QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // The CursorAdapter will take data from a source and use it to populate the ListView it's attached to.
        mTweetListAdapter = new TweetListAdapter(getActivity(), null, 0);
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

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        //txtQuery = (TextView) rootView.findViewById(R.id.listview_tweet_empty);
        mListView = (SwipeMenuListView) rootView.findViewById(android.R.id.list);
        mListView.setAdapter(mTweetListAdapter);
        mListView.setMenuCreator(creator);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id)
            {
                // Do the onItemLongClick action
                mListView.smoothOpenMenu(position);

                return true;
            }
        });
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
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
                        favouriteTweet();
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
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(SearchManager.QUERY))
        {
            /**
             * Use this query to display search results like 1. Getting the data
             * from SQLite and showing in listview 2. Making webrequest and
             * displaying the data For now we just display the query only
             */

            Bundle data = new Bundle();
            data.putString(SearchManager.QUERY, mQuery);
            getLoaderManager().initLoader(SEARCH_LOADER, data, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
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
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView lv, View view, int position, long rowID)
    {
        super.onListItemClick(lv, view, position, rowID);
        // Do the onItemClick action
        Cursor cursor = mTweetListAdapter.getCursor();
        if (cursor != null && cursor.moveToPosition(position))
        {
            ((onTweetItemSelectedCallback) getActivity()).onTweetItemSelected((int) rowID);
        }
        mPosition = position;
        Log.d("ROWSELECT", "" + rowID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle query) {

        //String startDate = TweetHashTracerContract.getDbDateString(new Date(),EDbDateLimit.DATE_FORMAT_DAY_LIMIT);

        // Sort order: Ascending, by date.
        String sortOrder = TweetEntry.COLUMN_TWEET_TEXT_DATE + " DESC";
        String[] selectionArgs = new String[]{ mQuery};
        String mHashTag = Utility.getPreferredHashTag(getActivity());
        Uri tweetForHashTagUri = TweetEntry.buildTweetHashTagWithSearchKey(mHashTag, mQuery);

        Log.w(LOG_TAG, "" + tweetForHashTagUri);

        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
        //CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(), tweetForHashTagUri, TWEET_COLUMNS, null, null, sortOrder);


        CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(), tweetForHashTagUri, TWEET_COLUMNS, null, selectionArgs, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        mTweetListAdapter.swapCursor(data);

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
        mTweetListAdapter.swapCursor(null);
    }

    public void updateEmptyView()
    {
        TextView emptyTextView = (TextView) getView().findViewById(R.id.listview_tweet_empty);

        if(mTweetListAdapter.getCount() == 0)
        {
            if (null != emptyTextView)
            {
                emptyTextView.setVisibility(View.VISIBLE);
                //If cursor is empty why do we have an invalid position
                int message = R.string.empty_tweet_list_none_found;

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

    private void shareTweet()
    {
        Cursor cursor = mTweetListAdapter.getCursor();
        String tweet_text = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT));

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, tweet_text);
        startActivity(Intent.createChooser(share, "Share Tweet"));
    }

    private void favouriteTweet()
    {
        Cursor cursor = mTweetListAdapter.getCursor();

        int _id = cursor.getInt(cursor.getColumnIndex(TweetEntry._ID));
        int hash_tag_id = cursor.getInt(cursor.getColumnIndex(TweetEntry.COLUMN_HASHTAG_KEY));
        long tweet_id = cursor.getLong(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_ID));
        String tweet_text = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT));
        String tweet_text_date = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT_DATE));
        String tweet_text_retweet_count = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT_RETWEET_COUNT));
        String tweet_text_favourite_count = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT_FAVOURITE_COUNT));
        String tweet_text_mentions_count = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_TEXT_MENTIONS_COUNT));
        String tweet_user_name = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_USERNAME));
        String tweet_user_name_image_url = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_USERNAME_IMAGE_URL));
        String tweet_user_name_location = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_USERNAME_LOCATION));
        String tweet_user_name_description = cursor.getString(cursor.getColumnIndex(TweetEntry.COLUMN_TWEET_USERNAME_DESCRIPTION));

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


        getActivity().getApplicationContext().getContentResolver().insert(TweetHashTracerContract.TweetFavEntry.CONTENT_URI, tweetFavValues);
        getActivity().getApplicationContext().getContentResolver().update(TweetEntry.CONTENT_URI, tweetValues, whereClause, selectionArgs);
        mTweetListAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(SEARCH_LOADER, null, this);
    }
    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface onTweetItemSelectedCallback
    {
        /**
         * TweetItemFragmentCallback for when an item has been selected.
         */
        void onTweetItemSelected(int date);
    }
}
