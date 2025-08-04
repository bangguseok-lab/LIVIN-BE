package org.livin.property.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper // MyBatis 매퍼 인터페이스임을 나타냅니다.
public interface FavoritePageMapper {

	/**
	 * 특정 사용자의 관심 매물 목록에서 매물을 삭제합니다.
	 * @param propertyId 삭제할 매물의 ID
	 * @param userId 관심 매물을 삭제하는 사용자의 ID
	 */
	void deleteFavoriteProperty(@Param("propertyId") Long propertyId, @Param("userId") Long userId);

	/**
	 * 특정 매물이 특정 사용자의 관심 매물인지 확인합니다.
	 * (옵션: 삭제 전 검증이나 상세 페이지에서 하트 표시 여부를 위해 사용될 수 있습니다.)
	 * @param propertyId 확인할 매물의 ID
	 * @param userId 사용자 ID
	 * @return 관심 매물이면 1, 아니면 0 또는 null
	 */
	Integer isPropertyFavorite(@Param("propertyId") Long propertyId, @Param("userId") Long userId);
}