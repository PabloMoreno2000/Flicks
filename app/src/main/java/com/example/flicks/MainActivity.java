package com.example.flicks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    //constants

    //the base URL of the API
    public final static String API_BASE_URL ="https://api.themoviedb.org/3";

    //The parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";


    //instance fields
    AsyncHttpClient client;



    //Tag for logging from this activity
    public final static String TAG = "MainActivity";

    //the list of currently playing movies
    ArrayList<Movie> movies;

    //the recycler view
    RecyclerView rvMovies;

    //the adapter wired to the recycler view
    MovieAdapter movieAdapter;

    //iamge config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the client
        client = new AsyncHttpClient();

        //Initializing the list of movies
        movies = new ArrayList<>();

        //initialize the adapter --movies array cannot be reinitialized after this point
        movieAdapter = new MovieAdapter(movies);

        //resolve the recycler view and connect a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(movieAdapter);

        //get the configuration on app creation
        getConfiguration();







    }


    //get the list of currently playing movies from the API
    private void getNowPlaying() {
        //Creating the url
        String url = API_BASE_URL + "/movie/now_playing";

        //set the request parameters
        RequestParams params = new RequestParams();

        //Putting keys and values of the parameters
        params.put(API_KEY_PARAM, getString(R.string.api_key)); //API always required

        //executing a GET request expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load the results into movies list
                try {
                    JSONArray results = response.getJSONArray("results");

                    //iterate through result set and create movie objects
                    for(int i = 0; i < results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));

                        movies.add(movie);

                        //notify the adapter that a row was added
                        movieAdapter.notifyItemInserted(movies.size() - 1);
                    }
                    //i is for information
                    // %s is a placeholder for results.length()
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));

                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }


    //get the configuration from the api
    private void getConfiguration() {

        //Creating the url
        String url = API_BASE_URL + "/configuration";

        //set the request parameters
        RequestParams params = new RequestParams();

        //Putting keys and values of the parameters
        params.put(API_KEY_PARAM, getString(R.string.api_key)); //API always required

        //executing GET request expecting JSON object
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    config = new Config(response);

                    Log.i(TAG, String.format("loaded configuration with imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));


                    movieAdapter.setConfig(config);
                    /**get the new playing movie list
                    we will put this here because we first need to finish
                    getConfiguration, and sometimes these methods does not
                     execute in order **/
                    getNowPlaying();

                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });



    }


    //handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        //always log the error
        Log.e(TAG, message, error);

        //alert the user to avoid silent errors
        if(alertUser) {
            //show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        }
    }

}
