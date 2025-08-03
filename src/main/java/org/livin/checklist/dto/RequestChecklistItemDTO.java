package org.livin.checklist.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestChecklistItemDTO {
	private List<ChecklistItemStatusDTO> items;
}
