package org.livin.user.controller;

import lombok.RequiredArgsConstructor;
import org.livin.user.dto.ChangeRoleRequest;
import org.livin.user.dto.UserResponseDto;
import org.livin.user.dto.UserUpdateRequestDto;
import org.livin.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api")

public class UserController {

    private final UserService userService;

    // 정보 조회
    @GetMapping("/users")
    public ResponseEntity<UserResponseDto> getUserInfo(@RequestParam Long userId) {
        UserResponseDto userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    // 정보 수정
    @PutMapping("/users")
    public ResponseEntity<String> updateUserInfo(@RequestParam Long userId,
                                                 @RequestBody UserUpdateRequestDto dto) {
        userService.updateUserInfo(userId, dto);
        return ResponseEntity.ok("회원 정보 수정 완료");
    }

    // 임차인/임대인 전환
    @PostMapping("/change-role")
    public ResponseEntity<String> changeUserRole(@RequestParam Long userId,
                                                 @RequestBody ChangeRoleRequest request) {
        userService.changeUserRole(userId, request.getNewRole());
        return ResponseEntity.ok("전환 완료");
    }
}