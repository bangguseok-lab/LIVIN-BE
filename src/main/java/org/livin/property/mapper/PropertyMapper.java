package org.livin.property.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;

import java.util.List;

@Mapper
public interface PropertyMapper {
    List<PropertyVO> selectPropertyListByRegion(FilteringDTO address);

    List<PropertyVO> selectFavoritePropertiesWithFilter(FilteringDTO address);

    List<PropertyImageVO> selectThumbnailImageByPropertyId(Long propertyId);

    List<PropertyImageVO> selectImagesByPropertyId(Long propertyId);

}
