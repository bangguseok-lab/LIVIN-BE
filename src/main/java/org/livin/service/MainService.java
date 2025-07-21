package org.livin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.livin.dto.*;
import org.livin.mapper.PropertyMapper;
import org.livin.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class MainService {

    private final UserMapper userMapper;
    private final PropertyMapper propertyMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    // 다른 팀원이 구현한 API 엔드포인트 (예시)
    private static final String USER_API_BASE_URL = "http://localhost:8080/api/users";
    private static final String PROPERTY_API_BASE_URL = "http://localhost:8080/api/properties";

    /**
     * 회원 정보 조회 - GET /api/users 호출
     */
    public UserInfoDTO getUserInfo(String username) {
        try {
            // 방법 1: 외부 API 호출
            String url = USER_API_BASE_URL + "/" + username;
            ResponseEntity<UserInfoDTO> response = restTemplate.getForEntity(url, UserInfoDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            // 방법 2: 직접 DB 조회 (백업)
            return userMapper.findUserByUsername(username);

        } catch (Exception e) {
            log.error("회원 정보 조회 실패: " + username, e);
            // 기본값 반환
            UserInfoDTO defaultUser = new UserInfoDTO();
            defaultUser.setUserId(username);
            defaultUser.setNickname("게스트");
            return defaultUser;
        }
    }

    /**
     * 찜한 매물 조회 - POST /api/users/favorite 호출
     */
    public List<FavoritePropertyDTO> getFavoriteProperties(String username, int limit) {
        try {
            // POST 요청 준비
            String url = USER_API_BASE_URL + "/favorite";

            // 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("userId", username);
            requestBody.put("limit", limit);
            requestBody.put("sort", "createdAt,desc"); // 최신순 정렬

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 엔티티 생성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<List<FavoritePropertyDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<FavoritePropertyDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<FavoritePropertyDTO> favorites = response.getBody();
                // 상위 limit개만 반환
                return favorites.size() > limit ? favorites.subList(0, limit) : favorites;
            }

            // 백업: DB에서 직접 조회
            return propertyMapper.findFavoritePropertiesByUserId(username, limit);

        } catch (Exception e) {
            log.error("찜한 매물 조회 실패: " + username, e);
            return new ArrayList<>();
        }
    }

    /**
     * 현재 위치 기반 최신 매물 조회 - GET /api/properties 호출
     * 위치 정보를 받아 해당 위치의 최신 순서대로 매물 4개 반환
     */
    public List<PropertyDTO> getNearbyLatestProperties(double lat, double lng, int limit) {
        try {
            // 쿼리 파라미터 구성 - 최신순 정렬 추가
            String url = String.format("%s?lat=%f&lng=%f&limit=%d&radius=2&sort=createdAt,desc",
                    PROPERTY_API_BASE_URL, lat, lng, limit);

            // API 호출
            ResponseEntity<List<PropertyDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PropertyDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            // 백업: DB에서 위치 기반 + 최신순 조회
            return propertyMapper.findNearbyLatestProperties(lat, lng, 2.0, limit);

        } catch (Exception e) {
            log.error("주변 최신 매물 조회 실패", e);
            return new ArrayList<>();
        }
    }

    /**
     * 거리 기반 매물 필터링 (백업용)
     */
    private List<PropertyDTO> filterNearbyProperties(List<PropertyDTO> properties,
                                                     double centerLat, double centerLng, int limit) {
        // 각 매물에 대해 거리 계산
        properties.forEach(property -> {
            double distance = calculateDistance(centerLat, centerLng,
                    property.getLatitude(), property.getLongitude());
            property.setDistance(distance);
        });

        // 거리순 정렬
        properties.sort(Comparator.comparing(PropertyDTO::getDistance));

        // 상위 limit개 반환
        return properties.size() > limit ? properties.subList(0, limit) : properties;
    }

    /**
     * 두 지점 간 거리 계산 (Haversine formula)
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // 지구 반경(km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // 거리(km)
    }
}