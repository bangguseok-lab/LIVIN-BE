package org.livin.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiskAddressResponseDTO {
	private String sido;
	private String sigungu;
	private String eupmyeondong;
	private String commUniqueNo;
}
