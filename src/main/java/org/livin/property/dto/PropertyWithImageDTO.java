package org.livin.property.dto;

import java.util.List;

import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyWithImageDTO {
	private Long propertyId;
	private String name;
	private String detailAddress;
	private String description;
	private List<PropertyImageVO> imageUrls;

	public static PropertyWithImageDTO of(PropertyVO propertyVO, List<PropertyImageVO> propertyImageVO) {
		return (propertyVO == null || propertyImageVO == null) ? null : PropertyWithImageDTO.builder()
			.propertyId(propertyVO.getPropertyId())
			.name(propertyVO.getName())
			.detailAddress(propertyVO.getDetailAddress())
			.description(propertyVO.getDescription())
			.imageUrls(propertyImageVO)
			.build();
	}

	public PropertyVO toVO() {
		return PropertyVO.builder()
			.propertyId(propertyId)
			.name(name)
			.detailAddress(detailAddress)
			.description(description)
			.images(imageUrls)
			.build();
	}
}