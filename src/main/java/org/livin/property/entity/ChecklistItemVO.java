package org.livin.property.entity;

import lombok.Data;

// ChecklistItem 테이블의 구조와 일치하는 VO (Value Object)
@Data
public class ChecklistItemVO {
	private Long checklistItemId;
	private Long checklistId;
	private String keyword;
	private Boolean isActive;
	private Boolean isChecked;
	private String type;
}
