package org.livin.checklist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistItemVO {
	private Long checklistItemId;
	private String keyword;
	private Boolean isActive;
	private String type;           // ROOM, BUILDING, OPTION 등
	private Long checklistId;
}
