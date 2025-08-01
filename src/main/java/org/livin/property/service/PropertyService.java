package org.livin.property.service;

import java.util.List;
import java.util.stream.Collectors;

import org.livin.property.dto.AddressDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.mapper.FavoritePropertyMapper;
import org.livin.property.mapper.PropertyMapper;
import org.livin.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PropertyService {

	private final UserMapper userMapper;
	private final FavoritePropertyMapper favoritePropertyMapper;
	private final PropertyMapper propertyMapper;

	// 관심 매물
	public List<PropertyDTO> getFavoritePropertiesForMain(AddressDTO address) {
		Long userId = userMapper.findUserIdByProviderId(String.valueOf(address.getProviderId()));

		//      2. providerId를 통해 받은 userId로 관심으로 등록한 매물들 조회
		address.setUserId(userId);
		log.info("쿼리 실행 전 address = {}", address);


		try {
			List<PropertyVO> list = propertyMapper.selectFavoritePropertiesWithFilter(address);
			log.info("_list: {}", list);

			// 각 property에 대해 images 리스트를 따로 채워 넣기
			for (PropertyVO property : list) {
				// List<PropertyImageVO> images = propertyMapper.selectImagesByPropertyId(property.getPropertyId());
				List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(property.getPropertyId());
				property.setImages(images);
			}

			return list.stream()
				.map(PropertyDTO::of)
				.collect(Collectors.toList());

		} catch (Exception e) {
			log.error("getPropertiesByRegion 에러:", e);
			return null;
		}
	}

	// 현재 위치 매물 리스트
	public List<PropertyDTO> getPropertiesByRegion(AddressDTO address) {
		log.info("getPropertiesByRegion({})", address);

		Long userId = userMapper.findUserIdByProviderId(String.valueOf(address.getProviderId()));

		address.setUserId(userId);
		log.info("쿼리 실행 전 address = {}", address);


		try {
			List<PropertyVO> list = propertyMapper.selectPropertyListByRegion(address);
			log.info("_list: {}", list);

			// 각 property에 대해 images 리스트를 따로 채워 넣기
			for (PropertyVO property : list) {
				// List<PropertyImageVO> images = propertyMapper.selectImagesByPropertyId(property.getPropertyId()); //모든 이미지
				List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(property.getPropertyId()); // 썸네일 이미지만
				property.setImages(images);
			}

			return list.stream()
				.map(PropertyDTO::of)
				.collect(Collectors.toList());

		} catch (Exception e) {
			log.error("getPropertiesByRegion 에러:", e);
			return null;
		}
	}

}