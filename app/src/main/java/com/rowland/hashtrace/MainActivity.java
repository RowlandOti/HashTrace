package com.rowland.hashtrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.rowland.fragments.DetailsFragment;
import com.rowland.fragments.FavouriteListFragment;
import com.rowland.fragments.MainFragment;
import com.rowland.fragments.TweetListFragment;
import com.rowland.sync.TweetHashTracerSyncAdapter;


public class MainActivity extends ActionBarActivity implements MainFragment.onMainFragmentItemSelectedCallback, TweetListFragment.onTweetItemSelectedCallback, FavouriteListFragment.onFavouriteItemSelectedCallback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null)
        {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // If we're being restored from a previous state, don't need to do anything
            // and should return or else we could end up with overlapping fragments.
            if (savedInstanceState != null)
            {
                return;
            }
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a fragment transaction.
            else
            {
                showDetailFragment(null);
            }
        }
        else
        {
            mTwoPane = false;
        }

        showMainFragment(null);
        TweetHashTracerSyncAdapter.initializeSyncAdapter(this);
    }

    private void showDetailFragment(Bundle args)
    {
        DetailsFragment fragment = DetailsFragment.newInstance(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
    }

    private void showMainFragment(Bundle args)
    {
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();

        MainFragment fragment = MainFragment.newInstance(args);

        ft.add(R.id.fragment_container, fragment);
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {

            case R.id.action_settings:
            {
                startActivity(new Intent(this, SettingsActivity.class));

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }

        }
    }

    @Override
    public void onMainFragmentItemSelected(String date)
    {

    }

    @Override
    public void onFavouriteItemSelected(String date)
    {
        itemIsClicked(date);
    }

    @Override
    public void onTweetItemSelected(String date)
    {
        itemIsClicked(date);
    }

    private void itemIsClicked(String date)
    {
        if (mTwoPane)
        {
            // In two-pane mode, show the detail view in this activity by adding or replacing the
            // detail fragment using a fragment transaction.
            Bundle args = new Bundle();
            args.putString(DetailsFragment.DATE_KEY, date);

            showDetailFragment(args);
        }
        else
        {
            Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsFragment.DATE_KEY, date);
            startActivity(intent);
        }

    }
}