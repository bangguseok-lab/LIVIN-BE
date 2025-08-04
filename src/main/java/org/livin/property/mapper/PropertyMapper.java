package org.livin.property.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.livin.property.dto.AddressDTO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;

import java.util.List;

@Mapper
public interface PropertyMapper {
    List<PropertyVO> selectPropertyListByRegion(AddressDTO address);

    List<PropertyVO> selectFavoritePropertiesWithFilter(AddressDTO address);

    List<PropertyImageVO> selectThumbnailImageByPropertyId(Long propertyId);

    List<PropertyImageVO> selectImagesByPropertyId(Long propertyId);

}
