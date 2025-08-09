package org.livin.property.dto.realestateregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
