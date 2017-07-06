package com.recommend.movie.services;


import com.recommend.movie.model.User;

import java.util.List;

public interface UserService {

    void saveUser(User user);

    List<User> getAllUsers();

    User login(User user);

    User logout(User user);
}
