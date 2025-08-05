package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void createUserFromCreationRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("123");
        request.setPassword("qwerty");

        User user = new User();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("_qwerty_");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User testUser = service.createUserFromCreationRequest(request);

        verify(userRepository).findByUsername(request.getUsername());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserFromCreationRequest_alreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("123");
        request.setPassword("qwerty");

        User user = new User();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> service.createUserFromCreationRequest(request));

        verify(userRepository).findByUsername(request.getUsername());
    }
}
