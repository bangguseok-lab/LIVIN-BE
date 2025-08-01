package org.livin.checklist.service;

import java.util.List;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;
import org.livin.checklist.dto.ChecklistDetailDTO;
import org.livin.checklist.dto.ChecklistListResponseDTO;

public interface ChecklistService {
	// 체크리스트 전체 목록 조회
	ChecklistListResponseDTO getAllList(Long userId, Long lastId, int size);

	// 체크리스트 상세 조회
	ChecklistDetailDTO getChecklistDetail(Long checklistId);

	// 체크리스트 생성
	ChecklistDetailDTO createChecklist(ChecklistCreateRequestDTO checklistDto, Long userId);

}
