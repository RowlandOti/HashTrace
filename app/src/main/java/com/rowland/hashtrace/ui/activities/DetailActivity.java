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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rowland.common.ui.activities.BaseToolBarActivity;
import com.rowland.hashtrace.R;
import com.rowland.hashtrace.ui.fragments.DetailFragment;

/**
 * @author Rowland
 *
 */
public class DetailActivity extends BaseToolBarActivity {

	private final String LOG_TAG = DetailActivity.class.getSimpleName();

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
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			else
			{
				int id = getIntent().getIntExtra(DetailFragment.ID_KEY, 0);
				//int date = getIntent().getIntExtra(DetailFragment.ID_KEY);
				Log.w("TWEETID",""+id);

				Bundle args = new Bundle();
				args.putInt(DetailFragment.ID_KEY, id);
				showTweetDetailFragment(args);
			}
		}
	}

	private void showTweetDetailFragment(Bundle args)
	{
		FragmentManager fm = getSupportFragmentManager();

		FragmentTransaction ft = fm.beginTransaction();

		DetailFragment fragment =  DetailFragment.newInstance(args);

		ft.add(R.id.fragment_container, fragment);
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_detailsactivity, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_settings:
			{
				startActivity(new Intent(this, SettingsActivity.class));

				return true;
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}
	}
}
