package org.livin.property.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livin.property.entity.PropertyVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyNearLocationDTO {
    private Long propertyId;
    private String name;

    public static PropertyNearLocationDTO of(PropertyVO vo) {
        return PropertyNearLocationDTO.builder()
                .propertyId(vo.getPropertyId())
                .name(vo.getName())
                .build();
    }

//    public PropertyVO toVO() {
//        return PropertyVO.builder()
//                .propertyId(propertyId)
//                .name(name)
//                .build();
//    }
}
