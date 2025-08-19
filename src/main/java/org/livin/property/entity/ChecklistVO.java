package org.livin.property.entity;

import java.time.LocalDateTime;

import lombok.Data;

// Checklist 테이블의 구조와 일치하는 VO (Value Object)
@Data
public class ChecklistVO {
	private Long checklistId;
	private Long userId;
	private String title;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String type;
}
