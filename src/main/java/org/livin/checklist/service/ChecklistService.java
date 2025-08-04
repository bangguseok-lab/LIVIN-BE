package org.livin.checklist.service;

import java.util.List;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;

public interface ChecklistService {
	// 체크리스트 전체 목록 조회
	List<ChecklistDTO> getAllList(Long userId);

	// 체크리스트 상세 조회
	// ChecklistDTO getDetailList(Long checklistId);

	// 체크리스트 생성
	ChecklistDTO createChecklist(ChecklistCreateRequestDTO checklistDto, Long userId);
}
