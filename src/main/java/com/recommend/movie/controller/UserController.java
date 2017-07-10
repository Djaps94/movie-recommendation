package com.recommend.movie.controller;


import com.recommend.movie.model.User;
import com.recommend.movie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }


    @RequestMapping (
            method = RequestMethod.GET,
            value = "/all",
            produces = "application/json"
    )
    @ResponseBody
    public List<User> checkUsers(){

        List<User> users = userService.createUsers();
        users.forEach(user -> userService.saveUser(user));
        return users;
    }

    @RequestMapping (
            method = RequestMethod.POST,
            value = "/register",
            consumes = "application/json",
            produces = "application/json"
    )
    @ResponseBody
    public User registerUser(@RequestBody User user){
        return userService.register(user);
    }

}
