package org.livin.property.service;

import org.livin.property.entity.FavChecklistVO;
import org.livin.property.entity.FavChecklistItemVO;
import org.livin.property.mapper.FavChecklistMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavChecklistServiceImpl implements FavChecklistService { // ✅ 인터페이스 구현

	private final FavChecklistMapper favChecklistMapper; // 매퍼 주입

	// 생성자를 통한 의존성 주입
	public FavChecklistServiceImpl(FavChecklistMapper favChecklistMapper) {
		this.favChecklistMapper = favChecklistMapper;
	}

	@Override // 인터페이스의 메서드를 오버라이드합니다.
	public List<FavChecklistVO> getFavChecklistsByUserId(Long userId) {
		return favChecklistMapper.selectFavChecklistsByUserId(userId);
	}

	@Override // 인터페이스의 메서드를 오버라이드합니다.
	public List<FavChecklistItemVO> getFavChecklistItemsByChecklistId(Long checklistId) {
		return favChecklistMapper.selectFavChecklistItemsByChecklistId(checklistId);
	}
}