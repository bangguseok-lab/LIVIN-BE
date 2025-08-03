package org.livin.property.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.livin.property.entity.FavChecklistVO; // FavChecklistVO 임포트
import org.livin.property.entity.FavChecklistItemVO; // FavChecklistItemVO 임포트
import java.util.List;

@Mapper
public interface FavChecklistMapper {
	// 사용자 ID를 받아서 FavChecklistVO 목록 (체크리스트 이름 목록)을 반환하는 메서드
	List<FavChecklistVO> selectFavChecklistsByUserId(Long userId);

	// 특정 체크리스트 ID를 받아서 FavChecklistItemVO 목록 (체크리스트 아이템 목록)을 반환하는 메서드
	List<FavChecklistItemVO> selectFavChecklistItemsByChecklistId(Long checklistId);
}
