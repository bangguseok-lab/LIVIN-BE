package org.livin.user.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.global.jwt.service.TokenService;
import org.livin.user.dto.UserNicknameDTO;
import org.livin.user.dto.UserResponseDTO;
import org.livin.user.dto.UserRoleUpdateDTO;
import org.livin.user.dto.UserUpdateDTO;
import org.livin.user.entity.UserVO;
import org.livin.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

	private final TokenService tokenService;
	public final UserMapper userMapper;

	@Override
	public UserResponseDTO getUserInfo(Long userId) {
		UserVO user = userMapper.findUserById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		return new UserResponseDTO(user);
	}

	@Override
	public void updateUserInfo(UserUpdateDTO dto) {
		userMapper.updateUser(dto);
	}

	@Override
	public void changeUserRole(UserRoleUpdateDTO dto) {
		userMapper.updateUserRole(dto.getUserId(), dto.getRole().name());
	}

	@Override
	public void deleteUser(String providerId) {
		userMapper.deleteByProviderAndProviderId(providerId);
		tokenService.deleteRefreshToken(providerId);
	}

	@Override
	public UserNicknameDTO getUserNickname(String providerId) {
		UserVO user = userMapper.findNicknameByUserId(providerId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		return new UserNicknameDTO(user.getNickname());
	}

	@Override
	public Long getUserIdByProviderId(String providerId) {
		Long userId = userMapper.findUserIdByProviderId(providerId);
		if (userId == null) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		return userId;
	}

	@Override
	public String uploadProfileImage(String providerId, MultipartFile imageFile) {
		String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
		String filePath = "profile-images/" + fileName;

		try {
			File uploadDir = new File("src/main/resources/static/profile-images");
			if (!uploadDir.exists())
				uploadDir.mkdirs();

			File dest = new File(uploadDir, fileName);
			imageFile.transferTo(dest);

			userMapper.updateProfileImage(providerId, filePath);

		} catch (IOException e) {
			throw new RuntimeException("프로필 이미지 저장 실패", e);
		}

		return "/static/" + filePath;
	}

	@Override
	public String getProfileImageUrl(String providerId) {
		String imageUrl = userMapper.findProfileImageByProviderId(providerId);
		if (imageUrl == null || imageUrl.isBlank()) {
			return "/static/profile-images/default.png";
		}
		return "/static/" + imageUrl;
	}
}