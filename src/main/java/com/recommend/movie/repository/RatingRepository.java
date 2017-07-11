package com.recommend.movie.repository;


import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<MovieRating, Long> {

    MovieRating save(MovieRating rating);

    List<MovieRating> findByMovie_id(long movie_id);

    boolean existsByMovie_idAndUser_id(long movie_id, long user_id);

    boolean existsByMovie_id(long movie_id);

    @Query("select r.movie, sum(r.rating)/count(r.rating) FROM MovieRating r left outer join r.movie m where m.id = ?1 group by r.movie, m.id order by sum(r.rating) desc ")
    List<Object[]> getRatedMovies(long id);

    List<MovieRating> findAllByUser_id(long id);

    @Query("select r.movie from MovieRating r left outer join r.user u where u.id = ?1 and r.rating >= 4")
    List<Movie> getTopMovies(Long userId);
}
