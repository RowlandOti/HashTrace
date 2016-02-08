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

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.rowland.BuildConfig;
import com.rowland.data.loaders.MovieLoader;
import com.rowland.data.repository.MovieRepository;
import com.rowland.rest.collections.MovieCollection;
import com.rowland.rest.enums.ESortOrder;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Oti Rowland on 12/21/2015.
 */
public class MovieCallBack implements Callback<MovieCollection> {

    // The class Log identifier
    private static final String LOG_TAG = MovieCallBack.class.getSimpleName();
    // Context instance
    private Context context;
    // Our sort order
    private ESortOrder mSortOrder;

    public MovieCallBack(Context context, ESortOrder sortOrder) {
        this.context = context;
        this.mSortOrder = sortOrder;
    }

    @Override
    public void onResponse(Response<MovieCollection> response, Retrofit retrofit) {
        // Check status of response before proceeding
        if (response.isSuccess()) {
            // Collection available
            MovieCollection reviewCollection = response.body();
            // MovieRepository instance
            MovieRepository mMovieRepository = new MovieRepository();
            // Save movies to data storage
            mMovieRepository.saveAll(reviewCollection, mSortOrder);
            // BroadCast the changes locally
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MovieLoader.INTENT_ACTION));
        } else {
            // Check whether we are in debugging mode;
            if (BuildConfig.IS_DEBUG_MODE) {
                // we got an error message - Do error handling here
                Log.d(LOG_TAG, response.errorBody().toString());
            }
        }
    }

    @Override
    public void onFailure(Throwable t) {
        // Inform user of failure due to no network e.t.c
        //Log.d(LOG_TAG, t.getMessage());
    }
}
