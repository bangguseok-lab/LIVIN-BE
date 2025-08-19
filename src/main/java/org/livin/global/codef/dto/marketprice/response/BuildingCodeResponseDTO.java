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
public class BuildingCodeResponseDTO {
	private BuildingCodeResultDTO result; // API 호출 결과 정보
	private List<ComplexDetailDto> data; // 아파트 및 오피스텔 단지 목록
}
