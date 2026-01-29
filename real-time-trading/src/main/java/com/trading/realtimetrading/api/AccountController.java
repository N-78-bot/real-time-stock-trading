package com.trading.realtimetrading.api;

import com.trading.realtimetrading.dto.AccountRequest;
import com.trading.realtimetrading.dto.AccountResponse;
import com.trading.realtimetrading.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 입출금 API
 * - JWT 인증 필수
 * - Authentication 객체에서 이메일 추출
 * - 본인 계좌만 접근 가능
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 잔고 충전 (입금)
     * JWT 토큰에서 이메일 추출 → 본인 계좌에만 입금
     */
    @PostMapping("/deposit")
    public ResponseEntity<AccountResponse> deposit(
            Authentication authentication,  // Spring Security가 자동 주입
            @Valid @RequestBody AccountRequest request
    ) {
        // JWT에서 추출한 이메일 (JwtAuthenticationFilter에서 설정함)
        String email = authentication.getName();

        AccountResponse response = accountService.depositByEmail(email, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 잔고 차감 (출금)
     * JWT 토큰에서 이메일 추출 → 본인 계좌에서만 출금
     */
    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            Authentication authentication,
            @Valid @RequestBody AccountRequest request
    ) {
        String email = authentication.getName();

        AccountResponse response = accountService.withdrawByEmail(email, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 잔고 조회
     * JWT 토큰에서 이메일 추출 → 본인 잔고만 조회
     */
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(Authentication authentication) {
        String email = authentication.getName();

        BigDecimal balance = accountService.getBalanceByEmail(email);
        return ResponseEntity.ok(balance);
    }
}