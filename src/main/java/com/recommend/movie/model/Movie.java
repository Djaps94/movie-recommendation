package com.recommend.movie.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements Serializable{


    private static final long serialVersionUID = -8825278804041456635L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column
    private String naturalId;

    @Column
    private String title;

    @Column
    private String backdropPath;

    @Column
    private String language;

    @Column
    private String homepage;

    @Column(length = 1000)
    private String overview;

    @Column
    private String posterPath;

    @Column
    private String releaseDate;

    @Column
    private int revenue;

    @Column
    private int runtime;

    @Column
    private String status;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    @JsonBackReference
    private Set<MovieRating> movieRatings = new HashSet<>();

    public String toString(){
        return title;
    }


}
