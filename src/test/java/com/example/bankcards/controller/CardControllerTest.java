package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class CardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Value("${jwt.admin_jwt}")
    private String token;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        cardRepository.deleteAll();

        User user = new User();
        user.setUsername("Admin");
        user.setPassword("Admin");
        user.setRole("ADMIN");
        userRepository.save(user);

        Card card = new Card();
        card.setCardNumber("1234-5678-9012-3456");
        card.setBalance(new BigDecimal("1000"));
        card.setStatus(Card.CardStatus.ACTIVE);
        card.setOwner(user);
        cardRepository.save(card);
    }

    @WithUserDetails("Admin")
    @Test
    @WithMockUser(username = "Admin", roles = {"ADMIN"})
    void getMyCards() throws Exception {
        mockMvc.perform(get("/card/myCards")
                    .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardNumber").value("1234-5678-9012-3456"));
    }
}