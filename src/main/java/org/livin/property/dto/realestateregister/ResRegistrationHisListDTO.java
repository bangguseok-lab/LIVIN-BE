package org.livin.property.dto.realestateregister;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResRegistrationHisListDTO {
	private String resType;
	private List<ResContentsListDTO> resContentsListDTO;
	private String resType1;
}
