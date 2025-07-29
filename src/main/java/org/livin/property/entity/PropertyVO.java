package org.livin.property.entity;

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
    private String description;
    private String imageUrl;
}
