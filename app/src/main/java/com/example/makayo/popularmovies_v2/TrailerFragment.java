package com.example.makayo.popularmovies_v2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.makayo.popularmovies_v2.adapter.TrailerAdapter;
import com.example.makayo.popularmovies_v2.dataModel.Trailer;
import com.example.makayo.popularmovies_v2.util.CommonAsyncTask;
import com.example.makayo.popularmovies_v2.util.Constants;
import com.example.makayo.popularmovies_v2.util.onTaskCompleted;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiao on 02/01/2016.
 */
public class TrailerFragment extends ListFragment implements onTaskCompleted {
    private static final String LOG_TAG = TrailerFragment.class.getSimpleName();

    public static final String MOVIE_ID = "movie_id";
    private String mParam;
    private ArrayList<Trailer> trailerList = new ArrayList<>();
    private TrailerAdapter mTrailerAdapter;

    public static TrailerFragment newInstance(String param) {
        TrailerFragment fragment = new TrailerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_ID,param);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrailerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mParam = getArguments().getString(MOVIE_ID);
            CommonAsyncTask asyncTask = new CommonAsyncTask(this, Constants.VIDEO_TRAILER_REQUEST);
            asyncTask.execute();
        }

        mTrailerAdapter = new TrailerAdapter(getActivity(),trailerList);
        setListAdapter(mTrailerAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Trailer trailer = trailerList.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }else {
            Intent ytIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOU_TUBE_BASE_URL + trailer.getKey()));
            startActivity(ytIntent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getListView().setDivider(new ColorDrawable(Color.BLACK));
        getListView().setDividerHeight(1);
    }

    @Override
    public void onTaskCompleted(Object object) {
        if(null!=object) {
            trailerList.addAll((List<Trailer>) object);
            mTrailerAdapter.notifyDataSetChanged();
            if(trailerList.size()==0){
                setEmptyText("No Trailers Found");
            }else{
                ((DetailFragment)getParentFragment()).setShareIntent("Watch the trailer - "+ Constants.YOU_TUBE_BASE_URL + trailerList.get(0).getKey());
                Log.i(LOG_TAG, "text to be passed for sharing"+Constants.YOU_TUBE_BASE_URL + trailerList.get(0).getKey() );
            }
        }else{
            Toast.makeText(getActivity(), "Ops, error occured", Toast.LENGTH_SHORT).show();
        }
    }





}
