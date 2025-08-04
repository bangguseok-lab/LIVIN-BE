package org.livin.property.service;

import java.util.List;
import java.util.stream.Collectors;

import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.mapper.FavoritePropertyMapper;
import org.livin.property.mapper.FavoritePageMapper;
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
	private final FavoritePageMapper favoritePageMapper;

	// // 관심 매물
	// public List<PropertyDTO> getFavoritePropertiesForMain(AddressDTO address) {
	// 	Long userId = userMapper.findUserIdByProviderId(String.valueOf(address.getProviderId()));
	//
	// 	//      2. providerId를 통해 받은 userId로 관심으로 등록한 매물들 조회
	// 	address.setUserId(userId);
	// 	log.info("쿼리 실행 전 address = {}", address);
	//
	//
	// 	try {
	// 		List<PropertyVO> list = propertyMapper.selectFavoritePropertiesWithFilter(address);
	// 		log.info("_list: {}", list);
	//
	// 		// 각 property에 대해 images 리스트를 따로 채워 넣기
	// 		for (PropertyVO property : list) {
	// 			// List<PropertyImageVO> images = propertyMapper.selectImagesByPropertyId(property.getPropertyId());
	// 			List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(property.getPropertyId());
	// 			property.setImages(images);
	// 		}
	//
	// 		return list.stream()
	// 			.map(PropertyDTO::of)
	// 			.collect(Collectors.toList());
	//
	// 	} catch (Exception e) {
	// 		log.error("getPropertiesByRegion 에러:", e);
	// 		return null;
	// 	}
	// }

	// 현재 위치 매물 리스트
	public List<PropertyDTO> getPropertiesByRegion(FilteringDTO address) {
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

	// ✅ 변경: 관심 매물 리스트 조회 (지역, 체크리스트 필터링 및 페이징 포함)
	public List<PropertyDTO> getFavoritePropertiesWithFilter(FilteringDTO address) {
		log.info("getFavoritePropertiesWithFilter({})", address);

		// providerId를 통해 userId를 얻어 AddressDTO에 설정합니다.
		// 관심 매물은 로그인한 사용자만 접근 가능하므로 providerId/userId 유효성 검사
		if (address.getProviderId() == null) {
			log.warn("관심 매물 조회를 위한 providerId가 없습니다. 로그인된 사용자가 아닙니다.");
			// 로그인되지 않은 사용자에게는 빈 리스트를 반환하거나 권한 없음 예외를 던질 수 있습니다.
			return List.of();
		}

		Long userId = userMapper.findUserIdByProviderId(String.valueOf(address.getProviderId()));
		if (userId == null) {
			log.warn("providerId에 해당하는 userId를 찾을 수 없습니다: {}", address.getProviderId());
			return List.of(); // 유효하지 않은 providerId일 경우 빈 리스트 반환
		}
		address.setUserId(userId);

		log.info("관심 매물 쿼리 실행 전 address = {}", address);

		try {
			List<PropertyVO> favoriteList = propertyMapper.selectFavoritePropertiesWithFilter(address);
			log.info("조회된 관심 매물 리스트 개수: {}", favoriteList.size());

			// ✅ N+1 쿼리 방지를 위해 이미지 조회 루프 제거.
			//    PropertyMapper.xml의 selectFavoritePropertiesWithFilter 쿼리에서
			//    썸네일 이미지 URL을 이미 가져오고 있으므로 별도의 추가 조회가 필요 없습니다.
			//    PropertyVO의 thumbnailImageUrl 필드가 채워져 있을 것입니다.

			return favoriteList.stream()
				.map(PropertyDTO::of)
				.collect(Collectors.toList());

		} catch (Exception e) {
			log.error("getFavoritePropertiesWithFilter 관심 매물 조회 중 에러 발생: {}", e.getMessage(), e);
			// 예외 발생 시 빈 리스트 반환 (NullPointerException 방지)
			return List.of();
		}
	}

	// ✅ 변경 없음: 관심 매물 삭제 (이전에 합의한 내용과 동일)
	public void removeFavoriteProperty(Long propertyId, Long userId) {
		log.info("Removing favorite property. propertyId: {}, userId: {}", propertyId, userId);
		try {
			// 선택 사항: 삭제 전에 해당 매물이 정말 사용자의 관심 매물인지 확인할 수 있습니다.
			// Integer isFavorite = favoritePageMapper.isPropertyFavorite(propertyId, userId);
			// if (isFavorite == null || isFavorite == 0) {
			//     log.warn("사용자 {}의 관심 매물 목록에 매물 {}이(가) 없습니다.", userId, propertyId);
			//     throw new IllegalArgumentException("Favorite property not found for user.");
			// }
			favoritePageMapper.deleteFavoriteProperty(propertyId, userId);
			log.info("매물 {}이 사용자 {}의 관심 매물에서 성공적으로 삭제되었습니다.", propertyId, userId);
		} catch (Exception e) {
			log.error("관심 매물 삭제 중 에러 발생. propertyId: {}, userId: {}: {}", propertyId, userId, e.getMessage(), e);
			throw new RuntimeException("관심 매물 삭제 실패", e); // 더 구체적인 예외 처리
		}
	}
}