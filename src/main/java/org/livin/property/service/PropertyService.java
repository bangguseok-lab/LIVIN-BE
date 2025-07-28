package org.livin.property.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.livin.property.mapper.FavoritePropertyMapper;
import org.livin.property.mapper.PropertyMapper;
import org.livin.property.dto.AddressDTO;
import org.livin.property.dto.PropertyNearLocationDTO;
import org.livin.property.dto.PropertyWithImageDTO;
import org.livin.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class PropertyService {

    private final UserMapper userMapper;
    private final FavoritePropertyMapper favoritePropertyMapper;
    private final PropertyMapper propertyMapper;


//    2) 관심 매물
    public List<PropertyWithImageDTO> getFavoritePropertiesForMain(String providerId, int limit) {
        Long userId = userMapper.findUserIdByProviderId(providerId);

//      2. providerId를 통해 받은 userId로 관심으로 등록한 매물들 조회
        return favoritePropertyMapper.getFavoritePropertiesWithImageByUserId(userId, limit).stream()
                .map(PropertyWithImageDTO::of)
                .toList();
    }


//    3) 현재 위치 매물 리스트
    public List<PropertyNearLocationDTO> getSimplePropertiesNearLocation(AddressDTO address) {
        return propertyMapper.selectPropertyNearLocationByUserId(address).stream()
                .map(PropertyNearLocationDTO::of)
                .toList();
    }


}