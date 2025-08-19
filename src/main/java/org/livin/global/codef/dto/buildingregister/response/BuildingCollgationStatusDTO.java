package org.livin.global.codef.dto.buildingregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BuildingCollgationStatusDTO {
	private String resType;
	private String resBuildingName;
	private String commAddrRoadName;
	private String resStructure;
	private String resRoof;
	private String resFloor;
	private String resUseType;
	private String resArea;
	private String resChangeDate;
	private String resChangeReason;
}
