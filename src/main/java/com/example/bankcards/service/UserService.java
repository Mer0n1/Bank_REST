package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void saveAll(List<User> list) { userRepository.saveAll(list); }

    @Transactional
    public void save(User user) { userRepository.save(user); }

    public User getUser(Long id) throws Exception { return userRepository.findById(id).orElseThrow(()->new Exception("Аккаунт не найден")); }

    public Optional<User> getUser(String username) { return userRepository.findByUsername(username); }

    @Transactional
    public User createUserFromCreationRequest(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new UserAlreadyExistsException("Пользователь с таким логином уже существует");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getBankCards().stream()
                                .map(Card::getId).toList()
                        )
                ).toList();
    }

}
