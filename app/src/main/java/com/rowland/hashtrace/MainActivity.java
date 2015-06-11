package com.rowland.hashtrace;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.rowland.fragments.DetailsFragment;
import com.rowland.fragments.MainFragment;
import com.rowland.sync.TweetHashTracerSyncAdapter;


public class MainActivity extends ActionBarActivity {

    private String[] TITLES = { "HOME", "ARCHIVE", "GRAPH" };
    private int[] ICONS = {R.drawable.ic_action_home, R.drawable.ic_action_labels, R.drawable.ic_action_graph};
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
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailsFragment())
                        .commit();
            }
        }
        else
        {
            mTwoPane = false;
        }

        showMainFragment();
        TweetHashTracerSyncAdapter.initializeSyncAdapter(this);
    }

    private void showMainFragment()
    {
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();

        MainFragment fragment = MainFragment.newInstance("", "");
        //fragment.setUseTodayLayout(!mTwoPane);

        ft.add(R.id.fragment_container, fragment);
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String[] getTITLES() {
        return TITLES;
    }

    public void setTITLES(String[] tITLES) {
        TITLES = tITLES;
    }

    public int[] getIcons() {
        return ICONS;
    }
}