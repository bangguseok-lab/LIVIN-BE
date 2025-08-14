package org.livin.user.controller;

import java.util.List;

import org.livin.global.jwt.filter.CustomUserDetails;
import org.livin.global.response.SuccessResponse;
import org.livin.property.dto.PropertyDTO;
import org.livin.user.service.RegisteredPropertyService;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
public class RegisteredPropertyController {

	private final UserService userService;
	private final RegisteredPropertyService registeredPropertyService;


	@GetMapping("/api/properties/landlord")
	public ResponseEntity<List<PropertyDTO>> getMyProperties(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		log.info("사용자 등록 매물 리스트 조회 요청 - userId: {}", userId);

		List<PropertyDTO> result = registeredPropertyService.getMyProperties(userId);
		log.info("사용자 등록 매물 {}건 조회 완료", result.size());

		return ResponseEntity.ok(result);
	}

	@GetMapping("/api/properties/landlord/count")
	public ResponseEntity<Long> countMyProperties(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		log.info("사용자 등록 매물 개수 조회 요청 - userId: {}", userId);

		long totalCount = registeredPropertyService.countMyProperties(userId);
		log.info("사용자 등록 매물 총 {}건", totalCount);

		return ResponseEntity.ok(totalCount);
	}

	@DeleteMapping("/api/properties/{id}")
	public ResponseEntity<SuccessResponse<Void>> deleteMyProperty(
		@PathVariable("id") Long propertyId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		log.info("사용자 등록 매물 삭제 요청 - propertyId: {}, userId: {}", propertyId, userId);

		registeredPropertyService.deleteMyProperty(propertyId, userId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT) // 204 No Content
			.body(new SuccessResponse<>(true, "매물을 성공적으로 삭제했습니다.", null));
	}
}