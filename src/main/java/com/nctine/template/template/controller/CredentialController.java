package com.nctine.template.template.controller;

import com.nctine.template.template.config.TokenProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cred")
public class CredentialController {
    @PostMapping("/login")
    public String login(@RequestBody UserDetails request) {
        String token = TokenProvider.generateToken(request.getUsername());
        return token;
    }
}