package org.livin.user.dto;

import org.livin.user.entity.UserVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserUpdateDTO {
	private Long userId;
	private String nickname;
	private String phone;
	private int profileImage;

	public UserVO toVO() {
		return UserVO.builder()
			.userId(userId)
			.nickname(nickname)
			.phone(phone)
			.profileImage(profileImage)
			.build();
	}

	public static UserUpdateDTO of(UserVO userVO) {
		return UserUpdateDTO.builder()
			.userId(userVO.getUserId())
			.nickname(userVO.getNickname())
			.phone(userVO.getPhone())
			.profileImage(userVO.getProfileImage())
			.build();
	}
}