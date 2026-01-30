package com.trading.realtimetrading.api;

import com.trading.realtimetrading.dto.AccountRequest;
import com.trading.realtimetrading.dto.AccountResponse;
import com.trading.realtimetrading.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 입금
     */
    @PostMapping("/deposit")
    public AccountResponse deposit(
            Authentication authentication,
            @Valid @RequestBody AccountRequest request) {

        String email = authentication.getName();
        return accountService.deposit(email, request);
    }

    /**
     * 출금
     */
    @PostMapping("/withdraw")
    public AccountResponse withdraw(
            Authentication authentication,
            @Valid @RequestBody AccountRequest request) {

        String email = authentication.getName();
        return accountService.withdraw(email, request);
    }

    /**
     * 잔고 조회
     */
    @GetMapping("/balance")
    public AccountResponse getBalance(Authentication authentication) {
        String email = authentication.getName();
        return accountService.getBalance(email);
    }
}