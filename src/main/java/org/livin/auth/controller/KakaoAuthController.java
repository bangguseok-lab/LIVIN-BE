package org.livin.auth.controller;

import lombok.RequiredArgsConstructor;

import org.livin.auth.dto.AdditionalUserInfo;
import org.livin.auth.dto.KakaoTokenResponse;
import org.livin.auth.dto.KakaoUserInfo;
import org.livin.auth.service.KakaoAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KakaoAuthController {

	private final KakaoAuthService authService;

	@GetMapping("/kakao/callback")
	public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
		KakaoTokenResponse kakaoToken = authService.getKakaoAccessToken(code);
		KakaoUserInfo kakaoUser = authService.getUserInfo(kakaoToken.getAccessToken());

		boolean exists = authService.existsUserByProviderId("kakao", kakaoUser.getProviderId());

		if (exists) {
			// 이미 등록된 사용자 - JWT 발급
			String jwt = authService.loginOrRegisterUser(kakaoUser, null);
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + jwt);

			return new ResponseEntity<>(kakaoUser.getProviderId(), headers, HttpStatus.OK);
		} else {
			// 미등록 사용자 - 추가정보 입력 요청
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(kakaoUser.getProviderId());
		}
	}

	@PostMapping("/kakao/register")
	public ResponseEntity<String> kakaoRegister(
		@RequestParam("providerId") String providerId,
		@RequestBody AdditionalUserInfo request
	) {
		KakaoUserInfo kakaoUser = new KakaoUserInfo();
		kakaoUser.setProvider("kakao");
		kakaoUser.setProviderId(providerId);

		String jwt = authService.loginOrRegisterUser(kakaoUser, request);
		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + jwt)
			.build();
	}

}