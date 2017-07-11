package com.recommend.movie.service.implementation;

import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.recommender.CosineSimilarity;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.RatingRepository;
import com.recommend.movie.service.MovieService;
import com.recommend.movie.util.MovieDataset;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;
    private MovieDataset movieDataset;
    private CosineSimilarity cosineSimilarity;
    private RatingRepository ratingRepository;

    private static final Logger log = Logger.getLogger("dsads");

    public MovieServiceImpl(MovieRepository movieRepository, MovieDataset movieDataset, CosineSimilarity cosineSimilarity, RatingRepository ratingRepository){
        this.movieRepository = movieRepository;
        this.movieDataset = movieDataset;
        this.ratingRepository = ratingRepository;
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

    @Override
    public List<Movie> topRated(int pageNumber) {

        log.info("Usao sam u top rated");

        List<Movie> movies = movieRepository.findAll();
        HashMap<Movie, Float> movieRatings = new HashMap<Movie, Float>();

        for(Movie m : movies){
            log.info("U FORU SAM");
            List<MovieRating> ratings = ratingRepository.findByMovie_id(m.getId());

            float sum = 0;
            for(MovieRating mr : ratings){
                sum += mr.getRating();
            }

            movieRatings.put(m, sum / ratings.size());
        }

        log.info("IZASAO IZ FORA");

        HashMap<Movie, Float> sorted = movieRatings.entrySet().stream().
                sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        List<Movie> movesToReturn = new ArrayList<Movie>();
        int i = 0;

        for(Movie m : sorted.keySet()){

            if(i >= 20*pageNumber+20){
                break;
            }

            if(i >= 20*pageNumber){
                movesToReturn.add(m);
            }

            i++;
        }

        return movesToReturn;
    }


}
