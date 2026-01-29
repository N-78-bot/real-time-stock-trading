package com.trading.realtimetrading;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/health/db")
    public String checkDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            return "MySQL 연결 성공! ✅";
        } catch (Exception e) {
            return "MySQL 연결 실패: " + e.getMessage();
        }
    }

    @GetMapping("/health/redis")
    public String checkRedis() {
        try {
            redisTemplate.opsForValue().set("test", "hello");
            String value = redisTemplate.opsForValue().get("test");
            return "Redis 연결 성공! 값: " + value + " ✅";
        } catch (Exception e) {
            return "Redis 연결 실패: " + e.getMessage();
        }
    }
}