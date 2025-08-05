package org.livin.user.service;

import org.livin.user.dto.UserNicknameDTO;
import org.livin.user.dto.UserResponseDTO;
import org.livin.user.dto.UserRoleUpdateDTO;
import org.livin.user.dto.UserUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
	UserResponseDTO getUserInfo(Long userId);

	void updateUserInfo(UserUpdateDTO dto);

	void changeUserRole(UserRoleUpdateDTO dto);

	void deleteUser(String providerId);

	UserNicknameDTO getUserNickname(String providerId);

	Long getUserIdByProviderId(String providerId);

	String uploadProfileImage(String providerId, MultipartFile imageFile);

	String getProfileImageUrl(String providerId);
}
