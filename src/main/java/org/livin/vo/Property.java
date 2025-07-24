package org.livin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Property {
    private Long propertyId;
    private String name;
    private String detailAddress;
    private String description;
}
