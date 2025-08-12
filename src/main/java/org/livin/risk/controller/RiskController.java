package org.livin.risk.controller;

import org.livin.global.response.SuccessResponse;
import org.livin.risk.dto.RiskAnalysisRequestDTO;
import org.livin.risk.service.RiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {
	private final RiskService riskService;

	@PostMapping("/risk-analysis")
	public ResponseEntity<SuccessResponse<Void>> analysisRisk(
		@RequestBody RiskAnalysisRequestDTO riskAnalysisRequestDTO
	) {
		riskService.createRiskTemporaryInfo(riskAnalysisRequestDTO);
		return ResponseEntity.ok(
			new SuccessResponse<>(true, "위험도 분석이 완료되었습니다.", null)
		);
	}

	@DeleteMapping("/risk-temporary/{commUniqueNo}")
	public ResponseEntity<SuccessResponse<Void>> deleteRiskTemporaryInfo(
		@PathVariable(name = "commUniqueNo") String commUniqueNo
	) {
		riskService.deleteRiskTemporaryInfo(commUniqueNo);
		return ResponseEntity.ok(
			new SuccessResponse<>(true, "고유번호가 일치하지 않아 진행과정을 취소합니다.", null)
		);
	}
}
