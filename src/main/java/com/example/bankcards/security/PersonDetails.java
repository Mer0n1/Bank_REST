package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;


@Component
@Data
@ToString
public class PersonDetails implements UserDetails {
    private Long id;
    private String ROLE;
    private String username;
    private String password;

    public PersonDetails(User user) {
        this.id       = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.ROLE     = user.getRole();
    }
    public PersonDetails() {}

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + ROLE));
    }
}

