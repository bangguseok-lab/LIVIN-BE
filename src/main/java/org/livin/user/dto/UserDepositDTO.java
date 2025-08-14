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
public class UserDepositDTO {
	private Long deposit;

	public UserVO toVO(String providerId) {
		return UserVO.builder()
			.providerId(providerId)
			.deposit(this.deposit)
			.build();
	}

	public static UserDepositDTO of(Long deposit) {
		return UserDepositDTO.builder()
			.deposit(deposit)
			.build();
	}
}
