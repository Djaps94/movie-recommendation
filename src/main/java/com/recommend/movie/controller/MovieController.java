package com.recommend.movie.controller;


import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.recommender.CosineSimilarity;
import com.recommend.movie.service.MovieService;
import com.recommend.movie.util.MovieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


@Controller
@RequestMapping(value = "movies")
public class MovieController {

    private MovieService movieService;
    private MovieDataset movieDataset;


    private static final Logger log = Logger.getLogger("dsads");

    @Autowired
    public MovieController(MovieService movieService, MovieDataset movieDataset) {

        this.movieService = movieService;
        this.movieDataset = movieDataset;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/all",
            produces = "application/json"
    )
    @ResponseBody
    public List<Movie> getMovies(){
        List<Movie> movies = movieService.getMovies();
        for (Movie movie : movies) {
            movieService.save(movie);
        }

        return movies;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/offset/{start}",
            produces = "application/json"
    )
    @ResponseBody
    public List<Movie> getOffsetMovies(@PathVariable("start") int pageNumber) {
        return movieService.getMoviesOffset(pageNumber);
    }

    @RequestMapping (
            method = RequestMethod.GET,
            value = "/similar/{movieId}",
            produces = "application/json"
    )
    @ResponseBody
    public List<Movie> getSimilarMovies(@PathVariable("movieId") long id) {
        return movieService.getSimliarMovies(id);
    }


    @RequestMapping(
            method = RequestMethod.GET,
            value = "/search/{pageNumber}/{movieTitle}",
            produces = "application/json"
    )
    @ResponseBody
    public List<Movie> searchMovie(@PathVariable("pageNumber") int pageNumber,@PathVariable("movieTitle") String movieTitle){
        return movieService.searchMovie(pageNumber,movieTitle);
    }


    @RequestMapping(
            method = RequestMethod.GET,
            value = "/topRated/{pageNumber}",
            produces = "application/json"
    )
    @ResponseBody
    public List<Movie> topRated(@PathVariable("pageNumber") int pageNumber){
        return movieService.topRated(pageNumber);
    }

    @RequestMapping (
            method = RequestMethod.GET,
            value = "/recommended",
            produces = "application/json"
    )
    @ResponseBody
    public Set<Movie> getRecommended(){
        return movieService.ratedMovies();
    }


}
