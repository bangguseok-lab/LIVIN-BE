package org.livin.checklist.controller;

import java.util.List;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;
import org.livin.checklist.service.ChecklistService;
import org.livin.global.response.SuccessResponse;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	// 체크리스트 조회
	@GetMapping("")
	public ResponseEntity<SuccessResponse<List<ChecklistDTO>>> getAllList(
		@RequestParam("providerId") String providerId) {
		try {
			Long userId = userService.getUserIdByProviderId(providerId);

			List<ChecklistDTO> allList = checklistService.getAllList(userId);

			return ResponseEntity.status(HttpStatus.OK)
				.body(new SuccessResponse<>(true, "체크리스트 목록을 성공적으로 조회했습니다.", allList));


		} catch (Exception e) {
			log.error("============> 체크리스트 전체 목록 조회 중 에러 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new SuccessResponse<>(false, "서버 오류", null));

		}
	}

	// 체크리스트 생성
	@PostMapping("")
	public ResponseEntity<SuccessResponse<ChecklistDTO>> createChecklist(@RequestParam("providerId") String providerId,
		@RequestBody ChecklistCreateRequestDTO createRequestDTO) {
		try {
			Long userId = userService.getUserIdByProviderId(providerId);
			ChecklistDTO checklist = checklistService.createChecklist(createRequestDTO, userId);

			return ResponseEntity.status(HttpStatus.CREATED)
				.body(new SuccessResponse<>(true, "체크리스트가 성공적으로 생성되었습니다.", checklist));
		} catch (Exception e) {
			log.error("============> 체크리스트 생성 중 에러 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new SuccessResponse<>(false, "서버 오류", null));
		}

		// Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// log.info("=== 컨트롤러 인증 정보 확인 ===");
		// log.info("SecurityContext: {}", SecurityContextHolder.getContext());
		// log.info("Authentication: {}", auth);
		// log.info("User Principal: {}", user);

		// if (auth != null) {
		// 	log.info("Principal type: {}", auth.getPrincipal().getClass().getName());
		// 	log.info("Authorities: {}", auth.getAuthorities());
		// }

		// log.info(SecurityContextHolder.getContext());
		// log.info("▶ auth: {}", auth);

		// String providerId = (String) auth.getPrincipal();
		// Long userId = userService.getUserIdByProviderId(user.getProviderId());
	}

}
