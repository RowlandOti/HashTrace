/*
 * Copyright 2015 Oti Rowland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rowland.data.callbacks;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;

import com.rowland.rest.models.Movie;

/**
 * Created by Oti Rowland on 12/25/2015.
 */
public class MovieSortedListAdapterCallBack extends SortedListAdapterCallback<Movie> {

    public MovieSortedListAdapterCallBack(RecyclerView.Adapter adapter) {
        super(adapter);
    }

    // Should compare two objects and return how they should be ordered.
    @Override
    public int compare(Movie o1, Movie o2) {
        // Compare local id
        if (o1.getId() < o2.getId()) {
            return -1;
        } else if (o1.getId() > o2.getId()) {
            return 1;
        }
        return 0;
    }

    // Called by the SortedList when it wants to check whether two items have the same data or not.
    @Override
    public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
        // Compare titles
        return oldItem.getTitle().equals(newItem.getTitle());
    }

    // Called by the SortedList to decide whether two object represent the same Item or not.
    @Override
    public boolean areItemsTheSame(Movie item1, Movie item2) {
        // Compare remote id
        return item1.getId() == item2.getId();
    }
}
