package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.BindingValidatorUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')") //поскольку это банковская система, создать аккаунт может только админ
    @PostMapping("/createUser")
    public ResponseEntity<?> serviceAPI(@RequestBody @Valid CreateUserRequest request,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult));

        User user = userService.createUserFromCreationRequest(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Аккаунт создан", "userId", user.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }
}
