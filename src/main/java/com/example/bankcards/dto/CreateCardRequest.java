package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCardRequest {
    @NotNull
    private Long ownerId;
}
