package com.nctine.template.template.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @RequestMapping("/")
    String welcome() {
        return "Welcome to template application!";
    }

    @RequestMapping("/token")
    String welcomeWithToken() {
        return "Welcome to template application with token!";
    }

}
