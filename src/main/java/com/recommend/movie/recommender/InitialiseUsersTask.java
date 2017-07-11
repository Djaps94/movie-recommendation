package com.recommend.movie.recommender;



import com.recommend.movie.model.MovieRating;
import com.recommend.movie.repository.RatingRepository;
import no.uib.cipr.matrix.sparse.SparseVector;

import java.util.List;
import java.util.concurrent.Semaphore;

public class InitialiseUsersTask implements Runnable {

    private int i;
    private SparseVector user;
    private RatingRepository ratingRepository;

    public InitialiseUsersTask(int i, SparseVector user, RatingRepository ratingRepository) {
        this.i = i;
        this.user = user;
        this.ratingRepository = ratingRepository;

    }

    @Override
    public void run() {
        user.set(0, i);
        List<MovieRating> ratings = ratingRepository.findAllByUser_id(((Integer)i).longValue()+1);
        if(ratings == null)
            return;
        ratings.stream().forEach(rating -> user.set(rating.getMovie().getId().intValue(), rating.getRating()));
    }
}
