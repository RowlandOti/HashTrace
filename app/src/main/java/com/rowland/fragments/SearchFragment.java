package com.rowland.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rowland.data.TweetHashTracerContract.HashTagEntry;
import com.rowland.data.TweetHashTracerContract.TweetEntry;
import com.rowland.hashtrace.R;
import com.rowland.utility.ImageManager;
import com.rowland.utility.Utility;
// TODO:Read and implement http://stackoverflow.com/questions/7707032/illegalstateexception-when-replacing-a-fragment

public class SearchFragment extends Fragment {

    private static SearchFragment fragmentInstance = null;
    private final String LOG_TAG = DetailsFragment.class.getSimpleName();
    private TextView txtQuery;
    private String mQuery;

    public static SearchFragment newInstance(Bundle args)
    {
        fragmentInstance = new SearchFragment();
        if (args != null)
        {
            fragmentInstance.setArguments(args);
        }
        return fragmentInstance;
    }

    public SearchFragment()
    {
        setRetainInstance(true);
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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        txtQuery = (TextView) rootView.findViewById(R.id.txtQuery);

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
            txtQuery.setText("Search Query: " + mQuery);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}
