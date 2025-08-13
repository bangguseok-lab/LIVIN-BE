package org.livin.global.codef.dto.buildingregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BuildingRegisterCollgationResponseDTO {
	private BuildingRegisterResultDTO result;
	private BuildingRegisterCollgationDataDTO data;
}
