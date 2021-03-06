package com.recommend.movie.recommender;


import com.recommend.movie.model.Movie;
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

    public void initialiseUserJaccardVector(long id){
        user.set(0, id);
        List<MovieRating> ratings = ratingRepository.findAllByUser_id(id);
        ratings.stream().forEach(rating -> {
            if(rating.getRating() >= 2.5)
                user.set(rating.getMovie().getId().intValue(), 1);
            else
                user.set(rating.getMovie().getId().intValue(), 0);
            }
        );
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

    private SparseVector jaccardVectors(int i){
        SparseVector userCompare = new SparseVector(size, size);
        userCompare.set(0, i);
        List<MovieRating> ratings = ratingRepository.findAllByUser_id(((Integer)i).longValue()+1);
        if(ratings.isEmpty())
            return null;
        ratings.stream().forEach((MovieRating rating) -> {
            if(rating.getRating() >= 2.5)
                userCompare.set(rating.getMovie().getId().intValue(), 1);
            else
                userCompare.set(rating.getMovie().getId().intValue(), 0);
        });
        return userCompare;
    }

    private double jaccardSimilarity(SparseVector user, SparseVector target){
        if(target == null)
            return 0;
        double m11 = 0;
        double m00 = 0;
        for(int i = 1; i < user.size(); i++){
            if(user.get(i) == 1 && target.get(i) == 1)
                m11++;
            else if(user.get(i) == 0 && target.get(i) == 0)
                m00++;
        }
        double distance = (user.size() + target.size() - 2*(m11+m00))/(user.size() + target.size() - (m00 + m11));
        return 1-distance;
    }

    public List<Movie> calculatePredictions(){
        Map<Long, Double> result = new HashMap<>();
        for(int i = 0; i < 20; i++){
            double sim = jaccardSimilarity(user, jaccardVectors(i+1));
            result.put(new Long(i+1), sim);
        }
        Map<Long, Double> r = result.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, n) -> o, LinkedHashMap::new));

        List<Movie> movies = new ArrayList<>();
        r.entrySet().stream().map(o -> o.getKey())
                             .limit(3)
                             .forEach(key -> movies.addAll(ratingRepository.getTopMovies(key)));


        return movies;
    }

    public void addToUserVector(int movieId, float rating){
        if(rating >= 2.5)
            user.set(movieId, 1);
        else
            user.set(movieId, 0);
    }
}
