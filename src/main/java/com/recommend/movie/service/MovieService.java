package com.recommend.movie.service;


import com.recommend.movie.model.Movie;

import java.util.List;

public interface MovieService {

    void save(Movie movie);

    Movie getMovieByTitle(String title);

    Movie getMoviesByGenres(String genre);

    List<Movie> getMoviesOffset(int startCount);

    List<Movie> getMovies();

    public List<Movie> getSimliarMovies(long id);

    public List<Movie> searchMovie(int pageNumber, String title);

}
