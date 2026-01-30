package com.trading.realtimetrading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String nickname;
    private String message;  // 추가 메시지

    // 3개 파라미터 생성자 추가
    public AuthResponse(String token, String email, String nickname) {
        this.token = token;
        this.email = email;
        this.nickname = nickname;
        this.message = "로그인 성공";
    }
}