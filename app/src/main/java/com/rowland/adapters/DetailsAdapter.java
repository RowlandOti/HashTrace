package com.rowland.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rowland.fragments.DetailsFragment;
import com.rowland.hashtrace.R;
import com.rowland.utility.ImageManager;

/**
 * Created by Rowland on 6/12/2015.
 */
public class DetailsAdapter extends CursorAdapter {

    public ImageManager imageManager;

    public DetailsAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);

        imageManager = new ImageManager(context);
    }

    /**
     * Cache of the children views for a tweet list item.
     */
    public static class ViewHolder {



        public ViewHolder(View view)
        {

        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {


        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {

    }
}
