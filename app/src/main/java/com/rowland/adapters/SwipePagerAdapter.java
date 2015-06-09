/**
 * Copyright 2014 Oti Rowland.
 * <p/>
 * All rights reserved. Stats Mtaani PROPRIETARY/CONFIDENTIAL. Use is subject to
 * license terms.
 * <p/>
 * User: can <rowlandmtetezi@statsmtaani.com> Date: 6/11/14 6:16 PM
 */
package com.rowland.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.astuetz.PagerSlidingTabStrip;
import com.rowland.fragments.FavouriteListFragment;
import com.rowland.fragments.GraphFragment;
import com.rowland.fragments.TweetListFragment;
import com.rowland.hashtrace.HashTraceActivity;

/**
 * @author Rowland
 *
 */
public class SwipePagerAdapter extends SmartFragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

	private HashTraceActivity ht = new HashTraceActivity();

	public SwipePagerAdapter(FragmentManager fm)
	{

		super(fm);

	}

	@Override
	public Fragment getItem(int index) {

		switch (index)
		{
		case 0:
		{
			// Home fragment activity
			return new TweetListFragment();
		}
		case 1:
		{
			// Favourite fragment activity
			return new FavouriteListFragment();
		}
		case 2:
		{
			// Graph fragment activity
			return new GraphFragment();
		}

		}

		return null;
	}

	@Override
	public int getCount() {

		// get item count - equal to number of tabs

		return ht.getTITLES().length;

	}

	@Override
	public CharSequence getPageTitle(int position) {

		return ht.getTITLES()[position];
	}

	@Override
	public int getPageIconResId(int position)
	{
		return ht.getIcons()[position];

	}
}
