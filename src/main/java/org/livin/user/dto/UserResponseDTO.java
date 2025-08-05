package org.livin.user.dto;

import java.time.LocalDate;

import org.livin.user.entity.UserRole;
import org.livin.user.entity.UserVO;

import lombok.Getter;

@Getter
public class UserResponseDTO {
	private String name;
	private String nickname;
	private String phone;
	private LocalDate birthDate;
	private UserRole role;
	private int profileImage;

	public UserResponseDTO(UserVO user) {
		this.name = user.getName();
		this.nickname = user.getNickname();
		this.phone = user.getPhone();
		this.birthDate = user.getBirthDate();
		this.role = user.getRole();
		this.profileImage = user.getProfileImage();

	}

}