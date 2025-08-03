package org.livin.checklist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 체크리스트 아이템 활성 상태에 대한 DTO
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistItemStatusDTO {
	private Long checklistItemId;
	private Boolean isActive;
}
