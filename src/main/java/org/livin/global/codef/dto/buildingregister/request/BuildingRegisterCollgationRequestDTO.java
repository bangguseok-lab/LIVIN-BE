package org.livin.global.codef.dto.buildingregister.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BuildingRegisterCollgationRequestDTO {
	private String organization;
	private String loginType;
	private String userId;
	private String userPassword;
	private String address;
	private String inquiryType;
	private String type;
	private String zipCode;
	private String originDataYN;
}