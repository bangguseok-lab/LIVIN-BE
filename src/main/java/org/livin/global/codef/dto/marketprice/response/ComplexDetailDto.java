package org.livin.global.codef.dto.marketprice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplexDetailDto {
	private String resType;
	private String resComplexName;
	private String commComplexNo;
}
