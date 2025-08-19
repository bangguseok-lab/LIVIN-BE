package org.livin.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // @Param 어노테이션 임포트
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.entity.PropertyVO;
import org.livin.user.dto.EditPropertyDTO;

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

	/**
	 * 매물의 가격 정보와 기타 정보를 수정합니다.
	 * @param editPropertyDTO 수정할 매물 정보가 담긴 DTO
	 * @return 업데이트된 행의 수
	 */
	int updatePropertyDetails(EditPropertyDTO editPropertyDTO);

	/**
	 * 매물 ID로 매물 소유자(사용자) ID를 조회합니다.
	 * @param propertyId 매물 ID
	 * @return 매물 소유자 ID
	 */
	Long getUserIdByPropertyId(Long propertyId);

	/**
	 * 매물 ID로 업데이트 한 매물 상세 정보를 조회합니다.
	 * @param propertyId 매물 ID
	 * @param userId 현재 사용자의 ID (찜 여부 확인용)
	 * @return 매물 상세 정보 VO
	 */
	PropertyDetailsVO selectPropertyDetails(@Param("propertyId") Long propertyId, @Param("userId") Long userId);
}