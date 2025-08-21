package org.livin.property.dto;

import org.livin.property.entity.BuildingVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingDTO {
	private int totalFloors;
	private int numParking;
	private String parking;
	private boolean elevator;
	private String entranceStructure;
	private String heatingType;
	private String heatingFuel;
	private int totalUnit;
	private int completionYear;
	private String postcode;
	private String roadAddress;

	public static BuildingDTO fromBuildingVO(BuildingVO buildingVO) {
		return BuildingDTO.builder()
			.totalFloors(buildingVO.getTotalFloors())
			.numParking(buildingVO.getNumParking())
			.parking(buildingVO.getParking().getLabel())
			.elevator(buildingVO.isElevator())
			.entranceStructure(buildingVO.getEntranceStructure().getLabel())
			.heatingType(buildingVO.getHeatingType().getLabel())
			.heatingFuel(buildingVO.getHeatingFuel().getLabel())
			.totalUnit(buildingVO.getTotalUnit())
			.completionYear(buildingVO.getCompletionYear())
			.postcode(buildingVO.getPostcode())
			.roadAddress(buildingVO.getRoadAddress())
			.build();
	}
}
