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

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        // User 생성 (Builder 패턴)
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .balance(new BigDecimal("1000000"))  // 초기 자본 100만원
                .build();

        userRepository.save(user);

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getNickname());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getNickname());
    }
}