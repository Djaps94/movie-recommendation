package com.recommend.movie.controller;

import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.model.User;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.RatingRepository;
import com.recommend.movie.repository.UserRepository;
import com.recommend.movie.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping(value = "rating")
public class RatingController {

    private RatingService ratingService;
    private UserRepository userRepository;
    private MovieRepository movieRepository;
    private RatingRepository ratingRepository;

    private static final Logger log = Logger.getLogger("dsads");

    @Autowired
    public RatingController(RatingService ratingService, UserRepository userRepository, MovieRepository movieRepository, RatingRepository ratingRepository){

        this.ratingService = ratingService;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
    }

    @RequestMapping (
            value = "save",
            method = RequestMethod.POST,
            consumes = "application/json"
    )
    public void saveRating(@RequestBody MovieRating rating){
        ratingService.saveRating(rating);
    }

    @RequestMapping (
            value = "/load/all",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public List<MovieRating> getRatings(){
        List<MovieRating> ratings = ratingService.getRatings();
        for(MovieRating rating : ratings){
            User user = rating.getUser();
            user.getMovieRatings().add(rating);
            userRepository.save(user);
            Movie movie = rating.getMovie();
            movie.getMovieRatings().add(rating);
            movieRepository.save(movie);
            ratingRepository.save(rating);
        }
        return ratings;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{movieID}",
            produces = "application/json"
    )
    @ResponseBody
    public List<MovieRating> getRatings(@PathVariable("movieID") long movieID){
        return ratingService.getMovieRating(movieID);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/rate/{movieID}/{userID}/{rateValue}",
            produces = "text/plain"
    )
    @ResponseBody
    public MovieRating rateMovie(@PathVariable("movieID") long movieID, @PathVariable("userID") long userID, @PathVariable("rateValue") float rate){
        return ratingService.rateMovie(movieID, userID, rate);
    }

}
