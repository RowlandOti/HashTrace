package com.rowland.hashtrace;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.rowland.adapters.SmartFragmentPagerAdapter;
import com.rowland.fragments.FavouriteListFragment;
import com.rowland.fragments.TweetListFragment;
import com.rowland.sync.TweetHashTracerSyncAdapter;

public class HashTraceActivity extends ActionBarActivity implements TweetListFragment.onTweetItemSelectedCallback, FavouriteListFragment.onFavouriteItemSelectedCallback {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MenuItem mRefresh = null;
	public String msg = "HASHTRACE : ";
	private String[] TITLES = { "HOME", "ARCHIVE", "GRAPH" };
	private int[] ICONS = {R.drawable.ic_action_home, R.drawable.ic_action_labels, R.drawable.ic_action_graph};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_hashtrace);

		// Initialize the ViewPager and set an adapter
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new SmartFragmentPagerAdapter(getSupportFragmentManager()));
		pager.setOnPageChangeListener(onPageChangeListener);

		addPageSlidingTabs();

		TweetHashTracerSyncAdapter.initializeSyncAdapter(this);
	}

	private SimpleOnPageChangeListener onPageChangeListener = new SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position)
		{
			super.onPageSelected(position);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private void addPageSlidingTabs()
	{
		// Bind the tabs to the ViewPager
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabsAdd);
		tabs.setViewPager(pager);
		tabs.setBackgroundResource(R.drawable.ab_stacked_solid_myactionbar);
		tabs.setIndicatorColorResource(R.color.apptheme_color);
		tabs.setDividerColorResource(R.color.apptheme_color);
		tabs.setUnderlineColorResource(R.color.apptheme_color);
		tabs.setUnderlineHeight(2);
		tabs.setTextColor(Color.WHITE);
		tabs.setTextSize(10);
		tabs.setShouldExpand(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_hashtraceactivity, menu);
		return true;
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

		default: {
			return super.onOptionsItemSelected(item);
		}

		}
	}

	@Override
	public void onFavouriteItemSelected(String date)
	{
		Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsActivity.DATE_KEY, date);
		startActivity(intent);
	}
	@Override
	public void onTweetItemSelected(String date)
	{
		Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsActivity.DATE_KEY, date);
		startActivity(intent);
	}


	public String[] getTITLES()
	{
		return TITLES;
	}

	public void setTITLES(String[] tITLES)
	{
		TITLES = tITLES;
	}

	public  int[] getIcons()
	{
		return ICONS;
	}




}
