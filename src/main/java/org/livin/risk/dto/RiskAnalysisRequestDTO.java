package org.livin.risk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiskAnalysisRequestDTO {
	private long jeonseDeposit;
	private String roadNo;
	private String buildingNo;
	private String buildingSubNo;
	private String zipCode; //우편번호
	private String dong;
	private String ho;
	@JsonProperty("isGeneral")
	private boolean isGeneral;
}
