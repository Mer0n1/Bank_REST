package com.example.bankcards.dto;

import com.example.bankcards.entity.CardBlockRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusCardBlockReq {
    @NotNull
    private Long reqId;
    @NotNull
    private CardBlockRequest.Status status;
}
