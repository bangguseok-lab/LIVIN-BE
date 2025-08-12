package org.livin.property.dto;

import lombok.Data;

@Data
public class ChecklistItemDTO {
	private Long checklistItemId; // PK
	private String keyword;       // 옵션 키워드
	private Boolean active;       // is_active
	private Boolean checked;      // is_checked
	private String type;          // ROOM/BUILDING/OPTION/INFRA/CIRCUMSTANCE/CUSTOM
	private Long checklistId;     // FK
}
