package org.livin.global.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

// JWT의 Refresh Token을 Redis에 저장하고 관리하는 서비스
@Service    // 서비스 계층 컴포넌트임을 표시하는 어노테이션, Spring Container가 이 클래스를 Bean으로 등록
@RequiredArgsConstructor    // 의존성 주입을 위한 생성자를 자동으로 만들어줌
public class TokenService {

    // 의존성 주입
    // RedisTemplate<String, String>: Spring Data Redis에서 제공하는 Redis 조작 템플릿
    // 용도: Redis에 데이터를 저장, 조회, 삭제하는 모든 작업을 수행
    private final RedisTemplate<String, String> redisTemplate;

    // 만료 시간 계산
    // 1000L(1초, 밀리초 단위) *60(1분) *60(1시간) *24(1일) *7(7일)
    // Refresh Token의 유효 기간 => 7일
    private final long refreshExpiration = 1000L * 60 * 60 * 24 * 7;

    // opsForValue().set(key, value, timeout): Redis 저장
    // set(key, value, timeout): 키-값을 저장하며 만료시간 설정
    // Duration.ofMillis(refreshExpiration): 7일 후 자동 삭제
    public void saveRefreshToken(String providerId, String refreshToken) {
        redisTemplate.opsForValue().set("refresh:" + providerId, refreshToken, Duration.ofMillis(refreshExpiration));
    }

    // RefreshToken이 존재하고 만료되지 않았으면, 토큰 문자열 반환
    // 없거나 만료되었으면 null 반환
    // 사용자가 Access Token 갱신 요청 시 or Refresh Token의 유효성 검증 시
    public String getRefreshToken(String providerId) {
        return redisTemplate.opsForValue().get("refresh:" + providerId);
    }

    // RefreshToken 삭제
    // 사용자 로그아웃 or 계정 탈퇴
    public void deleteRefreshToken(String providerId) {
        redisTemplate.delete("refresh:" + providerId);
    }
}
