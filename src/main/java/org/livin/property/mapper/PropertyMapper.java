package org.livin.property.mapper;

import org.livin.property.dto.AddressDTO;
import org.livin.property.entity.PropertyVO;

import java.util.List;

public interface PropertyMapper {
    List<PropertyVO> selectPropertyNearLocationByUserId(AddressDTO address);
}
