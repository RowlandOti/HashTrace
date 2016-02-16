package com.rowland.hashtrace.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rowland.hashtrace.R;

/**
 * Created by Rowland on 2/16/2016.
 */
public class AdViewAdapter extends CursorAdapter {

    private final Activity activity;
    private final CursorAdapter delegate;
    private int k = 10;

    // Constructor takes in a BaseAdapter also
    public AdViewAdapter(Context context, Activity activity, CursorAdapter delegate) {
        super(context,null);
        this.activity = activity;
        this.delegate = delegate;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Return an ad for every 5 items
        if ((cursor.getPosition() % k) == 0) {
            // Create a new AdView
            AdView adView = new AdView(activity);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(activity.getResources().getString(R.string.banner_ad_unit_id));
            // Convert the default layout parameters so that they play nice with ListView.
            float density = activity.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight() * density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, height);
            adView.setLayoutParams(params);

            // Need to set tag on the ViewHolder class
            TweetListAdapter.ViewHolder viewHolder = new TweetListAdapter.ViewHolder(adView);
            adView.setTag(viewHolder);
            // Return the ad
            return adView;
        } else {
            // Offload displaying other items to the delegate
            return delegate.newView(context, cursor, parent);
        }
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
        // Only show ads for AdViews
        if (convertView instanceof AdView) {
            // You can load the ad
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            ((AdView) convertView).loadAd(adRequest);
        } else {
            // Offload updating view items to the delegate
            delegate.bindView(convertView, context, cursor);
        }
    }
}
