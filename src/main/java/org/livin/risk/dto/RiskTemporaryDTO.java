package org.livin.risk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskTemporaryDTO {
	private Long maximum_bond_amount;  // 근저당권 채권 최고액
	@JsonProperty("owner")             // JSON 필드명 "owner"를 isOwner 필드에 매핑
	private boolean isOwner;         // 소유자 인지 확인
}
