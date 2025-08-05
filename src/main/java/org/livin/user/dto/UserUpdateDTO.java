package org.livin.user.dto;

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
}