package org.livin.global.codef.dto.buildingregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BuildingRegisterResultDTO {
	private String code;
	private String extraMessage;
	private String message;
	private String transactionId;
}
