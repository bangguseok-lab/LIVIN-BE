package org.livin.risk.service;

import org.livin.risk.dto.RiskAnalysisRequestDTO;

public interface RiskService {
	void createRiskTemporaryInfo(RiskAnalysisRequestDTO riskAnalysisRequestDTO);

	void deleteRiskTemporaryInfo(String commUniqueNo);

}
