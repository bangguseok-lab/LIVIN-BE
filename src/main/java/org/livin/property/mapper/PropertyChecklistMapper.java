package org.livin.property.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.livin.property.dto.ChecklistItemDTO;
import org.livin.property.dto.ChecklistItemUpdateRequestDTO;
import org.livin.property.dto.ChecklistTitleDTO;
import org.livin.property.entity.ChecklistItemVO;
import org.livin.property.entity.ChecklistVO;

@Mapper
public interface PropertyChecklistMapper {

	// user_id로 체크리스트 제목 목록 조회 (최신순)
	List<ChecklistTitleDTO> selectChecklistTitlesByUserId(@Param("userId") Long userId);

	// ====================================================================================

	// 원본 Checklist 조회
	ChecklistVO findChecklistByIdAndUserId(@Param("checklistId") Long checklistId, @Param("userId") Long userId);

	// 원본 ChecklistItem 목록 조회
	List<ChecklistItemVO> findItemsByChecklistId(@Param("checklistId") Long checklistId);

	// 복제된 Checklist 삽입 및 생성된 ID 받아오기
	void insertAndGetId(ChecklistVO checklist);

	// 복제된 Checklist를 Property_Checklist 테이블에 추가
	void insertPropertyChecklist(@Param("propertyId") Long propertyId, @Param("checklistId") Long checklistId);

	// 복제된 ChecklistItem 목록 한 번에 삽입 (Batch Insert)
	void batchInsertItems(List<ChecklistItemVO> items);

	// ====================================================================================

	// Property_Checklist 테이블에서 이 매물(propertyId)과 사용자(userId)에게 연결된 체크리스트 ID를 찾는다.
	Long findChecklistIdByPropertyAndUser(@Param("propertyId") Long propertyId, @Param("userId") Long userId);

	// 연결된 checklistId를 찾았을 경우, 해당 ID를 이용해 모든 옵션을 조회하여 반환
	List<ChecklistItemDTO> findChecklistItemsByChecklistIdAndUser(@Param("checklistId") Long checklistId, @Param("userId") Long userId);

	// ====================================================================================

	// 업데이트 목록 전체를 매퍼 메서드에 한 번에 전달
	void batchUpdateItemIsChecked(
		@Param("userId") Long userId,
		@Param("checklistId") Long checklistId,
		@Param("updates") List<ChecklistItemUpdateRequestDTO> updates
	);
}
