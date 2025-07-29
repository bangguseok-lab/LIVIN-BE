package org.livin.property.entity.property_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeatingType {
	INDIVIDUAL_HEATING("개별 난방"),
	CENTRAL_HEATING("중앙 난방"),
	DISTRICT_HEATING("지역 난방");

	private final String label;
}