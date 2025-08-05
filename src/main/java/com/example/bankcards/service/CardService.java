package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferBetweenCardsRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.PersonDetails;
import com.example.bankcards.util.AESUtils;
import com.example.bankcards.util.CalculationsUtils;
import com.example.bankcards.util.CardNumberGeneratorUtil;
import com.example.bankcards.util.CardUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${aes.key}")
    private String AESKeyStr;

    private SecretKey aesKey;

    @PostConstruct
    public void init() {
        this.aesKey = new SecretKeySpec(
                AESUtils.decryptBASE64(AESKeyStr),
                AESUtils.KEY_ALGORITHM);
    }

    public List<CardDto> getAllCards() {
        return cardRepository.findAll().stream()
                .map(card -> new CardDto(
                        card.getId(),
                        CardUtils.maskCardNumber(AESUtils.decryptCardNumber(card.getCardNumber(), aesKey)),
                        card.getBalance(),
                        card.getStatus(),
                        card.getExpireDate()
                ))
                .toList();
    }

    /** Упрощенная версия без Pageable */
    public List<CardDto> getCardsForCurrentUser(PersonDetails personDetails) {
        User user = userRepository.findById(personDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        return cardRepository.findByOwner(user).stream()
                .map(card -> new CardDto(
                        card.getId(),
                        AESUtils.decryptCardNumber(card.getCardNumber(), aesKey),
                        card.getBalance(),
                        card.getStatus(),
                        card.getExpireDate()
                ))
                .toList();
    }

    /** С участием Pageable */
    public Page<CardDto> getCardsForCurrentUser(PersonDetails personDetails, Pageable pageable) {
        User user = userRepository.findById(personDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        return cardRepository.findByOwner(user, pageable)
                .map(card -> new CardDto(
                        card.getId(),
                        AESUtils.decryptCardNumber(card.getCardNumber(), aesKey),
                        card.getBalance(),
                        card.getStatus(),
                        card.getExpireDate()
                ));
    }

    @Transactional
    public void transferBetweenCards(TransferBetweenCardsRequest request, PersonDetails personDetails) {
        User user = userRepository.findById(personDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Card cardFrom = cardRepository.findByIdAndOwner(request.getCardIdFrom(), user)
                .orElseThrow(() -> new CardNotFoundException("Карта отправления не найдена"));
        Card cardTo = cardRepository.findByIdAndOwner(request.getCardIdTo(), user)
                .orElseThrow(() -> new CardNotFoundException("Карта получения не найдена"));

        CalculationsUtils.transferBetweenCards(cardFrom, cardTo, request.getAmount());
    }


    @Transactional
    public Card createCard(CreateCardRequest request) {
        User user = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (user.getRole().equals("ADMIN"))
            throw new RuntimeException("Администраторы не могут иметь персональные карты");

        Card card = new Card();
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(Card.CardStatus.ACTIVE);
        card.setOwner(user);
        card.setCardNumber(AESUtils.encodeCardNumber(generateUniqueCardNumber(), aesKey));

        return cardRepository.save(card);
    }

    @Transactional
    public void changeCardStatus(Long cardId, Card.CardStatus newStatus) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));

        if (card.getStatus() == newStatus)
            throw new CardException("Карта уже в статусе: " + newStatus);
        if (card.getStatus() == Card.CardStatus.EXPIRED)
            throw new CardException("Карта не обслуживается");
        if (newStatus == Card.CardStatus.EXPIRED)
            card.setExpireDate(new Date(System.currentTimeMillis()));

        card.setStatus(newStatus);
    }

    private String generateUniqueCardNumber() {
        int maxCounts = 20;
        int count = 0;
        String cardNumber;

        cardNumber = CardNumberGeneratorUtil.generate();

        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = CardNumberGeneratorUtil.generate();
            count++;
            if (count >= maxCounts)
                throw new RuntimeException("Не удалось сгенерировать уникальный номер карты");
        }

        return cardNumber;
    }

}
