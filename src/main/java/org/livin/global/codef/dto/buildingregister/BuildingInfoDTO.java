package org.livin.global.codef.dto.buildingregister;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BuildingInfoDTO {
	private String hasElevator;
	private boolean isViolating;
	private String totalFloors;
	private int totalParkingSpaces;
}
