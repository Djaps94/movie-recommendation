package com.recommend.movie.service.implementation;

import com.recommend.movie.model.Movie;
import com.recommend.movie.recommender.CosineSimilarity;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.service.MovieService;
import com.recommend.movie.util.MovieDataset;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;
    private MovieDataset movieDataset;
    private CosineSimilarity cosineSimilarity;

    public MovieServiceImpl(MovieRepository movieRepository, MovieDataset movieDataset, CosineSimilarity cosineSimilarity){
        this.movieRepository = movieRepository;
        this.movieDataset = movieDataset;
        this.cosineSimilarity = cosineSimilarity;
    }

    @Override
    public void save(Movie movie) {
        movieRepository.save(movie);
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
    public List<Movie> getMovies() {
        return movieDataset.load();
    }

    @Override
    public List<Movie> getSimliarMovies(long id) {
        cosineSimilarity.addToSeen(id);
        cosineSimilarity.calculateUserSeen();
        return cosineSimilarity.calculatePredictionSeen();
    }

    @Override
    public List<Movie> getMoviesOffset(int pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, 20, Sort.Direction.ASC, "title");
        return movieRepository.findAllByTitleNotNull(pageable);

    }

    @Override
    public List<Movie> searchMovie(int pageNumber, String title) {
        Pageable pageable = new PageRequest(pageNumber, 20, Sort.Direction.ASC, "title");
        return movieRepository.findByTitleIgnoreCaseContainingOrderByTitle(title,pageable);
    }



}
