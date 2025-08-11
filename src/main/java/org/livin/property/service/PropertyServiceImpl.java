package org.livin.property.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.livin.global.codef.dto.realestateregister.request.OwnerInfoRequestDTO;
import org.livin.global.codef.dto.realestateregister.response.OwnerInfoResponseDTO;
import org.livin.global.codef.dto.realestateregister.response.RealEstateRegisterResponseDTO;
import org.livin.global.codef.service.CodefService;
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.dto.PropertyDetailsDTO;
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.mapper.PropertyMapper;
import org.livin.risk.dto.RiskTemporaryDTO;
import org.livin.user.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PropertyServiceImpl implements PropertyService {

	private final PropertyMapper propertyMapper;
	private final UserService userService;
	private final CodefService codefService;
	private final RedisTemplate<String, RiskTemporaryDTO> riskTemporaryRedisTemplate;

	// 관심 매물
	@Override
	public List<PropertyDTO> getFavoritePropertiesForMain(FilteringDTO address) {

		log.info("쿼리 실행 전 address = {}", address);

		try {
			List<PropertyVO> list = propertyMapper.selectFavoritePropertiesWithFilter(address);
			log.info("_list: {}", list);

			// 각 property에 대해 images 리스트를 따로 채워 넣기
			for (PropertyVO property : list) {
				// List<PropertyImageVO> images = propertyMapper.selectImagesByPropertyId(property.getPropertyId());
				List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(
					property.getPropertyId());
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
	@Override
	public List<PropertyDTO> getPropertiesByRegion(FilteringDTO address) {

		log.info("쿼리 실행 전 address = {}", address);

		try {
			// lastId가 있다면, 해당 매물의 createdAt 값을 구해서 lastCreatedAt에 세팅
			if (address.getLastId() != null && address.getLastId() > 0) {
				LocalDateTime cursorCreatedAt = propertyMapper.findCreatedAtByPropertyId(address.getLastId());
				address.setLastCreatedAt(cursorCreatedAt);
				log.info("lastId {} → createdAt: {}", address.getLastId(), cursorCreatedAt);
			}

			// 메인 쿼리 실행
			List<PropertyVO> list = propertyMapper.selectPropertyListByRegion(address);

			log.info("_list: {}", list);

			// 각 매물의 썸네일 이미지 주입
			for (PropertyVO property : list) {
				// List<PropertyImageVO> images = propertyMapper.selectImagesByPropertyId(property.getPropertyId()); //모든 이미지
				List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(
					property.getPropertyId()); // 썸네일 이미지만
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

	@Override
	public long countProperties(FilteringDTO filter) {
		log.info("countProperties - filter: {}", filter);
		try {
			return propertyMapper.countProperties(filter);
		} catch (Exception e) {
			log.error("countProperties 에러:", e);
			return 0L;
		}
	}

	@Override
	public PropertyDetailsDTO getPropertyDetails(Long propertyId, String providerId) {
		Long userId = userService.getUserIdByProviderId(providerId);
		PropertyDetailsVO propertyDetailsVO = propertyMapper.getPropertyDetailsById(propertyId, userId)
			.orElseThrow(() -> new CustomException(
				ErrorCode.NOT_FOUND));
		log.info(propertyDetailsVO);
		PropertyDetailsDTO propertyDetailsDTO = PropertyDetailsDTO.fromPropertyDetailsVO(propertyDetailsVO);
		log.info(propertyDetailsDTO);
		return propertyDetailsDTO;
	}

	// ✅ 구현: 관심 매물 리스트 조회 (지역, 체크리스트 필터링 및 페이징 포함)
	@Override
	public List<PropertyDTO> getFavoritePropertiesWithFilter(FilteringDTO filteringDTO) {
		log.info("서비스: 필터링된 관심 매물 조회 요청 - filteringDTO: {}", filteringDTO);

		// 서비스 계층에서는 이미 FilteringDTO 안에 userId가 설정되어 있다고 가정합니다 (컨트롤러에서 설정했으므로).
		if (filteringDTO.getUserId() == null) {
			log.error("getFavoritePropertiesWithFilter: userId가 FilteringDTO에 설정되지 않았습니다.");
			throw new IllegalArgumentException("사용자 ID가 필요합니다.");
		}

		try {
			// 매퍼 호출 (Mapping.xml에 selectFavoritePropertiesWithFilter 쿼리가 필요함)
			List<PropertyVO> list = propertyMapper.selectFavoritePropertiesWithFilter(filteringDTO);

			// 각 매물에 썸네일 이미지 주입
			for (PropertyVO property : list) {
				List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(
					property.getPropertyId());
				property.setImages(images);
			}

			log.info("getFavoritePropertiesWithFilter: {}건의 관심 매물 조회 완료", list.size());
			return list.stream()
				.map(PropertyDTO::of)
				.collect(Collectors.toList());

		} catch (Exception e) {
			log.error("getFavoritePropertiesWithFilter 서비스 에러: {}", e.getMessage(), e);
			throw new RuntimeException("관심 매물 필터링 조회 실패", e);
		}
	}

	// ✅ 관심 매물 삭제 (ChecklistServiceImpl 패턴 적용)
	@Transactional
	@Override
	public void removeFavoriteProperty(Long propertyId, Long userId) {
		log.info("서비스: 관심 매물 삭제 요청 - propertyId: {}, userId: {}", propertyId, userId);
		if (userId == null) {
			throw new IllegalArgumentException("삭제를 위해 사용자 ID가 필요합니다.");
		}
		try {
			int deletedRows = propertyMapper.deleteFavoriteProperty(propertyId, userId);
			if (deletedRows == 0) {
				log.warn("removeFavoriteProperty: 매물 {}이 사용자 {}의 관심 매물에 없거나 이미 삭제되었습니다.", propertyId, userId);
				throw new IllegalArgumentException("관심 매물 목록에 없거나 이미 삭제된 매물입니다.");
			}
			log.info("removeFavoriteProperty: 매물 {}이 사용자 {}의 관심 매물에서 성공적으로 삭제되었습니다.", propertyId, userId);
		} catch (Exception e) {
			log.error("============> 관심 매물 삭제 중 에러 발생", e);
			throw new RuntimeException("관심 매물 삭제 실패", e);
		}
	}

	// ✅ 관심 매물 추가 (ChecklistServiceImpl 패턴 적용)
	@Transactional
	@Override
	public PropertyDTO addFavoriteProperty(Long userId, Long propertyId) {
		log.info("관심 매물 추가 요청 - userId: {}, propertyId: {}", userId, propertyId);
		try {
			// 1. 이미 관심 매물로 등록되어 있는지 확인
			Integer count = propertyMapper.checkIfFavoriteExists(userId, propertyId);
			if (count != null && count > 0) {
				log.warn("이미 등록된 관심 매물 - userId: {}, propertyId: {}", userId, propertyId);
				throw new IllegalArgumentException("이미 관심 매물로 등록되어 있습니다.");
			}

			// 2. 관심 매물 추가 (saved_at 필드는 현재 시간으로 설정)
			int insertedRows = propertyMapper.addFavoriteProperty(userId, propertyId, LocalDateTime.now());
			if (insertedRows == 0) {
				log.error("관심 매물 추가 실패 - userId: {}, propertyId: {}", userId, propertyId);
				throw new RuntimeException("관심 매물 추가에 실패했습니다.");
			}

			// 3. 성공 시, 추가된 매물 정보를 조회하여 DTO로 반환
			PropertyVO addedProperty = propertyMapper.selectPropertyById(propertyId, userId)
				.orElseThrow(() -> new IllegalArgumentException("추가된 매물을 찾을 수 없습니다."));

			List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(
				addedProperty.getPropertyId());
			addedProperty.setImages(images);

			return PropertyDTO.of(addedProperty);
		} catch (Exception e) {
			log.error("============> 관심 매물 추가 중 에러 발생", e);
			throw new RuntimeException("관심 매물 추가 실패", e);
		}
	}

	public OwnerInfoResponseDTO getRealEstateRegisters(OwnerInfoRequestDTO ownerInfoRequestDTO) {
		RealEstateRegisterResponseDTO realEstateRegisterResponseDTO = codefService.requestRealEstateResister(
			ownerInfoRequestDTO
		);
		Long maximumBondAmount = RealEstateRegisterResponseDTO.parseMaximumBondAmount(
			realEstateRegisterResponseDTO
		);
		OwnerInfoResponseDTO ownerInfoResponseDTO = OwnerInfoResponseDTO.fromRealEstateRegisterResponseDTO(
			realEstateRegisterResponseDTO);
		RiskTemporaryDTO riskTemporaryDTO = RiskTemporaryDTO.builder()
			.isOwner(Objects.equals(ownerInfoResponseDTO.getOwnerName(), ownerInfoRequestDTO.getOwnerName()))
			.maximum_bond_amount(maximumBondAmount)
			.build();
		long ownerExpiration = 1000L * 60 * 60 * 24;

		riskTemporaryRedisTemplate.opsForValue()
			.set(ownerInfoResponseDTO.getCommUniqueNo(), riskTemporaryDTO, Duration.ofMillis(ownerExpiration));
		return ownerInfoResponseDTO;
	}
}