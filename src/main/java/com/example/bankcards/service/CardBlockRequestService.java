package com.example.bankcards.service;

import com.example.bankcards.dto.BlockCardRequest;
import com.example.bankcards.dto.CardBlockReqDto;
import com.example.bankcards.dto.ChangeStatusCardBlockReq;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardBlockReqNotFoundException;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CardBlockRequestService {
    @Autowired
    private CardBlockRequestRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardRepository cardRepository;

    public List<CardBlockReqDto> getAllCardBlockReq() {
        return repository.findAll().stream()
                .map(req -> new CardBlockReqDto(
                        req.getId(),
                        req.getCard().getId(),
                        req.getAuthor().getId(),
                        req.getDate(),
                        req.getStatus()
                )).toList();
    }

    @Transactional
    public void changeStatusCardBlockRequest(ChangeStatusCardBlockReq req) {
        CardBlockRequest cardBlockRequest = repository.findById(req.getReqId())
                .orElseThrow(() -> new CardBlockReqNotFoundException("Заявка не найдена"));

        if (cardBlockRequest.getStatus() == (req.getStatus()))
            throw new CardException("Заявка уже в статусе: " + req.getStatus());
        if (cardBlockRequest.getStatus() != CardBlockRequest.Status.ACTIVE)
            throw new CardException("Заявка не активна");

        cardBlockRequest.setStatus(req.getStatus());
    }

    @Transactional
    public void registerRequest(PersonDetails personDetails, BlockCardRequest blockCardRequest) {
        User user = userRepository.findById(personDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Card card = cardRepository.findById(blockCardRequest.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));

        CardBlockRequest cardBlockRequest = new CardBlockRequest();
        cardBlockRequest.setStatus(CardBlockRequest.Status.ACTIVE);
        cardBlockRequest.setDate(new Date(System.currentTimeMillis()));
        cardBlockRequest.setAuthor(user);
        cardBlockRequest.setCard(card);

        repository.save(cardBlockRequest);
    }
}
