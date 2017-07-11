package com.recommend.movie.tasks;


import com.recommend.movie.model.Movie;
import com.recommend.movie.repository.RatingRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class FetchTopRatedTask implements Runnable{
    private int i;
    private ConcurrentHashMap<Movie, Double> map;
    private RatingRepository ratingRepository;

    public FetchTopRatedTask(int i, ConcurrentHashMap<Movie, Double> map, RatingRepository ratingRepository) {
        this.i = i;
        this.map = map;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public void run() {
        List<Object[]> value = ratingRepository.getRatedMovies(new Long(i+1));
        map.put((Movie)value.get(0)[0], (Double) value.get(0)[1]);
    }
}
