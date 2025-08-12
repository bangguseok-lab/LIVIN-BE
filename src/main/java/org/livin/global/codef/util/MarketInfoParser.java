package org.livin.global.codef.util;

import org.livin.global.codef.dto.marketprice.response.MarketInfoData;
import org.livin.global.codef.dto.marketprice.response.MarketInfoResponseDTO;
import org.livin.global.codef.dto.marketprice.response.MarketPriceInfoDTO;
import org.livin.global.codef.dto.marketprice.response.ResHoPriceDto;
import org.livin.property.entity.property_enum.HeatingType;

public class MarketInfoParser {
	public static MarketPriceInfoDTO parseMarketPriceInfo(MarketInfoResponseDTO marketInfoResponseDTO) {
		// 응답 데이터가 유효하지 않은 경우, 기본값으로 DTO를 빌드하여 반환합니다.
		if (marketInfoResponseDTO == null || marketInfoResponseDTO.getData() == null) {
			return MarketPriceInfoDTO.builder().build();
		}

		// 응답 데이터의 'data' 필드에 접근
		MarketInfoData data = marketInfoResponseDTO.getData();

		// resHoPriceList에서 첫 번째 항목의 정보를 가져옵니다.
		// 만약 리스트가 비어있거나 여러 항목이 있을 경우, 비즈니스 로직에 따라 다른 처리가 필요할 수 있습니다.
		ResHoPriceDto firstHoPrice = null;
		if (data.getResHoPriceList() != null && !data.getResHoPriceList().isEmpty()) {
			firstHoPrice = data.getResHoPriceList().get(0);
		}

		// HeatingType 매핑 로직 (예시)
		// 실제 API 응답의 HeatingSystem 필드 값에 따라 HeatingType enum으로 변환하는 로직이 필요합니다.
		// 여기서는 간단히 빈 문자열이 아닌 경우 OTHER로 가정했습니다.
		HeatingType heatingType = parseHeatingType(data.getResHeatingSystem());

		// BuildingInfoDTO를 빌드하여 반환합니다.
		return MarketPriceInfoDTO.builder()
			.propertyType(data.getResType()) // 아파트/오피스텔 등
			.roadAddress(data.getCommAddrRoadName()) // 도로명 주소
			.compositionCnt(data.getResCompositionCnt()) // 총 세대수
			.heatingType(heatingType) // 난방 방식
			.facility(data.getResFacility()) // 주변 환경
			.realty(data.getResRealty()) // 중개사 이름
			.telNo(data.getResTelNo()) // 중개사 번호
			.imgUrl(data.getResImageLink()) // 이미지 URL
			.area(firstHoPrice != null ? firstHoPrice.getResArea() : null) // 첫 번째 호별 정보에서 면적
			.topPrice(firstHoPrice != null ? firstHoPrice.getResTopPrice() : null) // 첫 번째 호별 정보에서 매매 상한가
			.lowestPrice(firstHoPrice != null ? firstHoPrice.getResLowestPrice() : null) // 첫 번째 호별 정보에서 매매 하한가
			.build();
	}

	private static HeatingType parseHeatingType(String heatingSystem) {
		if (heatingSystem == null || heatingSystem.trim().isEmpty()) {
			return null; // 또는 HeatingType.UNKNOWN 등 적절한 기본값
		}
		return switch (heatingSystem) {
			case "개별 난방" -> HeatingType.INDIVIDUAL_HEATING;
			case "중앙 난방" -> HeatingType.CENTRAL_HEATING;
			case "지역 난방" -> HeatingType.DISTRICT_HEATING;
			default -> null;
		};
	}
}
