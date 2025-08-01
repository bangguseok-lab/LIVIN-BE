package org.livin.checklist.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;
import org.livin.checklist.dto.ChecklistDetailDTO;
import org.livin.checklist.service.ChecklistService;
import org.livin.global.jwt.filter.CustomUserDetails;
import org.livin.global.response.SuccessResponse;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

	// 체크리스트 전체 목록 조회 => todo: 무한스크롤 기능 구현
	@GetMapping("")
	public ResponseEntity<SuccessResponse<List<ChecklistDTO>>> getAllList(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			log.info("🍀 체크리스트 전체 목록 조회 실행");
			log.info("================================> userDetails:{} ", userDetails);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth != null && auth.isAuthenticated()) {
				log.info("Principal type: {}", auth.getPrincipal().getClass().getName());

				if (auth.getPrincipal() instanceof CustomUserDetails) {
					CustomUserDetails principal = (CustomUserDetails)auth.getPrincipal();
					log.info("✅ provider: {}", principal.getProvider());
					log.info("✅ providerId: {}", principal.getProviderId());
					log.info("✅ role: {}", principal.getRole());

					Long userId = userService.getUserIdByProviderId(principal.getProviderId());

					List<ChecklistDTO> allList = checklistService.getAllList(userId);

					return ResponseEntity.status(HttpStatus.OK)
						.body(new SuccessResponse<>(true, "체크리스트 목록을 성공적으로 조회했습니다.", allList));

				} else {
					log.warn("⚠️ 예상과 다른 Principal 타입: {}", auth.getPrincipal());
				}
			}
		} catch (Exception e) {
			log.error("❌ 체크리스트 전체 목록 조회 중 에러 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new SuccessResponse<>(false, "서버 오류", null));
		}
			return null;
	}

	// 체크리스트 상세 조회
	@GetMapping("/{checklistId}")
	public ResponseEntity<SuccessResponse<ChecklistDetailDTO>> getChecklistDetail(@PathVariable Long checklistId) {
		try{
			log.info("🍀 체크리스트 전체 목록 조회 실행");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth != null && auth.isAuthenticated()) {
				log.info("Principal type: {}", auth.getPrincipal().getClass().getName());

				if (auth.getPrincipal() instanceof CustomUserDetails) {
					CustomUserDetails principal = (CustomUserDetails)auth.getPrincipal();
					log.info("✅ provider: {}", principal.getProvider());
					log.info("✅ providerId: {}", principal.getProviderId());
					log.info("✅ role: {}", principal.getRole());

					Long userId = userService.getUserIdByProviderId(principal.getProviderId());

					log.info("✅ 조회할 체크리스트 ID: {}", checklistId);
					ChecklistDetailDTO checklistDetailList = checklistService.getChecklistDetail(checklistId);

					return ResponseEntity.status(HttpStatus.OK)
						.body(new SuccessResponse<>(true, "체크리스트 목록을 성공적으로 조회했습니다.", checklistDetailList));

				} else {
					log.warn("⚠️ 예상과 다른 Principal 타입: {}", auth.getPrincipal());
				}
			}

		} catch (Exception e) {
			log.error("❌ 체크리스트 전체 목록 조회 중 에러 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new SuccessResponse<>(false, "서버 오류", null));
		}
		return null;
	}

	// 체크리스트 생성
	@PostMapping("")
	public ResponseEntity<SuccessResponse<ChecklistDetailDTO>> createChecklist(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody ChecklistCreateRequestDTO createRequestDTO) {
		try {
			log.info("🍀 체크리스트 생성 실행");
			log.info("================================> userDetails:{} ", userDetails);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth != null && auth.isAuthenticated()) {
				CustomUserDetails principal = (CustomUserDetails)auth.getPrincipal();
				log.info("✅ provider: {}", principal.getProvider());
				log.info("✅ providerId: {}", principal.getProviderId());
				log.info("✅ role: {}", principal.getRole());

				Long userId = userService.getUserIdByProviderId(principal.getProviderId());

				ChecklistDetailDTO checklist = checklistService.createChecklist(createRequestDTO, userId);
				return ResponseEntity.status(HttpStatus.OK)
					.body(new SuccessResponse<>(true, "체크리스트를 성공적으로 생성했습니다.", checklist));
			}
		} catch (Exception e) {
			log.error("❌ 체크리스트 생성 중 에러 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new SuccessResponse<>(false, "서버 오류", null));
		}
		return null;
	}

}
