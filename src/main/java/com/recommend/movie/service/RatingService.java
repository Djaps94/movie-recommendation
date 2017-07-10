package com.recommend.movie.service;


import com.recommend.movie.model.MovieRating;

import java.util.List;

public interface RatingService {

    void saveRating(MovieRating rating);

    public List<MovieRating> getRatings();

    public List<MovieRating> getMovieRating(long movieID);

    public String rateMovie(long movieID, long userID, float rate);
}
