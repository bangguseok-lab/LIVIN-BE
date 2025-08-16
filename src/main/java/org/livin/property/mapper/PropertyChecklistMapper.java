package org.livin.property.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.livin.property.dto.ChecklistItemDTO;
import org.livin.property.dto.ChecklistTitleDTO;
import org.livin.property.entity.ChecklistItemVO;
import org.livin.property.entity.ChecklistVO;

@Mapper
public interface PropertyChecklistMapper {

	// user_id로 체크리스트 제목 목록 조회 (최신순)
	List<ChecklistTitleDTO> selectChecklistTitlesByUserId(@Param("userId") Long userId);

	// ====================================================================================

	// === 기존 메서드들 ===
	boolean existsByPropertyIdAndChecklistId(@Param("propertyId") Long propertyId, @Param("checklistId") Long checklistId);

	void insertPropertyChecklist(@Param("propertyId") Long propertyId, @Param("checklistId") Long checklistId);

	// === ✅ 복제 기능에 필요한 메서드들 추가 ===

	// 원본 Checklist 조회
	ChecklistVO findChecklistByIdAndUserId(@Param("checklistId") Long checklistId, @Param("userId") Long userId);

	// 원본 ChecklistItem 목록 조회
	List<ChecklistItemVO> findItemsByChecklistId(@Param("checklistId") Long checklistId);

	// 복제된 Checklist 삽입 및 생성된 ID 받아오기
	void insertAndGetId(ChecklistVO checklist);

	// 복제된 ChecklistItem 목록 한 번에 삽입 (Batch Insert)
	void batchInsertItems(List<ChecklistItemVO> items);

	// ====================================================================================

	List<ChecklistItemDTO> selectChecklistItemsForProperty(
		@Param("userId") Long userId,
		@Param("propertyId") Long propertyId,
		@Param("checklistId") Long checklistId
	);

	void updateChecklistItemIsChecked(
		@Param("userId") Long userId,
		@Param("checklistId") Long checklistId,
		@Param("checklistItemId") Long checklistItemId,
		@Param("isChecked") boolean isChecked
	);
}
