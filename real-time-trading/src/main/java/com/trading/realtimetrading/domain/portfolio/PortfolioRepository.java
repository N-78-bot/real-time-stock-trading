package com.trading.realtimetrading.domain.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    // 사용자별 포트폴리오 조회
    List<Portfolio> findByUserId(Long userId);

    // 특정 종목 보유 여부 확인
    Optional<Portfolio> findByUserIdAndStockCode(Long userId, String stockCode);
}
