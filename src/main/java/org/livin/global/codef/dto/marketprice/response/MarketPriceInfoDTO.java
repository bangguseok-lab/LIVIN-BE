package org.livin.global.codef.dto.marketprice.response;

import org.livin.property.entity.property_enum.HeatingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MarketPriceInfoDTO {
	private String propertyType;
	private String roadAddress;
	private String compositionCnt;    //세대 수
	private HeatingType heatingType;
	private String facility;    //주변 환경
	private String realty;    //중개사 이름
	private String telNo;    //중개사 번호
	private String imgUrl;    //이미지 url
	private String area;    // 전용면적
	private String topPrice; // 매매 상한가
	private String lowestPrice; //매매 하한가
}
