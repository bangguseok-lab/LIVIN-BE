package org.livin.risk.service;

import org.livin.global.codef.service.CodefService;
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.risk.dto.RiskAnalysisRequestDTO;
import org.livin.risk.dto.RiskTemporaryDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiskServiceImpl implements RiskService {

	private final RedisTemplate<String, RiskTemporaryDTO> riskTemporaryRedisTemplate;
	private final CodefService codefService;

	@Override
	public void createRiskTemporaryInfo(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {

		requestBuildingRegister(riskAnalysisRequestDTO);
	}

	@Override
	public void deleteRiskTemporaryInfo(String commUniqueNo) {
		try {
			riskTemporaryRedisTemplate.delete(commUniqueNo);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private void requestBuildingRegister(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		codefService.requestBuildingRegister(riskAnalysisRequestDTO);
	}
}
