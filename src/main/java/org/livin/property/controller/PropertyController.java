package org.livin.property.controller;

import java.util.List;

import org.livin.global.jwt.filter.CustomUserDetails;
import org.livin.property.dto.AddressDTO;
import org.livin.property.dto.PropertyNearLocationDTO;
import org.livin.property.dto.PropertyWithImageDTO;
import org.livin.property.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	private final PropertyService propertyService;

	// 관심 매물 조회
	@GetMapping("/properties/favorite")
	public ResponseEntity<?> getFavoriteProperties(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestParam(defaultValue = "3") int limit
	) {
		String providerId = customUserDetails.getProviderId();
		log.info("providerId = {}로 관심 매물 요청, limit = {}만큼 매물 정보 전달", providerId, limit);

		List<PropertyWithImageDTO> result = propertyService.getFavoritePropertiesForMain(providerId, limit);
		log.info("회원 {}의 관심 매물 {}건 조회 완료", providerId, result.size());

		return ResponseEntity.ok(result);
	}

	// 위치 정보 기반 전체 매물 조회
	@PostMapping("/properties")
	public ResponseEntity<List<PropertyNearLocationDTO>> getNearbyProperties(@RequestBody AddressDTO address) {
		List<PropertyNearLocationDTO> result = propertyService.getPropertiesNearLocation(address);

		return ResponseEntity.ok(result);
	}

}