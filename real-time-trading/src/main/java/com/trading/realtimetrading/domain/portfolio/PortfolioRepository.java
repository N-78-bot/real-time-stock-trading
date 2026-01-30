package com.trading.realtimetrading.domain.portfolio;

import com.trading.realtimetrading.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUserAndStockCode(User user, String stockCode);
    List<Portfolio> findByUser(User user);
}
