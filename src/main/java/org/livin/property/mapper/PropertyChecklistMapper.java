package org.livin.property.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.livin.property.dto.ChecklistItemDTO;
import org.livin.property.dto.ChecklistTitleDTO;

@Mapper
public interface PropertyChecklistMapper {

	// user_id로 체크리스트 제목 목록 조회 (최신순)
	List<ChecklistTitleDTO> selectChecklistTitlesByUserId(@Param("userId") Long userId);

	List<ChecklistItemDTO> selectChecklistItemsOwnedByUser(
		@Param("userId") Long userId,
		@Param("checklistId") Long checklistId
	);

	void updateChecklistItemIsChecked(
		@Param("userId") Long userId,
		@Param("checklistId") Long checklistId,
		@Param("checklistItemId") Long checklistItemId,
		@Param("isChecked") boolean isChecked
	);
}
