package org.livin.checklist.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestCustomItemsDTO {
	private List<CustomItemDTO> customItems;
}
