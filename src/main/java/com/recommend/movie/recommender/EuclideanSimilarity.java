package com.recommend.movie.recommender;


import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.RatingRepository;
import com.recommend.movie.repository.UserRepository;
import no.uib.cipr.matrix.sparse.SparseVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class EuclideanSimilarity {

    private List<SparseVector> users;
    private SparseVector user;


    private UserRepository userRepository;
    private RatingRepository ratingRepository;
    private MovieRepository movieRepository;

    @Autowired
    public EuclideanSimilarity(UserRepository userRepository, RatingRepository ratingRepository, MovieRepository movieRepository){
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.movieRepository = movieRepository;
    }

    @PostConstruct
    private void init(){
        int size = ((Long)movieRepository.count()).intValue()+1;
        users = new ArrayList<>(((Long)userRepository.count()).intValue());
        user = new SparseVector(size, size);
    }


    public void initialiseUserVector(long id){
        user.set(0, id);
        List<MovieRating> ratings = ratingRepository.findAllByUser_id(id);
        ratings.stream().forEach(rating -> user.set(rating.getMovie().getId().intValue(), rating.getRating()));
    }

}
