package org.livin.property.controller;

import java.util.List;

import org.livin.property.dto.AddressDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.service.PropertyService;
import org.springframework.http.ResponseEntity;
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

	private final PropertyService propertyService;

	// 관심 매물 조회
	@GetMapping("/properties/favorite")
	public ResponseEntity<?> getFavoriteProperties(
		@RequestParam String providerId,
		@RequestParam(defaultValue = "3") int limit
	) {
		log.info("providerId = {}로 관심 매물 요청, limit = {}만큼 매물 정보 전달", providerId, limit);

		List<PropertyDTO> result = propertyService.getFavoritePropertiesForMain(providerId, limit);
		log.info("회원 {}의 관심 매물 {}건 조회 완료", providerId, result.size());

		return ResponseEntity.ok(result);
	}

	// 위치 정보(읍, 명, 동) 기반 전체 매물 조회
	@GetMapping("/properties")
	public ResponseEntity<List<PropertyDTO>> getNearbyProperties(@ModelAttribute AddressDTO address) {
		List<PropertyDTO> result = propertyService.getPropertiesNearLocation(address);

		return ResponseEntity.ok(result);
	}

}