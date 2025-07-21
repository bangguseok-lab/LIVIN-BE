package org.livin.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.livin.dto.*;
import org.livin.service.MainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final MainService mainService;

    /**
     * 1. 회원 정보 받아와 닉네임을 프론트에 전달
     * GET /api/main/user-info
     */
    @GetMapping("/user-info")
    public ResponseEntity<?> getUserNickname(Authentication authentication) {
        log.info("회원 닉네임 조회 요청");

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "인증되지 않은 사용자입니다."));
            }

            String username = authentication.getName();
            UserInfoDTO userInfo = mainService.getUserInfo(username);

            // 닉네임만 전달
            Map<String, String> response = new HashMap<>();
            response.put("nickname", userInfo.getNickname());

            log.info("닉네임 조회 성공: {}", userInfo.getNickname());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("회원 정보 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "회원 정보 조회에 실패했습니다."));
        }
    }

    /**
     * 2. 찜한 매물 정보를 최신 순서대로 3개를 가져와서 프론트에 전달
     * GET /api/main/favorite-properties
     */
    @GetMapping("/favorite-properties")
    public ResponseEntity<?> getLatestFavoriteProperties(Authentication authentication) {
        log.info("찜한 매물 최신 3개 조회 요청");

        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "인증되지 않은 사용자입니다."));
            }

            String username = authentication.getName();
            List<FavoritePropertyDTO> favorites = mainService.getFavoriteProperties(username, 3);

            Map<String, Object> response = new HashMap<>();
            response.put("properties", favorites);
            response.put("count", favorites.size());

            log.info("찜한 매물 조회 성공: {}개", favorites.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("찜한 매물 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "찜한 매물 조회에 실패했습니다."));
        }
    }

    /**
     * 3. 현재 위치 정보를 받아와서 해당 위치의 최신 순서대로 매물 4개를 가져와서 프론트에 전달
     * POST /api/main/nearby-properties
     */
    @PostMapping("/nearby-properties")
    public ResponseEntity<?> getNearbyLatestProperties(@RequestBody LocationRequest locationRequest) {
        log.info("위치 기반 최신 매물 4개 조회 요청 - 위도: {}, 경도: {}",
                locationRequest.getLat(), locationRequest.getLng());

        try {
            // 위치 정보 검증
            if (locationRequest.getLat() == null || locationRequest.getLng() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "위도와 경도 정보가 필요합니다."));
            }

            List<PropertyDTO> properties = mainService.getNearbyLatestProperties(
                    locationRequest.getLat(),
                    locationRequest.getLng(),
                    4
            );

            Map<String, Object> response = new HashMap<>();
            response.put("properties", properties);
            response.put("count", properties.size());
            response.put("location", Map.of(
                    "lat", locationRequest.getLat(),
                    "lng", locationRequest.getLng()
            ));

            log.info("주변 매물 조회 성공: {}개", properties.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("주변 매물 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "주변 매물 조회에 실패했습니다."));
        }
    }
}

// 위치 정보 요청 DTO
@Data
class LocationRequest {
    private Double lat;
    private Double lng;
}