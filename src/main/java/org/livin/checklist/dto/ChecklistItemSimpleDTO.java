package org.livin.checklist.dto;

import lombok.Builder;
import lombok.Getter;

// 각 아이템에서 필요한 최소 필드만 포함
@Builder
@Getter
public class ChecklistItemSimpleDTO {
	private Long checklistItemId;
	private String keyword;
	private boolean isActive;
	private String type;
}
