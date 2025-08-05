package org.livin.property.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.livin.property.entity.ManagementVO;
import org.livin.property.entity.OptionVO;
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.entity.property_enum.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDetailsDTO {
	private Long propertyId;
	private boolean isFavorite;
	private boolean isSafe;
	private String name;
	private String transactionType;
	private String detailAddress;
	private String price;
	private BigDecimal supplyAreaM2;
	private BigDecimal exclusiveAreaM2;
	private int floor;
	private int room;
	private String direction;
	private boolean duplexStructure;
	private String moveInDate;
	private String description;
	private String pet;
	private String loan;
	private BuildingDTO building;
	private List<PropertyImageDTO> imgUrls;
	private List<ManagementDTO> management;
	private List<String> options;

	public static PropertyDetailsDTO fromPropertyDetailsVO(PropertyDetailsVO propertyDetailsVO) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return PropertyDetailsDTO.builder()
				.propertyId(propertyDetailsVO.getPropertyId())
				.isFavorite(propertyDetailsVO.getFavoritePropertyVO() == null)
				.isSafe(propertyDetailsVO.getRiskAnalysisVO().getIsSafe())
				.name(propertyDetailsVO.getName())
				.transactionType(propertyDetailsVO.getTransactionType().getLabel())
				.detailAddress(propertyDetailsVO.getDetailAddress())
				.price(
					propertyDetailsVO.getTransactionType() == TransactionType.JEONSE ?
						String.valueOf(propertyDetailsVO.getJeonseDeposit()) :
						propertyDetailsVO.getMonthlyDeposit() + "/" +
							propertyDetailsVO.getMonthlyRent()
				)
				.supplyAreaM2(propertyDetailsVO.getSupplyAreaM2())
				.exclusiveAreaM2(propertyDetailsVO.getExclusiveAreaM2())
				.floor(propertyDetailsVO.getFloor())
				.direction(propertyDetailsVO.getMainDirection())
				.duplexStructure(propertyDetailsVO.getDuplexStructure())
				.moveInDate(propertyDetailsVO.getMoveInDate().format(formatter))
				.description(propertyDetailsVO.getDescription())
				.pet(propertyDetailsVO.getPet().getLabel())
				.loan(propertyDetailsVO.getLoan().getLabel())
				.building(BuildingDTO.fromBuildingVO(propertyDetailsVO.getBuildingVO()))
				.imgUrls(propertyDetailsVO.getPropertyImageVOList().stream()
					.map(PropertyImageDTO::fromPropertyImageVO)
					.collect(Collectors.toList()))
				.management(Optional.ofNullable(propertyDetailsVO.getManagementVOList())
					.orElseGet(Collections::emptyList)
					.stream()
					.filter(ManagementVO::getExcludeInclude)
					.map(ManagementDTO::fromManagementVO)
					.collect(Collectors.toList()))
				.options(propertyDetailsVO.getOptionVOList().stream()
					.map(OptionVO::getOptionType)
					.collect(Collectors.toList()))
				.build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}
