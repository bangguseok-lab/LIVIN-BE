package org.livin.property.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizedChecklistDTO {
	private Long checklistId;
	private String title;
	private List<ChecklistItemDTO> items;
}
