package com.recommend.movie.util;


import com.recommend.movie.model.Movie;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class MovieDataset {

    public List<String> createMovies(){
        List<Movie> movies = new ArrayList<>();
        List<String> readMovies = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File("/home/predrag/Code/movie-recommendation/movie-dataset/movies.csv")));
            reader.skip(1);
            while(reader.readLine() != null){
                if(reader.readLine() != null)
                    readMovies.add(reader.readLine().trim());
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return readMovies;
    }
}
