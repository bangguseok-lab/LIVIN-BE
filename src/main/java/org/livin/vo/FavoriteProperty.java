package org.livin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteProperty {
    // 유저와 매물 사이 허브(?) 역할
    private Long favoritePropertyId;
    private LocalDateTime savedAt;
    private Long userId;                 // FK - User
    private Long propertyId;             // FK - Property
}
