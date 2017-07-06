package com.recommend.movie.services.implementation;

import com.recommend.movie.model.User;
import com.recommend.movie.repository.UserRepository;
import com.recommend.movie.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User login(User user) {
        return null;
    }

    @Override
    public User logout(User user) {
        return null;
    }
}
