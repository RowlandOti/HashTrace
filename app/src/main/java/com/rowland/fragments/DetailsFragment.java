package com.rowland.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rowland.hashtrace.R;

public class DetailsFragment extends Fragment {

    private static DetailsFragment fragmentInstance = null;
    private final String LOG_TAG = DetailsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    public static DetailsFragment newInstance(String param1, String param2)
    {
        if(fragmentInstance != null)
        {
            return fragmentInstance;
        }
        else
        {
            Bundle args = new Bundle();
            args.putString("", param1);
            args.putString("", param2);

            fragmentInstance = new DetailsFragment();
            fragmentInstance.setArguments(args);
            return fragmentInstance;
        }
    }

    public DetailsFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        return rootView;
    }

}
