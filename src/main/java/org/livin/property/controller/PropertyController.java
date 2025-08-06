package org.livin.property.controller;

import java.util.List;

import org.livin.global.jwt.filter.CustomUserDetails;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.dto.PropertyDetailsDTO;
import org.livin.property.service.PropertyServiceImpl;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class PropertyController {

	private final UserService userService;
	private final PropertyServiceImpl propertyService;

	// 관심 매물 조회 - 메인페이지 전용
	@GetMapping("/properties/favorite")
	public ResponseEntity<List<PropertyDTO>> getFavoriteProperties(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@ModelAttribute FilteringDTO address
	) {
		// 인증 정보로 userId 추출
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		address.setUserId(userId);
		// log.info("인증정보 {}, 읍면동limit {} 호출이 잘 되는지", userDetails.getUsername(), address);

		List<PropertyDTO> result = propertyService.getFavoritePropertiesForMain(address);
		// log.info("회원의 관심 매물 {}건 조회 완료", result.size());

		return ResponseEntity.ok(result);
	}

	// 위치 정보(읍, 명, 동) 기반 전체 매물 조회 - 매물 조회 페이지
	@GetMapping("/properties")
	public ResponseEntity<List<PropertyDTO>> getPropertiesByRegion(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@ModelAttribute FilteringDTO address) {

		log.info("address = {}로 매물 요청", address);

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		address.setUserId(userId);

		List<PropertyDTO> result = propertyService.getPropertiesByRegion(address);

		log.info("매물 {}건 조회 완료", result.size());
		log.info("{}", result);

		return ResponseEntity.ok(result);
	}

	@GetMapping("/properties/details/{id}")
	public ResponseEntity<PropertyDetailsDTO> getPropertyDetails(
		@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable(name = "id") Long propertyId) {
		String providerId = customUserDetails.getProviderId();
		PropertyDetailsDTO propertyDetailsDTO = propertyService.getPropertyDetails(propertyId, providerId);
		return ResponseEntity.ok(propertyDetailsDTO);
	}

	// 관심 매물 리스트 조회 (지역, 체크리스트 필터링 및 페이징 포함)
	@GetMapping("/favorite-properties")
	public ResponseEntity<List<PropertyDTO>> getFavoritePropertiesWithFilter(
		@AuthenticationPrincipal CustomUserDetails userDetails, // CustomUserDetails 추가
		@ModelAttribute FilteringDTO filteringDTO // FilteringDTO 사용
	) {
		log.info("관심 매물 필터링 조회 요청 - filteringDTO: {}, userDetails: {}", filteringDTO, userDetails);

		if (userDetails == null || userDetails.getProviderId() == null) {
			log.warn("관심 매물 필터링 조회를 위한 인증 정보가 없습니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		if (userId == null) {
			log.warn("providerId에 해당하는 userId를 찾을 수 없습니다: {}", userDetails.getProviderId());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		filteringDTO.setUserId(userId); // FilteringDTO에 userId 설정

		List<PropertyDTO> favoriteProperties = propertyService.getFavoritePropertiesWithFilter(filteringDTO);

		log.info("관심 매물 {}건 조회 완료", favoriteProperties.size());
		log.info("{}", favoriteProperties);

		return ResponseEntity.ok(favoriteProperties);
	}

	// 수정: 관심 매물 삭제
	@DeleteMapping("/properties/{id}/favorite")
	public ResponseEntity<Void> removeFavoriteProperty(
		@PathVariable("id") Long propertyId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("관심 매물 삭제 요청 - propertyId: {}, userDetails: {}", propertyId, userDetails);

		if (userDetails == null || userDetails.getProviderId() == null) {
			log.warn("관심 매물 삭제를 위한 인증 정보가 없습니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		if (userId == null) {
			log.warn("제공된 providerId에 해당하는 userId를 찾을 수 없습니다: {}", userDetails.getProviderId());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			propertyService.removeFavoriteProperty(propertyId, userId);

			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			log.warn("관심 매물 삭제 중 사용자 관련 에러: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (RuntimeException e) {
			log.error("관심 매물 삭제 실패: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 관심 매물 추가 API
	@PostMapping("/properties/{id}/favorite")
	public ResponseEntity<Void> addFavoriteProperty(
		@PathVariable("id") Long propertyId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("관심 매물 추가 요청 - propertyId: {}, userDetails: {}", propertyId, userDetails);

		if (userDetails == null || userDetails.getProviderId() == null) {
			log.warn("관심 매물 추가를 위한 인증 정보가 없습니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		if (userId == null) {
			log.warn("제공된 providerId에 해당하는 userId를 찾을 수 없습니다: {}", userDetails.getProviderId());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			propertyService.addFavoriteProperty(userId, propertyId);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (IllegalArgumentException e) { // 예를 들어 이미 찜한 매물인 경우
			log.warn("관심 매물 추가 중 사용자 관련 에러: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (RuntimeException e) { // 기타 런타임 에러
			log.error("관심 매물 추가 실패: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}