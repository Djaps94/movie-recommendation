package com.recommend.movie.repository;

import com.recommend.movie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {


    List<User> findAll();

    User findById();

    User save(User user);

}
