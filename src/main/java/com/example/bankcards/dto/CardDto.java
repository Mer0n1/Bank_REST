package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class CardDto {
    private Long id;
    private String cardNumberMasked;
    private BigDecimal balance;
    private Card.CardStatus status;
    private Date expireDate;
}
