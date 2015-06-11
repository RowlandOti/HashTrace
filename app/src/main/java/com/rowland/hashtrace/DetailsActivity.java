package com.rowland.hashtrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.rowland.fragments.DetailsFragment;

/**
 * @author Rowland
 *
 */
public class DetailsActivity extends ActionBarActivity {

	public static final String DATE_KEY = "tweet_text_date";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_details);
		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null)
		{
			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null)
			{
				return;
			}
		}
		showTweetDetailFragment();
	}

	private void showTweetDetailFragment()
	{
		FragmentManager fm = getSupportFragmentManager();

		FragmentTransaction ft = fm.beginTransaction();

		DetailsFragment fragment =  DetailsFragment.newInstance("", "");

		ft.add(R.id.fragment_container, fragment);
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_detailsactivity, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.action_settings)
		{
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
