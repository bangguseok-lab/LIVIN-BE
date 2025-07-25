package org.livin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.livin.dto.UserNicknameDTO;
import org.livin.vo.User;

@Mapper
public interface UserMapper {

//  1) 메인페이지 회원 닉네임 출력
    UserNicknameDTO findNicknameByUserId(String providerId);

//  2) 메인페이지 회원 아이디에 따른 관심 매물 리스트 출력
    Long findUserIdByProviderId(String providerId);

    User findByProviderId(String userId);

}