package org.livin.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.dto.PropertyDetailsDTO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.mapper.PropertyMapper;
import org.livin.user.dto.EditPropertyDTO;
import org.livin.user.mapper.RegisteredPropertyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RegisteredPropertyServiceImpl implements RegisteredPropertyService {

	private final RegisteredPropertyMapper registeredPropertyMapper;
	private final PropertyMapper propertyMapper;

	@Override
	public List<PropertyDTO> getMyProperties(Long userId) {
		log.info("서비스: 사용자 등록 매물 조회 요청 - userId: {}", userId);
		if (userId == null) {
			log.warn("User ID is null. Cannot fetch properties.");
			return Collections.emptyList();
		}

		List<PropertyVO> propertyVOList = registeredPropertyMapper.selectMyProperties(userId);

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

	@Transactional
	@Override
	public PropertyDetailsDTO updatePropertyDetailsAndFetch(EditPropertyDTO editPropertyDTO, Long userId) {
		log.info("서비스: 사용자 등록 매물 수정 요청 - propertyId: {}, userId: {}", editPropertyDTO.getPropertyId(), userId);
		if (userId == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		if (editPropertyDTO.getPropertyId() == null) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		Long ownerId = registeredPropertyMapper.getUserIdByPropertyId(editPropertyDTO.getPropertyId());
		if (ownerId == null || !ownerId.equals(userId)) {
			log.warn("updatePropertyDetailsAndFetch: 매물 {}이 사용자 {}의 소유가 아님.", editPropertyDTO.getPropertyId(), userId);
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		int updatedRows = registeredPropertyMapper.updatePropertyDetails(editPropertyDTO);

		if (updatedRows == 0) {
			log.warn("updatePropertyDetailsAndFetch: 매물 {} 업데이트 실패", editPropertyDTO.getPropertyId());
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
		log.info("updatePropertyDetailsAndFetch: 매물 {} 정보가 성공적으로 수정되었습니다.", editPropertyDTO.getPropertyId());

		PropertyDetailsVO updatedVO = registeredPropertyMapper.selectPropertyDetails(editPropertyDTO.getPropertyId(), userId);

		if (updatedVO == null) {
			log.error("updatePropertyDetailsAndFetch: 수정 후 매물 {} 조회 실패", editPropertyDTO.getPropertyId());
			throw new CustomException(ErrorCode.NOT_FOUND);
		}

		return PropertyDetailsDTO.fromPropertyDetailsVO(updatedVO);
	}
}