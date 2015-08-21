/*
 * Copyright (C) 2015 The Android Open Source Project
 */

package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    /** get the intent object and populate the detail view*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movieDetailMap")) {
            HashMap<String, String> movieDetail = (HashMap<String, String>) intent.getSerializableExtra("movieDetailMap");

            ((TextView) rootView.findViewById(R.id.detail_title_textView))
                    .setText(movieDetail.get(MOVIE_ORIGINAL_TITLE));

            ((TextView) rootView.findViewById(R.id.detail_releaseDate_textView))
                    .setText(movieDetail.get(MOVIE_RELEASE_DATE));

            ((TextView) rootView.findViewById(R.id.detail_voteAverage_textView))
                    .setText(movieDetail.get(MOVIE_VOTE_AVERAGE));

            ((TextView) rootView.findViewById(R.id.detail_overview_textView))
                    .setText(movieDetail.get(MOVIE_OVERVIEW));

            ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_poster_imageView);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500" + movieDetail.get(MOVIE_POSTER_PATH)).into(imageView);
        }
        return rootView;
    }
}
