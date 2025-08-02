package org.livin.checklist.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.livin.checklist.dto.ChecklistItemJoinDTO;
import org.livin.checklist.entity.ChecklistVO;
import org.mapstruct.Mapper;

@Mapper
public interface ChecklistMapper {
	// 체크리스트 목록 전체 조회
	List<ChecklistVO> getAllList(@Param("userId") Long userId, @Param("lastId") Long lastId, @Param("size") int size);

	// 체크리스트 목록 상세 조회
	List<ChecklistItemJoinDTO> getChecklistDetail(Long ChecklistId);

	// 체크리스트 생성
	Long create(ChecklistVO checklist);

	// 체크리스트 기본 아이템 생성
	void createChecklistDefaultItem(Long checklistId);

	// 체크리스트 이름, 설명 수정
	Long updateChecklist(@Param("title") String title, @Param("description") String description,
		@Param("checklistId") Long checklistId);

	// 체크리스트 삭제
	void deleteChecklist(Long checklistId);
}
