package com.recommend.movie.util;

import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.model.User;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RatingDataset {

    private Logger LOGGER = LoggerFactory.getLogger("");
    private UserRepository userRepository;
    private MovieRepository movieRepository;

    @Autowired
    public RatingDataset(UserRepository userRepository, MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public List<MovieRating> createRatings(){
        List<MovieRating> ratings = new ArrayList<>();
        File file = new File("/home/predrag/Code/movie-recommendation/movie-dataset/ratings.csv");
        try{
            CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);
            for(CSVRecord record : parser.getRecords()){
                if(record.get(0).equals("userId"))
                    continue;
                Optional<User> opt = userRepository.findById(Long.parseLong(record.get(0)));
                Optional<Movie> optM = movieRepository.findById(Long.parseLong(record.get(1)));

                if(opt.isPresent() && optM.isPresent()){
                    LOGGER.info("Here");
                    MovieRating rating = new MovieRating();
                    rating.setRating(Float.parseFloat(record.get(2)));
                    rating.setUser(opt.get());
                    rating.setMovie(optM.get());
                    ratings.add(rating);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return ratings;
    }
}
