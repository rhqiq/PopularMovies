package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscoveryScreenFragment extends Fragment {

    private MovieAdapter gridDiscoveryScreenAdapter;

    public DiscoveryScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.discovery_screen_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_action) {
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_discovery_screen, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movie);


//        gridDiscoveryScreenAdapter = new ArrayAdapter<String>(getActivity(),
//                R.layout.grid_item_movie, R.id.grid_item_forecast_textView, new ArrayList<String>());

        gridDiscoveryScreenAdapter = new MovieAdapter(getActivity(), new ArrayList<HashMap<String, String>>());

        gridview.setAdapter(gridDiscoveryScreenAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                HashMap<String, String> movieDetails = gridDiscoveryScreenAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("movieDetailMap", movieDetails);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String apiKey = prefs.getString(getString(R.string.pref_apikey_key),
                getString(R.string.pref_apikey_default));

        SharedPreferences sortPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sortPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_highest_rated));

        movieTask.execute(apiKey, sortBy);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        /**
         * Take the String representing the complete movies in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private ArrayList getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_RESULTS = "results";
            final String MOVIE_TITLE = "title";
            final String MOVIE_ORIGINAL_TITLE = "original_title";
            final String MOVIE_POSTER_PATH = "poster_path";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_VOTE_AVERAGE = "vote_average";
            final String MOVIE_RELEASE_DATE = "release_date";


            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultArray = moviesJson.getJSONArray(MOVIE_RESULTS);

            ArrayList moviesInfoList = new ArrayList();

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject movieJsonObj = resultArray.getJSONObject(i);

                HashMap movieInfoMap = new HashMap();
                movieInfoMap.put(MOVIE_TITLE, movieJsonObj.getString(MOVIE_TITLE));
                movieInfoMap.put(MOVIE_ORIGINAL_TITLE, movieJsonObj.getString(MOVIE_ORIGINAL_TITLE));
                movieInfoMap.put(MOVIE_POSTER_PATH, getPosterSrc(movieJsonObj.getString(MOVIE_POSTER_PATH), "w185"));
                movieInfoMap.put(MOVIE_OVERVIEW, movieJsonObj.getString(MOVIE_OVERVIEW));
                movieInfoMap.put(MOVIE_VOTE_AVERAGE, movieJsonObj.getString(MOVIE_VOTE_AVERAGE));
                movieInfoMap.put(MOVIE_RELEASE_DATE, movieJsonObj.getString(MOVIE_RELEASE_DATE));
                moviesInfoList.add(movieInfoMap);
            }
            return moviesInfoList;
        }

        private String getPosterSrc(String img, String posterSize) {

            return "http://image.tmdb.org/t/p/" + posterSize + img;
        }


        @Override
        protected ArrayList doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the tehmoviedb query

                final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
                final String API_KEY = "api_key";
                final String SORT_BY = "sort_by";

                Uri buildUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, params[0])
                        .appendQueryParameter(SORT_BY, params[1])
                        .build();

                Log.v("urlllllllll", buildUri.toString());

                URL url = new URL(buildUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList moviesInfoList) {
            if (!moviesInfoList.isEmpty()) {
//                gridDiscoveryScreenAdapter.clear();
                gridDiscoveryScreenAdapter.setMoviesInfoList(moviesInfoList);
/*
                for (int i = 0; i < moviesInfoList.size(); i++) {
                    HashMap tmpData = (HashMap<String, String>) moviesInfoList.get(i);
                    Set<String> key = tmpData.keySet();
                    Iterator it = key.iterator();
                    while (it.hasNext()) {
                        String hmKey = (String) it.next();
                        String hmData = (String) tmpData.get(hmKey);

                        if (hmKey.equals("title")) {
                            gridDiscoveryScreenAdapter.add(hmData.toString());
                        }
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                }
                */
            }
        }
    }
}
