package com.recommend.movie.service;


import com.recommend.movie.model.MovieRating;

import java.util.List;

public interface RatingService {

    void saveRating(MovieRating rating);

    List<MovieRating> getRatings();

    List<MovieRating> getMovieRating(long movieID);

    MovieRating rateMovie(long movieID, long userID, float rate);
}
