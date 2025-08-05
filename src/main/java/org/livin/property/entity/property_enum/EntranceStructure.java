package org.livin.property.entity.property_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntranceStructure {
	COMBINED("복도식+계단식"),
	STAIRS("계단식"),
	HALLWAY("복도식"),
	NO_INFORMATION("정보 없음");

	private final String label;
}
