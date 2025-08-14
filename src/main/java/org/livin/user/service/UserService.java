package org.livin.user.service;

import org.livin.user.dto.UserNicknameDTO;
import org.livin.user.dto.UserProfileImageDTO;
import org.livin.user.dto.UserResponseDTO;
import org.livin.user.dto.UserRoleUpdateDTO;
import org.livin.user.dto.UserUpdateDTO;

public interface UserService {

	UserResponseDTO getUserInfo(Long userId);

	UserUpdateDTO updateUserInfo(UserUpdateDTO dto);

	UserRoleUpdateDTO updateUserRole(UserRoleUpdateDTO dto);

	void deleteUser(String providerId);

	UserNicknameDTO getUserNickname(String providerId);

	Long getUserIdByProviderId(String providerId);

	UserUpdateDTO updateProfileImage(UserUpdateDTO dto);

	UserProfileImageDTO getProfileImage(Long userId);

	Long getUserDeposit(String providerId);
}
