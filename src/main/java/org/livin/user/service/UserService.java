package org.livin.user.service;

import lombok.RequiredArgsConstructor;
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.global.jwt.service.TokenService;
import org.livin.user.dto.UserNicknameDTO;
import org.livin.user.entity.UserVO;
import org.livin.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<UserVO> user = Optional.ofNullable(userMapper.findNicknameByUserId(providerId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return UserNicknameDTO.builder()
                .nickname(user.get().getNickname())
                .build();
    }
}
