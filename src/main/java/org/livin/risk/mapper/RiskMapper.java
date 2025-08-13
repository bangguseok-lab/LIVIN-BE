package org.livin.risk.mapper;

import org.apache.ibatis.annotations.Param;
import org.livin.risk.entity.RiskAnalysisVO;

public interface RiskMapper {
	void createRiskAnalysis(RiskAnalysisVO riskAnalysisVO);

	RiskAnalysisVO getRiskAnalysis(@Param("propertyId") Long propertyId);
}
