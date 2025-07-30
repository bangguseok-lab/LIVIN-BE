package org.livin.checklist.mapper;

import java.util.List;

import org.livin.checklist.entity.ChecklistVO;
import org.mapstruct.Mapper;

@Mapper
public interface ChecklistMapper {
	// 체크리스트 목록 전체 조회
	List<ChecklistVO> getAllList(Long userId);

	// 체크리스트 목록 상세 조회

	// 체크리스트 생성
	void create(ChecklistVO checklist);
}
