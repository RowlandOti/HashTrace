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

package com.rowland.hashtrace.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.rowland.hashtrace.utility.ImageManager;

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
