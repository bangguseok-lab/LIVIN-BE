package org.livin.property.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavChecklistVO {
	private Long checklistId;
	private String title;
	private Long userId;
}
