package org.livin.global.codef.dto.marketprice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResHoPriceDto {
	private String resDong; // 동 (예: "109")
	private String resHo; // 호 (예: "201")
	private String resArea; // 면적 (예: "84.65")
	private String resTopPrice; // 최고 가격 (예: "47300")
	private String resLowestPrice; // 최저 가격 (예: "43000")
	private String resTopPrice1; // 다른 최고 가격 필드 (예: "30000")
	private String resLowestPrice1; // 다른 최저 가격 필드 (예: "25000")
	private String resCompositionCnt; // 구성 세대수 (예: "544")
	private String resLowerAveragePrice; // 하위 평균 가격 (예: "43000")
	private String resTopAveragePrice; // 상위 평균 가격 (예: "52000")
	private String resLowerAveragePrice1; // 다른 하위 평균 가격 필드 (예: "25000")
	private String resTopAveragePrice1; // 다른 상위 평균 가격 필드 (예: "30000")
	private String resSuretyAmt; // 보증금 (예: "3000")
	private String resMonthlyRent; // 월세 (예: "120~130")
}
