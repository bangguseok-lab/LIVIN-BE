package org.livin.property.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyImageVO {
	private Long propertyImageId;	//매물 이미지 ID
	private Long propertyId;	// 매물 ID
	private String imageUrl;	// 매물 이미지 주소
	private Boolean represent;	// 대표 이미지
}

