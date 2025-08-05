package org.livin.checklist.dto;

import java.time.LocalDateTime;

import org.livin.checklist.entity.ChecklistVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistCreateRequestDTO {
	private String title;
	private String description;
	private String type;

	public ChecklistVO toVo(Long userId) {
		LocalDateTime now = LocalDateTime.now();
		return ChecklistVO.builder()
			.userId(userId)
			.title(this.title)
			.description(this.description)
			.type(this.type)
			.createdAt(now)
			.updatedAt(now)
			.build();
	}
}
