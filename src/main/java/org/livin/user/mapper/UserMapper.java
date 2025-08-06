package org.livin.user.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.livin.user.dto.UserUpdateDTO;
import org.livin.user.entity.UserVO;

public interface UserMapper {

	UserVO findByProviderAndProviderId(@Param("provider") String provider,
		@Param("providerId") String providerId);

	void insertUser(UserVO user);

	void deleteByProviderAndProviderId(@Param("providerId") String providerId);

	//  1) 메인페이지 회원 닉네임 출력
	Optional<UserVO> findNicknameByUserId(String providerId);

	//  2) 메인페이지 회원 아이디에 따른 관심 매물 리스트 출력
	Long findUserIdByProviderId(String providerId);

	UserVO findByProviderId(String userId);

	Optional<UserVO> findUserById(@Param("id") Long id);

	void updateUser(UserUpdateDTO dto);

	void updateUserRole(@Param("userId") Long userId, @Param("roleId") String role);

	void updateProfileImage(@Param("providerId") String providerId, @Param("imageUrl") String imageUrl);

	String findProfileImageByProviderId(@Param("providerId") String providerId);

	String findProviderIdByUserId(Long userId);
}