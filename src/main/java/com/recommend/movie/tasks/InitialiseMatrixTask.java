package com.recommend.movie.tasks;


import com.recommend.movie.model.Genre;
import com.recommend.movie.model.Movie;
import com.recommend.movie.repository.MovieRepository;
import no.uib.cipr.matrix.DenseMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Semaphore;

public class InitialiseMatrixTask implements Runnable {

    private int i;
    private MovieRepository movieRepository;
    private DenseMatrix movieMatrix;
    private Semaphore s;

    public InitialiseMatrixTask(int value, MovieRepository movieRepository, DenseMatrix movieMatrix, Semaphore s){
        this.i = value;
        this.movieMatrix = movieMatrix;
        this.movieRepository = movieRepository;
        this.s = s;
    }

    @Override
    public void run() {
        Optional<Movie> movie = movieRepository.findById(((Integer) i).longValue() + 1);
        if (!movie.isPresent())
            return;
        movieMatrix.set(i, 0, movie.get().getId());
        for (Genre genre : movie.get().getGenres()) {
            switch (genre.getGenre()) {
                case "Action":
                    movieMatrix.set(i, 1, 1);
                    break;
                case "Adventure":
                    movieMatrix.set(i, 2, 1);
                    break;
                case "Animation":
                    movieMatrix.set(i, 3, 1);
                    break;
                case "Childrens":
                    movieMatrix.set(i, 4, 1);
                    break;
                case "Comedy":
                    movieMatrix.set(i, 5, 1);
                    break;
                case "Crime":
                    movieMatrix.set(i, 6, 1);
                    break;
                case "Documentary":
                    movieMatrix.set(i, 7, 1);
                    break;
                case "Drama":
                    movieMatrix.set(i, 8, 1);
                    break;
                case "Fantasy":
                    movieMatrix.set(i, 9, 1);
                    break;
                case "Film-Noir":
                    movieMatrix.set(i, 10, 1);
                    break;
                case "Horror":
                    movieMatrix.set(i, 11, 1);
                    break;
                case "Musical":
                    movieMatrix.set(i, 12, 1);
                    break;
                case "Mystery":
                    movieMatrix.set(i, 13, 1);
                    break;
                case "Romance":
                    movieMatrix.set(i, 14, 1);
                    break;
                case "Sci-Fi":
                    movieMatrix.set(i, 15, 1);
                    break;
                case "Thriller":
                    movieMatrix.set(i, 16, 1);
                    break;
                case "War":
                    movieMatrix.set(i, 17, 1);
                    break;
                case "Western":
                    movieMatrix.set(i, 18, 1);
                    break;
            }
        }
        s.release();
    }
}
