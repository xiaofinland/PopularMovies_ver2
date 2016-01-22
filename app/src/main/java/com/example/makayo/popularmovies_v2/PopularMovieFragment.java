package com.example.makayo.popularmovies_v2;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.makayo.popularmovies_v2.adapter.MovieAdapter;
import com.example.makayo.popularmovies_v2.dataModel.Movie;
import com.example.makayo.popularmovies_v2.util.CommonAsyncTask;
import com.example.makayo.popularmovies_v2.util.Constants;
import com.example.makayo.popularmovies_v2.util.onTaskCompleted;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiao on 20/12/2015.
 */
public class PopularMovieFragment extends Fragment implements onTaskCompleted, AdapterView.OnItemClickListener {
    private ArrayList<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private GridView gridView;
    private int pageCount=1;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean itemClicked = false;

    public static PopularMovieFragment newInstance() {
        PopularMovieFragment fragment = new PopularMovieFragment();
        return fragment;
    }
    public PopularMovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState !=null){
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView)rootView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public int currentScrollState;
            public int currentFirstVisibleItem;
            public int currentVisibleItemCount;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    this.currentFirstVisibleItem = firstVisibleItem;
                    this.currentVisibleItemCount = visibleItemCount;
                }
            }


            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            private void isScrollCompleted() {
                if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
                    pageCount++;
                    CommonAsyncTask asyncTask = new CommonAsyncTask(PopularMovieFragment.this, Constants.MOST_POPULAR_REQUEST);
                    asyncTask.execute(pageCount + "");
                }
            }
        });
        movieAdapter = new MovieAdapter(getActivity(), movieList);
        gridView.setAdapter(movieAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            gridView.setNumColumns(3);
        }else{
            gridView.setNumColumns(2);
        }
        if(movieList==null || movieList.size()==0) {
            CommonAsyncTask asyncTask = new CommonAsyncTask(this, Constants.MOST_POPULAR_REQUEST);
            asyncTask.execute(pageCount + "");
        }
        else if(movieList!=null && movieList.size()>0 && ((MainActivity)getActivity()).mTwoPane) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, DetailFragment.newInstance(movieList.get(0)), DETAILFRAGMENT_TAG)
                    .commit();
            itemClicked = true;
        }
        getActivity().setTitle("Popular Movies");

    }


    @Override
    public void onTaskCompleted(Object object) {
        if(null!=object) {
            movieList.addAll((ArrayList<Movie>) object);
            movieAdapter.notifyDataSetChanged();
            if(((MainActivity)getActivity()).mTwoPane && !itemClicked) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, DetailFragment.newInstance(movieList.get(0)), DETAILFRAGMENT_TAG)
                        .commit();
                itemClicked = true;
            }
        }else{
            Toast.makeText(getActivity(), "Ops, error occured", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(((MainActivity)getActivity()).mTwoPane) {
            DetailFragment detailFragment = DetailFragment.newInstance(movieList.get(i));
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        }else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("movie", movieList.get(i));
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) movieList);
    }

}
