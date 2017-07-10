package com.recommend.movie.repository;


import com.recommend.movie.model.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<MovieRating, Long> {

    MovieRating save(MovieRating rating);

    List<MovieRating> findByMovie_id(long movie_id);

    boolean existsByMovie_idAndUser_id(long movie_id, long user_id);
}
