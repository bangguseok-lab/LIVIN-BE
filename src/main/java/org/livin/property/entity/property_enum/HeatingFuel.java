package org.livin.property.entity.property_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeatingFuel {
	LPG("LPG"),
	CITY_GAS("도시가스");

	private final String label;
}
