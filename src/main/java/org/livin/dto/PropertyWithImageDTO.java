package org.livin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyWithImageDTO {
    private Long propertyId;
    private String name;
    private String detailAddress;
    private String description;
    private String imageUrl;            // 대표 이미지 url 하나만 받을 것이므로 리스트 처리 X
}