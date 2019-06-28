package com.example.flicks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    //list of movies
    ArrayList<Movie> movies;

    //config needed for image urls
    Config config;

    //context for rendering
    Context context;


    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }


    //creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //get the context and create the inflater
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, viewGroup, false);

        //return a new ViewHolder
        return new ViewHolder(movieView);
    }

    //binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //get the movie data at the specified position
        Movie movie = movies.get(i);

        //populate the view with the movie data
        viewHolder.tvTitle.setText(movie.getTitle());

        viewHolder.tvOverview.setText(movie.getOverview());

        //build url for poster image
        String imageUrl = null;




        //determine the current orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;


        //if in portrait mode, load the poster image
        if(isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        }

        else {
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        //get the correct placeholder and imageview for the current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? viewHolder.ivPosterImage : viewHolder.ivBackdropImage;

        //load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);
    }

    //returns the size of the entire data set
    @Override
    public int getItemCount() {
        return movies.size();
    }



    //create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //Track view objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Log.i("ADAPTER", "entered onClick");

            //get item position
            int pos = getAdapterPosition();

            //make sure that the position is valid, if it exists
            if(pos != RecyclerView.NO_POSITION) {

                Log.i("ADAPTER", "started intent");
                //get the movie at that position
                Movie movie = movies.get(pos);

                //create intent for the new activity
                Intent intent = new Intent(context, MovieInfo.class);

                //Serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

               // intent.putExtra(Config.class.getSimpleName(), Parcels.wrap(config));


                Log.i("ADAPTER", "before launching intent");

                //show the activity
                context.startActivity(intent);
            }
        }


        /**
         *
         *
         *         public void onClick(View v) {
         *             // gets item position
         *             int position = getAdapterPosition();
         *             // make sure the position is valid, i.e. actually exists in the view
         *             if (position != RecyclerView.NO_POSITION) {
         *                 // get the movie at the position, this won't work if the class is static
         *                 Movie movie = movies.get(position);
         *                 // create intent for the new activity
         *                 Intent intent = new Intent(context, MovieDetailsActivity.class);
         *                 // serialize the movie using parceler, use its short name as a key
         *                 intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
         *                 // show the activity
         *                 context.startActivity(intent);
         *             }
         *         }
         */


    }





    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
