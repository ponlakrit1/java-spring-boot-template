package com.nctine.template.template.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/")
    String welcome() {
        return "Welcome to template application!";
    }

    @GetMapping("/token")
    String welcomeWithToken() {
        return "Welcome to template application with token!";
    }

}
