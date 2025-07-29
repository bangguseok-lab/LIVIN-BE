package org.livin.property.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.livin.property.entity.property_enum.AbleStatus;
import org.livin.property.entity.property_enum.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyVO {
	private Long propertyId;
	private String name;
	private String detailAddress;
	private Long jeonseDeposit;
	private Long monthlyDeposit;
	private Integer monthlyRent;
	private String propertyType;
	private TransactionType transactionType; // enum
	private BigDecimal supplyAreaM2;
	private BigDecimal exclusiveAreaM2;
	private Integer floor;
	private Integer numRoom;
	private Integer numBathrooms;
	private String mainDirection;
	private Boolean duplexStructure;
	private LocalDate moveInDate;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String sido;
	private String sigungu;
	private String eupmyendong;
	private String propertyNum;
	private AbleStatus pet;
	private AbleStatus loan;
	private Long buildingId;

	private List<PropertyImageVO> images;
	// 안전 매물 여부, 관심 매물 여부를 단일로
}
