package org.livin.checklist.controller;

import java.util.List;

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
			log.info("================> 🍀 체크리스트 전체 목록 조회 실행");
			// Authentication : Spring Security의 인증 정보를 담고 있는 인터페이스
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth.isAuthenticated()) {
				// auth.getPrincipal() : 인증된 사용자 정보 반환
				// auth 객체가 CustomUserDetails 클래스의 인스턴스인지 확인
				// CustomUserDetails : Spring Security 에서 사용자 인증 정보를 다루기 위해 UserDetails를 구현한 클래스를 커스텀한 클래스
				// 현재 인증된 사용자의 principal이 우리가 만든 CustomUserDetails 인지 확인
				if (auth.getPrincipal() instanceof CustomUserDetails) {
					CustomUserDetails principal = (CustomUserDetails)auth.getPrincipal();
					// log.info("✅ provider: {}", principal.getProvider());
					// log.info("✅ providerId: {}", principal.getProviderId());
					// log.info("✅ role: {}", principal.getRole());

					// providerId로 userId 가져오기
					Long userId = userService.getUserIdByProviderId(principal.getProviderId());

					// user의 전체 체크리스트 목록 가져오기
					List<ChecklistDTO> allList = checklistService.getAllList(userId);

					log.info("================> 🍀 체크리스트 전체 목록 조회 실행 완료");
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
		try {
			log.info("================> 🍀 체크리스트 상세 목록 조회 실행");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth != null && auth.isAuthenticated()) {
				if (auth.getPrincipal() instanceof CustomUserDetails) {
					log.info("✅ 조회할 체크리스트 ID: {}", checklistId);

					// 체크리스트 ID로 해당 체크리스트의 상세 정보 조회
					ChecklistDetailDTO checklistDetailList = checklistService.getChecklistDetail(checklistId);

					log.info("================> 🍀 체크리스트 {}번 상세 목록 조회 실행 완료", checklistId);
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
		@RequestBody ChecklistCreateRequestDTO createRequestDTO) {
		try {
			log.info("================> 🍀 체크리스트 생성 실행");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth != null && auth.isAuthenticated()) {
				CustomUserDetails principal = (CustomUserDetails)auth.getPrincipal();

				Long userId = userService.getUserIdByProviderId(principal.getProviderId());

				// 체크리스트 생성
				ChecklistDetailDTO checklist = checklistService.createChecklist(createRequestDTO, userId);

				log.info("================> 🍀 체크리스트 생성 완료");
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

	// todo: 체크리스트 이름, 설명 수정

	// todo: 체크리스트 아이템 수정

	// todo: 체크리스트 삭제

	// todo: 나만의 아이템 생성

	// todo: 나만의 아이템 삭제

	// todo: 특정 체크리스트가 적용된 매물 조회


}
