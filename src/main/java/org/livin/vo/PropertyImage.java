package org.livin.vo;

import lombok.Data;

@Data
public class PropertyImage {
    private Long propertyImageId;
    private String imageUrl;

    private Long propertyId;            // FK to Property
}
