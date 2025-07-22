package org.livin.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.livin.dto.*;
import org.livin.exception.MainPageException;
import org.livin.service.MainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final MainService mainService;


    // 1. 회원 정보 받아와 닉네임을 프론트에 전달
    //    닉네임 정보와 함께 인사말 출력.
    // TODO: 인증(Authentication)을 authentication으로 받아오는건지 논의 필요,
    //  try catch문 대신해서 쓸 new 명령문으로 vue에 출력되도록 전달.
    @GetMapping("/users")
    public ResponseEntity<?> getUserNickname(Authentication authentication) {
        // Authentication - Spring Security에서 로그인 인증을 받아오기 위한 객체.
        log.info("회원 닉네임 조회 요청");

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new MainPageException.UnauthorizedException("인증되지 않은 회원입니다."); // 회원 정보 인증 검증 과정 - 실패 예외 처리
            }

            String username = authentication.getName(); // getName() : 보통 “로그인한 사용자의 ID(이메일/username)”
            UserInfoDTO userInfo = mainService.getUserInfo(username);

            // 회원 인증 이후, 정보가 없을 경우
            if (userInfo == null) {
                throw new MainPageException.ResourceNotFoundException("회원 정보를 찾을 수 없습니다.");
            }

            // 정상 응답
            // 닉네임만 전달
            Map<String, String> response = new HashMap<>();
            response.put("nickname", userInfo.getNickname());

            log.info("닉네임 조회 성공: {}", userInfo.getNickname());
            return ResponseEntity.ok(response);

    }


    // 2. 찜한 매물 정보를 최신 순서대로 3개를 가져와서 프론트에 전달
    @GetMapping("/users/favorite")
    public ResponseEntity<?> getLatestFavoriteProperties(Authentication authentication) {
        log.info("찜한 매물 최신 3개 조회 요청");

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new MainPageException.UnauthorizedException("인증되지 않은 회원입니다.");
            }

            String username = authentication.getName();
            List<FavoritePropertyDTO> favorites = mainService.getFavoriteProperties(username, 3);

            // 정상 응답
            Map<String, Object> response = new HashMap<>();
            response.put("properties", favorites);
            response.put("count", favorites.size());

            log.info("찜한 매물 조회 성공: {}개", favorites.size());
            return ResponseEntity.ok(response);
    }


    // 3. 현재 위치 정보를 받아와서 해당 위치의 최신 순서대로 매물 4개를 가져와서 프론트에 전달
    @PostMapping("/properties")
    public ResponseEntity<?> getNearbyLatestProperties(@RequestBody LocationRequest locationRequest) {

        // 네이버지도에서 보낸 위도/경도를 받아서 처리
        log.info("네이버지도 위치 정보 수신 - 위도: {}, 경도: {}",
                locationRequest.getLat(), locationRequest.getLng());

        // 위치 검증 - MainPageException에서 모아둔 예외 처리를 한번에 처리함.
        // 기존 if 문에서 위도 또는 경도 값의 null 여부를 판별해 처리함.
        MainPageException.validateLocation(locationRequest.getLat(), locationRequest.getLng());

        // 해당 위치 주변 매물 검색
        List<PropertyDTO> properties = mainService.getNearbyLatestProperties(
                locationRequest.getLat(),
                locationRequest.getLng(),
                4  // 4개만 가져오기
        );

        // 정상 응답
        Map<String, Object> response = new HashMap<>();
        response.put("properties", properties);
        response.put("count", properties.size());

        log.info("주변 매물 조회 성공: {}", properties.size());
        return ResponseEntity.ok(response);
    }
}

// 위치 정보 요청 DTO
@Data
class LocationRequest {
    private Double lat;
    private Double lng;
}