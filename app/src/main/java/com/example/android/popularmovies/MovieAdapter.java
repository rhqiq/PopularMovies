package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MovieAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> moviesInfoList;
    private ArrayList<String> movieTitle = new ArrayList<String>() ;
    private ArrayList<String> moviePosterSrc = new ArrayList<String>() ;

    public MovieAdapter(Context context, ArrayList<HashMap<String, String>> moviesInfoList) {
        this.context = context;
        this.moviesInfoList = moviesInfoList;
    }

    public void setMoviesInfoList(ArrayList<HashMap<String, String>> moviesInfoList) {
        this.moviesInfoList = moviesInfoList;
        setSource();
        notifyDataSetChanged();
    }

    private void setSource() {

        movieTitle.clear();
        moviePosterSrc.clear();

        for (int i = 0; i < moviesInfoList.size(); i++) {
            HashMap tmpData = (HashMap<String, String>) moviesInfoList.get(i);
            Set<String> key = tmpData.keySet();
            Iterator it = key.iterator();
            while (it.hasNext()) {
                String hmKey = (String) it.next();
                String hmData = (String) tmpData.get(hmKey);

                if (hmKey.equals("title")) {
                    movieTitle.add(hmData.toString());
                }

                if (hmKey.equals("poster_path")) {
                    moviePosterSrc.add(hmData.toString());
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.grid_item_movie, null);

            // set value into textview
            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_movie_textView);
            textView.setText(movieTitle.get(position));

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_movie_imageView);

            Picasso.with(context).load(moviePosterSrc.get(position)).into(imageView);

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return moviesInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}