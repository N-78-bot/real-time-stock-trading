package com.trading.realtimetrading.service;

import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.domain.user.UserRepository;
import com.trading.realtimetrading.dto.AccountRequest;
import com.trading.realtimetrading.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 입출금 비즈니스 로직
 * - 이메일 기반 메서드 (JWT 인증용)
 * - userId 기반 메서드 (기존 호환성 유지)
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;

    // ========================================
    // 이메일 기반 메서드 (JWT 인증용)
    // ========================================

    /**
     * 이메일로 입금 처리
     * JWT에서 추출한 이메일로 사용자 조회
     */
    @Transactional
    public AccountResponse depositByEmail(String email, AccountRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 잔고 증가
        BigDecimal newBalance = user.getBalance().add(request.getAmount());
        user.setBalance(newBalance);
        userRepository.save(user);

        return new AccountResponse("입금 완료", newBalance, request.getAmount());
    }

    /**
     * 이메일로 출금 처리
     */
    @Transactional
    public AccountResponse withdrawByEmail(String email, AccountRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 잔고 확인
        if (user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("잔고가 부족합니다");
        }

        // 잔고 감소
        BigDecimal newBalance = user.getBalance().subtract(request.getAmount());
        user.setBalance(newBalance);
        userRepository.save(user);

        return new AccountResponse("출금 완료", newBalance, request.getAmount());
    }

    /**
     * 이메일로 잔고 조회
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalanceByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return user.getBalance();
    }

    // ========================================
    // userId 기반 메서드 (기존 호환성 유지)
    // ========================================

    /**
     * userId로 입금 처리 (기존 메서드)
     */
    @Transactional
    public AccountResponse deposit(Long userId, AccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        BigDecimal newBalance = user.getBalance().add(request.getAmount());
        user.setBalance(newBalance);
        userRepository.save(user);

        return new AccountResponse("입금 완료", newBalance, request.getAmount());
    }

    /**
     * userId로 출금 처리 (기존 메서드)
     */
    @Transactional
    public AccountResponse withdraw(Long userId, AccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("잔고가 부족합니다");
        }

        BigDecimal newBalance = user.getBalance().subtract(request.getAmount());
        user.setBalance(newBalance);
        userRepository.save(user);

        return new AccountResponse("출금 완료", newBalance, request.getAmount());
    }

    /**
     * userId로 잔고 조회 (기존 메서드)
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return user.getBalance();
    }
}