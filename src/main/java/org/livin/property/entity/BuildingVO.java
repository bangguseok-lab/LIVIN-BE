package org.livin.property.entity;
import org.livin.property.entity.property_enum.EntranceStructure;
import org.livin.property.entity.property_enum.HeatingFuel;
import org.livin.property.entity.property_enum.HeatingType;
import org.livin.property.entity.property_enum.ParkingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingVO {
	private Long buildingId;
	private int totalFloors;
	private int numParking;
	private ParkingStatus parking;
	private boolean elevator;
	private EntranceStructure entranceStructure;
	private HeatingType heatingType;
	private HeatingFuel heatingFuel;
	private int totalUnit;
	private int completionYear;
	private String postcode;
	private String roadAddress;
}
