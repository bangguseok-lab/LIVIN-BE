package org.livin.property.controller;

import org.livin.property.entity.FavChecklistVO;
import org.livin.property.entity.FavChecklistItemVO;
import org.livin.property.service.FavChecklistService;
import org.springframework.http.HttpStatus; // ResponseEntity에 HttpStatus 사용을 위해 추가
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.livin.global.jwt.filter.CustomUserDetails; // ✅ 실제 프로젝트의 CustomUserDetails 경로
import org.livin.user.service.UserService; // ✅ UserService 임포트 (userId 변환을 위해 필요)
import lombok.RequiredArgsConstructor; // ✅ Lombok의 @RequiredArgsConstructor 어노테이션 사용을 위해 추가

@RestController // 이 클래스가 RESTful API 컨트롤러임을 나타냅니다.
@RequestMapping("/api/checklist-filters") // ✅ 이 컨트롤러의 기본 URL 경로 (v1 제거 및 - 제거)
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어 줍니다.
public class FavChecklistController {

	private final FavChecklistService favChecklistService; // FavChecklistService 인터페이스 주입
	private final UserService userService; // ✅ UserService 주입 (CustomUserDetails에서 userId를 얻기 위함)

	// @RequiredArgsConstructor 어노테이션이 있으므로, 이 생성자 코드는 명시적으로 작성할 필요가 없습니다.
	// public FavChecklistController(FavChecklistService favChecklistService, UserService userService) {
	//     this.favChecklistService = favChecklistService;
	//     this.userService = userService;
	// }

	/**
	 * GET /api/checklist-filters
	 * 현재 로그인한 사용자의 체크리스트 목록(이름)을 조회합니다.
	 * 필터 바에 표시될 체크리스트명 버튼들을 위한 API입니다.
	 * @param userDetails 현재 로그인한 사용자의 인증 정보 (CustomUserDetails 타입)
	 * @return FavChecklistVO 목록을 포함하는 HTTP 응답
	 */
	@GetMapping
	public ResponseEntity<List<FavChecklistVO>> getUserFavChecklists(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		// CustomUserDetails에서 providerId를 가져와 UserService를 통해 DB의 실제 userId를 얻습니다.
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		// 서비스 계층을 통해 사용자의 체크리스트 목록을 조회합니다.
		List<FavChecklistVO> checklists = favChecklistService.getFavChecklistsByUserId(userId);

		// HTTP 200 OK 상태 코드와 함께 조회된 목록을 반환합니다.
		return ResponseEntity.ok(checklists);
	}

	/**
	 * GET /api/checklistfilters/{checklistId}/items
	 * 특정 체크리스트 ID에 해당하는 즐겨찾기(관심 매물) 체크리스트 아이템 목록을 조회합니다.
	 * 체크리스트명 버튼 클릭 시 뜨는 모달에 표시될 아이템들을 위한 API입니다.
	 * @param checklistId URL 경로에서 추출한 체크리스트 ID
	 * @param userDetails 현재 로그인한 사용자의 인증 정보 (CustomUserDetails 타입)
	 * @return FavChecklistItemVO 목록을 포함하는 HTTP 응답
	 */
	@GetMapping("/{checklistId}/items")
	public ResponseEntity<List<FavChecklistItemVO>> getFavChecklistItems(
		@PathVariable Long checklistId, // URL 경로의 {checklistId} 값을 받습니다.
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		// 서비스 계층을 통해 특정 체크리스트의 아이템 목록을 조회합니다.
		List<FavChecklistItemVO> items = favChecklistService.getFavChecklistItemsByChecklistId(checklistId);

		// HTTP 200 OK 상태 코드와 함께 조회된 목록을 반환합니다.
		return ResponseEntity.ok(items);
	}
}