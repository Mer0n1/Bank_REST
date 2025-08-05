package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardRequest {
    @NotNull
    private Long cardId;
}
