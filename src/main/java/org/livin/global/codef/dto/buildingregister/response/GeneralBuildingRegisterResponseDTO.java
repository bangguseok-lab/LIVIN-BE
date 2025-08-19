package org.livin.global.codef.dto.buildingregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneralBuildingRegisterResponseDTO {
	private BuildingRegisterResultDTO result;
	private GeneralBuildingRegisterDataDTO data;
}
