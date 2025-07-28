package org.livin.user.service;

import lombok.RequiredArgsConstructor;
import org.livin.global.jwt.service.TokenService;
import org.livin.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TokenService tokenService;
    private final UserMapper userMapper;

    public void deleteUser(String providerId) {
        userMapper.deleteByProviderAndProviderId(providerId);
        tokenService.deleteRefreshToken(providerId);
    }
}
