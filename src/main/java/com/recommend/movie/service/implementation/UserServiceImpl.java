package com.recommend.movie.service.implementation;

import com.recommend.movie.model.User;
import com.recommend.movie.repository.UserRepository;
import com.recommend.movie.service.UserService;
import com.recommend.movie.util.UserDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserDataset userDataset;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDataset userDataset){

        this.userRepository = userRepository;
        this.userDataset = userDataset;
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
    public User login(String username) {
        if(checkUser(username)) {
            Optional<User> u = userRepository.findByUsername(username);
            if(u.isPresent())
                return u.get();
        }

        return null;
    }

    @Override
    public User logout(User user) {
        return null;
    }

    @Override
    public User register(User user) {
        if(checkUser(user.getUsername()))
            return null;

        return userRepository.save(user);
    }

    @Override
    public List<User> createUsers() {
        return userDataset.createUsers();
    }

    @Override
    public boolean checkUser(String username) {
        return userRepository.existsByUsername(username);
    }


}
