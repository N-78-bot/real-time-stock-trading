package com.trading.realtimetrading.domain.account;

import com.trading.realtimetrading.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Double balance;  // 현금 잔고

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (balance == null) {
            balance = 0.0;
        }
    }

    /**
     * 입금
     */
    public void deposit(Double amount) {
        if (amount <= 0) {
            throw new RuntimeException("입금 금액은 0보다 커야 합니다");
        }
        this.balance += amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 출금
     */
    public void withdraw(Double amount) {
        if (amount <= 0) {
            throw new RuntimeException("출금 금액은 0보다 커야 합니다");
        }
        if (this.balance < amount) {
            throw new RuntimeException("잔고가 부족합니다");
        }
        this.balance -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 잔고 확인
     */
    public boolean hasEnoughBalance(Double amount) {
        return this.balance >= amount;
    }
}