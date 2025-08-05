package org.livin.checklist.dto;

import org.livin.checklist.entity.ChecklistItemVO;
import org.livin.checklist.entity.ChecklistVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomItemDTO {
	private String keyword;
	private Boolean isActive;
	private  String type;

	// VO -> DTO 변환 (of())
	public static CustomItemDTO of(ChecklistItemVO vo) {
		return vo == null ? null : CustomItemDTO.builder()
			.keyword(vo.getKeyword())
			.isActive(vo.getIsActive())
			.type(vo.getType())
			.build();
	}

	// DTO -> VO 변환 (toVo())
	public ChecklistItemVO toVo(Long checklistId) {
		return ChecklistItemVO.builder()
			.keyword(keyword)
			.isActive(isActive)
			.type(type)
			.checklistId(checklistId)
			.build();
	}
}
