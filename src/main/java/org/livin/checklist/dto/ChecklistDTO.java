package org.livin.checklist.dto;

import java.time.LocalDateTime;

import javax.swing.event.ChangeEvent;

import org.livin.checklist.entity.ChecklistVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistDTO {
	private Long checklistId;
	private Long userId;
	private String title;
	private String description;
	private String type;
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;

	// VO -> DTO 변환 (of())
	public static ChecklistDTO of(ChecklistVO vo) {
		return vo == null ? null : ChecklistDTO.builder()
			.checklistId(vo.getChecklistId())
			.userId(vo.getUserId())
			.title(vo.getTitle())
			.description(vo.getDescription())
			.type(vo.getType())
			.createdAt(vo.getCreatedAt())
			.updateAt(vo.getUpdateAt())
			.build();
	}

	// DTO -> VO 변환 (toVo())
	public ChecklistVO toVo() {
		return ChecklistVO.builder()
			.checklistId(checklistId)
			.userId(userId)
			.title(title)
			.description(description)
			.type(type)
			.createdAt(createdAt)
			.updateAt(updateAt)
			.build();
	}

}
