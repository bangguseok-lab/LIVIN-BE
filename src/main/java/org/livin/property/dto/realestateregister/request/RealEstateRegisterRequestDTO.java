package org.livin.property.dto.realestateregister.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateRegisterRequestDTO {
	private String organization;
	private String phoneNo;
	private String password;
	private String inquiryType;
	private String uniqueNo;
	@JsonProperty("ePrepayNo")
	private String ePrepayNo;
	@JsonProperty("ePrepayPass")
	private String ePrepayPass;
	private String issueType;
}
