package org.livin.property.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;

public interface PropertyMapper {
	List<PropertyVO> selectPropertyListByRegion(FilteringDTO address);

	List<PropertyVO> selectFavoritePropertiesWithFilter(FilteringDTO address);

	List<PropertyImageVO> selectThumbnailImageByPropertyId(Long propertyId);

	List<PropertyImageVO> selectImagesByPropertyId(Long propertyId);

	LocalDateTime findCreatedAtByPropertyId(Long propertyId);

	Optional<PropertyDetailsVO> getPropertyDetailsById(@Param("propertyId") Long propertyId,
		@Param("userId") Long userId);
}
