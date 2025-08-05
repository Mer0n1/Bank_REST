package com.example.bankcards.exception;

public class CardBlockReqNotFoundException extends RuntimeException {
    public CardBlockReqNotFoundException(String message) {
        super(message);
    }
}
