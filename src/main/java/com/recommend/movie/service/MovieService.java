package com.recommend.movie.service;


import com.recommend.movie.model.Movie;

import java.util.List;
import java.util.Set;

public interface MovieService {

    void save(Movie movie);

    Movie getMovieByTitle(String title);

    Movie getMoviesByGenres(String genre);

    List<Movie> getMoviesOffset(int startCount);

    List<Movie> getMovies();

    List<Movie> getSimliarMovies(long id);

    List<Movie> searchMovie(int pageNumber, String title);

    List<Movie> topRated(int pageNumber);

    Set<Movie> ratedMovies();


}
