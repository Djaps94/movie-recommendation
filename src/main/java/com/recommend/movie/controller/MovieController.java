package com.recommend.movie.controller;


import com.recommend.movie.model.Movie;
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
import java.util.logging.Logger;


@Controller
@RequestMapping(value = "movies")
public class MovieController {

    private MovieService movieService;
    private MovieDataset movieDataset;
    private CosineSimilarity cosineSimilarity;

    private static final Logger log = Logger.getLogger("dsads");

    @Autowired
    public MovieController(MovieService movieService, MovieDataset movieDataset, CosineSimilarity cosineSimilarity) {

        this.movieService = movieService;
        this.movieDataset = movieDataset;
        this.cosineSimilarity = cosineSimilarity;
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
}
