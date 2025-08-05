package org.livin.property.dto;

import org.livin.property.entity.PropertyImageVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyImageDTO {
	private boolean represent;
	private String imageUrl;

	public static PropertyImageDTO fromPropertyImageVO(PropertyImageVO propertyImageVO) {
		return PropertyImageDTO.builder()
			.represent(propertyImageVO.getRepresent())
			.imageUrl(propertyImageVO.getImageUrl())
			.build();
	}
}
