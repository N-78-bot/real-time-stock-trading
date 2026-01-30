package com.trading.realtimetrading.controller;

import com.trading.realtimetrading.domain.account.Account;
import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.domain.user.UserRepository;
import com.trading.realtimetrading.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    /**
     * 계좌 정보 조회
     * GET /api/account
     */
    @GetMapping
    public AccountResponse getAccount(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        Account account = accountService.getAccount(user);
        return new AccountResponse(account.getBalance());
    }

    /**
     * 입금
     * POST /api/account/deposit
     */
    @PostMapping("/deposit")
    public AccountResponse deposit(
            Authentication authentication,
            @RequestBody DepositRequest request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        Account account = accountService.deposit(user, request.getAmount());
        return new AccountResponse(account.getBalance());
    }

    /**
     * 출금
     * POST /api/account/withdraw
     */
    @PostMapping("/withdraw")
    public AccountResponse withdraw(
            Authentication authentication,
            @RequestBody WithdrawRequest request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        Account account = accountService.withdraw(user, request.getAmount());
        return new AccountResponse(account.getBalance());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class DepositRequest {
        private Double amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class WithdrawRequest {
        private Double amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class AccountResponse {
        private Double balance;
    }
}
