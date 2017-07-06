package com.recommend.movie.service;


import com.recommend.movie.model.Movie;

import java.util.List;

public interface MovieService {

    void save(Movie movie);

    Movie getMovieByTitle(String title);

    Movie getMoviesByGenres(String genre);

    List<String> getMovies();
}
