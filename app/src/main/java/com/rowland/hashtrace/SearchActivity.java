package com.rowland.hashtrace;

/**
 * @author Rowland
 *
 */

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.rowland.fragments.DetailsFragment;
import com.rowland.fragments.SearchFragment;


public class SearchActivity extends ActionBarActivity implements SearchFragment.onTweetItemSelectedCallback{

	private final String LOG_TAG = SearchActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		if (findViewById(R.id.fragment_container) != null)
		{
			// However, if we're being restored from a previous state, then we don't need to do
			// anything and should return or else we could end up with overlapping fragments.
			if (savedInstanceState != null)
			{
				return;
			}
			// Create the detail fragment and add it to the activity using a fragment transaction.
			else
			{
				String searchQuery = getIntentSearchQuery(getIntent());

				Bundle args = new Bundle();
				args.putString(SearchManager.QUERY, searchQuery);
				showTweetSearchFragment(args);
			}
		}
	}

	private void showTweetSearchFragment(Bundle args)
	{
		FragmentManager fm = getSupportFragmentManager();

		FragmentTransaction ft = fm.beginTransaction();

		SearchFragment fragment  = SearchFragment.newInstance(args);

		ft.add(R.id.fragment_container, fragment);
		ft.commit();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		setIntent(intent);
	}

	/**
	 * Getting intent search query
	 */
	private String getIntentSearchQuery(Intent intent)
	{
		String query = "Tweet";

		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			query = intent.getStringExtra(SearchManager.QUERY);
		}

		return query;
	}

	@Override
	public void onTweetItemSelected(int id)
	{
		itemIsClicked(id);
	}

	private void itemIsClicked(int id)
	{
		/*if (mTwoPane)
		{
			// In two-pane mode, show the detail view in this activity by adding or replacing the
			// detail fragment using a fragment transaction.
			Bundle args = new Bundle();
			//args.putString(DetailsFragment.ID_KEY, date);
			args.putInt(DetailsFragment.ID_KEY, id);

			showDetailFragment(args);
		}
		else
		{*/
			Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsFragment.ID_KEY, id);
			startActivity(intent);
		//}
	}
}
