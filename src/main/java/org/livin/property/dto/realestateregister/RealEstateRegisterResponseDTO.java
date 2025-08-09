package org.livin.property.dto.realestateregister;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
