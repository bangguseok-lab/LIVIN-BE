package org.livin.user.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.livin.global.jwt.service.TokenService;
import org.livin.global.jwt.util.JwtUtil;
import org.livin.user.entity.UserRole;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final TokenService tokenService;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("providerId") String providerId) {
        tokenService.deleteRefreshToken(providerId);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam("providerId") String providerId) {
        String refreshToken = tokenService.getRefreshToken(providerId);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰 없음");
        }

        Claims claims;
        try {
            claims = jwtUtil.validateToken(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰");
        }

        String newAccessToken = jwtUtil.generateAccessToken(claims.get("provider").toString(), claims.get("providerId").toString(), UserRole.valueOf((String) claims.get("role")));

        return ResponseEntity.ok(newAccessToken);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> deleteUser(@RequestParam("providerId") String providerId) {
        userService.deleteUser(providerId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

}
