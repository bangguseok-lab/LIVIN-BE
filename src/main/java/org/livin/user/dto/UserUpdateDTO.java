package org.livin.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserUpdateDTO {
	private Long userId;
	private String nickname;
	private String phone;
	private int profilImage;
}
