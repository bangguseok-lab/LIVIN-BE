package org.livin.checklist.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

// 각 아이템에서 필요한 최소 필드만 포함
@Builder
@Data
public class ChecklistItemSimpleDTO {
	private Long checklistItemId;
	private String keyword;
	private Boolean isActive;
	private String type;
}
