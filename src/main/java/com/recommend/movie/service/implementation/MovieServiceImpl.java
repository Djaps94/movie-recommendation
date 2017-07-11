package com.recommend.movie.service.implementation;

import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.recommender.CosineSimilarity;
import com.recommend.movie.recommender.EuclideanSimilarity;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.RatingRepository;
import com.recommend.movie.service.MovieService;
import com.recommend.movie.tasks.FetchTopRatedTask;
import com.recommend.movie.util.MovieDataset;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;
    private MovieDataset movieDataset;
    private CosineSimilarity cosineSimilarity;
    private RatingRepository ratingRepository;
    private EuclideanSimilarity euclideanSimilarity;

    private static final Logger log = Logger.getLogger("dsads");

    public MovieServiceImpl(MovieRepository movieRepository, MovieDataset movieDataset, CosineSimilarity cosineSimilarity, RatingRepository ratingRepository, EuclideanSimilarity euclideanSimilarity){
        this.movieRepository = movieRepository;
        this.movieDataset = movieDataset;
        this.ratingRepository = ratingRepository;
        this.cosineSimilarity = cosineSimilarity;
        this.euclideanSimilarity = euclideanSimilarity;
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
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ConcurrentHashMap<Movie, Double> movieRatings = new ConcurrentHashMap<>();
        int i;
        for(i = 0; i < 100; i++) {
            if (!ratingRepository.existsByMovie_id(new Long(i + 1)))
                continue;

            exec.execute(new FetchTopRatedTask(i,movieRatings, ratingRepository));

        }

        HashMap<Movie, Double> sorted = movieRatings.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                                                                        .collect(Collectors.toMap(  Map.Entry::getKey,
                                                                                                    Map.Entry::getValue,
                                                                                                    (e1, e2) -> e1,
                                                                                                    LinkedHashMap::new
                                                                                            ));


        List<Movie> moviesToReturn = sorted.entrySet().stream().map(o -> o.getKey())
                .limit(10)
                .collect(Collectors.toList());


        return moviesToReturn;
    }

    public Set<Movie> ratedMovies(){
        List<Movie> jaccardMovie = euclideanSimilarity.calculatePredictions();
        Set<Movie> movies = new HashSet<>();
        movies.addAll(cosineSimilarity.calculatePrediction());
        for(int i = 0; i < 4; i++){
            int rnd = new Random().nextInt(jaccardMovie.size());
            movies.add(jaccardMovie.get(rnd));
        }
        return movies;
    }


}
