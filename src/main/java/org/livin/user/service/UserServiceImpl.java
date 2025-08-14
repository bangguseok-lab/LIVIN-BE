package org.livin.user.service;

import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.global.jwt.service.TokenService;
import org.livin.user.dto.UserNicknameDTO;
import org.livin.user.dto.UserProfileImageDTO;
import org.livin.user.dto.UserResponseDTO;
import org.livin.user.dto.UserRoleUpdateDTO;
import org.livin.user.dto.UserUpdateDTO;
import org.livin.user.entity.UserVO;
import org.livin.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

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
	public UserUpdateDTO updateUserInfo(UserUpdateDTO dto) {
		userMapper.updateUser(dto.toVO());
		return UserUpdateDTO.of(userMapper.findByUserId(dto.getUserId()));
	}

	@Override
	public UserRoleUpdateDTO updateUserRole(UserRoleUpdateDTO dto) {
		userMapper.updateUserRole(dto.toVO());
		return UserRoleUpdateDTO.of(userMapper.findByUserId(dto.getUserId()));
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
	public UserUpdateDTO updateProfileImage(UserUpdateDTO dto) {
		userMapper.updateProfileImage(dto.toVO());
		return UserUpdateDTO.of(userMapper.findByUserId(dto.getUserId()));
		// return updateProfileImage(dto.toVO());
	}

	@Override
	public UserProfileImageDTO getProfileImage(Long userId) {
		UserVO profileImage = userMapper.findUserById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		return UserProfileImageDTO.of(profileImage);
	}

	@Override
	public Long getUserDeposit(String providerId) {
		return userMapper.getUserDeposit(providerId);
	}
}