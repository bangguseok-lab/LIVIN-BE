package org.livin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditPropertyDTO {
	private Long propertyId;
	private String transactionType; // "전세" or "월세"
	private Long jeonseDeposit; // 전세금
	private Long monthlyDeposit; // 월세 보증금
	private Integer monthlyRent; // 월세
	private String description;
}