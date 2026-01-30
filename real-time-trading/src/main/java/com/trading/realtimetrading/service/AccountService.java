package com.trading.realtimetrading.service;

import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.domain.user.UserRepository;
import com.trading.realtimetrading.dto.AccountRequest;
import com.trading.realtimetrading.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;

    @Transactional
    public AccountResponse deposit(String email, AccountRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        user.deposit(request.getAmount());

        return new AccountResponse(user.getEmail(), user.getBalance(), request.getAmount());
    }

    @Transactional
    public AccountResponse withdraw(String email, AccountRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        user.withdraw(request.getAmount());

        return new AccountResponse(user.getEmail(), user.getBalance(), request.getAmount());
    }

    @Transactional(readOnly = true)
    public AccountResponse getBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return new AccountResponse(user.getEmail(), user.getBalance());
    }
}