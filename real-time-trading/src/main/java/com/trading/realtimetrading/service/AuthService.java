package com.trading.realtimetrading.service;

import com.trading.realtimetrading.config.JwtTokenProvider;
import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.domain.user.UserRepository;
import com.trading.realtimetrading.dto.AuthResponse;
import com.trading.realtimetrading.dto.LoginRequest;
import com.trading.realtimetrading.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);

        // 계좌 자동 생성
        accountService.createAccount(user);

        String token = jwtTokenProvider.createToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getNickname());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        String token = jwtTokenProvider.createToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getNickname());
    }
}
