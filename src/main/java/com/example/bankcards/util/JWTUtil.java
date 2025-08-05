package com.example.bankcards.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    private static String secret;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        secret = environment.getProperty("jwt.secret");
    }
    public static DecodedJWT validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("TODO")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt;
    }
}
