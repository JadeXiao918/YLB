package com.ylb.ajaxdemo.controller;

import com.ylb.ajaxdemo.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/user/query")
    public User queryUser(){
        User user = new User(1001,"张强",20,"男");
        return user;
    }
}
