package com.recommend.movie.recommender;

import com.recommend.movie.model.Movie;
import com.recommend.movie.model.MovieRating;
import com.recommend.movie.repository.GenreRepository;
import com.recommend.movie.repository.MovieRepository;
import com.recommend.movie.repository.RatingRepository;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Component
public class CosineSimilarity {

    private DenseMatrix movieMatrix;
    private DenseMatrix userSeen;
    private DenseMatrix userLikes;
    private SparseVector idf;
    private SparseVector userProfile;
    private SparseVector userProfileSeen;
    private DenseMatrix tfIdf;
    private ExecutorService exec;

    private MovieRepository movieRepository;
    private GenreRepository genreRepository;
    private RatingRepository ratingRepository;

    @Autowired
    public CosineSimilarity(MovieRepository movieRepository, GenreRepository genreRepository, RatingRepository ratingRepository){
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.ratingRepository = ratingRepository;
    }

    @PostConstruct
    private void init(){
        long movieRows = movieRepository.count();
        long movieCols = genreRepository.count() + 1;
        exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        movieMatrix = new DenseMatrix(((Long)movieRows).intValue(), ((Long)movieCols).intValue());
        userSeen = new DenseMatrix(((Long)movieRows).intValue(), 1);
        userLikes = new DenseMatrix(((Long)movieRows).intValue(), 1);
        idf = new SparseVector(((Long)movieCols).intValue() -1);
        initialiseMovieMatrix(movieMatrix, idf);
    }

    public void initialiseUserLikes(long userID){
        List<MovieRating> ratings = ratingRepository.findAllByUser_id(userID);
        ratings.stream().map(rating -> rating.getMovie().getId())
                        .forEach(id -> addToProfile(id));
    }

    private void initialiseMovieMatrix(DenseMatrix movieMatrix, SparseVector idf){
        Semaphore s = new Semaphore(0);
        int i;
        try {
            for (i = 0; i < movieMatrix.numRows(); i++) {
                exec.execute(new InitialiseMatrixTask(i, movieRepository, movieMatrix, s));
            }
        } finally {
            exec.shutdown();
        }
        try {
            s.acquire(i);
            normalizeMatrix(movieMatrix, idf);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    private void normalizeMatrix(DenseMatrix movieMatrix, SparseVector idf){
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Semaphore s = new Semaphore(0);
        int i;
        for(i = 0; i < movieMatrix.numRows(); i++) {
            exec.execute(new NormalizeMatrixTask(movieMatrix, i, s));
        }
        try {
            s.acquire(i);
            calculateIDF(movieMatrix, idf);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void calculateIDF(DenseMatrix movieMatrix, SparseVector idf){
        for(int j = 1; j < movieMatrix.numColumns(); j++){
            int ones = 0;
            for(int i = 0; i < movieMatrix.numRows(); i++){
                if(movieMatrix.get(i, j) > 0)
                    ones++;
            }
            if(ones == 0) {
                idf.set(j-1, 0);
                continue;
            }
            double result = Math.log10(movieMatrix.numRows()/ones);
            idf.set(j-1, result);
        }
        userProfileSeen = calculateUserProfile(movieMatrix, userSeen);
        userProfile = calculateUserProfile(movieMatrix, userLikes);
        tfIdf = calculateTFIDF(idf, movieMatrix);
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

    public List<Movie> calculatePredictionSeen(){
        Map<Long, Double> predictions = new TreeMap<>();
        setPredictions(predictions);
        Map<Long, Double> returnMap = predictions.entrySet().stream()
                                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (old, newValue) -> old, LinkedHashMap::new));

        List<Long> keys = returnMap.entrySet().stream().map(o -> o.getKey())
                                                        .limit(12)
                                                        .collect(Collectors.toList());

        List<Movie> temp = new ArrayList<>();
        for(Long key : keys){
            Optional<Movie> movie = movieRepository.findById(key);
            if(movie.isPresent())
                temp.add(movie.get());
        }
        return temp;
    }

    public List<Movie> calculatePrediction(){
        Map<Long, Double> predictions = new TreeMap<>();
        setPredictions(predictions);
        Map<Long, Double> returnMap = predictions.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (old, newValue) -> old, LinkedHashMap::new));


        List<Long> keys = returnMap.entrySet().stream().map(o -> o.getKey())
                                                        .limit(4)
                                                        .collect(Collectors.toList());

        List<Movie> temp = new ArrayList<>();
        for(Long key : keys){
            Optional<Movie> movie = movieRepository.findById(key);
            if(movie.isPresent())
                temp.add(movie.get());
        }


        return temp;
    }

    private void setPredictions(Map<Long, Double> predictions){
        for(int i = 0; i < tfIdf.numRows(); i++) {
            double result = 0;
            for (int j = 0; j < userProfile.size(); j++) {
                result += userProfile.get(j) * tfIdf.get(i,j);
            }
            predictions.put(((Integer)i).longValue()+1, result);
        }
    }

    public void addToSeen(Long movieId){
        for(int i = 0; i < userSeen.numRows(); i++)
            userSeen.set(i, 0, 0);

        userSeen.set(((Long)movieId).intValue()-1, 0, 1);
    }

    public void addToProfile(Long movieId){
        userLikes.set(((Long)movieId).intValue()-1, 0, 1);
    }

    public void calculateUserSeen(){
        userProfileSeen = calculateUserProfile(movieMatrix, userSeen);
    }

    public void calculateUserLikes() { userProfile = calculateUserProfile(movieMatrix, userLikes); }
}