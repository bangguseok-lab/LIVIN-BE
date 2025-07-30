package org.livin.checklist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 체크리스트 상세 정보와 아이템 정보를 조인한 결과를 한 줄 한 줄 그대로 담기 위한 DTO
// Checklist와 ChecklistItem 테이블의 필드를 전부 가지고 있음
// 체크리스트마다 연결된 아이템을 알기 위함
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChecklistItemJoinDTO {
	// Checklist
	private Long checklistId;
	private String title;
	private String description;
	private String checklistType;	// 두 테이블 모두 type 필드를 가지고 있어서 지정한 Alias로 필드명 지정

	// ChecklistItem
	private Long checklistItemId;
	private String keyword;
	private boolean isActive;
	private String itemType;	// 두 테이블 모두 type 필드를 가지고 있어서 지정한 Alias로 필드명 지정
}
