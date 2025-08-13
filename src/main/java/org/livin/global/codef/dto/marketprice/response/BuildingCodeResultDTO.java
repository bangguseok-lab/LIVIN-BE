package org.livin.global.codef.dto.marketprice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingCodeResultDTO {
	private String code;
	private String extraMessage;
	private String message;
	private String transactionId;
}
