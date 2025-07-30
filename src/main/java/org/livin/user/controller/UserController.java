package org.livin.user.controller;

import org.livin.global.jwt.service.TokenService;
import org.livin.global.jwt.util.JwtUtil;
import org.livin.user.dto.UserNicknameDTO;
import org.livin.user.dto.UserResponseDTO;
import org.livin.user.entity.UserRole;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
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

		String newAccessToken = jwtUtil.generateAccessToken(claims.get("provider").toString(),
			claims.get("providerId").toString(), UserRole.valueOf((String)claims.get("role")));

		return ResponseEntity.ok(newAccessToken);
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<String> deleteUser(@RequestParam("providerId") String providerId) {
		userService.deleteUser(providerId);
		return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
	}

	// 회원 닉네임 조회
	@GetMapping("/nickname")
	public ResponseEntity<UserNicknameDTO> getUserNickname(@RequestParam("providerId") String providerId) {
		log.info("getUserNickname: " + providerId);

		UserNicknameDTO userNicknameDTO = userService.getUserNickname(providerId);

		return ResponseEntity.ok(userNicknameDTO);
	}

	// 회원 정보 조회
	@GetMapping("")
	public ResponseEntity<UserResponseDTO> getUserInfo(@RequestParam Long userId) {
		UserResponseDTO userInfo = userService.getUserInfo(userId);
		return ResponseEntity.ok(userInfo);
	}

}
