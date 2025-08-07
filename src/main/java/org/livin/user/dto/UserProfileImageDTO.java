package org.livin.user.dto;

import org.livin.user.entity.UserVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserProfileImageDTO {
	private int profileImage;

	public static UserProfileImageDTO of(UserVO userVO) {
		return UserProfileImageDTO.builder()
			.profileImage(userVO.getProfileImage())
			.build();
	}
}
