package org.livin.property.dto;

import lombok.Data;

@Data
public class ChecklistItemDTO {
	private Long checklistItemId; // PK
	private String keyword;       // 옵션 키워드
	private Boolean isActive;       // is_active
	private Boolean isChecked;      // is_checked
	private String type;          // ROOM/BUILDING/OPTION/INFRA/CIRCUMSTANCE/CUSTOM
	private Long checklistId;     // FK
}
