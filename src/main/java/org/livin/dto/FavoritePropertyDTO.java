package org.livin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePropertyDTO {
    private Long propertyId;
    private String propertyName;
    private String address;
    private String propertyType;
    private Integer monthlyRent;
    private Integer deposit;
    private Double area;
    private Integer floor;
    private String imageUrl;
    private LocalDateTime favoriteDate;
    private Double latitude;
    private Double longitude;
}