package com.example.bankcards.dto;

import com.example.bankcards.entity.CardBlockRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CardBlockReqDto {
    private Long id;
    private Long cardId;
    private Long ownerId;
    private Date date;
    private CardBlockRequest.Status status;
}
