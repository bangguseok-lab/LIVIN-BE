package org.livin.property.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.livin.global.codef.dto.realestateregister.request.OwnerInfoRequestDTO;
import org.livin.global.codef.dto.realestateregister.response.OwnerInfoResponseDTO;
import org.livin.global.codef.dto.realestateregister.response.RealEstateRegisterResponseDTO;
import org.livin.global.codef.service.CodefService;
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.property.dto.ChecklistItemDTO;
import org.livin.property.dto.ChecklistItemUpdateRequestDTO;
import org.livin.property.dto.ChecklistTitleDTO;
import org.livin.global.s3.service.S3ServiceImpl;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.ManagementDTO;
import org.livin.property.dto.OptionDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.dto.PropertyDetailsDTO;
import org.livin.property.dto.PropertyImgRequestDTO;
import org.livin.property.dto.PropertyRequestDTO;
import org.livin.property.dto.PropertyTemporaryDTO;
import org.livin.property.entity.BuildingVO;
import org.livin.property.entity.ChecklistItemVO;
import org.livin.property.entity.ChecklistVO;
import org.livin.property.entity.OptionVO;
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.mapper.FavoritePropertyMapper;
import org.livin.property.mapper.PropertyChecklistMapper;
import org.livin.property.mapper.PropertyMapper;
import org.livin.risk.dto.RiskTemporaryDTO;
import org.livin.risk.service.RiskService;
import org.livin.user.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PropertyServiceImpl implements PropertyService {

	private final PropertyMapper propertyMapper;
	private final UserService userService;
	private final PropertyChecklistMapper propertyChecklistMapper;
	private final CodefService codefService;
	private final RiskService riskService;
	private final S3ServiceImpl s3ServiceImpl;
	private final RedisTemplate<String, RiskTemporaryDTO> riskTemporaryRedisTemplate;
	private final RedisTemplate<String, PropertyTemporaryDTO> propertyTemporaryRedisTemplate;

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

	// 관심 매물 리스트 조회 (지역, 체크리스트 필터링 및 페이징 포함)
	@Override
	public List<PropertyDTO> getFavoritePropertiesWithFilter(FilteringDTO filteringDTO) {
		log.info("서비스: 필터링된 관심 매물 조회 요청 - filteringDTO: {}", filteringDTO);

		if (filteringDTO.getChecklistId() != null && !filteringDTO.getChecklistId().isEmpty()) {
			filteringDTO.setChecklistIdSize(filteringDTO.getChecklistId().size());
		}

		if (filteringDTO.getUserId() == null) {
			log.error("getFavoritePropertiesWithFilter: userId가 FilteringDTO에 설정되지 않았습니다.");
			throw new IllegalArgumentException("사용자 ID가 필요합니다.");
		}

		try {
			if (filteringDTO.getLastId() != null && filteringDTO.getLastId() > 0) {
				propertyMapper.findSavedAtByPropertyIdAndUserId(filteringDTO.getLastId(), filteringDTO.getUserId())
					.ifPresent(savedAt -> {
						filteringDTO.setLastCreatedAt(savedAt);
						log.info("Favorite lastId {} -> saved_at: {}", filteringDTO.getLastId(), savedAt);
					});
			}

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

	// 관심 매물 삭제
	@Transactional
	@Override
	public void removeFavoriteProperty(Long propertyId, Long userId) {
		log.info("서비스: 관심 매물 삭제 요청 - propertyId: {}, userId: {}", propertyId, userId);
		if (userId == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		int deletedRows = propertyMapper.deleteFavoriteProperty(propertyId, userId);
		if (deletedRows == 0) {
			log.warn("removeFavoriteProperty: 매물 {}이 사용자 {}의 관심 매물에 없거나 이미 삭제되었습니다.", propertyId, userId);
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		log.info("removeFavoriteProperty: 매물 {}이 사용자 {}의 관심 매물에서 성공적으로 삭제되었습니다.", propertyId, userId);
	}

	// 관심 매물 추가
	@Transactional
	@Override
	public PropertyDTO addFavoriteProperty(Long userId, Long propertyId) {
		log.info("관심 매물 추가 요청 - userId: {}, propertyId: {}", userId, propertyId);

		// 1. 이미 관심 매물로 등록되어 있는지 확인
		Integer count = propertyMapper.checkIfFavoriteExists(userId, propertyId);
		if (count != null && count > 0) {
			log.warn("이미 등록된 관심 매물 - userId: {}, propertyId: {}", userId, propertyId);
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		// 2. 관심 매물 추가
		int insertedRows = propertyMapper.addFavoriteProperty(userId, propertyId, LocalDateTime.now());
		if (insertedRows == 0) {
			log.error("관심 매물 추가 실패 - userId: {}, propertyId: {}", userId, propertyId);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		// 3. 성공 시, 추가된 매물 정보를 조회하여 DTO로 반환
		PropertyVO addedProperty = propertyMapper.selectPropertyById(propertyId, userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(addedProperty.getPropertyId());
		addedProperty.setImages(images);

		return PropertyDTO.of(addedProperty);
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

	public void createProperty(PropertyRequestDTO propertyRequestDTO, List<MultipartFile> imageFiles,
		String providerId) {
		try {
			Long userId = userService.getUserIdByProviderId(providerId);
			PropertyTemporaryDTO propertyTemporaryDTO = propertyTemporaryRedisTemplate.opsForValue()
				.get(propertyRequestDTO.getPropertyNum());
			long buildingId = createBuilding(propertyTemporaryDTO.getBuildingVO());
			PropertyVO propertyVO = PropertyRequestDTO.toPropertyVO(propertyRequestDTO,
				buildingId, userId);
			propertyMapper.createProperty(propertyVO);
			riskService.createRiskAnalysis(propertyTemporaryDTO.getRiskAnalysisVO(), propertyVO.getPropertyId());

			// 옵션 리스트 처리
			List<Long> optionIdList = propertyRequestDTO.getOptionIdList();
			if (optionIdList != null && !optionIdList.isEmpty()) {
				propertyMapper.createPropertyOptions(propertyVO.getPropertyId(), optionIdList);
			}

			// 관리비 리스트 처리
			List<ManagementDTO> managementDTOList = propertyRequestDTO.getManagementDTOList();
			if (managementDTOList != null && !managementDTOList.isEmpty()) {
				propertyMapper.createManagement(propertyVO.getPropertyId(), managementDTOList);
			}

			// 이미지 리스트 처리
			List<PropertyImgRequestDTO> imageMetadataList = propertyRequestDTO.getImgRepresentList();
			if (imageFiles != null && !imageFiles.isEmpty() && imageMetadataList != null
				&& !imageMetadataList.isEmpty()) {
				if (imageFiles.size() != imageMetadataList.size()) {
					throw new IllegalArgumentException("이미지 파일 수와 메타데이터 수가 일치하지 않습니다.");
				}
				List<PropertyImageVO> propertyImages = new ArrayList<>();
				for (int i = 0; i < imageFiles.size(); i++) {
					MultipartFile file = imageFiles.get(i);
					boolean represent = imageMetadataList.get(i).getRepresent();

					if (!file.isEmpty()) {
						String imageUrl = s3ServiceImpl.uploadFile(file);
						PropertyImageVO propertyImageVO = PropertyImageVO.builder()
							.propertyId(propertyVO.getPropertyId())
							.represent(represent)
							.imageUrl(imageUrl)
							.build();
						propertyImages.add(propertyImageVO);
					}
				}
				// 생성된 이미지 URL들을 DB에 저장
				propertyMapper.createPropertyImages(propertyImages);
				propertyTemporaryRedisTemplate.delete(propertyRequestDTO.getPropertyNum());
			}

		} catch (Exception e) {
			// 예외 발생 시, 롤백 로직 추가 필요
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private Long createBuilding(BuildingVO buildingVO) {
		BuildingVO building;
		if (propertyMapper.existsBuilding(buildingVO.getRoadAddress())) {
			building = propertyMapper.getBuilding(buildingVO.getRoadAddress());
			return building.getBuildingId();
		}
		propertyMapper.createBuilding(buildingVO);
		return buildingVO.getBuildingId();
	}

	public List<OptionDTO> getOptionList() {
		List<OptionVO> optionVOList = propertyMapper.getOptionList();
		if (optionVOList.isEmpty()) {
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
		return optionVOList.stream()
			.map(OptionDTO::fromOptionVO)
			.toList();
	}

	// 매물 상세 페이지 체크리스트 목록 출력
	@Transactional
	@Override
	public List<ChecklistTitleDTO> getChecklistTitlesByUserId(Long userId) {

		Objects.requireNonNull(userId, "userId must not be null");

		try {
			List<ChecklistTitleDTO> titles = propertyChecklistMapper.selectChecklistTitlesByUserId(userId);
			return (titles == null) ? java.util.Collections.emptyList() : titles;
		} catch (Exception e) {
			log.error("체크리스트 제목 조회 실패 userId={}", userId, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}


	@Transactional
	@Override
	public Long cloneChecklistForProperty(Long userId, Long propertyId, Long sourceChecklistId) {
		log.info(">> 체크리스트 복제 시작: userId={}, propertyId={}, sourceChecklistId={}", userId, propertyId, sourceChecklistId);

		// 1. 원본 데이터 조회
		// 원본 체크리스트가 사용자의 소유인지 확인하며 조회
		ChecklistVO sourceChecklist = propertyChecklistMapper.findChecklistByIdAndUserId(sourceChecklistId, userId);
		if (sourceChecklist == null) {
			throw new CustomException(ErrorCode.FORBIDDEN);   // 내 템플릿이 아니면 거부
		}
		// 원본 체크리스트의 아이템들 조회
		List<ChecklistItemVO> sourceItems = propertyChecklistMapper.findItemsByChecklistId(sourceChecklistId);

		// 2. Checklist 복제 및 삽입
		ChecklistVO newChecklist = new ChecklistVO();

		newChecklist.setUserId(userId);
		// String.format을 이용해 '체크리스트 템플릿 제목(propertyId)' 형식으로 새 제목을 설정합니다.
		newChecklist.setTitle(String.format("%s(%d)", sourceChecklist.getTitle(), propertyId));
		newChecklist.setDescription(sourceChecklist.getDescription());
		newChecklist.setType(sourceChecklist.getType());

		// 복제된 Checklist를 DB에 삽입하고, 생성된 PK(ID)를 newChecklist 객체에 받아옴
		propertyChecklistMapper.insertAndGetId(newChecklist);
		Long newChecklistId = newChecklist.getChecklistId();
		log.info("새로운 체크리스트 생성 완료: newChecklistId={}", newChecklistId);

		// 3. ChecklistItem 복제 및 삽입
		if (sourceItems != null && !sourceItems.isEmpty()) {
			// 복제할 아이템들을 담을 리스트 생성
			List<ChecklistItemVO> newItems = new ArrayList<>();
			for (ChecklistItemVO sourceItem : sourceItems) {
				ChecklistItemVO newItem = new ChecklistItemVO();
				newItem.setChecklistId(newChecklistId); // 새로 생성된 Checklist ID 설정
				newItem.setKeyword(sourceItem.getKeyword());
				newItem.setIsActive(sourceItem.getIsActive());
				newItem.setIsChecked(false); // 복사본의 체크 상태는 항상 false로 초기화
				newItem.setType(sourceItem.getType());
				newItems.add(newItem);
			}
			// 복제된 아이템들을 DB에 Batch Insert
			propertyChecklistMapper.batchInsertItems(newItems);
			log.info("{}개의 체크리스트 아이템 복제 완료", newItems.size());
		}

		// === 4. (핵심) Property_Checklist 테이블에 연결 ===
		// 이전에 만든 연결용 메서드를 재사용
		log.info("매물과 새 체크리스트 연결 시작: propertyId={}, newChecklistId={}", propertyId, newChecklistId);
		propertyChecklistMapper.insertPropertyChecklist(propertyId, newChecklistId);

		// 새로 생성된 체크리스트 ID 반환
		log.info("<< 체크리스트 복제 및 연결 전체 과정 성공");
		return newChecklistId;
	}


	// @Transactional
	// @Override
	// public void linkChecklistToProperty(Long userId, Long propertyId, Long checklistId) {
	// 	// ✅ {} 플레이스홀더를 사용하면 성능에 이점이 있습니다.
	// 	log.info(">> 매물-체크리스트 연결 시작: userId={}, propertyId={}, checklistId={}", userId, propertyId, checklistId);
	//
	// 	Objects.requireNonNull(userId, "userId must not be null");
	// 	Objects.requireNonNull(propertyId, "propertyId must not be null");
	// 	Objects.requireNonNull(checklistId, "checklistId must not be null");
	//
	// 	log.info("1. 소유권 검증 시작...");
	// 	boolean isOwner = propertyChecklistMapper.isChecklistOwnedByUser(userId, checklistId);
	// 	if (!isOwner) {
	// 		log.warn("소유권 검증 실패: userId={}는 checklistId={}의 소유자가 아님", userId, checklistId);
	// 		throw new CustomException(ErrorCode.FORBIDDEN);
	// 	}
	// 	log.info("소유권 검증 통과");
	//
	// 	log.info("2. 중복 연결 검증 시작...");
	// 	boolean alreadyExists = propertyChecklistMapper.existsByPropertyIdAndChecklistId(propertyId, checklistId);
	// 	if (alreadyExists) {
	// 		log.warn("중복 연결 발견: propertyId={}와 checklistId={}는 이미 연결되어 있음", propertyId, checklistId);
	// 		throw new CustomException(ErrorCode.ALREADY_EXISTS);
	// 	}
	// 	log.info("중복 연결 없음 확인");
	//
	// 	log.info("3. 데이터 삽입 시작...");
	// 	propertyChecklistMapper.insertPropertyChecklist(propertyId, checklistId);
	// 	log.info("<< 매물-체크리스트 연결 성공");
	// }

	// @Transactional
	// @Override
	// public void linkChecklistToProperty(Long userId, Long propertyId, Long checklistId) {
	// 	Objects.requireNonNull(userId, "userId must not be null");
	// 	Objects.requireNonNull(propertyId, "propertyId must not be null");
	// 	Objects.requireNonNull(checklistId, "checklistId must not be null");
	//
	// 	// 1. 해당 체크리스트가 사용자의 소유인지 권한 검증
	// 	boolean isOwner = propertyChecklistMapper.isChecklistOwnedByUser(userId, checklistId);
	// 	if (!isOwner) {
	// 		// 내 체크리스트가 아니면 예외 처리 (예: 403 Forbidden)
	// 		throw new CustomException(ErrorCode.FORBIDDEN);
	// 	}
	//
	// 	// 2. 이미 매물에 해당 체크리스트가 연결되어 있는지 중복 검증
	// 	boolean alreadyExists = propertyChecklistMapper.existsByPropertyIdAndChecklistId(propertyId, checklistId);
	// 	if (alreadyExists) {
	// 		// 이미 연결된 경우 예외 처리 (예: 409 Conflict)
	// 		throw new CustomException(ErrorCode.ALREADY_EXISTS);
	// 	}
	//
	// 	// 3. 검증 통과 후, Property_Checklist 테이블에 데이터 삽입
	// 	propertyChecklistMapper.insertPropertyChecklist(propertyId, checklistId);
	// }

	// 매물 상세 페이지 체크리스트 목록에서 선택한 체크리스트 (이미 생성된 매물 체크리스트가 있으면 → 그 체크리스트 조회)
	@Transactional(readOnly = true)
	@Override
	public List<ChecklistItemDTO> getPersonalizedChecklistForProperty(Long userId, Long propertyId) {
		// 1. Property_Checklist 테이블에서 이 매물(propertyId)과 사용자(userId)에게
		//    연결된 체크리스트 ID를 찾습니다.
		Long checklistId = propertyChecklistMapper.findChecklistIdByPropertyAndUser(propertyId, userId);

		// 2. 만약 연결된 체크리스트가 없다면 (null), 빈 리스트를 반환합니다.
		if (checklistId == null) {
			return Collections.emptyList();
		}

		// 3. 연결된 checklistId를 찾았다면, 해당 ID를 이용해 모든 아이템을 조회하여 반환합니다.
		//    (보안을 위해 userId로 다시 한번 소유권을 확인하는 것이 좋습니다.)
		return propertyChecklistMapper.findChecklistItemsByChecklistIdAndUser(checklistId, userId);
	}

	// 매물 상세 페이지 체크리스트 아이템(옵션) 수정
	@Transactional
	@Override
	public void updateChecklistItems(Long userId, Long checklistId, List<ChecklistItemUpdateRequestDTO> updates) {
		Objects.requireNonNull(userId, "userId must not be null");
		Objects.requireNonNull(checklistId, "checklistId must not be null");
		Objects.requireNonNull(updates, "updates must not be null");

		// 로그 추가: 메서드 시작 시 주요 파라미터 값 출력
		log.info("Starting updateChecklistItems. userId: {}, checklistId: {}, updates count: {}", userId, checklistId, updates.size());

		for (ChecklistItemUpdateRequestDTO update : updates) {
			// 로그 추가: 각 아이템 업데이트 직전에 파라미터 값 출력
			log.info("Attempting to update checklist item. checklistItemId: {}, isChecked: {}", update.getChecklistItemId(), update.isChecked());

			propertyChecklistMapper.updateChecklistItemIsChecked(userId, checklistId, update.getChecklistItemId(),
				update.isChecked());

			// MyBatis 업데이트 결과 로그 출력
			// 이 부분은 MyBatis 설정을 통해 확인할 수 있으므로, 별도의 로그 코드를 추가하지 않아도 됩니다.
		}
		log.info("Finished updateChecklistItems for checklistId: {}", checklistId);
	}
}