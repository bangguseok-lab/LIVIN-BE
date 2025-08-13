package org.livin.property.dto;

import lombok.Data;

@Data
public class ChecklistItemUpdateRequestDTO {
	private Long checklistItemId;
	private boolean isChecked;
}
