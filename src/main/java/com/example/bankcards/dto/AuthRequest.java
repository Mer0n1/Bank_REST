package com.example.bankcards.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthRequest {

    @NotEmpty
    public String username;

    @NotEmpty
    public String password;
}
