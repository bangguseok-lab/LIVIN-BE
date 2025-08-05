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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	// // 관심 매물 조회
	// @GetMapping("/properties/favorite")
	// public ResponseEntity<?> getFavoriteProperties(
	// 	@AuthenticationPrincipal CustomUserDetails customUserDetails,
	// 	@RequestParam(defaultValue = "3") int limit
	// ) {
	// 	String providerId = customUserDetails.getProviderId();
	// 	log.info("providerId = {}로 관심 매물 요청, limit = {}만큼 매물 정보 전달", providerId, limit);
	//
	// 	List<PropertyWithImageDTO> result = propertyService.getFavoritePropertiesForMain(providerId, limit);
	// 	log.info("회원 {}의 관심 매물 {}건 조회 완료", providerId, result.size());
	//
	// 	return ResponseEntity.ok(result);
	// }

	// 위치 정보(읍, 명, 동) 기반 전체 매물 조회 - 매물 조회 페이지
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

}