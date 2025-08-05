package org.livin.property.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionPropertyVO {
	private Long optionPropertyId;   // 옵션_매물 ID
	private Long propertyId;         // 매물 ID
	private Long optionId;           // 옵션 ID
}
