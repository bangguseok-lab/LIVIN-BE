package org.livin.mapper;

import org.apache.ibatis.annotations.Param;
import org.livin.dto.AddressDTO;
import org.livin.property.entity.PropertyVO;

import java.util.List;

public interface PropertyMapper {
    List<PropertyVO> selectPropertyNearLocationByUserId(AddressDTO address);
}
