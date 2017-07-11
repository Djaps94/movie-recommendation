package com.recommend.movie.recommender;


import com.recommend.movie.model.MovieRating;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.RatingRepository;
import com.recommend.movie.repository.UserRepository;
import no.uib.cipr.matrix.sparse.SparseVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Component
public class EuclideanSimilarity {

    private Logger logger = LoggerFactory.getLogger("Euclid");
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
        users = new ArrayList<>();
        for(int i = 0; i < ((Long)userRepository.count()).intValue()/3; i++)
            users.add(new SparseVector(size, size));

        user = new SparseVector(size, size);
        initialiseUsersVectors();
    }


    public void initialiseUserVector(long id){
        user.set(0, id);
        List<MovieRating> ratings = ratingRepository.findAllByUser_id(id);
        ratings.stream().forEach(rating -> user.set(rating.getMovie().getId().intValue(), rating.getRating()));
    }

    private SparseVector initialiseUsersVectors(int i, int size){
            SparseVector userCompare = new SparseVector(size, size);
            userCompare.set(0, i);
            List<MovieRating> ratings = ratingRepository.findAllByUser_id(((Integer)i).longValue()+1);
            if(ratings == null)
                return null;
            ratings.stream().forEach(rating -> userCompare.set(rating.getMovie().getId().intValue(), rating.getRating()));
            return userCompare;
    }



    public List<SparseVector> test(){
        return users;
    }

}
