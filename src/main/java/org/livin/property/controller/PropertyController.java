package org.livin.property.controller;

import java.util.List;

import org.livin.global.jwt.filter.CustomUserDetails;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.service.PropertyServiceImpl;
import org.livin.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.livin.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class PropertyController {

	private final UserService userService;
	private final PropertyServiceImpl propertyService;

	// 관심 매물 조회
	@GetMapping("/properties/favorite")
	public ResponseEntity<?> getFavoriteProperties(@ModelAttribute FilteringDTO address
	) {
		log.info("address = {}로 매물 요청", address);
		List<PropertyDTO> result = propertyService.getFavoritePropertiesForMain(address);
		log.info("매물 {}건 조회 완료", result.size());
		log.info("{}", result);

		return ResponseEntity.ok(result);
	}

	// 위치 정보(읍, 명, 동) 기반 전체 매물 조회

	//@ModelAttribute AddressDTO address 의미: 요청 파라미터(query string)**로 전달된 값을 AddressDTO 객체에 자동 바인딩
	//properties?sido=서울특별시&sigungu=강남구&eupmyendong=역삼동

	@GetMapping("/properties")
	public ResponseEntity<List<PropertyDTO>> getPropertiesByRegion(@AuthenticationPrincipal CustomUserDetails userDetails,
		@ModelAttribute FilteringDTO address) {

		log.info("address = {}로 매물 요청", address);

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		address.setUserId(userId);

		List<PropertyDTO> result = propertyService.getPropertiesByRegion(address);

		log.info("매물 {}건 조회 완료", result.size());
		log.info("{}", result);

		return ResponseEntity.ok(result);
	}

	// 관심 매물 리스트 조회 (지역, 체크리스트 필터링 및 페이징 포함)
	@GetMapping("/favorite-properties")
	public ResponseEntity<List<PropertyDTO>> getFavoritePropertiesWithFilter(@ModelAttribute FilteringDTO address) {
		log.info("관심 매물 조회 요청 - address: {}", address);

		// getFavoritePropertiesWithFilter는 AddressDTO에 providerId가 이미 있으므로, 서비스에서 userId를 찾습니다.
		// (PropertyService 내부에서 userMapper를 사용함)
		List<PropertyDTO> favoriteProperties = propertyService.getFavoritePropertiesWithFilter(address);

		log.info("관심 매물 {}건 조회 완료", favoriteProperties.size());
		log.info("{}", favoriteProperties);

		return ResponseEntity.ok(favoriteProperties);
	}

	// 관심 매물 삭제
	@DeleteMapping("/properties/{id}/favorite")
	public ResponseEntity<Void> removeFavoriteProperty(
		@PathVariable("id") Long propertyId,
		@RequestHeader("X-User-Provider-Id") String providerId // 사용자 식별을 위한 providerId (헤더에서 받음)
	) {
		log.info("관심 매물 삭제 요청 - propertyId: {}, providerId: {}", propertyId, providerId);

		try {
			// ✅ 기존 FavChecklistController 패턴과 동일하게 UserService를 통해 userId 변환
			Long userId = userService.getUserIdByProviderId(providerId); // UserService를 사용하여 userId 변환

			if (userId == null) {
				log.warn("제공된 providerId에 해당하는 userId를 찾을 수 없습니다: {}", providerId);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
			}

			// 변환된 userId를 서비스로 전달
			propertyService.removeFavoriteProperty(propertyId, userId);

			return ResponseEntity.ok().build(); // 삭제 성공 시 200 OK 반환
		} catch (IllegalArgumentException e) { // 서비스에서 던진 예외 (예: Invalid user)
			log.warn("관심 매물 삭제 중 사용자 관련 에러: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
		}
		catch (RuntimeException e) { // 서비스에서 던진 기타 RuntimeException 처리
			log.error("관심 매물 삭제 실패: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
		}
	}
}