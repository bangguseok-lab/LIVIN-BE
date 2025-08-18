package org.livin.property.dto;

import org.livin.user.entity.UserVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandlordDTO {
	private String name;
	private String phone;

	public static LandlordDTO fromUserVO(UserVO userVO) {
		return LandlordDTO.builder()
			.name(userVO.getName())
			.phone(userVO.getPhone())
			.build();
	}
}
