package com.recommend.movie.util;


import ch.qos.logback.core.net.ObjectWriter;
import com.recommend.movie.model.Genre;
import com.recommend.movie.model.Movie;
import com.recommend.movie.repository.GenreRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MovieDataset {

    private GenreRepository genreRepository;
    private final String API_KEY = "492e0a9afc7b89cc3b1fdb665a6ddc2e";

    private Logger LOGGER = LoggerFactory.getLogger("Counter");

    public MovieDataset(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Movie> createMovies(){
        List<Movie> movies = new ArrayList<>();
        File file = new File("/home/predrag/Code/movie-recommendation/movie-dataset/movies.csv");
        try {
            CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(),CSVFormat.DEFAULT);
            for(CSVRecord record : parser.getRecords()){
                if(record.get(1).equals("title"))
                    continue;

                String[] genres = record.get(2).split("\\|");
                Movie movie = new Movie();
                movie.setNaturalId(record.get(0).trim());
                movie.setTitle(record.get(1).trim());
                for(String g : genres) {
                    Optional<Genre> genre = genreRepository.findByGenre(g.trim());
                    if(genre.isPresent()) {
                        movie.getGenres().add(genre.get());
                    }
                }
                movies.add(movie);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        parseLinks(movies);
        save(movies);
        return movies;
    }

    public void parseLinks(List<Movie> movies){
        File file = new File("/home/predrag/Code/movie-recommendation/movie-dataset/links.csv");
        try {
            CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);
            int i = 0;
            int j = 0;
            for(CSVRecord record : parser.getRecords()){
                if(record.get(2).equals("tmdbId"))
                    continue;

                LOGGER.info(String.valueOf(j));
                j++;
                Optional<Movie> m = movies.parallelStream().filter(movie -> movie.getNaturalId().equals(record.get(0))).findFirst();
                if(m.isPresent()){
                    if(i == 40) {
                        Thread.sleep(10000);
                        i = 0;
                    }
                    if(!record.get(2).isEmpty() || !record.get(2).equals(""))
                    callApi(m.get(), record.get(2));
                    i++;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void callApi(Movie movie, String id){
        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<String> response = template.getForEntity("https://api.themoviedb.org/3/movie/" + id + "?api_key=" + API_KEY, String.class);
            JSONObject json = new JSONObject(response.getBody());
            if (!json.isNull("backdrop_path")) {
                movie.setBackdropPath((String) json.get("backdrop_path"));
            } else
                movie.setBackdropPath("");
            movie.setOverview((String) json.get("overview"));
            movie.setHomepage((String) json.get("homepage"));
            movie.setLanguage((String) json.get("original_language"));
            if (!json.isNull("poster_path")) {
                movie.setPosterPath((String) json.get("poster_path"));
            } else
                movie.setPosterPath("");
            movie.setReleaseDate((String) json.get("release_date"));
            movie.setStatus((String) json.get("status"));
            if(json.get("revenue") instanceof Long)
                movie.setRevenue(((Long)json.get("revenue")).intValue());
            else
                movie.setRevenue((Integer) json.get("revenue"));

            if(json.get("runtime") instanceof Long)
                movie.setRuntime(((Long)json.get("runtime")).intValue());
            else
                movie.setRuntime((Integer) json.get("runtime"));
        } catch (HttpClientErrorException e) {
            LOGGER.info("I was here");
            return;
        }
    }

    public void save(List<Movie> movies){
        try {
            FileOutputStream fout = new FileOutputStream("/home/predrag/Code/movie-recommendation/movie-dataset/movies.bin");
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(movies);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Movie> load(){
        try{
            FileInputStream fin = new FileInputStream("D:\\04 GODINA\\movies.bin232");
            ObjectInputStream in = new ObjectInputStream(fin);
            List<Movie> movie = (ArrayList<Movie>) in.readObject();
            in.close();
            return movie;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
