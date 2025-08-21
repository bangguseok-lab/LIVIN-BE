package org.livin.global.codef.dto.marketprice.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketInfoData {
	private String resFixedDate; // 기준일자 (예: "20250804")
	private String resType; // 주거 유형 (예: "아파트")
	private String resComplexName; // 단지명 (예: "중산하늘채더퍼스트")
	private String commAddrRoadName; // 도로명 주소 (예: "경상북도 경산시 경산로 280")
	private String commAddrLotNumber; // 지번 주소 (예: "경상북도 경산시 중산동 693")
	private String resDongCnt; // 동 개수 (예: "12")
	private String resCompositionCnt; // 총 세대수 (예: "1184")
	private String resApprovalDate; // 승인 일자 (예: "202103")
	private String resHeatingSystem; // 난방 방식 (예: "")
	private String resFacility; // 주변 시설 정보 (예: "정평(2호선),...")
	private String resRealty; // 부동산 정보 (예: "세방공인중개사사무소")
	private String resTelNo; // 전화번호 (예: "053-815-5600")
	private String resImageLink; // 이미지 링크 (예: "https://www.rtech.or.kr/...")
	private List<Object> resAreaPriceList; // 면적별 가격 리스트 (현재 빈 배열)
	private List<ResHoPriceDto> resHoPriceList; // 호별 가격 리스트
}
