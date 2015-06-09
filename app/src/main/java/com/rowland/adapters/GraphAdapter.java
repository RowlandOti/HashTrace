/**
 *
 */
package com.rowland.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Rowland
 *
 */
public class GraphAdapter extends CursorAdapter {



	public GraphAdapter(Context context, Cursor c, int flags)
	{
		super(context, c, flags);
	}


	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{

	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{

		return null;
	}

}
