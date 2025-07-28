package org.livin.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.livin.property.entity.PropertyVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyWithImageDTO {
    private Long propertyId;
    private String name;
    private String detailAddress;
    private String description;
    private String imageUrl;            // 대표 이미지 url 하나만 받을 것이므로 리스트 처리 X

    public static PropertyWithImageDTO of(PropertyVO vo) {
        return vo == null ? null : PropertyWithImageDTO.builder()
                .propertyId(vo.getPropertyId())
                .name(vo.getName())
                .detailAddress(vo.getDetailAddress())
                .description(vo.getDescription())
                .imageUrl(vo.getImageUrl())
                .build();
    }

    public PropertyVO toVO() {
        return PropertyVO.builder()
                .propertyId(propertyId)
                .name(name)
                .detailAddress(detailAddress)
                .description(description)
                .imageUrl(imageUrl)
                .build();
    }
}