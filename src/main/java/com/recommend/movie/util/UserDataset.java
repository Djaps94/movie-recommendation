package com.recommend.movie.util;


import com.recommend.movie.model.User;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserDataset {

    private final int USER_COUNT = 1000;

    private Set<String> readUsernames(){
        List<String> usernames = new ArrayList<>();
        Set<String> returnList = new HashSet<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File("D:\\04 GODINA\\users.txt232")));
            while (reader.readLine() != null){
                if(reader.readLine() != null)
                    usernames.add(reader.readLine().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0; i < USER_COUNT; i++){
            returnList.add(usernames.get(new Random().nextInt(usernames.size())));
        }

        return returnList;

    }

    private List<String> readPasswords(){
        List<String> passwords = new ArrayList<>();
        List<String> returnList = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File("D:\\04 GODINA\\passwords.txt332")));
            while(reader.readLine() != null){
                if(reader.readLine() != null)
                    passwords.add(reader.readLine().trim());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < USER_COUNT; i++){
            returnList.add(passwords.get(new Random().nextInt(passwords.size())));
        }

        return returnList;
    }

    public List<User> createUsers() {
        List<User> users = new LinkedList<>();
        List<String> usernames = readUsernames().stream().collect(Collectors.toList());
        List<String> passwords = readPasswords();

        for(int i = 0; i < usernames.size(); i++){
            users.add(new User(usernames.get(i), passwords.get(i)));
        }

        return users;
    }
}
