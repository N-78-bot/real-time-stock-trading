package com.trading.realtimetrading.config;

import com.trading.realtimetrading.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 * - JWT 기반 인증
 * - Stateless 세션 정책
 * - 공개 URL vs 인증 필요 URL 분리
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용 시 불필요)
                .csrf(csrf -> csrf.disable())

                // 세션 사용 안 함 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // URL별 인증 설정
                .authorizeHttpRequests(auth -> auth
                        // 공개 API (인증 불필요)
                        .requestMatchers("/api/auth/**").permitAll()     // 회원가입, 로그인
                        .requestMatchers("/api/test/**").permitAll()     // Kafka 테스트
                        .requestMatchers("/api/stocks/**").permitAll()   // 주식 시세 조회
                        .requestMatchers("/health/**").permitAll()       // Health Check

                        // WebSocket & 정적 리소스 (인증 불필요)
                        .requestMatchers("/ws/**").permitAll()           // WebSocket 엔드포인트
                        .requestMatchers("/topic/**").permitAll()        // STOMP topic
                        .requestMatchers("/app/**").permitAll()          // STOMP app
                        .requestMatchers("/*.html").permitAll()          // HTML 파일
                        .requestMatchers("/static/**").permitAll()       // 정적 리소스
                        .requestMatchers("/css/**").permitAll()          // CSS
                        .requestMatchers("/js/**").permitAll()           // JavaScript
                        .requestMatchers("/images/**").permitAll()       // 이미지

                        // 나머지는 모두 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}