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

package com.rowland.hashtrace.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.SlidingTabStripLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rowland.hashtrace.R;
import com.rowland.hashtrace.ui.activities.MainActivity;
import com.rowland.hashtrace.ui.adapters.SmartNestedViewPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    // ButterKnife injected views
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.slidingTabStrips)
    SlidingTabStripLayout mSlidingTabStrips;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    private SmartNestedViewPagerAdapter pagerAdapter;
    private String[] TITLES = {"HOME", "ARCHIVE", "GRAPH"};
    private int[] ICONS = {R.drawable.ic_action_home, R.drawable.ic_action_labels, R.drawable.ic_action_graph};

    public MainFragment() {

    }

    // TODO: Rename and change types of parameters
    public static MainFragment newInstance(Bundle args) {
        MainFragment fragmentInstance = new MainFragment();
        if (args != null) {
            fragmentInstance.setArguments(args);
        }
        return fragmentInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Don't destroy fragment across orientation change
        setRetainInstance(true);
    }

    // Create the view for this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Initialize the ViewPager and TabStripLayout
        ButterKnife.bind(this, rootView);
        // Return the view for this fragment
        return rootView;
    }


    // Called after the containing activity is created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set the ToolBar
        ((MainActivity) getActivity()).setToolbar(mToolbar, false, false, R.drawable.ic_logo_48px);
    }

    // Called after fragmnet's view is created by onCreateView()
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Initialize the fragments adapter
        pagerAdapter = new SmartNestedViewPagerAdapter(getActivity().getSupportFragmentManager());
        // Set up the adapter
        mViewPager.setAdapter(pagerAdapter);
        // Set up the viewPager
        mSlidingTabStrips.setupWithViewPager(mViewPager);
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

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface onMainFragmentItemSelectedCallback {
        /**
         * TweetItemFragmentCallback for when an item has been selected.
         */
        void onMainFragmentItemSelected(String date);
    }


}
