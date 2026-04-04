package com.financeledger.financeledger.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @Builder @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String role;
    private String name;
    private String email;

}
