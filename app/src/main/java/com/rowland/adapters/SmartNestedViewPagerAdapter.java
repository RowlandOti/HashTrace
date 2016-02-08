package com.rowland.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.astuetz.PagerSlidingTabStrip;
import com.rowland.common.ui.adapters.SmartFragmentStatePagerAdapter;
import com.rowland.fragments.MainFragment;
import com.rowland.fragments.subfragment.FavouriteListFragment;
import com.rowland.fragments.subfragment.GraphFragment;
import com.rowland.fragments.subfragment.TweetListFragment;

/**
 * Created by Rowland on 6/11/2015.
 */
public class SmartNestedViewPagerAdapter extends SmartFragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private MainFragment ht = new MainFragment();

    public SmartNestedViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int index)
    {
        switch (index)
        {
            case 0:
            {
                // Home -Tweet fragment activity
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
