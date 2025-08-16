package org.livin.property.controller;

import java.util.List;
import java.util.Map;

import org.livin.global.codef.dto.realestateregister.request.OwnerInfoRequestDTO;
import org.livin.global.codef.dto.realestateregister.response.OwnerInfoResponseDTO;
import org.livin.global.jwt.filter.CustomUserDetails;
import org.livin.global.response.SuccessResponse;
import org.livin.property.dto.ChecklistCloneRequest;
import org.livin.property.dto.ChecklistItemDTO;
import org.livin.property.dto.ChecklistItemUpdateRequestDTO;
import org.livin.property.dto.ChecklistTitleDTO;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.OptionDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.dto.PropertyDetailsDTO;
import org.livin.property.dto.PropertyRequestDTO;
import org.livin.property.service.PropertyService;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class PropertyController {

	private final UserService userService;
	private final PropertyService propertyService;

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

	// 매물 개수 카운트
	@GetMapping("/properties/count")
	public ResponseEntity<Long> countProperties(FilteringDTO filter) {
		long total = propertyService.countProperties(filter); // 커서 무시 쿼리
		return ResponseEntity.ok(total);
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
	public ResponseEntity<SuccessResponse<String>> removeFavoriteProperty(
		@PathVariable("id") Long propertyId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("관심 매물 삭제 요청 - propertyId: {}, userDetails: {}", propertyId, userDetails);

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		propertyService.removeFavoriteProperty(propertyId, userId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "관심 매물을 성공적으로 삭제했습니다.", "{}"));
	}

	// 관심 매물 추가 API
	@PostMapping("/properties/{id}/favorite")
	public ResponseEntity<SuccessResponse<PropertyDTO>> addFavoriteProperty(
		@PathVariable("id") Long propertyId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("관심 매물 추가 요청 - propertyId: {}, userDetails: {}", propertyId, userDetails);

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		PropertyDTO added = propertyService.addFavoriteProperty(userId, propertyId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new SuccessResponse<>(true, "관심 매물을 성공적으로 추가했습니다.", added));
	}

	//등기부등본 열람 api
	@PostMapping("/properties/real-estate-registers")
	public ResponseEntity<SuccessResponse<OwnerInfoResponseDTO>> getRealEstateRegisters(
		@RequestBody OwnerInfoRequestDTO ownerInfoRequestDTO
	) {
		log.info("부동산 고유 번호 : {}", ownerInfoRequestDTO.getCommUniqueNo());

		OwnerInfoResponseDTO ownerInfoResponseDTO = propertyService.getRealEstateRegisters(ownerInfoRequestDTO);
		return ResponseEntity.ok(
			new SuccessResponse<>(true, "등기부등본 열람이 성공하였습니다.", ownerInfoResponseDTO)
		);
	}

	@PostMapping("/properties")
	public ResponseEntity<SuccessResponse<Void>> createProperty(
		@RequestPart("propertyRequest") PropertyRequestDTO propertyRequestDTO,
		@RequestPart("images") List<MultipartFile> imageFiles,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {

		propertyService.createProperty(propertyRequestDTO, imageFiles, customUserDetails.getProviderId());
		return ResponseEntity.ok(
			new SuccessResponse<>(true, "매물 등록이 완료되었습니다.", null)
		);
	}

	@GetMapping("/properties/options")
	public ResponseEntity<SuccessResponse<List<OptionDTO>>> getOptionList() {
		List<OptionDTO> optionDTOList = propertyService.getOptionList();
		return ResponseEntity.ok(
			new SuccessResponse<>(true, "옵션 조회가 완료되었습니다.", optionDTOList)
		);
	}

	// 매물 상세 페이지 체크리스트 목록 출력
	@GetMapping("/properties/checklist")
	public ResponseEntity<List<ChecklistTitleDTO>> getChecklistTitles(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		// 인증 정보 -> userId
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		// 사용자가 만든 체크리스트 제목 목록 조회
		List<ChecklistTitleDTO> list = propertyService.getChecklistTitlesByUserId(userId);

		return ResponseEntity.ok(list);
	}

	// 매물 상세 페이지 체크리스트 목록에서 선택한 체크리스트 (아직 없으면 → 체크리스트 '복제' 기능을 통해 새로 생성)
	@PostMapping("/properties/{propertyId}/checklists")
	public ResponseEntity<Map<String, Long>> cloneChecklistForProperty(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long propertyId,
		@RequestBody ChecklistCloneRequest request
	) {
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		Long newChecklistId = propertyService.cloneChecklistForProperty(userId, propertyId, request.getSourceChecklistId());

		// 성공 시, 새로 생성된 체크리스트의 ID를 반환
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("newChecklistId", newChecklistId));
	}

	// 매물 상세 페이지 체크리스트 목록에서 선택한 체크리스트 (이미 생성된 매물 체크리스트가 있으면 → 그 체크리스트 조회)
	@GetMapping("/properties/{propertyId}/checklist")
	public ResponseEntity<List<ChecklistItemDTO>> getPersonalizedChecklistForProperty(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long propertyId
	) {
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		// 새 서비스 메서드 호출
		List<ChecklistItemDTO> items = propertyService.getPersonalizedChecklistForProperty(userId, propertyId);

		// 프론트엔드는 이 응답이 비어있는지 여부로
		// 기존 체크리스트를 보여줄지, 새로 생성하는 화면을 보여줄지 결정할 수 있습니다.
		return ResponseEntity.ok(items);
	}

	// 매물 상세 페이지 체크리스트 아이템(옵션) 수정
	@PutMapping("/properties/checklist/{checklistId}/items")
	public ResponseEntity<Void> updateChecklistItems(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long checklistId,
		@RequestBody List<ChecklistItemUpdateRequestDTO> updates
	) {

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		propertyService.updateChecklistItems(userId, checklistId, updates);

		return ResponseEntity.ok().build();
	}
}