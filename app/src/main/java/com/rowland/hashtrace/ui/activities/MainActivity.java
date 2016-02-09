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

package com.rowland.hashtrace.ui.activities;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.rowland.common.ui.activities.BaseToolBarActivity;
import com.rowland.hashtrace.R;
import com.rowland.hashtrace.sync.TweetHashTracerSyncAdapter;
import com.rowland.hashtrace.ui.fragments.DetailsFragment;
import com.rowland.hashtrace.ui.fragments.MainFragment;
import com.rowland.hashtrace.ui.fragments.subfragment.FavouriteListFragment;
import com.rowland.hashtrace.ui.fragments.subfragment.TweetListFragment;




public class MainActivity extends BaseToolBarActivity implements MainFragment.onMainFragmentItemSelectedCallback, TweetListFragment.onTweetItemSelectedCallback, FavouriteListFragment.onFavouriteItemSelectedCallback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
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
            else
            {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a fragment transaction.
                //Needs ad id to select automatically
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
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();

        DetailsFragment fragment = DetailsFragment.newInstance(args);

        ft.replace(R.id.detail_container, fragment)
        .commit();
    }

    private void showMainFragment(Bundle args)
    {
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();

        MainFragment fragment  = MainFragment.newInstance(args);

        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);

        SearchManager SManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        mSearchView.setSearchableInfo(SManager.getSearchableInfo(new ComponentName(getApplicationContext(), SearchActivity.class)));
        mSearchView.setIconifiedByDefault(true);

        return super.onCreateOptionsMenu(menu);

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
            case R.id.action_search:

                Log.w(LOG_TAG, "You called me Search");

                return true;
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onMainFragmentItemSelected(String date)
    {

    }

    @Override
    public void onFavouriteItemSelected(int id)
    {
        itemIsClicked(id);
    }

    @Override
    public void onTweetItemSelected(int id)
    {
        itemIsClicked(id);
    }

    private void itemIsClicked(int id)
    {
        if (mTwoPane)
        {
            // In two-pane mode, show the detail view in this activity by adding or replacing the
            // detail fragment using a fragment transaction.
            Bundle args = new Bundle();
            //args.putString(DetailsFragment.ID_KEY, date);
            args.putInt(DetailsFragment.ID_KEY, id);

            showDetailFragment(args);
        }
        else
        {
            Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsFragment.ID_KEY, id);
            startActivity(intent);
        }
    }
}