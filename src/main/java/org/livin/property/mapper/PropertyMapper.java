package org.livin.property.mapper;

import org.livin.property.dto.FilteringDTO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;

import java.time.LocalDateTime;
import java.util.List;

public interface PropertyMapper {
    List<PropertyVO> selectPropertyListByRegion(FilteringDTO address);

    List<PropertyVO> selectFavoritePropertiesWithFilter(FilteringDTO address);

    List<PropertyImageVO> selectThumbnailImageByPropertyId(Long propertyId);

    List<PropertyImageVO> selectImagesByPropertyId(Long propertyId);

    LocalDateTime findCreatedAtByPropertyId(Long propertyId);

    int deleteFavoriteProperty(Long propertyId, Long userId);
}
