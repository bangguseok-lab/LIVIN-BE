package org.livin.property.service;

import org.livin.property.entity.FavChecklistVO;
import org.livin.property.entity.FavChecklistItemVO;
import java.util.List;

public interface FavChecklistService {

	/**
	 * 사용자의 즐겨찾기(관심 매물) 필터링을 위한 체크리스트 목록(이름)을 조회합니다.
	 * @param userId 현재 로그인한 사용자의 ID
	 * @return FavChecklistVO 목록
	 */
	List<FavChecklistVO> getFavChecklistsByUserId(Long userId);

	/**
	 * 특정 체크리스트 ID에 해당하는 즐겨찾기(관심 매물) 체크리스트 아이템 목록을 조회합니다.
	 * @param checklistId 조회할 체크리스트의 ID
	 * @return FavChecklistItemVO 목록
	 */
	List<FavChecklistItemVO> getFavChecklistItemsByChecklistId(Long checklistId);
}
