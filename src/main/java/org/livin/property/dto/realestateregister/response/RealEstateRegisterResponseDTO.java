package org.livin.property.dto.realestateregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateRegisterResponseDTO {
	private RealEstateResultDTO result;
	private RealEstateDataDTO data;
	private String eprepayNo;
	private String eprepayPass;
}
