package org.livin.property.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PropertyChecklistMapper {

	// user_id로 체크리스트 제목 목록 조회 (최신순)
	List<String> selectChecklistTitlesByUserId(@Param("userId") Long userId);

}
