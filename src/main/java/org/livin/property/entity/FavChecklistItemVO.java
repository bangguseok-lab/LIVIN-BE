package org.livin.property.entity;

import org.livin.checklist.entity.checklist_enum.ChecklistItemType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavChecklistItemVO {
	private Long checklistItemId;
	private Long checklistId;
	private ChecklistItemType type;
	private String keyword;
	private boolean active;
}
