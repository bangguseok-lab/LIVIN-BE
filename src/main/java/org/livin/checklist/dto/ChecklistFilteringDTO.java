package org.livin.checklist.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistFilteringDTO {

	private String nickname;
	private String title;

	// 지역 필터링
	private String sido;
	private String sigungu;
	private String eupmyendong;

	// 전세 보증금, 월세 보증금 및 월세
	private Long jeonseDepositMin;
	private Long jeonseDepositMax;
	private Long monthlyDepositMin;
	private Long monthlyDepositMax;
	private Integer monthlyMin;
	private Integer monthlyMax;

	// 체크리스트 정보 (어떤 체크리스트에서 매물 필터링을 적용할 것인지)
	private Long checklistId;

	// 매물 리스트 어디까지 출력했는지 값 저장 (출력이 모두 되었을 경우, 로직이 종료되고 해당 필드에 값 저장이 안됨.)
	private Long lastId;

	// 로그인 인증 정보를 통해 얻은 userId 정보를 저장하는 필드.
	private Long userId;

	// 관심 매물 필터링 여부
	private Boolean isFavorite;

	// 매물 리스트 출력시, 등록일순으로 나열하기 위해
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

}
