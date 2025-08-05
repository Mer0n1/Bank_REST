package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> credential = userService.getUser(username);
        return credential.map(PersonDetails::new).orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
    }
}
