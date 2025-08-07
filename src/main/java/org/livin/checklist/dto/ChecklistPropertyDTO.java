package org.livin.checklist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.entity.property_enum.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistPropertyDTO {
		private Long propertyId;                   // 매물 ID
		private String name;                       // 매물 이름
		private String detailAddress;              // 상세 주소
		private String description;                // 설명

		private Long jeonseDeposit;                // 전세 보증금
		private Long monthlyDeposit;               // 월세 보증금
		private Integer monthlyRent;               // 월세
		private TransactionType transactionType;   // 거래 유형 (JEONSE / MONTHLY_RENT)
		private String propertyType;               // 매물 유형 (오피스텔, 빌라 등)

		private String sido;                       // 시/도
		private String sigungu;                    // 시/군/구
		private String eupmyendong;                // 읍면동

		private Integer floor;                     // 층수
		private String mainDirection;              // 방향
		private BigDecimal supplyAreaM2;           // 공급 면적
		private BigDecimal exclusiveAreaM2;        // 전용 면적
		private LocalDateTime createdAt;           // 등록일 (String 또는 LocalDateTime)

		private Integer totalFloors;				// 총 층수
		private String roadAddress;					// 도로명 주소

		private Boolean isSafe;                    // 안전 매물 여부
		private Boolean isFavorite;                // 관심 매물 여부

		private List<PropertyImageVO> imageUrls;   // 이미지 리스트
		private LocalDateTime savedAt;

		public static org.livin.property.dto.PropertyDTO of(PropertyVO propertyVO) {
			return (propertyVO == null) ? null : org.livin.property.dto.PropertyDTO.builder()
				.propertyId(propertyVO.getPropertyId())
				.name(propertyVO.getName())
				.detailAddress(propertyVO.getDetailAddress())
				.description(propertyVO.getDescription())

				.jeonseDeposit(propertyVO.getJeonseDeposit())
				.monthlyDeposit(propertyVO.getMonthlyDeposit())
				.monthlyRent(propertyVO.getMonthlyRent())
				.transactionType(propertyVO.getTransactionType())
				.propertyType(propertyVO.getPropertyType())

				.sido(propertyVO.getSido())
				.sigungu(propertyVO.getSigungu())
				.eupmyendong(propertyVO.getEupmyendong())

				.floor(propertyVO.getFloor())
				.mainDirection(propertyVO.getMainDirection())
				.supplyAreaM2(propertyVO.getSupplyAreaM2())
				.exclusiveAreaM2(propertyVO.getExclusiveAreaM2())
				.createdAt(propertyVO.getCreatedAt())

				.totalFloors(propertyVO.getTotalFloors())
				.roadAddress(propertyVO.getRoadAddress())

				.isSafe(propertyVO.getIsSafe())
				.isFavorite(propertyVO.getIsFavorite())
				.savedAt(propertyVO.getSavedAt())
				.imageUrls(propertyVO.getImages())
				.build();
		}

		public PropertyVO toVO() {
			return PropertyVO.builder()
				.propertyId(propertyId)
				.name(name)
				.detailAddress(detailAddress)
				.description(description)

				.jeonseDeposit(jeonseDeposit)
				.monthlyDeposit(monthlyDeposit)
				.monthlyRent(monthlyRent)
				.transactionType(transactionType)
				.propertyType(propertyType)

				.sido(sido)
				.sigungu(sigungu)
				.eupmyendong(eupmyendong)

				.floor(floor)
				.mainDirection(mainDirection)
				.supplyAreaM2(supplyAreaM2)
				.exclusiveAreaM2(exclusiveAreaM2)
				.createdAt(createdAt)

				.isSafe(isSafe)
				.isFavorite(isFavorite)

				.images(imageUrls)
				.build();
		}
}
