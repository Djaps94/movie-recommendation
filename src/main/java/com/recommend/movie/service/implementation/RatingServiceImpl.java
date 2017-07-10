package com.recommend.movie.service.implementation;


import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.model.User;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.RatingRepository;
import com.recommend.movie.repository.UserRepository;
import com.recommend.movie.service.RatingService;
import com.recommend.movie.util.RatingDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService{

    private RatingRepository ratingRepository;
    private RatingDataset ratingDataset;
    private UserRepository userRepository;
    private MovieRepository movieRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, RatingDataset ratingDataset, MovieRepository movieRepository, UserRepository userRepository){
        this.ratingRepository = ratingRepository;
        this.ratingDataset = ratingDataset;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
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
    public String rateMovie(long movieID, long userID, float rate) {
        MovieRating oldRating = ratingRepository.findByMovie_idAndUser_id(movieID, userID);

        if(oldRating != null){
            return "Already rated!";
        }

        Movie movie = movieRepository.findOne(movieID);
        User user = userRepository.findOne(userID);

        MovieRating newMovieRating = new MovieRating();
        newMovieRating.setUser(user);
        newMovieRating.setMovie(movie);
        newMovieRating.setRating(rate);

        user.getMovieRatings().add(newMovieRating);
        userRepository.save(user);

        movie.getMovieRatings().add(newMovieRating);
        movieRepository.save(movie);

        ratingRepository.save(newMovieRating);

        return "Successfully rated!";
    }
}
