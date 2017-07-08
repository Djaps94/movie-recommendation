package com.recommend.movie.repository;


import com.recommend.movie.model.Genre;
import com.recommend.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Movie save(Movie movie);

    List<Movie> findByGenres(Set<Genre> genres);

    Optional<Movie> findByTitle(String title);

    Optional<Movie> findById(Long id);
}
