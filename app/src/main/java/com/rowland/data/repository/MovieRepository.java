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

package com.rowland.data.repository;

import android.util.Log;

import com.activeandroid.query.Select;
import com.rowland.BuildConfig;
import com.rowland.rest.collections.MovieCollection;
import com.rowland.rest.enums.ESortOrder;
import com.rowland.rest.models.Movie;

import java.util.List;

/**
 * Created by Oti Rowland on 12/22/2015.
 * <p/>
 * Movie Repository
 */
public class MovieRepository {

    // The class Log identifier
    private static final String LOG_TAG = MovieRepository.class.getSimpleName();

    //Default constructor
    public MovieRepository() {

    }

    public List<Movie> getAllWhere(ESortOrder sortOrder) {
        // Holds the where clause
        String whereClause = null;
        // Find out which where clause to use
        switch (sortOrder) {
            case POPULAR_DESCENDING:
                whereClause = "isPopular = ?";
                break;
            case HIGHEST_RATED_DESCENDING:
                whereClause = "isHighestRated = ?";
                break;
            case FAVOURITE_DESCENDING:
                whereClause = "isFavourite = ?";
                break;
        }
        if (whereClause != null) {
            // ToDo: Move this logic to the Movie model where it belongs
            // Query ActiveAndroid for list of data
            List<Movie> queryResults = new Select()
                    .from(Movie.class)
                    .where(whereClause, true)
                    .orderBy("id ASC")
                    .limit(100).execute();
            // This is how you execute a query
            return queryResults;
        }
        return null;
    }

    public Movie getWhereId(long id) {

        Movie queryResult = new Select()
                .from(Movie.class)
                .where("id_ = ?", id)
                .executeSingle();
        // This is how you execute a query
        return queryResult;

    }

    // Save the movie list
    public void saveAll(MovieCollection reviewCollection, ESortOrder sortOrder) {

        for (Movie movie : reviewCollection.getResults()) {
            // Set any necessary details
            movie.setIsHighestRated(sortOrder.isHighestRated());
            movie.setIsFavourite(sortOrder.isFavourite());
            movie.setIsPopular(sortOrder.isPopular());
            // Check if is duplicate
            boolean iSExistingMovie = new Select()
                    .from(Movie.class)
                    .where("id_ = ?", movie.getId_()).exists();
            // Check whether we are in debug mode
            if (BuildConfig.IS_DEBUG_MODE) {
                Log.d(LOG_TAG, "Movie: " + iSExistingMovie);
            }
            // Save only new movies to the database
            if (!iSExistingMovie) {
                // Save movie
                movie.save();
                // Check whether we are in debug mode
                if (BuildConfig.IS_DEBUG_MODE) {
                    Log.d(LOG_TAG, "Movie: " + movie.getTitle());
                    Log.d(LOG_TAG, "Movie: " + movie.getReleaseDate());
                    Log.d(LOG_TAG, "Movie: " + movie.getId());
                    Log.d(LOG_TAG, "Movie HighestRated: " + movie.getIsHighestRated());
                    Log.d(LOG_TAG, "Movie Favourite: " + movie.getIsFavourite());
                    Log.d(LOG_TAG, "Movie Popular: " + movie.getIsPopular());
                }
            }
        }
    }
}
