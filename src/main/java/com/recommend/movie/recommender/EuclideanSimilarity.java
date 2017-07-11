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
import java.util.*;
import java.util.stream.Collectors;


@Component
public class EuclideanSimilarity {

    private Logger logger = LoggerFactory.getLogger("Euclid");
    private List<SparseVector> users;
    private SparseVector user;
    private int size;


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
        size = ((Long)movieRepository.count()).intValue()+1;
        users = new ArrayList<>();
        for(int i = 0; i < ((Long)userRepository.count()).intValue()/3; i++)
            users.add(new SparseVector(size, size));

        user = new SparseVector(size, size);
    }


    public void initialiseUserVector(long id){
        user.set(0, id);
        List<MovieRating> ratings = ratingRepository.findAllByUser_id(id);
        ratings.stream().forEach(rating -> user.set(rating.getMovie().getId().intValue(), rating.getRating()));
    }

    private SparseVector initialiseUsersVectors(int i){
            SparseVector userCompare = new SparseVector(size, size);
            userCompare.set(0, i);
            List<MovieRating> ratings = ratingRepository.findAllByUser_id(((Integer)i).longValue()+1);
            if(ratings.isEmpty())
                return null;
            ratings.stream().forEach(rating -> userCompare.set(rating.getMovie().getId().intValue(), rating.getRating()));
            return userCompare;
    }

    private double euclideanSimilarity(SparseVector user, SparseVector target){
        if(target == null)
            return 0;
        int sum = 0;
        for(int i = 1; i < user.size(); i++){
            sum += Math.pow(user.get(i) - target.get(i), 2);
        }
        double distance = Math.sqrt(sum);
        return 1/(1+distance);
    }

    public Map<Integer, Double> calculatePredictions(){
        Map<Integer, Double> result = new HashMap<>();
        for(int i = 100; i < 120; i++){
            double sim = euclideanSimilarity(initialiseUsersVectors(1), initialiseUsersVectors(i+1));
            result.put(i+1, sim);
        }
        Map r = result.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, n) -> o, LinkedHashMap::new));
        return r;
    }

    public Map<Integer, Double> test(){
        return calculatePredictions();
    }

}
