package org.livin.user.service;

import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.global.jwt.service.TokenService;
import org.livin.user.dto.UserNicknameDTO;
import org.livin.user.entity.UserVO;
import org.livin.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final TokenService tokenService;
	private final UserMapper userMapper;

	public void deleteUser(String providerId) {
		userMapper.deleteByProviderAndProviderId(providerId);
		tokenService.deleteRefreshToken(providerId);
	}

	//    1) 닉네임
	public UserNicknameDTO getUserNickname(String providerId) {
		UserVO user = userMapper.findNicknameByUserId(providerId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		return UserNicknameDTO.builder()
			.nickname(user.getNickname())
			.build();
	}

	// providerId를 통해서 UserId를 조회해서 가져오는 메서드
	public Long getUserIdByProviderId(String providerId) {
		Long userId = userMapper.findUserIdByProviderId(providerId);
		if (userId == null) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		return userId;
	}
}
