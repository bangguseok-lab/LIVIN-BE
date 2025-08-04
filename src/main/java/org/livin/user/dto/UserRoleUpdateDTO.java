package org.livin.user.dto;

import org.livin.user.entity.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserRoleUpdateDTO {
	private Long userId;
	private UserRole role;
}
