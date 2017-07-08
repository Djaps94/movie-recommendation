package com.recommend.movie.recommender;

import com.recommend.movie.model.Genre;
import com.recommend.movie.model.Movie;
import com.recommend.movie.repository.GenreRepository;
import com.recommend.movie.repository.MovieRepository;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.SparseVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Optional;

@Component
public class CosineSimilarity {


    private DenseMatrix movieMatrix;
    private DenseMatrix userMatrix;
    private SparseVector idf;
    private SparseVector userProfile;

    private MovieRepository movieRepository;
    private GenreRepository genreRepository;

    @Autowired
    public CosineSimilarity(MovieRepository movieRepository, GenreRepository genreRepository){
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    @PostConstruct
    private void init(){
        long movieRows = movieRepository.count();
        long movieCols = genreRepository.count() + 1;
        movieMatrix = new DenseMatrix(((Long)movieRows).intValue(), ((Long)movieCols).intValue());
        userMatrix = new DenseMatrix(((Long)movieRows).intValue(), 1);
        userProfile = new SparseVector(((Long)movieCols).intValue()- 1);
        idf = new SparseVector(((Long)movieCols).intValue() -1);
        initialiseMovieMatrix(movieMatrix);
    }


    private void initialiseMovieMatrix(DenseMatrix movieMatrix){

        for(long i = 1; i <= movieMatrix.numRows(); i++){
            Optional<Movie> movie = movieRepository.findById(i);
            if(!movie.isPresent())
                continue;
            movieMatrix.add(((Long)i).intValue(), 0, movie.get().getId());
            for(Genre genre : movie.get().getGenres()){
                switch(genre.getGenre()){
                    case "Action" : movieMatrix.add(((Long)i).intValue(), 1, 1); break;
                    case "Adventure": movieMatrix.add(((Long)i).intValue(), 2, 1); break;
                    case "Animation": movieMatrix.add(((Long)i).intValue(), 3, 1); break;
                    case "Childrens": movieMatrix.add(((Long)i).intValue(), 4, 1); break;
                    case "Comedy": movieMatrix.add(((Long)i).intValue(), 5, 1); break;
                    case "Crime": movieMatrix.add(((Long)i).intValue(), 6, 1); break;
                    case "Documentary": movieMatrix.add(((Long)i).intValue(), 7, 1); break;
                    case "Drama": movieMatrix.add(((Long)i).intValue(), 8, 1); break;
                    case "Fantasy": movieMatrix.add(((Long)i).intValue(), 9, 1); break;
                    case "Film-Noir": movieMatrix.add(((Long)i).intValue(), 10, 1); break;
                    case "Horror": movieMatrix.add(((Long)i).intValue(), 11, 1); break;
                    case "Musical": movieMatrix.add(((Long)i).intValue(), 12, 1); break;
                    case "Mystery": movieMatrix.add(((Long)i).intValue(), 13, 1); break;
                    case "Romance": movieMatrix.add(((Long)i).intValue(), 14, 1); break;
                    case "Sci-Fi": movieMatrix.add(((Long)i).intValue(), 15, 1); break;
                    case "Thriller": movieMatrix.add(((Long)i).intValue(), 16, 1); break;
                    case "War": movieMatrix.add(((Long)i).intValue(), 17, 1); break;
                    case "Western": movieMatrix.add(((Long)i).intValue(), 18, 1); break;
                }

            }
        }
    }

}