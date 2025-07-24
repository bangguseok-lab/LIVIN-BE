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
    private Long jeonseDeposit;
    private Long monthlyDeposit;
    private Integer monthlyRent;
    private String propertyType;
    private String transactionType;         // enum('JEONSE', 'MONTHLY_RENT')
    private BigDecimal supplyAreaM2;
    private BigDecimal exclusiveAreaM2;
    private Integer floor;
    private Integer numRoom;
    private Integer numBathrooms;
    private String mainDirection;
    private Boolean duplexStructure;
    private LocalDate moveInDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String filteringDistrictName;
    private String propertyNum;
    private String pet;                     // ABLE, UNABLE, NEEDS_CHECK
    private String loan;                    // ABLE, UNABLE, NEEDS_CHECK
    private Long buildingId;                // FK
}
