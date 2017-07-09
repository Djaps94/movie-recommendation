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
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CosineSimilarity {


    private DenseMatrix movieMatrix;
    private DenseMatrix userMatrix;
    private SparseVector idf;
    private SparseVector userProfile;
    private DenseMatrix tfIdf;

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
        userMatrix.set(12,0, 1);
        idf = new SparseVector(((Long)movieCols).intValue() -1);
        initialiseMovieMatrix(movieMatrix);
        normalizeMatrix(movieMatrix);
        calculateIDF(movieMatrix, idf);
        userProfile = calculateUserProfile(movieMatrix, userMatrix);
        tfIdf = calculateTFIDF(idf, movieMatrix);
    }


    private void initialiseMovieMatrix(DenseMatrix movieMatrix){

        for(int i = 0; i < movieMatrix.numRows(); i++){
            Optional<Movie> movie = movieRepository.findById(((Integer)i).longValue()+1);
            if(!movie.isPresent())
                continue;
            movieMatrix.set(i, 0, movie.get().getId());
            for(Genre genre : movie.get().getGenres()){
                switch(genre.getGenre()){
                    case "Action" : movieMatrix.set(i, 1, 1); break;
                    case "Adventure": movieMatrix.set(i, 2, 1); break;
                    case "Animation": movieMatrix.set(i, 3, 1); break;
                    case "Childrens": movieMatrix.set(i, 4, 1); break;
                    case "Comedy": movieMatrix.set(i, 5, 1); break;
                    case "Crime": movieMatrix.set(i, 6, 1); break;
                    case "Documentary": movieMatrix.set(i, 7, 1); break;
                    case "Drama": movieMatrix.set(i, 8, 1); break;
                    case "Fantasy": movieMatrix.set(i, 9, 1); break;
                    case "Film-Noir": movieMatrix.set(i, 10, 1); break;
                    case "Horror": movieMatrix.set(i, 11, 1); break;
                    case "Musical": movieMatrix.set(i, 12, 1); break;
                    case "Mystery": movieMatrix.set(i, 13, 1); break;
                    case "Romance": movieMatrix.set(i, 14, 1); break;
                    case "Sci-Fi": movieMatrix.set(i, 15, 1); break;
                    case "Thriller": movieMatrix.set(i, 16, 1); break;
                    case "War": movieMatrix.set(i, 17, 1); break;
                    case "Western": movieMatrix.set(i, 18, 1); break;
                }

            }
        }
    }

    private void normalizeMatrix(DenseMatrix movieMatrix){
        for(int i = 0; i < movieMatrix.numRows(); i++){
            int counter = 0;
            for(int j = 1; j < movieMatrix.numColumns(); j++){
                if(movieMatrix.get(i, j) == 1)
                    counter++;
            }
            for(int k = 1; k < movieMatrix.numColumns(); k++){
                if(movieMatrix.get(i, k) == 1){
                    movieMatrix.set(i, k , 1/Math.sqrt(counter));
                }
            }
        }
    }

    private void calculateIDF(DenseMatrix movieMatrix, SparseVector idf){
        int numberOfRows = movieMatrix.numRows();
        for(int j = 1; j < movieMatrix.numColumns(); j++){
            int ones = 0;
            for(int i = 1; i < movieMatrix.numRows(); i++){
                if(movieMatrix.get(i, j) > 0)
                    ones++;
            }
            if(ones == 0) {
                idf.set(j, 0);
                continue;
            }
            double result = Math.log10(numberOfRows/ones);
            idf.set(j-1, result);
        }
    }

    private SparseVector calculateUserProfile(DenseMatrix movieMatrix, DenseMatrix userMatrix){
        SparseVector temp = new SparseVector(movieMatrix.numColumns() -1);

        for(int j = 1; j < movieMatrix.numColumns(); j++){
            List<Double> pom = new ArrayList<>();
            for(int k = 0; k < userMatrix.numRows(); k++){
                pom.add(movieMatrix.get(k, j) * userMatrix.get(k,0));
            }
            temp.set(j-1, pom.stream().mapToDouble(Double::doubleValue).sum());
        }
        return temp;
    }

    private DenseMatrix calculateTFIDF(SparseVector idf, DenseMatrix movieMatrix){
        DenseMatrix temp = new DenseMatrix(movieMatrix.numRows(), idf.size());
        for(int j = 0; j < movieMatrix.numRows(); j++) {
            for (int i = 0; i < idf.size(); i++) {
                temp.set(j, i, idf.get(i) * movieMatrix.get(j, i+1));
            }
        }
        return temp;
    }

    public Map<Long, Double> calculatePrediction(){
        Map<Long, Double> predictions = new TreeMap<>();
        for(int i = 0; i < tfIdf.numRows(); i++) {
            double result = 0;
            for (int j = 0; j < userProfile.size(); j++) {
                result += userProfile.get(j) * tfIdf.get(i,j);
            }
            predictions.put(((Integer)i).longValue()+1, result);
        }
        Map returnMap = predictions.entrySet().stream()
                                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (old, newValue) -> old, LinkedHashMap::new));

        return returnMap;
    }

    public SparseVector test(){
        return userProfile;
    }

}