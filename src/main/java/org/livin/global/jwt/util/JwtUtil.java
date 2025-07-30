package org.livin.global.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;

import org.livin.user.entity.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Log4j2
@Component
public class JwtUtil {

	// JWT 서명 키, JWT 토큰의 무결성을 보장하는 비밀 키
	// 이 키가 노출되면 토큰 위조 가능 -> 보안 매우 중요!
	@Value("${jwt.secret}")
	private String secret;

	// Access Token 만료시간: 1시간
	private final long expiration = 1000 * 60 * 60;

	// Refresh Token 만료시간: 7일
	private final long refreshExpiration = 1000L * 60 * 60 * 24 * 7;

	// 토큰 검증
	public Claims validateToken(String token) {
		try {
			// "Bearer " 접두사 제거
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7).trim(); // 앞 7글자("Bearer ") 제거 + 공백 제거
			} else if (token != null) {
				token = token.trim(); // 그냥 공백만 제거
			}
			log.info("🔍 토큰 유효성 검사 시작 (정제 후): '{}'", token);

			return Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();
		} catch (JwtException e) {
			log.info(e);
			log.error("❌ JWT 토큰 유효성 검증 실패", e);
			throw new RuntimeException("JWT 토큰 유효성 검증 실패", e);
		}
	}

	// 소셜로그인용 Access Token 발급
	public String generateAccessToken(String provider, String providerId, UserRole role) {
		return Jwts.builder()
			.setSubject(provider + ":" + providerId) // subject로 식별
			.claim("provider", provider)
			.claim("providerId", providerId)
			.claim("role", role.name())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(SignatureAlgorithm.HS256, secret)
			.compact();
	}

	// 소셜로그인용 Refresh Token 발급
	public String generateRefreshToken(String provider, String providerId) {
		return Jwts.builder()
			.setSubject(provider + ":" + providerId)
			.claim("provider", provider)
			.claim("providerId", providerId)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
			.signWith(SignatureAlgorithm.HS256, secret)
			.compact();
	}
}
