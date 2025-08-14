package org.livin.risk.service;

import org.livin.risk.dto.RiskAnalysisRequestDTO;
import org.livin.risk.dto.RiskAnalysisResponseDTO;
import org.livin.risk.entity.RiskAnalysisVO;

public interface RiskService {
	void createRiskTemporaryInfo(RiskAnalysisRequestDTO riskAnalysisRequestDTO);

	void deleteRiskTemporaryInfo(String commUniqueNo);

	void createRiskAnalysis(RiskAnalysisVO riskAnalysisVO, Long propertyId);

	RiskAnalysisResponseDTO getRiskAnalysis(Long propertyId, String providerId);
}
