package org.livin.checklist.controller;

import java.util.List;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDetailDTO;
import org.livin.checklist.dto.ChecklistItemSimpleDTO;
import org.livin.checklist.dto.ChecklistListResponseDTO;
import org.livin.checklist.dto.RequestChecklistItemDTO;
import org.livin.checklist.dto.RequestCustomItemsDTO;
import org.livin.checklist.service.ChecklistService;
import org.livin.global.jwt.filter.CustomUserDetails;
import org.livin.global.response.SuccessResponse;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {

	private final UserService userService;
	private final ChecklistService checklistService;

	// 체크리스트 전체 목록 조회
	@GetMapping("")
	public ResponseEntity<SuccessResponse<ChecklistListResponseDTO>> getAllList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(required = false) Long lastId,
		@RequestParam(defaultValue = "20") int size) {

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		log.info("🍀 체크리스트 전체 목록 조회 요청: userId={}, lastId={}, size={}", userId, lastId, size);
		ChecklistListResponseDTO allList = checklistService.getAllList(userId, lastId, size);

		return ResponseEntity.ok(
			new SuccessResponse<>(true, "체크리스트 목록을 성공적으로 조회했습니다.", allList)
		);
	}


	// 체크리스트 상세 조회
	@GetMapping("/{checklistId}")
	public ResponseEntity<SuccessResponse<ChecklistDetailDTO>> getChecklistDetail(@PathVariable Long checklistId) {

		log.info("🍀 체크리스트 상세 목록 조회 실행");

		// 체크리스트 ID로 해당 체크리스트의 상세 정보 조회
		ChecklistDetailDTO checklistDetailList = checklistService.getChecklistDetail(checklistId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "체크리스트 목록을 성공적으로 조회했습니다.", checklistDetailList));

	}


	// 체크리스트 생성
	@PostMapping("")
	public ResponseEntity<SuccessResponse<ChecklistDetailDTO>> createChecklist(
		@RequestBody ChecklistCreateRequestDTO createRequestDTO,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		log.info("🍀 체크리스트 생성 실행");
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		// 체크리스트 생성
		ChecklistDetailDTO checklist = checklistService.createChecklist(createRequestDTO, userId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "체크리스트를 성공적으로 생성했습니다.", checklist));
	}


	// 기본 항목 외에 체크리스트 아이템 항목 생성
	@PostMapping("/{checklistId}/{type}/items")
	public ResponseEntity<SuccessResponse<List<ChecklistItemSimpleDTO>>> createNewItemList(
		@PathVariable Long checklistId,
		@PathVariable String type) {

		log.info("🍀 기본 항목 외 체크리스트 아이템 항목 생성 실행");
		log.info("입력 체크리스트 id: {}", checklistId);
		log.info("입력 type: {}", type);

		// type	에 해당하는 아이템 리스트 생성
		List<ChecklistItemSimpleDTO> createdNewItemLists = checklistService.createOtherTypeItemList(checklistId, type);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new SuccessResponse<>(true, "기본 항목 외 아이템 생성 완료", createdNewItemLists));
	}


	// 체크리스트 이름, 설명 수정 실행
	@PutMapping("/{checklistId}")
	public ResponseEntity<SuccessResponse<ChecklistDetailDTO>> modifyChecklist(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long checklistId,
		@RequestBody ChecklistCreateRequestDTO updateChecklistDTO) {

		log.info("🍀 체크리스트 이름, 설명 수정 실행");
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		// 체크리스트 이름, 설명 수정 실행
		ChecklistDetailDTO updatedChecklist = checklistService.updateChecklist(userId, checklistId, updateChecklistDTO);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "체크리스트를 성공적으로 수정했습니다.", updatedChecklist));
	}


	// 체크리스트 아이템 수정 (is_active의 true, false 값 수정)
	@PutMapping("/{checklistId}/items")
	public ResponseEntity<SuccessResponse<ChecklistDetailDTO>> checklistNewItem(
		@PathVariable Long checklistId,
		@RequestBody RequestChecklistItemDTO requestChecklistItemDTO) {

		log.info("🍀 체크리스트 아이템 활성 상태 수정 실행");

		// 체크리스트 아이템 활성 상태 수정
		ChecklistDetailDTO checklistItemsResponseDTO = checklistService.updateItem(checklistId,
			requestChecklistItemDTO);
		log.info(checklistItemsResponseDTO);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "체크리스트에 새로운 항목을 추가했습니다.", checklistItemsResponseDTO));
	}


	// 체크리스트 삭제
	@DeleteMapping("/{checklistId}")
	public ResponseEntity<SuccessResponse<String>> deleteChecklist(
		@PathVariable Long checklistId
	) {
		log.info("🍀 체크리스트 삭제 실행");

		// 체크리스트 삭제 실행
		checklistService.deleteChecklist(checklistId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "체크리스트를 성공적으로 삭제했습니다.", "{}"));
	}


	// 나만의 아이템 항목 생성
	@PostMapping("/{checklistId}/custom/item")
	public ResponseEntity<SuccessResponse<List<ChecklistItemSimpleDTO>>> createCustomItem(
		@PathVariable Long checklistId,
		@RequestBody RequestCustomItemsDTO requestCustomItemsDTO
	) {
		log.info("🍀 나만의 아이템 항목 생성 실행");

		// 나만의 아이템 항목 생성
		List<ChecklistItemSimpleDTO> createCustomItem = checklistService.createCustomItem(checklistId, requestCustomItemsDTO);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new SuccessResponse<>(true, "나만의 아이템 항목 생성 완료", createCustomItem));
	}


	// todo: 나만의 아이템 삭제

	// todo: 특정 체크리스트가 적용된 매물 조회

}
