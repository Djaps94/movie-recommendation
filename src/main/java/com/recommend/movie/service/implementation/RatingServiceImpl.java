package com.recommend.movie.service.implementation;


import com.recommend.movie.model.MovieRating;
import com.recommend.movie.repository.RatingRepository;
import com.recommend.movie.service.RatingService;
import com.recommend.movie.util.RatingDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService{

    private RatingRepository ratingRepository;
    private RatingDataset ratingDataset;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, RatingDataset ratingDataset){
        this.ratingRepository = ratingRepository;
        this.ratingDataset = ratingDataset;
    }

    @Override
    public void saveRating(MovieRating rating) {
        ratingRepository.save(rating);
    }

    public List<MovieRating> getRatings(){
        List<MovieRating> ratings = ratingDataset.createRatings();
        return ratings;
    }
}
