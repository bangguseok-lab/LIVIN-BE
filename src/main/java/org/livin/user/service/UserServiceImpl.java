package org.livin.user.service;

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
		log.info("update user info: {}", dto);
		userMapper.updateUser(dto);
	}

	@Override
	public void changeUserRole(UserRoleUpdateDTO dto) {
		log.info("service userid: {}", dto.getUserId());
		// TODO: 정범, try catch문 리팩토링 필요.
		try {
			userMapper.updateUserRole(dto.getUserId(), dto.getRole().name());
		} catch (Exception e) {
			log.error(e);
		}
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
	public String getProfileImageUrl(Long userId) {
		String providerId = userMapper.findProviderIdByUserId(userId);
		if (providerId == null) {
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
		return getProfileImageUrl(userId);
	}
}