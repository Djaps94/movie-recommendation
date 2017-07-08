package com.recommend.movie.repository;


import com.recommend.movie.model.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RatingRepository extends JpaRepository<MovieRating, Long> {

    MovieRating save(MovieRating rating);

}
