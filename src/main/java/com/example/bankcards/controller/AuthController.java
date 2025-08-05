package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.PersonDetails;
import com.example.bankcards.service.JwtService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.BindingValidatorUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthRequest authRequest,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult).toString());

        Authentication authenticate = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword())
                );

        PersonDetails user = (PersonDetails) authenticate.getPrincipal();
        String token = jwtService.generateToken(user);

        log.info("Авторизация пользователя: {}, роль {}, id {}", user.getUsername(), user.getROLE(), user.getId());

        return ResponseEntity.ok().body(token);
    }
}
