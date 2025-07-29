package org.livin.property.entity.property_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParkingStatus {
	ABLE("가능"),
	UNABLE("불가능"),
	NEEDS_CHECK("확인 필요");

	private final String label;
}
