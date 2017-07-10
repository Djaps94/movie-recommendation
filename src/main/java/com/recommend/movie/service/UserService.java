package com.recommend.movie.service;


import com.recommend.movie.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    void saveUser(User user);

    List<User> getAllUsers();

    User login(String username);

    User logout(User user);

    User register(User user);

    List<User> createUsers();

    public boolean checkUser(String username);

}
