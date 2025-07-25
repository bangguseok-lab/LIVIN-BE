package org.livin.user.service;


import org.livin.user.dto.UserResponseDto;
import org.livin.user.dto.UserUpdateRequestDto;
import org.springframework.stereotype.Service;

@Service

public interface UserService {
    UserResponseDto getUserInfo(Long userId);
    void updateUserInfo(Long userId, UserUpdateRequestDto dto);
    void changeUserRole(Long userId, String newRole);
}
