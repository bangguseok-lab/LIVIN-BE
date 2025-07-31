package org.livin.checklist.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistVO {
	private Long checklistId;
	private Long userId;
	private String title;
	private String description;
	private String type;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
