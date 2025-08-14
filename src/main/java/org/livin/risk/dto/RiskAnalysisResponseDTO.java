package org.livin.risk.dto;

import org.livin.risk.entity.RiskAnalysisVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAnalysisResponseDTO {
	private Boolean isSafe;           // 안심매물 여부
	private String floatingCharge;   // 근저당권
	private boolean checkLandlord;    // 임대인 확인
	private boolean injusticeBuilding; // 불법 건축물 여부
	private int jeonseRatio;          // 전세가율

	public static RiskAnalysisResponseDTO fromRiskAnalysisVO(RiskAnalysisVO riskAnalysisVO, Long userDeposit) {
		Long userDepositValue = (userDeposit != null) ? userDeposit : 0L;
		Long maximumBondAmountValue =
			(riskAnalysisVO.getMaximumBondAmount() != null) ? riskAnalysisVO.getMaximumBondAmount() : 0L;
		String floatingCharge = getFloatingCharge(riskAnalysisVO, userDepositValue, maximumBondAmountValue);

		return RiskAnalysisResponseDTO.builder()
			.floatingCharge(floatingCharge)
			.jeonseRatio(riskAnalysisVO.getJeonseRatio())
			.injusticeBuilding(riskAnalysisVO.isInjusticeBuilding())
			.checkLandlord(riskAnalysisVO.isCheckLandlord())
			.build();
	}

	private static String getFloatingCharge(RiskAnalysisVO riskAnalysisVO, Long userDepositValue,
		Long maximumBondAmountValue) {
		Long salePriceValue = riskAnalysisVO.getSalePrice();
		String floatingCharge;
		if (salePriceValue == null || salePriceValue == 0L) {
			floatingCharge = "계산 불가";
		} else {
			double debtRatio = (double)(userDepositValue + maximumBondAmountValue) / salePriceValue;
			long debtPercentage = Math.round(debtRatio * 100);

			if (debtPercentage >= 70) {
				floatingCharge = "위험";
			} else {
				floatingCharge = "안전";
			}
		}
		return floatingCharge;
	}
}
