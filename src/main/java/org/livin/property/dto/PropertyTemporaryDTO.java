package org.livin.property.dto;

import org.livin.property.entity.BuildingVO;
import org.livin.risk.dto.RiskTemporaryDTO;
import org.livin.risk.entity.RiskAnalysisVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyTemporaryDTO {
	private BuildingVO buildingVO;
	private RiskAnalysisVO riskAnalysisVO;
	private RiskTemporaryDTO riskTemporaryDTO;
}
