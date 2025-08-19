package org.livin.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ChecklistItemUpdateRequestDTO {
	private Long checklistItemId;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private boolean isChecked;
}
