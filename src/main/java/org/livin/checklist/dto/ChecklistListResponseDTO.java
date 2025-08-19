package org.livin.checklist.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChecklistListResponseDTO {
	private List<ChecklistDTO> checklists;
	private boolean isLast;
}
