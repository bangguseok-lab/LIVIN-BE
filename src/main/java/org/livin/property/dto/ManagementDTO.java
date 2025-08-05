package org.livin.property.dto;

import org.livin.property.entity.ManagementVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementDTO {
	private String managementType;   // 관리비 항목
	private String managementFee;    // 관리비 금액

	public static ManagementDTO fromManagementVO(ManagementVO managementVO) {
		return ManagementDTO.builder()
			.managementType(managementVO.getManagementType())
			.managementFee(managementVO.getManagementFee())
			.build();
	}
}
