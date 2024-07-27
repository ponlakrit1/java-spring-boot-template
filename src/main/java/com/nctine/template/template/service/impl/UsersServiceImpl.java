package com.nctine.template.template.service.impl;

import com.nctine.template.template.entity.UsersEntity;
import com.nctine.template.template.model.request.RegisterUserRequest;
import com.nctine.template.template.repository.UsersRepository;
import com.nctine.template.template.service.UsersService;
import com.nctine.template.template.type.ErrorType;
import com.nctine.template.template.utils.ErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service(value = "usersService")
public class UsersServiceImpl implements UserDetailsService, UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public UserDetails loadUserByUsername(String username) {
        UsersEntity users = usersRepository.findByUsernameAndActiveIsTrue(username);

        if (users == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        } else if (!users.isActive()) {
            throw new UsernameNotFoundException("Username is not active.");
        }

        return new org.springframework.security.core.userdetails.User(users.getUsername(), users.getPassword(), getAuthority(users));
    }

    private Set<SimpleGrantedAuthority> getAuthority(UsersEntity users) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + users.getRole()));

        return authorities;
    }

    @Override
    public UsersEntity create(RegisterUserRequest request) throws Exception {
        UsersEntity user = usersRepository.findByUsernameAndActiveIsTrue(request.getUsername());
        if (user != null) {
            throw new ErrorException(ErrorType.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        UsersEntity nUsers = UsersEntity.builder()
                .username(request.getUsername())
                .password(bcryptEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .role("USER")
                .active(true)
                .createdAt(new Date())
                .build();

        return usersRepository.save(nUsers);
    }

}
