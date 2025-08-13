package org.livin.risk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAnalysisVO {
	private Long riskAnalysisId;      // 위험도분석 아이디
	private Long propertyId;          // 매물 아이디
	private Boolean isSafe;           // 안심매물 여부
	private boolean floatingCharge;   // 근저당권
	private boolean checkLandlord;    // 임대인 확인
	private boolean injusticeBuilding; // 불법 건축물 여부
	private int jeonseRatio;          // 전세가율
	private long maximum_bond_amount;    //채권 최고액
}