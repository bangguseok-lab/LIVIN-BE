package org.livin.global.codef.dto.realestateregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealEstateResultDTO {
	private String code;
	private String extraMessage;
	private String message;
	private String transactionId;
}
