package com.recommend.movie.repository;


import com.recommend.movie.model.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import static org.hibernate.hql.internal.antlr.HqlTokenTypes.FROM;

public interface RatingRepository extends JpaRepository<MovieRating, Long> {

    MovieRating save(MovieRating rating);

    List<MovieRating> findByMovie_id(long movie_id);

    boolean existsByMovie_idAndUser_id(long movie_id, long user_id);

    boolean existsByMovie_id(long movie_id);

    @Query("select r.id, sum(r.rating) FROM MovieRating r join r.movie m where m.id = ?1 group by r.id order by r.id desc ")
    List<Object[]> getRatedMovies(long id);
}
