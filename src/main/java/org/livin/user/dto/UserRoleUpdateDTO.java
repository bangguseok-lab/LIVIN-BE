package org.livin.user.dto;

import org.livin.user.entity.UserRole;
import org.livin.user.entity.UserVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserRoleUpdateDTO {
	private Long userId;
	private UserRole role;

	public UserVO toVO() {
		return UserVO.builder()
			.userId(userId)
			.role(role)
			.build();
	}

	public static UserRoleUpdateDTO of(UserVO userVO) {
		return UserRoleUpdateDTO.builder()
			.userId(userVO.getUserId())
			.role(userVO.getRole())
			.build();
	}
}