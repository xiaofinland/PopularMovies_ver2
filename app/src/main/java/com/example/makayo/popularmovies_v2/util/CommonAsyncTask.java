package com.example.makayo.popularmovies_v2.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.makayo.popularmovies_v2.BuildConfig;
import com.example.makayo.popularmovies_v2.PopularMovieFragment;
import com.example.makayo.popularmovies_v2.ReviewFragment;
import com.example.makayo.popularmovies_v2.TopRatedMovieFragment;
import com.example.makayo.popularmovies_v2.TrailerFragment;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

/**
 * Created by Xiao on 02/12/2015.
 */
public class CommonAsyncTask extends AsyncTask<String,Void,Object> {
    private Fragment fragment;
    private int request_type;

    public CommonAsyncTask(Fragment fragment, int request_type) {
        this.fragment = fragment;
        this.request_type = request_type;
    }

    @Override
    protected Object doInBackground(String... strings) {
        String page = "1";
        if (null != strings && strings.length > 0)
            page = strings[0];
        Uri.Builder builder = null;

        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
        if (request_type == Constants.MOST_POPULAR_REQUEST) {
            builder = Uri.parse(Constants.REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY)
                    .appendQueryParameter("page", page)
                    .appendQueryParameter("sort_by", "popularity.desc");

        } else if (request_type == Constants.TOP_RATED_REQUEST) {
            builder = Uri.parse(Constants.REQUEST_BASE_URL).buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY)
                    .appendQueryParameter("page", page)
                    .appendQueryParameter("sort_by", "vote_average.desc");

        } else if (request_type == Constants.VIDEO_TRAILER_REQUEST) {
            String movieId = fragment.getArguments().getString(TrailerFragment.MOVIE_ID);
            builder = Uri.parse(Constants.TRAILER_REVIEWS_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY);

        } else if (request_type == Constants.MOVIE_REVIEWS_REQUEST) {
            String movieId = fragment.getArguments().getString(TrailerFragment.MOVIE_ID);
            builder = Uri.parse(Constants.TRAILER_REVIEWS_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY)
                    .appendQueryParameter("page", page);
        }

            Uri uri = builder.build();

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .build();

            Response response = null;
            List<Object> list = null;
            JSONParser parser = new JSONParser();
            try {
                response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    return null;
                }
                String json = response.body().string();
                if (request_type == Constants.MOST_POPULAR_REQUEST || request_type == Constants.TOP_RATED_REQUEST) {
                    list = parser.parseMovieList(json);
           }else if(request_type == Constants.VIDEO_TRAILER_REQUEST) {
                list = parser.parseMovieTrailers(json);
            }else if(request_type == Constants.MOVIE_REVIEWS_REQUEST) {
                list = parser.parseMovieReviews(json);

                }

                System.out.println("Response " + json);

            } catch (IOException e) {
                Log.e("CommonAsyncTask", "Exception", e);
            }

            return list;
        }

        @Override
        protected void onPostExecute (Object object){
            super.onPostExecute(object);
            if (request_type == Constants.MOST_POPULAR_REQUEST)
                ((PopularMovieFragment) fragment).onTaskCompleted(object);
        else if(request_type == Constants.TOP_RATED_REQUEST)
            ((TopRatedMovieFragment)fragment).onTaskCompleted(object);
        else if(request_type == Constants.VIDEO_TRAILER_REQUEST)
            ((TrailerFragment)fragment).onTaskCompleted(object);
        else if(request_type == Constants.MOVIE_REVIEWS_REQUEST) {
            ((ReviewFragment)fragment).onTaskCompleted(object);
        }

        }

}
