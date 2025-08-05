package com.example.bankcards.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.bankcards.security.PersonDetails;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtService {

    private static String secret;
    private static Integer expiration; //standard

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        secret     = environment.getProperty("jwt.secret");
        expiration = Integer.valueOf(environment.getProperty("jwt.expiration"));
    }

    public String generateToken(PersonDetails user) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(expiration).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("username", user.getUsername())
                .withClaim("password", user.getUsername())
                .withClaim("id", user.getId())
                .withClaim("ROLE", user.getROLE())
                .withIssuedAt(new Date())
                .withIssuer("TODO")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }
}