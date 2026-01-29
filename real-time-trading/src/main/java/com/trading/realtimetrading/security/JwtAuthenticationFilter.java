package com.trading.realtimetrading.security;

import com.trading.realtimetrading.config.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT 인증 필터
 * - 모든 HTTP 요청마다 실행됨
 * - Authorization 헤더에서 JWT 토큰 추출
 * - 토큰 검증 후 Spring Security에 인증 정보 저장
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Request에서 JWT 토큰 추출
            String token = getTokenFromRequest(request);

            // 2. 토큰 검증
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 3. 토큰에서 이메일 추출
                String email = jwtTokenProvider.getEmailFromToken(token);

                // 4. Spring Security 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,           // Principal (인증된 사용자 식별자)
                                null,            // Credentials (비밀번호, JWT에선 불필요)
                                new ArrayList<>() // Authorities (권한, 일단 빈 리스트)
                        );

                // 5. 요청 세부 정보 추가 (IP, Session ID 등)
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 6. SecurityContext에 인증 정보 저장
                // → 이후 Controller에서 Authentication 객체로 접근 가능
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 토큰 검증 실패 시 로그 출력
            System.err.println("JWT 인증 실패: " + e.getMessage());
        }

        // 7. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 JWT 토큰 추출
     * Authorization: Bearer <token>
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // "Bearer " 접두사 확인 및 제거
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후 문자열 반환
        }

        return null;
    }
}