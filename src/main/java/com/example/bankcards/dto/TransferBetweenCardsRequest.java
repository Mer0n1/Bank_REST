package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferBetweenCardsRequest {
    @NotNull
    private Long cardIdFrom;
    @NotNull
    private Long cardIdTo;
    @NotNull
    private BigDecimal amount;
}
