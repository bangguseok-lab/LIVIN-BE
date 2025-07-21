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
    private String propertyType; // 원룸, 투룸, 오피스텔 등
    private Integer monthlyRent; // 월세
    private Integer deposit; // 보증금
    private Double area; // 면적
    private Integer floor; // 층수
    private String imageUrl; // 대표 이미지
    private LocalDateTime favoriteDate; // 찜한 날짜
    private Double latitude;
    private Double longitude;
}