package org.livin.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.livin.user.entity.UserVO;
import org.livin.user.entity.User;

import java.util.Optional;


public interface UserMapper {

    User findByProviderAndProviderId(@Param("provider") String provider,
                                     @Param("providerId") String providerId);

    void insertUser(User user);

    void deleteByProviderAndProviderId(@Param("providerId") String providerId);


    //  1) 메인페이지 회원 닉네임 출력
    Optional<UserVO> findNicknameByUserId(String providerId);

    //  2) 메인페이지 회원 아이디에 따른 관심 매물 리스트 출력
    Long findUserIdByProviderId(String providerId);

    UserVO findByProviderId(String userId);
}
