package org.livin.property.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.dto.PropertyDetailsDTO;
import org.livin.property.dto.realestateregister.RiskTemporaryDTO;
import org.livin.property.dto.realestateregister.request.OwnerInfoRequestDTO;
import org.livin.property.dto.realestateregister.request.RealEstateRegisterRequestDTO;
import org.livin.property.dto.realestateregister.response.OwnerInfoResponseDTO;
import org.livin.property.dto.realestateregister.response.RealEstateRegisterResponseDTO;
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.mapper.FavoritePropertyMapper;
import org.livin.property.mapper.PropertyChecklistMapper;
import org.livin.property.mapper.PropertyMapper;
import org.livin.user.mapper.UserMapper;
import org.livin.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PropertyServiceImpl implements PropertyService {

	private final UserMapper userMapper;
	private final FavoritePropertyMapper favoritePropertyMapper;
	private final PropertyMapper propertyMapper;
	private final UserService userService;
	private final PropertyChecklistMapper propertyChecklistMapper;

	private final RsaEncryptionService rsaEncryptionService;
	private final ObjectMapper objectMapper;
	@Value("${codef.password}")
	private String password;
	@Value("${codef.ePrepayNo}")
	private String ePrepayNo;
	@Value("${codef.ePrepayPass}")
	private String ePrepayPass;
	@Value("${codef.real-estate-registry}")
	private String codefUrl;
	@Value("${codef.client-id}")
	private String clientId;
	@Value("${codef.client-secret}")
	private String clientSecret;

	private String codefAccessToken = "";
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
	// 관심 매물 리스트 조회 (지역, 체크리스트 필터링 및 페이징 포함)
	@Override
	public List<PropertyDTO> getFavoritePropertiesWithFilter(FilteringDTO filteringDTO) {
		log.info("서비스: 필터링된 관심 매물 조회 요청 - filteringDTO: {}", filteringDTO);

		if (filteringDTO.getUserId() == null) {
			log.error("getFavoritePropertiesWithFilter: userId가 FilteringDTO에 설정되지 않았습니다.");
			throw new IllegalArgumentException("사용자 ID가 필요합니다.");
		}

		try {
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
		String encryptionPassword = "";
		try {
			encryptionPassword = rsaEncryptionService.encryptWithExternalPublicKey(password);
		} catch (Exception e) {
			log.error("암호화 실패");
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		RealEstateRegisterRequestDTO realEstateRegisterRequestDTO = RealEstateRegisterRequestDTO.builder()
			.organization("0002")
			.phoneNo("01083376023")
			.password(encryptionPassword)
			.inquiryType("0")
			.uniqueNo(ownerInfoRequestDTO.getCommUniqueNo())
			.ePrepayNo(ePrepayNo)
			.ePrepayPass(ePrepayPass)
			.issueType("1")
			.build();

		return requestCodef(realEstateRegisterRequestDTO, ownerInfoRequestDTO);
	}

	private OwnerInfoResponseDTO requestCodef(RealEstateRegisterRequestDTO realEstateRegisterRequestDTO,
		OwnerInfoRequestDTO ownerInfoRequestDTO) {
		int retryCount = 0;
		while (true) {
			// 토큰이 없거나 만료되었을 때만 재발급
			if (codefAccessToken.isEmpty()) {
				HashMap<String, Object> map = CodefService.publishToken(clientId, clientSecret);
				if (map != null && map.containsKey("access_token")) {
					this.codefAccessToken = (String)map.get("access_token");
				} else {
					log.error("CodeF access token 발급 실패.");
					throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(codefAccessToken);
			HttpEntity<RealEstateRegisterRequestDTO> requestEntity = new HttpEntity<>(realEstateRegisterRequestDTO,
				headers);
			RestTemplate restTemplate = new RestTemplate();
			try {
				ResponseEntity<String> responseEntity = restTemplate.postForEntity(codefUrl, requestEntity,
					String.class);
				log.info("CodeF API 요청 성공. Status Code: {}", responseEntity.getStatusCode());
				String rawResponseBody = responseEntity.getBody();
				String decodedBody = URLDecoder.decode(rawResponseBody, StandardCharsets.UTF_8.name());
				RealEstateRegisterResponseDTO realEstateRegisterResponseDTO = objectMapper.readValue(decodedBody,
					RealEstateRegisterResponseDTO.class);
				Long maximumBondAmount = RealEstateRegisterResponseDTO.parseMaximumBondAmount(
					realEstateRegisterResponseDTO);
				OwnerInfoResponseDTO ownerInfoResponseDTO = OwnerInfoResponseDTO.fromRealEstateRegisterResponseDTO(
					realEstateRegisterResponseDTO);

				RiskTemporaryDTO riskTemporaryDTO = RiskTemporaryDTO.builder()
					.isOwner(Objects.equals(ownerInfoResponseDTO.getOwnerName(), ownerInfoRequestDTO.getOwnerName()))
					.maximum_bond_amount(maximumBondAmount)
					.build();
				long ownerExpiration = 1000L * 60 * 60 * 24;

				riskTemporaryRedisTemplate.opsForValue()
					.set(ownerInfoResponseDTO.getCommUniqueNo(), riskTemporaryDTO, Duration.ofMillis(ownerExpiration));

				RiskTemporaryDTO retrievedDto = riskTemporaryRedisTemplate.opsForValue()
					.get(ownerInfoResponseDTO.getCommUniqueNo());
				if (retrievedDto != null) {
					log.info("Redis에서 가져온 정보: isOwner={}, maximumBondAmount={}",
						retrievedDto.isOwner(), retrievedDto.getMaximum_bond_amount());
				}
				//레디스 저장 추가
				return ownerInfoResponseDTO;
			} catch (Exception e) {
				// 401 에러 발생 시 재시도
				if (e.getMessage() != null && e.getMessage().contains("401") && retryCount < 1) {
					log.warn("401 Unauthorized 에러 발생. 토큰 재발급 후 재시도합니다.");
					this.codefAccessToken = "";
					retryCount++;
					continue;
				}
				log.error("CodeF API 요청 중 오류 발생: {}", e.getMessage(), e);
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}

		}
	}

	// 매물 상세페이지에서 체크리스트 목록 출력
	@Transactional
	@Override
	public List<String> getChecklistTitlesByUserId(Long userId) {

		Objects.requireNonNull(userId, "userId must not be null");

		try {
			List<String> titles = propertyChecklistMapper.selectChecklistTitlesByUserId(userId);
			return (titles == null) ? java.util.Collections.emptyList() : titles;
		} catch (Exception e) {
			log.error("체크리스트 제목 조회 실패 userId={}", userId, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

	}
}