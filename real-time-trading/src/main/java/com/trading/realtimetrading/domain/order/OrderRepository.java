package com.trading.realtimetrading.domain.order;

import com.trading.realtimetrading.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByUserAndStatusOrderByCreatedAtDesc(User user, OrderStatus status);
}
