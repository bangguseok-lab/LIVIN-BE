package org.livin.property.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementVO {
	private Long managementId;       // 관리비 ID
	private String managementType;   // 관리비 항목
	private String managementFee;    // 관리비 금액
	private Boolean excludeInclude;  // 포함 여부
	private Long propertyId;         // 매물 ID (외래키)
}
