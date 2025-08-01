package org.livin.property.entity.property_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
	JEONSE("전세"),
	MONTHLY_RENT("월세");

	private final String label;
}
