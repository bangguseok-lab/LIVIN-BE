package org.livin.checklist.service;

import java.util.List;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDetailDTO;
import org.livin.checklist.dto.ChecklistItemSimpleDTO;
import org.livin.checklist.dto.ChecklistListResponseDTO;
import org.livin.checklist.dto.RequestChecklistItemDTO;
import org.livin.checklist.dto.RequestCustomItemsDTO;

public interface ChecklistService {
	// 체크리스트 전체 목록 조회
	ChecklistListResponseDTO getAllList(Long userId, Long lastId, int size);

	// 체크리스트 상세 조회
	ChecklistDetailDTO getChecklistDetail(Long checklistId);

	// 체크리스트 생성
	ChecklistDetailDTO createChecklist(ChecklistCreateRequestDTO checklistDto, Long userId);

	// 나만의 체크리스트 항목 생성
	List<ChecklistItemSimpleDTO> createCustomItem(Long checklistId, RequestCustomItemsDTO requestCustomItemsDTO);

	// 기본 항목 외 체크리스트 아이템 리스트 생성 후 생성된 타입별 아이템 리스트 조회
	List<ChecklistItemSimpleDTO> createOtherTypeItemList(Long checklistId, String type);

	// 체크리스트 이름, 설명 수정
	ChecklistDetailDTO updateChecklist(Long userId, Long checklistId, ChecklistCreateRequestDTO updateChecklistDTO);

	// 체크리스트 아이템 활성 상태 수정
	ChecklistDetailDTO updateItem(Long checklistId, RequestChecklistItemDTO requestChecklistItemDTO);

	// 체크리스트 삭제
	void deleteChecklist(Long checklistId);

	// 나만의 항목 삭제
	void deleteCustomItem(Long checklistId, Long checklistItemId);
}
