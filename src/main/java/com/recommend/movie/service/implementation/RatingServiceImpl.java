package com.recommend.movie.service.implementation;


import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.model.User;
import com.recommend.movie.recommender.CosineSimilarity;
import com.recommend.movie.recommender.EuclideanSimilarity;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.RatingRepository;
import com.recommend.movie.repository.UserRepository;
import com.recommend.movie.service.RatingService;
import com.recommend.movie.util.RatingDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatingServiceImpl implements RatingService{

    private RatingRepository ratingRepository;
    private RatingDataset ratingDataset;
    private UserRepository userRepository;
    private MovieRepository movieRepository;
    private CosineSimilarity cosineSimilarity;


    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, RatingDataset ratingDataset, MovieRepository movieRepository, UserRepository userRepository, CosineSimilarity cosineSimilarity, EuclideanSimilarity euclideanSimilarity){
        this.ratingRepository = ratingRepository;
        this.ratingDataset = ratingDataset;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.cosineSimilarity = cosineSimilarity;

    }

    @Override
    public void saveRating(MovieRating rating) {
        ratingRepository.save(rating);
    }

    public List<MovieRating> getRatings(){
        List<MovieRating> ratings = ratingDataset.createRatings();
        return ratings;
    }

    @Override
    public List<MovieRating> getMovieRating(long movieID) {
        return ratingRepository.findByMovie_id(movieID);
    }

    @Override
    public MovieRating rateMovie(long movieID, long userID, float rate) {

        if(ratingRepository.existsByMovie_idAndUser_id(movieID, userID)){
            return null;
        }

        Optional<Movie> movie = movieRepository.findById(movieID);
        Optional<User> user = userRepository.findById(userID);

        if(!user.isPresent() || !movie.isPresent())
            return null;

        MovieRating newMovieRating = new MovieRating();
        newMovieRating.setUser(user.get());
        newMovieRating.setMovie(movie.get());
        newMovieRating.setRating(rate);

        user.get().getMovieRatings().add(newMovieRating);
        userRepository.save(user.get());

        movie.get().getMovieRatings().add(newMovieRating);
        movieRepository.save(movie.get());

        ratingRepository.save(newMovieRating);

        if(rate >= 3)
            cosineSimilarity.addToProfile(movieID);



        return newMovieRating;
    }

}
