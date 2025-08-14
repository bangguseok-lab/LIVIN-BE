package org.livin.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.livin.property.entity.PropertyVO;

import java.util.List;

@Mapper
public interface RegisteredPropertyMapper {

	/**
	 * 특정 사용자가 등록한 모든 매물 리스트를 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 매물 VO 리스트
	 */
	List<PropertyVO> selectMyProperties(Long userId);

	/**
	 * 특정 사용자가 등록한 매물의 총 개수를 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 매물 개수
	 */
	long countMyProperties(Long userId);

	/**
	 * 매물 ID와 사용자 ID를 기반으로 특정 매물을 삭제합니다.
	 *
	 * @param propertyId 삭제할 매물의 ID
	 * @param userId     현재 로그인한 사용자의 ID (권한 확인용)
	 * @return 삭제된 행의 수
	 */
	int deleteProperty(@Param("propertyId") Long propertyId, @Param("userId") Long userId);
}
