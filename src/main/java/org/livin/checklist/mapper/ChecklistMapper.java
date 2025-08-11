package org.livin.checklist.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.livin.checklist.dto.ChecklistFilteringDTO;
import org.livin.checklist.dto.ChecklistItemJoinDTO;
import org.livin.checklist.dto.ChecklistItemSimpleDTO;
import org.livin.checklist.entity.ChecklistVO;
import org.apache.ibatis.annotations.Mapper;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;

@Mapper
public interface ChecklistMapper {
	// 체크리스트 목록 전체 조회
	List<ChecklistVO> getAllList(@Param("userId") Long userId, @Param("lastId") Long lastId, @Param("size") int size);

	// 체크리스트 목록 상세 조회
	List<ChecklistItemJoinDTO> getChecklistDetail(Long ChecklistId);

	// 체크리스트 타입별 아이템 조회
	List<ChecklistItemSimpleDTO> getItemListByType(@Param("checklistId") Long checklistId, @Param("type") String type);

	// 체크리스트 생성
	Long create(ChecklistVO checklist);

	// 체크리스트 기본 아이템 생성
	void createChecklistDefaultItem(Long checklistId);

	// INFRA 타입의 아이템 생성
	void createInfraItem(Long checklistId);
	// OPTION 타입의 아이템 생성
	void createOptionItem(Long checklistId);
	// CIRCUMSTANCE 타입의 아이템 생성
	void createCircumstanceItem(Long checklistId);

	// 나만의 체크리스트 아이템 항목 생성
	void createCustomItem(@Param("checklistId") Long checklistId, @Param("keyword") String keyword);

	// 체크리스트 이름, 설명 수정
	Long updateChecklist(@Param("title") String title, @Param("description") String description,
		@Param("checklistId") Long checklistId);

	// 체크리스트 아이템 활성 상태 수정
	void updateItem(@Param("checklistItemId") Long checklistItemId, @Param("isActive") Boolean isActive);

	// 체크리스트 삭제
	void deleteChecklist(Long checklistId);

	// 나만의 아이템 삭제
	void deleteCustomItem(@Param("checklistId") Long checklistId, @Param("checklistItemId") Long checklistItemId);

	LocalDateTime findChecklistCreatedAtByPropertyId(Long PropertyId);

	List<PropertyVO> selectChecklistPropertyListByRegion(ChecklistFilteringDTO checklistFilteringDTO);

	List<PropertyImageVO> selectChecklistThumbnailImageByPropertyId(Long propertyId);

	List<PropertyVO> selectPropertiesByChecklistId(ChecklistFilteringDTO ChecklistFilteringDTO);

}
