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

package com.rowland.data.loaders;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.rowland.BuildConfig;
import com.rowland.common.data.loaders.BaseLoader;
import com.rowland.common.data.broadcastrecievers.DataSetChangeBroadCastReceiver;
import com.rowland.data.repository.MovieRepository;
import com.rowland.rest.enums.ESortOrder;
import com.rowland.rest.models.Movie;

import java.util.List;

/**
 * Created by Oti Rowland on 12/12/2015.
 */
public class MovieLoader extends BaseLoader {

    // DataChangeObserver Intent Receiver action
    public static final String INTENT_ACTION = "com.rowland.movies.MOVIE_DATA_CHANGE";
    // The class Log identifier
    private static final String LOG_TAG = MovieLoader.class.getSimpleName();
    // The sort order type
    private ESortOrder mSortOrder;

    public MovieLoader(Context context, ESortOrder mSortOrder) {
        super(context);
        // Sort order in use
        this.mSortOrder = mSortOrder;
        // Set the data change observer
        setDataSetChangeObserver(new DataSetChangeBroadCastReceiver(this, new IntentFilter(INTENT_ACTION)));
    }

    @Override
    public List<Movie> loadInBackground() {
        // Return the list of movies from local database
        return getLocalData();
    }

    // Get the list of movies from local
    @Override
    public List<Movie> getLocalData() {
        // Check whether we are in debug mode
        if (BuildConfig.IS_DEBUG_MODE) {
            Log.d(LOG_TAG, "Local data loaded ");
        }
        // Movie repository in use
        MovieRepository mMovieRepository = new MovieRepository();
        if (mSortOrder != null) {
            // Return local list
            return mMovieRepository.getAllWhere(mSortOrder);
        }

        return null;
    }
}