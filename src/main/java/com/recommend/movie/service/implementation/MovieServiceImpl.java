package com.recommend.movie.service.implementation;

import com.recommend.movie.model.Movie;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.service.MovieService;
import com.recommend.movie.util.MovieDataset;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;
    private MovieDataset movieDataset;

    public MovieServiceImpl(MovieRepository movieRepository, MovieDataset movieDataset){
        this.movieRepository = movieRepository;
        this.movieDataset = movieDataset;
    }

    @Override
    public void save(Movie movie) {

    }

    @Override
    public Movie getMovieByTitle(String title) {
        return null;
    }

    @Override
    public Movie getMoviesByGenres(String genre) {
        return null;
    }

    @Override
    public List<String> getMovies() {
        return movieDataset.createMovies();
    }
}
