package com.example.flicks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Movie;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieInfo extends AppCompatActivity {

    TextView tvTitle;
    TextView tvOverview;
    TextView tvPopularity;
    ImageView ivPoster;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        //unwrap the movie
        Movie movie =(Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvDescription);
        tvPopularity = (TextView) findViewById(R.id.tvPopularity);

        ivPoster = (ImageView) findViewById(R.id.ivPosterImage);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvPopularity.setText("Popularity: " + movie.getPopularity());

        //load image using glide
        Glide.with(this)
                .load(movie.getPosterPath())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                //.placeholder(placeholderId)
                //.error(placeholderId)
                .into(ivPoster);
    }







}
