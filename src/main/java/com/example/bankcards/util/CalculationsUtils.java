package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculationsUtils {
    public static void transferBetweenCards(Card cardFrom, Card cardTo, BigDecimal amount) {
        if (cardFrom.getBalance().compareTo(amount.setScale(2,  RoundingMode.HALF_UP)) > 0) {
            cardFrom.setBalance(cardFrom.getBalance().subtract(amount).setScale(2,  RoundingMode.HALF_UP));
            cardTo.setBalance(cardTo.getBalance().add(amount).setScale(2,  RoundingMode.HALF_UP));
        } else
            throw new InsufficientFundsException("Недостаточно средств");
    }
}
