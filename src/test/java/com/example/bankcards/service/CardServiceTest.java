package com.example.bankcards.service;

import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferBetweenCardsRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.PersonDetails;
import com.example.bankcards.util.AESUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    private CardService service;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;


    @Test
    void transferBetweenCards() {
        TransferBetweenCardsRequest request = new TransferBetweenCardsRequest();
        request.setCardIdFrom(1L);
        request.setCardIdTo(2L);
        request.setAmount(new BigDecimal("10"));

        PersonDetails personDetails = new PersonDetails();
        personDetails.setId(1L);

        User user = new User();
        user.setId(personDetails.getId());

        Card cardFrom = new Card();
        Card cardTo   = new Card();
        cardFrom.setBalance(new BigDecimal("20"));
        cardTo.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(personDetails.getId())).thenReturn(Optional.of(user));
        when(cardRepository.findByIdAndOwner(request.getCardIdFrom(), user))
                .thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByIdAndOwner(request.getCardIdTo(), user))
                .thenReturn(Optional.of(cardTo));

        service.transferBetweenCards(request, personDetails);

        verify(userRepository).findById(personDetails.getId());
        verify(cardRepository).findByIdAndOwner(request.getCardIdFrom(), user);
        verify(cardRepository).findByIdAndOwner(request.getCardIdTo(), user);

        assertEquals(cardFrom.getBalance(), new BigDecimal("10.00"));
        assertEquals(cardTo.getBalance()  , new BigDecimal("10.00"));
    }

    @Test
    void transferBetweenCards_insufficientFunds_shouldThrow() {
        TransferBetweenCardsRequest request = new TransferBetweenCardsRequest();
        request.setCardIdFrom(1L);
        request.setCardIdTo(2L);
        request.setAmount(new BigDecimal("50")); //больше чем ест

        PersonDetails personDetails = new PersonDetails();
        personDetails.setId(1L);

        User user = new User();
        user.setId(1L);

        Card cardFrom = new Card();
        Card cardTo = new Card();
        cardFrom.setBalance(new BigDecimal("20"));
        cardTo.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.findByIdAndOwner(1L, user)).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByIdAndOwner(2L, user)).thenReturn(Optional.of(cardTo));

        assertThrows(InsufficientFundsException.class, () -> service.transferBetweenCards(request, personDetails));
    }

    @Test
    void createCard() {
        CreateCardRequest request = new CreateCardRequest();
        request.setOwnerId(1L);

        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setId(11L);
        card.setCardNumber("1234567890");

        try (MockedStatic<AESUtils> aesMock = Mockito.mockStatic(AESUtils.class)) {
            aesMock.when(() -> AESUtils.encodeCardNumber(anyString(), any()))
                    .thenReturn("qwerty");

            when(userRepository.findById(request.getOwnerId())).thenReturn(Optional.of(user));
            when(cardRepository.save(any(Card.class))).thenReturn(card);
            when(cardRepository.existsByCardNumber(any())).thenReturn(false);

            Card testCard = service.createCard(request);

            verify(userRepository).findById(request.getOwnerId());
            verify(cardRepository).save(any(Card.class));
            verify(cardRepository).existsByCardNumber(anyString());

            assertEquals(testCard, card);
        }
    }

    @Test
    void createCard_generationNumberError() {
        CreateCardRequest request = new CreateCardRequest();
        request.setOwnerId(1L);

        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setId(11L);
        card.setCardNumber("1234567890");

        try (MockedStatic<AESUtils> aesMock = Mockito.mockStatic(AESUtils.class)) {
            aesMock.when(() -> AESUtils.encodeCardNumber(anyString(), any()))
                    .thenReturn("qwerty");

            when(userRepository.findById(request.getOwnerId())).thenReturn(Optional.of(user));
            //when(cardRepository.save(any(Card.class))).thenReturn(card);
            when(cardRepository.existsByCardNumber(any())).thenReturn(true);

            assertThrows(RuntimeException.class, () -> service.createCard(request));

            verify(userRepository).findById(request.getOwnerId());
        }
    }

    @Test
    void changeCardStatus_success() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(Card.CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        service.changeCardStatus(cardId, Card.CardStatus.BLOCKED);

        verify(cardRepository).findById(cardId);
        assertEquals(Card.CardStatus.BLOCKED, card.getStatus());
        assertNull(card.getExpireDate());
    }

    @Test
    void changeCardStatus_sameStatus_throwsException() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(Card.CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardException ex = assertThrows(CardException.class,
                () -> service.changeCardStatus(cardId, Card.CardStatus.ACTIVE));

        assertEquals("Карта уже в статусе: ACTIVE", ex.getMessage());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardsForCurrentUser_notFoundUser() {
        PersonDetails personDetails = new PersonDetails();
        personDetails.setId(1L);

        when(userRepository.findById(personDetails.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getCardsForCurrentUser(personDetails));

        verify(userRepository).findById(personDetails.getId());
    }

    @Test
    void getCardsForCurrentUserPageable_notFoundUser() {
        PersonDetails personDetails = new PersonDetails();
        personDetails.setId(1L);

        Pageable pageable = PageRequest.of(1, 1);

        when(userRepository.findById(personDetails.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getCardsForCurrentUser(personDetails, pageable));

        verify(userRepository).findById(personDetails.getId());
    }
}
