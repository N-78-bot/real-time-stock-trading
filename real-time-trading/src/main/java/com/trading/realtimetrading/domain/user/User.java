package com.trading.realtimetrading.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // 입금
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("입금 금액은 0보다 커야 합니다");
        }
        this.balance = this.balance.add(amount);
    }

    // 출금
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("출금 금액은 0보다 커야 합니다");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("잔고가 부족합니다");
        }
        this.balance = this.balance.subtract(amount);
    }

    // 닉네임 변경
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}