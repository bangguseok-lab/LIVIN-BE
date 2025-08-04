package org.livin.property.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilteringDTO {
    private String sido;
    private String sigungu;
    private String eupmyendong;

    private Long providerId;
    private Long userId;

    private int lastId;
    private int limit;

    private Long checklistId;
}
