package org.livin.user.service;

import lombok.RequiredArgsConstructor; // 추가
import lombok.extern.log4j.Log4j2; // 추가
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.mapper.PropertyMapper; // 추가
import org.livin.user.mapper.RegisteredPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션 추가
@Log4j2 // Log4j2를 사용하기 위한 어노테이션 추가
public class RegisteredPropertyServiceImpl implements RegisteredPropertyService {

	private final RegisteredPropertyMapper registeredPropertyMapper; // Mapper 의존성 주입
	private final PropertyMapper propertyMapper; // 이미지 조회를 위한 Mapper 의존성 주입

	@Override
	public List<PropertyDTO> getMyProperties(Long userId) {
		log.info("서비스: 사용자 등록 매물 조회 요청 - userId: {}", userId);
		if (userId == null) {
			log.warn("User ID is null. Cannot fetch properties.");
			return Collections.emptyList();
		}

		List<PropertyVO> propertyVOList = registeredPropertyMapper.selectMyProperties(userId);

		// 각 매물에 썸네일 이미지 주입
		for (PropertyVO property : propertyVOList) {
			List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(property.getPropertyId());
			property.setImages(images);
		}

		return propertyVOList.stream()
			.map(PropertyDTO::of)
			.collect(Collectors.toList());
	}

	@Override
	public long countMyProperties(Long userId) {
		log.info("서비스: 사용자 등록 매물 개수 조회 요청 - userId: {}", userId);
		if (userId == null) {
			return 0;
		}
		return registeredPropertyMapper.countMyProperties(userId);
	}

	@Transactional
	@Override
	public void deleteMyProperty(Long propertyId, Long userId) {
		log.info("서비스: 사용자 등록 매물 삭제 요청 - propertyId: {}, userId: {}", propertyId, userId);
		if (userId == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		int deletedRows = registeredPropertyMapper.deleteProperty(propertyId, userId);
		if (deletedRows == 0) {
			log.warn("deleteMyProperty: 매물 {}이 사용자 {}의 매물이 아니거나 이미 삭제되었습니다.", propertyId, userId);
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
		log.info("deleteMyProperty: 매물 {}이 사용자 {}에 의해 성공적으로 삭제되었습니다.", propertyId, userId);
	}
}