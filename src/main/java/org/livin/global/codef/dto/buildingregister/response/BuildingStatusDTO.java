package org.livin.global.codef.dto.buildingregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BuildingStatusDTO {
	private String resType;
	private String resFloor;
	private String resStructure;
	private String resUseType;
	private String resArea;
}
