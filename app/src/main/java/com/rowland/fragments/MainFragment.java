package com.rowland.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.rowland.adapters.SmartNestedViewPagerAdapter;
import com.rowland.hashtrace.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private static MainFragment fragmentInstance = null;
    private PagerSlidingTabStrip slidingTabStrips;
    private ViewPager pager;
    private SmartNestedViewPagerAdapter pagerAdapter;
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private String[] TITLES = { "HOME", "ARCHIVE", "GRAPH" };
    private int[] ICONS = {R.drawable.ic_action_home, R.drawable.ic_action_labels, R.drawable.ic_action_graph};

    // TODO: Rename and change types of parameters
    public static MainFragment newInstance(Bundle args)
    {
        /*if(fragmentInstance != null)
        {
            return fragmentInstance;
        }
        else
        {*/
            fragmentInstance = new MainFragment();
            if(args != null)
            {
                fragmentInstance.setArguments(args);
            }
            return fragmentInstance;
       // }
    }

    public MainFragment()
    {
        setRetainInstance(true);
    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface onMainFragmentItemSelectedCallback
    {
        /**
         * TweetItemFragmentCallback for when an item has been selected.
         */
        public void onMainFragmentItemSelected(String date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //View detailsFrame = getActivity().findViewById(R.id.fragment_container);

        // Initialize the ViewPager and set an Adapter: pass data, etc.
        this.pagerAdapter = new SmartNestedViewPagerAdapter((getActivity().getSupportFragmentManager()));
        this.pager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        this.pager.setAdapter(pagerAdapter);
        this.pager.setOnPageChangeListener(onPageChangeListener);

        // Bind the slidingTabStrips to the ViewPager
        this.slidingTabStrips = (PagerSlidingTabStrip) getActivity().findViewById(R.id.slidingTabStrips);
        this.slidingTabStrips.setViewPager(pager);
        this.slidingTabStrips.setBackgroundResource(R.drawable.ab_stacked_solid_myactionbar);
        this.slidingTabStrips.setIndicatorColorResource(R.color.apptheme_color);
        this.slidingTabStrips.setDividerColorResource(R.color.apptheme_color);
        this.slidingTabStrips.setUnderlineColorResource(R.color.apptheme_color);
        this.slidingTabStrips.setUnderlineHeight(2);
        this.slidingTabStrips.setTextColor(Color.WHITE);
        this.slidingTabStrips.setTextSize(10);
        this.slidingTabStrips.setShouldExpand(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    private ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    public String[] getTITLES()
    {
        return TITLES;
    }

    public void setTITLES(String[] tITLES)
    {
        TITLES = tITLES;
    }

    public int[] getIcons()
    {
        return ICONS;
    }


}
