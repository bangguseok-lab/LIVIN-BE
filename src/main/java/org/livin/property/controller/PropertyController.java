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
	public ResponseEntity<?> getFavoriteProperties(@ModelAttribute AddressDTO address
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
	public ResponseEntity<List<PropertyDTO>> getPropertiesByRegion(@ModelAttribute AddressDTO address) {
		log.info("address = {}로 매물 요청", address);
		List<PropertyDTO> result = propertyService.getPropertiesByRegion(address);

		log.info("매물 {}건 조회 완료", result.size());
		log.info("{}", result);

		return ResponseEntity.ok(result);
	}

}