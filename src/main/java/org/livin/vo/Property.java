package org.livin.vo;

import lombok.Data;

@Data
public class Property {
    private Long propertyId;
    private String name;
    private String detailAddress;
    private String description;
    private String imageUrl;
}
