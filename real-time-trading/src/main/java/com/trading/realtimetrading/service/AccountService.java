package com.trading.realtimetrading.service;

import com.trading.realtimetrading.domain.account.Account;
import com.trading.realtimetrading.domain.account.AccountRepository;
import com.trading.realtimetrading.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * 계좌 생성 (회원가입 시)
     */
    @Transactional
    public Account createAccount(User user) {
        Account account = Account.builder()
                .user(user)
                .balance(0.0)
                .build();

        accountRepository.save(account);
        log.info("Account created for user: {}", user.getEmail());
        return account;
    }

    /**
     * 계좌 조회
     */
    @Transactional(readOnly = true)
    public Account getAccount(User user) {
        return accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다"));
    }

    /**
     * 입금
     */
    @Transactional
    public Account deposit(User user, Double amount) {
        Account account = getAccount(user);
        account.deposit(amount);
        accountRepository.save(account);
        log.info("Deposited {} to user: {}", amount, user.getEmail());
        return account;
    }

    /**
     * 출금
     */
    @Transactional
    public Account withdraw(User user, Double amount) {
        Account account = getAccount(user);
        account.withdraw(amount);
        accountRepository.save(account);
        log.info("Withdrew {} from user: {}", amount, user.getEmail());
        return account;
    }
}
