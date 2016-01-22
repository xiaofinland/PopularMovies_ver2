package com.example.makayo.popularmovies_v2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.example.makayo.popularmovies_v2.dataModel.Movie;

/**
 * Created by Xiao on 20/11/2015.
 */
public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null && null != getIntent()) {
            Movie movie = getIntent().getParcelableExtra("movie");
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", movie);
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container,detailFragment)
                    .commit();
        }
    }

}
