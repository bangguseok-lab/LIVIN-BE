package org.livin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.livin.dto.*;
import org.livin.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 개발/테스트용 컨트롤러 - 인증 없이 테스트 가능
 * 운영 환경에서는 제거해야 함!
 */
@RestController
@RequestMapping("/test/main")
@RequiredArgsConstructor
@Log4j2
public class MainControllerTest {

    private final MainService mainService;

    /**
     * 테스트용 - 하드코딩된 사용자로 닉네임 조회
     */
    @GetMapping("/user-info")
    public ResponseEntity<?> getUserNickname() {
        log.info("테스트: 회원 닉네임 조회");

        try {
            // 테스트용 하드코딩된 사용자
            String testUsername = "testuser";
            UserNicknameDTO userInfo = mainService.getUserInfo(testUsername);

            Map<String, String> response = new HashMap<>();
            response.put("nickname", userInfo.getNickname());
            response.put("testMode", "true");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("테스트 실패", e);
            // 모의 데이터 반환
            return ResponseEntity.ok(Map.of(
                    "nickname", "테스트유저",
                    "testMode", "true",
                    "mock", "true"
            ));
        }
    }

    /**
     * 테스트용 - 모의 찜한 매물 데이터 반환
     */
    @GetMapping("/favorite-properties")
    public ResponseEntity<?> getFavoriteProperties() {
        log.info("테스트: 찜한 매물 조회");

        try {
            String testUsername = "testuser";
            List<FavoritePropertyDTO> favorites = mainService.getFavoriteProperties(testUsername, 3);

            if (favorites.isEmpty()) {
                // 모의 데이터 생성
                favorites = createMockFavorites();
            }

            return ResponseEntity.ok(Map.of(
                    "properties", favorites,
                    "count", favorites.size(),
                    "testMode", true
            ));
        } catch (Exception e) {
            log.error("테스트 실패", e);
            // 모의 데이터 반환
            return ResponseEntity.ok(Map.of(
                    "properties", createMockFavorites(),
                    "count", 3,
                    "testMode", true,
                    "mock", true
            ));
        }
    }

    /**
     * 테스트용 - 위치 기반 매물 조회 (인증 불필요)
     */
    @PostMapping("/nearby-properties")
    public ResponseEntity<?> getNearbyProperties(@RequestBody Map<String, Double> location) {
        log.info("테스트: 위치 기반 매물 조회 - 위도: {}, 경도: {}",
                location.get("lat"), location.get("lng"));

        try {
            Double lat = location.get("lat");
            Double lng = location.get("lng");

            if (lat == null || lng == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "위도와 경도 정보가 필요합니다."));
            }

            List<PropertyDTO> properties = mainService.getNearbyLatestProperties(lat, lng, 4);

            if (properties.isEmpty()) {
                // 모의 데이터 생성
                properties = createMockProperties(lat, lng);
            }

            return ResponseEntity.ok(Map.of(
                    "properties", properties,
                    "count", properties.size(),
                    "location", Map.of("lat", lat, "lng", lng),
                    "testMode", true
            ));
        } catch (Exception e) {
            log.error("테스트 실패", e);
            Double lat = location.getOrDefault("lat", 37.5665);
            Double lng = location.getOrDefault("lng", 126.9780);

            return ResponseEntity.ok(Map.of(
                    "properties", createMockProperties(lat, lng),
                    "count", 4,
                    "location", Map.of("lat", lat, "lng", lng),
                    "testMode", true,
                    "mock", true
            ));
        }
    }

    // 모의 데이터 생성 메소드들
    private List<FavoritePropertyDTO> createMockFavorites() {
        List<FavoritePropertyDTO> list = new ArrayList<>();

        FavoritePropertyDTO p1 = new FavoritePropertyDTO();
        p1.setPropertyId(1L);
        p1.setPropertyName("강남역 원룸");
        p1.setAddress("서울시 강남구 역삼동 123-45");
        p1.setPropertyType("원룸");
        p1.setMonthlyRent(80);
        p1.setDeposit(1000);
        p1.setArea(23.5);
        p1.setFloor(3);
        p1.setImageUrl("/resources/images/sample1.jpg");
        p1.setFavoriteDate(LocalDateTime.now().minusDays(1));
        list.add(p1);

        FavoritePropertyDTO p2 = new FavoritePropertyDTO();
        p2.setPropertyId(2L);
        p2.setPropertyName("신림역 투룸");
        p2.setAddress("서울시 관악구 신림동 456-78");
        p2.setPropertyType("투룸");
        p2.setMonthlyRent(100);
        p2.setDeposit(1500);
        p2.setArea(33.0);
        p2.setFloor(5);
        p2.setImageUrl("/resources/images/sample2.jpg");
        p2.setFavoriteDate(LocalDateTime.now().minusDays(2));
        list.add(p2);

        FavoritePropertyDTO p3 = new FavoritePropertyDTO();
        p3.setPropertyId(3L);
        p3.setPropertyName("홍대 오피스텔");
        p3.setAddress("서울시 마포구 서교동 789-12");
        p3.setPropertyType("오피스텔");
        p3.setMonthlyRent(120);
        p3.setDeposit(2000);
        p3.setArea(28.0);
        p3.setFloor(8);
        p3.setImageUrl("/resources/images/sample3.jpg");
        p3.setFavoriteDate(LocalDateTime.now().minusDays(3));
        list.add(p3);

        return list;
    }

    private List<PropertyDTO> createMockProperties(double lat, double lng) {
        List<PropertyDTO> list = new ArrayList<>();

        // 입력받은 위치 주변에 가상의 매물 생성
        for (int i = 1; i <= 4; i++) {
            PropertyDTO p = new PropertyDTO();
            p.setPropertyId((long) i);
            p.setPropertyName("테스트 매물 " + i);
            p.setAddress("서울시 중구 테스트동 " + i);
            p.setPropertyType(i % 2 == 0 ? "원룸" : "투룸");
            p.setMonthlyRent(80 + (i * 10));
            p.setDeposit(1000 + (i * 200));
            p.setArea(20.0 + (i * 3));
            p.setFloor(i);
            p.setTotalFloors(10);
            p.setImageUrl("/resources/images/sample" + i + ".jpg");
            // 입력받은 위치 근처에 랜덤하게 배치
            p.setLatitude(lat + (Math.random() * 0.01 - 0.005));
            p.setLongitude(lng + (Math.random() * 0.01 - 0.005));
            p.setHasElevator(i > 2);
            p.setHasParking(i % 2 == 0);
            p.setCreatedAt(LocalDateTime.now().minusHours(i));
            list.add(p);
        }

        return list;
    }
}