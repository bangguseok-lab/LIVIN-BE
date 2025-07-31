package org.livin.checklist.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 전체 체크리스트 정보 + 아이템을 type 기준으로 Map으로 묶음
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistDetailDTO {
	private Long checklistId;
	private String title;
	private String description;
	private String type;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// type별로 묶인 item 리스트 (type: ROOM, BUILDING, OPTION, INFRA, CIRCUMSTANCE, CUSTOM)
	private Map<String, List<ChecklistItemSimpleDTO>> items;

	public static ChecklistDetailDTO from(List<ChecklistItemJoinDTO> joinList) {
		if (joinList.isEmpty()) {
			throw new IllegalArgumentException("데이터가 없습니다.");
		}

		// 체크리스트 공통 정보는 joinList의 첫 번째 row에서 추출
		// 왜? 모든 row는 동일한 체크리스트 정보를 포함하기 때문
		ChecklistItemJoinDTO first = joinList.get(0);

		// item type 기준으로 group화
		Map<String , List<ChecklistItemSimpleDTO>> typeGrouped = joinList.stream()
			.filter(join -> join.getItemType() != null)	// null 제거
			.collect(Collectors.groupingBy(		// 아이템의 타입(예: ROOM, BUILDING)을 기준으로 묶음
				ChecklistItemJoinDTO::getItemType,
				Collectors.mapping(		// join DTO -> ItemSimpleDTO 로 변환
					join -> ChecklistItemSimpleDTO.builder()
					.checklistItemId(join.getChecklistItemId())
					.keyword(join.getKeyword())
					.isActive(join.isActive())
					.type(join.getItemType())
					.build(),
					Collectors.toList()
				)
			));

		return ChecklistDetailDTO.builder()
			.checklistId(first.getChecklistId())
			.title(first.getTitle())
			.description(first.getDescription())
			.type(first.getChecklistType())
			.createdAt(first.getCreatedAt())
			.updatedAt(first.getUpdatedAt())
			.items(typeGrouped)
			.build();
	}


}
