package org.livin.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.livin.auth.dto.AdditionalUserInfo;
import org.livin.auth.dto.NaverTokenResponse;
import org.livin.auth.dto.NaverUserInfo;
import org.livin.auth.service.NaverAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NaverAuthController {

    private final NaverAuthService authService;

    // 1. 네이버 콜백에서 인가코드와 state 받아서 처리
    @GetMapping("/naver/callback")
    public ResponseEntity<?> naverCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) {

        NaverTokenResponse tokenResponse = authService.getNaverAccessToken(code, state);
        NaverUserInfo naverUser = authService.getUserInfo(tokenResponse.getAccessToken());

        boolean exists = authService.existsUserByProviderId("naver", naverUser.getProviderId());

        if (exists) {
            // 이미 등록된 사용자 - JWT 발급
            String jwt = authService.loginOrRegisterUser(naverUser, null);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwt);

            return new ResponseEntity<>(naverUser.getProviderId(), headers, HttpStatus.OK);
        } else {
            // 미등록 사용자 - 추가정보 입력 요청
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(naverUser.getProviderId());
        }
    }

    // 2. 추가 정보 입력 받아 회원가입 및 JWT 발급
    @PostMapping("/naver/register")
    public ResponseEntity<String> naverRegister(
            @RequestParam("providerId") String providerId,
            @RequestBody AdditionalUserInfo request) {

        NaverUserInfo userInfo = new NaverUserInfo();
        userInfo.setProvider("naver");
        userInfo.setProviderId(providerId);

        String jwt = authService.loginOrRegisterUser(userInfo, request);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwt)
                .build();
    }
}

