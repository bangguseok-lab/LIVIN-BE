package org.livin.user.dto;

import org.livin.user.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserRoleUpdateDTO {
	private Long userId;
	private UserRole role;
}