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

		// [사전 작업] 이 매물에 이미 연결된 기존 개인화 체크리스트가 있는지 확인하고, 있다면 삭제
		Long existingChecklistId = propertyChecklistMapper.findChecklistIdByPropertyAndUser(propertyId, userId);

		if (existingChecklistId != null) {
			log.info("기존에 연결된 체크리스트(ID: {})를 삭제하고 새로 생성합니다.", existingChecklistId);

			// 순서 중요: 외래 키 제약 조건 때문에 자식 테이블 데이터부터 삭제해야 해야 한다.
			propertyChecklistMapper.deletePropertyChecklistLink(existingChecklistId); 			// 1) Property_Checklist 연결 삭제
			propertyChecklistMapper.deleteChecklistItemsByChecklistId(existingChecklistId); 	// 2) ChecklistItem 들 삭제
			propertyChecklistMapper.deleteChecklistById(existingChecklistId); 					// 3) Checklist 본체 삭제
		}

		// 1. 원본 데이터 조회
		// 원본 체크리스트가 사용자의 소유인지 확인하며 조회
		ChecklistVO sourceChecklist = propertyChecklistMapper.findChecklistByIdAndUserId(sourceChecklistId, userId);

		if (sourceChecklist == null) {
			log.warn("원본 체크리스트를 찾을 수 없거나 소유자가 아닙니다: sourceChecklistId={}", sourceChecklistId);
			throw new CustomException(ErrorCode.FORBIDDEN);   // 내 템플릿이 아니면 거부
		}
		log.info("원본 체크리스트 조회 성공: title='{}'", sourceChecklist.getTitle());

		// 원본 체크리스트의 아이템들 조회
		log.info("이제 원본 체크리스트의 아이템 목록을 조회합니다: checklistId={}", sourceChecklistId);
		List<ChecklistItemVO> sourceItems = propertyChecklistMapper.findItemsByChecklistId(sourceChecklistId);

		// [핵심 로그] 조회된 아이템 개수 확인
		// sourceItems가 null일 경우를 대비하여 3항 연산자 사용
		log.info("조회된 원본 아이템 개수: {}개", sourceItems == null ? 0 : sourceItems.size());

		// 2. Checklist 복제 및 삽입
		ChecklistVO newChecklist = new ChecklistVO();
		newChecklist.setUserId(userId);
		// String.format을 이용해 '체크리스트 템플릿 제목(propertyId)' 형식으로 새 제목을 설정한다.
		newChecklist.setTitle(String.format("%s(%d)", sourceChecklist.getTitle(), propertyId));
		newChecklist.setDescription(sourceChecklist.getDescription());
		newChecklist.setType(sourceChecklist.getType());

		// 복제된 Checklist를 DB에 삽입하고, 생성된 PK(ID)를 newChecklist 객체에 받아온다
		propertyChecklistMapper.insertAndGetId(newChecklist);
		Long newChecklistId = newChecklist.getChecklistId();
		log.info("새로운 체크리스트 생성 완료: newChecklistId={}", newChecklistId);

		// 3. ChecklistItem 복제 및 삽입
		if (sourceItems != null && !sourceItems.isEmpty()) {
			// 복제 로직 진입 확인
			log.info("아이템 복제 로직을 시작합니다...");
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
			// 복제된 ChecklistItem 목록 한 번에 삽입
			propertyChecklistMapper.batchInsertItems(newItems);
		}

		// === 4. Property_Checklist 테이블에 연결 ===
		// 복제된 Checklist를 Property_Checklist 테이블에 추가
		log.info("매물과 새 체크리스트 연결 시작: propertyId=" + propertyId + ", newChecklistId=" + newChecklistId);
		propertyChecklistMapper.insertPropertyChecklist(propertyId, newChecklistId);

		// 새로 생성된 체크리스트 ID 반환
		log.info("<< 체크리스트 복제 및 연결 전체 과정 성공");
		return newChecklistId;
	}


	// 매물 상세 페이지 체크리스트 목록에서 선택한 체크리스트 (이미 생성된 매물 체크리스트가 있으면 → 그 체크리스트 조회)
	@Transactional(readOnly = true)
	@Override
	public List<ChecklistItemDTO> getPersonalizedChecklistForProperty(Long userId, Long propertyId) {
		// 1. Property_Checklist 테이블에서 이 매물(propertyId)과 사용자(userId)에게 연결된 체크리스트 ID를 찾는다.
		Long checklistId = propertyChecklistMapper.findChecklistIdByPropertyAndUser(propertyId, userId);

		// 2. 만약 연결된 체크리스트가 없다면 (null), 빈 리스트를 반환한다.
		if (checklistId == null) {
			return Collections.emptyList();
		}

		// 3. 연결된 checklistId를 찾았을 경우, 해당 ID를 이용해 모든 옵션을 조회하여 반환한다.
		//    (보안을 위해 userId로 다시 한번 소유권을 확인)
		return propertyChecklistMapper.findChecklistItemsByChecklistIdAndUser(checklistId, userId);
	}

	// 매물 상세 페이지 체크리스트 아이템(옵션) 수정
	@Transactional
	@Override
	public void updateChecklistItems(Long userId, Long propertyId, Long checklistId, List<ChecklistItemUpdateRequestDTO> updates) {
		if (updates == null || updates.isEmpty()) {
			return; // 업데이트할 내용이 없으면 종료
		}

		// 업데이트 목록 전체를 매퍼 메서드에 한 번에 전달
		propertyChecklistMapper.batchUpdateItemIsChecked(userId, checklistId, updates);
	}
}