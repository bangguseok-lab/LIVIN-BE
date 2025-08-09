package org.livin.property.dto.realestateregister.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResRegistrationHisListDTO {
	private String resType;
	private List<ResContentsListDTO> resContentsListDTO;
	private String resType1;
}
