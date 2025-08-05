package org.livin.checklist.dto;

import java.time.LocalDateTime;

import javax.swing.event.ChangeEvent;

import org.livin.checklist.entity.ChecklistVO;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistDTO {
	private Long checklistId;
	private Long userId;
	private String title;
	private String description;
	private String type;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	// VO -> DTO 변환 (of())
	public static ChecklistDTO of(ChecklistVO vo) {
		return vo == null ? null : ChecklistDTO.builder()
			.checklistId(vo.getChecklistId())
			.userId(vo.getUserId())
			.title(vo.getTitle())
			.description(vo.getDescription())
			.type(vo.getType())
			.createdAt(vo.getCreatedAt())
			.updatedAt(vo.getUpdatedAt())
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
			.updatedAt(updatedAt)
			.build();
	}

}
