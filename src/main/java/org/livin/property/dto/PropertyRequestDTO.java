package org.livin.property.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.livin.property.entity.PropertyVO;
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
public class PropertyRequestDTO {
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
	private String sido;
	private String sigungu;
	private String eupmyendong;
	private String propertyNum;
	private AbleStatus pet;
	private AbleStatus loan;
	private List<Long> optionIdList;
	private List<PropertyImgRequestDTO> imgRepresentList;
	private List<ManagementDTO> managementDTOList;

	public static PropertyVO toPropertyVO(PropertyRequestDTO propertyRequestDTO, Long buildingId, Long userId) {
		return PropertyVO.builder()
			.name(propertyRequestDTO.getName())
			.detailAddress(propertyRequestDTO.getDetailAddress())
			.jeonseDeposit(propertyRequestDTO.getJeonseDeposit())
			.monthlyDeposit(propertyRequestDTO.getMonthlyDeposit())
			.monthlyRent(propertyRequestDTO.getMonthlyRent())
			.propertyType(propertyRequestDTO.getPropertyType())
			.transactionType(propertyRequestDTO.getTransactionType())
			.supplyAreaM2(propertyRequestDTO.getSupplyAreaM2())
			.exclusiveAreaM2(propertyRequestDTO.getExclusiveAreaM2())
			.floor(propertyRequestDTO.getFloor())
			.numRoom(propertyRequestDTO.getNumRoom())
			.numBathrooms(propertyRequestDTO.getNumBathrooms())
			.mainDirection(propertyRequestDTO.getMainDirection())
			.duplexStructure(propertyRequestDTO.getDuplexStructure())
			.moveInDate(propertyRequestDTO.getMoveInDate())
			.description(propertyRequestDTO.getDescription())
			.createdAt(LocalDateTime.now())
			.sido(propertyRequestDTO.getSido())
			.sigungu(propertyRequestDTO.getSigungu())
			.eupmyendong(propertyRequestDTO.getEupmyendong())
			.propertyNum(propertyRequestDTO.getPropertyNum())
			.pet(propertyRequestDTO.getPet())
			.loan(propertyRequestDTO.getLoan())
			.buildingId(buildingId)
			.userId(userId)
			.build();
	}
}
