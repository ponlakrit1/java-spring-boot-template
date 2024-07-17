package com.nctine.template.template.service.impl;

import com.nctine.template.template.entity.UsersEntity;
import com.nctine.template.template.repository.UsersRepository;
import com.nctine.template.template.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service(value = "usersService")
public class UsersServiceImpl implements UserDetailsService, UsersService {

    @Autowired
    private UsersRepository usersRepository;

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

}
