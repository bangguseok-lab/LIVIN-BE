package org.livin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
    private Long propertyId;
    private String propertyName;
    private String address;
    private String propertyType;
    private Integer monthlyRent;
    private Integer deposit;
    private Double area;
    private Integer floor;
    private Integer totalFloors;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String description;
    private Boolean hasElevator;
    private Boolean hasParking;
    private LocalDateTime availableDate;
    private LocalDateTime createdAt; // 생성일 추가

    @JsonIgnore
    private Double distance; // 현재 위치로부터의 거리 (km)
}