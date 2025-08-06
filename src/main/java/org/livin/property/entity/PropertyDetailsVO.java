package org.livin.property.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.livin.property.entity.property_enum.AbleStatus;
import org.livin.property.entity.property_enum.TransactionType;
import org.livin.risk.entity.RiskAnalysisVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDetailsVO {
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
	private FavoritePropertyVO favoritePropertyVO;
	private BuildingVO buildingVO;
	private List<ManagementVO> managementVOList;
	private List<PropertyImageVO> propertyImageVOList;
	private RiskAnalysisVO riskAnalysisVO;
	private List<OptionVO> optionVOList;
}
