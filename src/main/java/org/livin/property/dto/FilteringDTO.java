package org.livin.property.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilteringDTO {
    private String sido;
    private String sigungu;
    private String eupmyendong;

    private Long jeonseDepositMin;
    private Long jeonseDepositMax;
    private Long monthlyDepositMin;
    private Long monthlyDepositMax;
    private Integer monthlyMin;
    private Integer monthlyMax;

    private Long userId;

    private Long lastId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastCreatedAt;

    private String transactionType;
    private Boolean onlySecure; //안심매물 필터 추가 (true일 경우에만 is_safe = true)

    @Builder.Default
    private int limit = 20;

    // Setter에서 0 이하일 경우 자동 보정
    public void setLimit(int limit) {
        this.limit = (limit <= 0) ? 20 : limit;
    }

    private Long checklistId;
}
