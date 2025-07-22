package org.livin.global.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.livin.user.entity.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // 1시간
    private final long expiration = 1000 * 60 * 60;

    // 7일
    private final long refreshExpiration = 1000L * 60 * 60 * 24 * 7;

    // 토큰 검증
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
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
