package com.nctine.template.template.controller;

import com.nctine.template.template.config.TokenProvider;
import com.nctine.template.template.entity.UsersEntity;
import com.nctine.template.template.model.request.LoginUser;
import com.nctine.template.template.model.request.RegisterUserRequest;
import com.nctine.template.template.model.response.AuthToken;
import com.nctine.template.template.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cred")
public class CredentialController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginUser request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
        );

        return this.genAuthResponse(authentication);
    }

    private ResponseEntity<?> genAuthResponse(Authentication authentication) {
        final String token = jwtTokenUtil.generateAccessToken(authentication);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(authentication);

        return ResponseEntity.status(HttpStatus.OK).body(new AuthToken(token, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody @Validated RegisterUserRequest user) throws Exception {
        return ResponseEntity.ok(usersService.create(user));
    }
}