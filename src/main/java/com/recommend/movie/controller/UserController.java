package com.recommend.movie.controller;


import com.recommend.movie.model.User;
import com.recommend.movie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
            value = "/register/{username}/{password}",
            produces = "application/json"
    )
    @ResponseBody
    public User registerUser(@PathVariable("username") String username, @PathVariable("password") String password){
        return userService.register(new User(username, password));
    }

    @RequestMapping (
            method = RequestMethod.POST,
            value = "/login/{username}/{password}",
            produces = "application/json"
    )
    @ResponseBody
    public User login(@PathVariable("username") String username, @PathVariable("password") String password){
        return userService.login(username);
    }

}
