package org.livin.user.controller;

import org.livin.global.jwt.filter.CustomUserDetails;
import org.livin.global.jwt.service.TokenService;
import org.livin.global.jwt.util.JwtUtil;
import org.livin.global.response.SuccessResponse;
import org.livin.user.dto.UserDepositDTO;
import org.livin.user.dto.UserNicknameDTO;
import org.livin.user.dto.UserProfileImageDTO;
import org.livin.user.dto.UserResponseDTO;
import org.livin.user.dto.UserRoleUpdateDTO;
import org.livin.user.dto.UserUpdateDTO;
import org.livin.user.entity.UserRole;
import org.livin.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final TokenService tokenService;

	private final JwtUtil jwtUtil;

	private final UserService userService;

	@ApiOperation(value = "로그아웃", notes = "refresh 토큰 삭제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String providerId = userDetails.getProviderId();
		tokenService.deleteRefreshToken(providerId);
		return ResponseEntity.ok("로그아웃 성공");
	}

	@ApiOperation(value = "리프레시", notes = "accessToken 재발급")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(@AuthenticationPrincipal CustomUserDetails userDetails) {
		String providerId = userDetails.getProviderId();

		String refreshToken = tokenService.getRefreshToken(providerId);

		if (refreshToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰 없음");
		}

		Claims claims;
		try {
			claims = jwtUtil.validateToken(refreshToken);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰");
		}

		String newAccessToken = jwtUtil.generateAccessToken(claims.get("provider").toString(),
			claims.get("providerId").toString(), UserRole.valueOf((String)claims.get("role")));

		return ResponseEntity.ok(newAccessToken);
	}

	@ApiOperation(value = "회원탈퇴", notes = "DB에서 유저 삭제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@DeleteMapping("/withdraw")
	public ResponseEntity<String> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {

		String providerId = userDetails.getProviderId();

		userService.deleteUser(providerId);
		return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
	}

	@ApiOperation(value = "닉네임 가져오기", notes = "accessToken 재발급")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@GetMapping("/nickname")
	public ResponseEntity<UserNicknameDTO> getUserNickname(
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		String providerId = customUserDetails.getProviderId();

		log.info("getUserNickname: " + providerId);

		UserNicknameDTO userNicknameDTO = userService.getUserNickname(providerId);

		return ResponseEntity.ok(userNicknameDTO);
	}

	@ApiOperation(value = "회원정보 조회", notes = "회원 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@GetMapping("")
	public ResponseEntity<SuccessResponse<UserResponseDTO>> getUserInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		UserResponseDTO userInfo = userService.getUserInfo(userId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "회원 정보 조회되었습니다.", userInfo));
	}

	@ApiOperation(value = "회원정보 수정", notes = "회원 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@PutMapping("")
	public ResponseEntity<SuccessResponse<UserUpdateDTO>> updateUserinfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UserUpdateDTO dto
	) {
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		dto.setUserId(userId);
		UserUpdateDTO updateDTO = userService.updateUserInfo(dto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "회원 정보 수정되었습니다.", updateDTO));
	}


	@ApiOperation(value = "유저 역할 변경", notes = "유저의 역할을 변경합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@PutMapping("/role")
	public ResponseEntity<SuccessResponse<UserRoleUpdateDTO>> updateUserRole(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UserRoleUpdateDTO dto
	) {
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		dto.setUserId(userId);
		UserRoleUpdateDTO roleUpdateDTO = userService.updateUserRole(dto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "전환 되었습니다.", roleUpdateDTO));
	}

	@ApiOperation(value = "프로필 이미지 변경", notes = "프로필 이미지를 변경합니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@PutMapping("/profile-image")
	public ResponseEntity<SuccessResponse<UserUpdateDTO>> updateProfileImage(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UserUpdateDTO dto
	) {
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		dto.setUserId(userId);
		UserUpdateDTO updateDTO = userService.updateProfileImage(dto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "프로필 이미지가 수정되었습니다.", updateDTO));
	}

	@ApiOperation(value = "프로필 이미지 가져오기", notes = "프로필 이미지를 가져옵니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 401, message = "리프레시 토큰 없음 또는 유효하지 않음")
	})
	@GetMapping("/profile-image")
	public ResponseEntity<SuccessResponse<UserProfileImageDTO>> getProfileImage(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		UserProfileImageDTO profileImage = userService.getProfileImage(userId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "프로필 이미지가 조회되었습니다.", profileImage));
	}

	// 보증금 조회
	@GetMapping("/deposit")
	public ResponseEntity<SuccessResponse<UserDepositDTO>> getDeposit(
		@AuthenticationPrincipal CustomUserDetails principal
	) {
		Long deposit = userService.getUserDeposit(principal.getProviderId()); // Long or null
		UserDepositDTO dto = UserDepositDTO.of(deposit);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "보증금이 조회되었습니다.", dto));
	}

	// 보증금 입력
	@PutMapping("/deposit")
	public ResponseEntity<SuccessResponse<UserDepositDTO>> upsertDeposit(
		@AuthenticationPrincipal CustomUserDetails principal,
		@RequestBody UserDepositDTO dto
	) {
		// dto에는 deposit만 들어옴
		UserDepositDTO result = userService.upsertDeposit(principal.getProviderId(), dto.getDeposit());

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "보증금이 저장(수정)되었습니다.", result));
	}

	// org.livin.user.controller.UserController

	@DeleteMapping("/deposit")
	public ResponseEntity<SuccessResponse<UserDepositDTO>> clearDeposit(
		@AuthenticationPrincipal CustomUserDetails principal) {

		String providerId = principal.getProviderId();

		UserDepositDTO result = userService.clearDeposit(providerId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new SuccessResponse<>(true, "보증금이 초기화되었습니다.", result));
	}

}
