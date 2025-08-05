package com.example.bankcards.service;

import com.example.bankcards.dto.BlockCardRequest;
import com.example.bankcards.dto.ChangeStatusCardBlockReq;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.PersonDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CardBlockRequestServiceTest {
    @InjectMocks
    private CardBlockRequestService service;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;

    @Test
    void changeStatusCardBlockRequest() {
        ChangeStatusCardBlockReq req = new ChangeStatusCardBlockReq();
        req.setReqId(1L);
        req.setStatus(CardBlockRequest.Status.ACCEPTED);

        CardBlockRequest blockReq = new CardBlockRequest();
        blockReq.setStatus(CardBlockRequest.Status.ACTIVE);

        when(cardBlockRequestRepository.findById(req.getReqId()))
                .thenReturn(Optional.of(blockReq));

        service.changeStatusCardBlockRequest(req);
        assertEquals(CardBlockRequest.Status.ACCEPTED, req.getStatus());
    }

    @Test
    void changeStatusCardBlockRequest_exception() {
        // given
        ChangeStatusCardBlockReq req = new ChangeStatusCardBlockReq();
        req.setReqId(1L);
        req.setStatus(CardBlockRequest.Status.ACTIVE);

        CardBlockRequest blockReq = new CardBlockRequest();
        blockReq.setStatus(CardBlockRequest.Status.ACTIVE);

        when(cardBlockRequestRepository.findById(req.getReqId()))
                .thenReturn(Optional.of(blockReq));

        assertThrows(CardException.class,
                () -> service.changeStatusCardBlockRequest(req));
    }

    @Test
    void registerRequest() {
        PersonDetails personDetails = new PersonDetails();
        personDetails.setId(1L);

        BlockCardRequest req = new BlockCardRequest();
        req.setCardId(10L);

        User user = new User();
        user.setId(1L);
        Card card = new Card();
        card.setId(10L);

        when(userRepository.findById(personDetails.getId())).thenReturn(Optional.of(user));
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        when(cardBlockRequestRepository.save(any(CardBlockRequest.class)))
                .thenReturn(new CardBlockRequest());

        service.registerRequest(personDetails, req);

        verify(cardBlockRequestRepository).save(any(CardBlockRequest.class));
    }

    @Test
    void registerRequest_shouldThrowException_exception() {
        PersonDetails personDetails = new PersonDetails();
        personDetails.setId(1L);

        BlockCardRequest req = new BlockCardRequest();
        req.setCardId(10L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(personDetails.getId())).thenReturn(Optional.of(user));
        when(cardRepository.findById(req.getCardId())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> service.registerRequest(personDetails, req));
    }


}
