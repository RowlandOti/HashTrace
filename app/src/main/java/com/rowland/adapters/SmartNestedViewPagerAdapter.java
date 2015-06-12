package com.rowland.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.astuetz.PagerSlidingTabStrip;
import com.rowland.fragments.FavouriteListFragment;
import com.rowland.fragments.GraphFragment;
import com.rowland.fragments.MainFragment;
import com.rowland.fragments.TweetListFragment;

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

/*    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        // Remove the object (we don't want to keep it in memory as it will get recreated and cached when needed)
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    @Override
    public Object instantiateItem(View pager, final int position)
    {
        // inflate your 'content'-views here (in my case I added a listview here)
        // similar to a listadapter's `getView()`-method

        //View wrapper = ... // inflate your layout
        //... // fill your data to the appropriate views

        //((ViewPager) pager).addView(wrapper);
        return null;
    }*/

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
