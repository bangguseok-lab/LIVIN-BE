package org.livin.property.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.dto.PropertyDetailsDTO;
import org.livin.property.dto.realestateregister.response.OwnerInfoDTO;
import org.livin.property.dto.realestateregister.request.RealEstateRegisterRequestDTO;
import org.livin.property.dto.realestateregister.response.RealEstateRegisterResponseDTO;
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;
import org.livin.property.mapper.FavoritePropertyMapper;
import org.livin.property.mapper.PropertyMapper;
import org.livin.user.mapper.UserMapper;
import org.livin.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
@RequiredArgsConstructor
@Log4j2
public class PropertyServiceImpl implements PropertyService {

	private final UserMapper userMapper;
	private final FavoritePropertyMapper favoritePropertyMapper;
	private final PropertyMapper propertyMapper;
	private final UserService userService;

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
				List<PropertyImageVO> images = propertyMapper.selectThumbnailImageByPropertyId(property.getPropertyId());
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

	// ✅ 구현: 관심 매물 삭제
	@Override
	public void removeFavoriteProperty(Long propertyId, Long userId) {
		log.info("서비스: 관심 매물 삭제 요청 - propertyId: {}, userId: {}", propertyId, userId);

		if (userId == null) {
			throw new IllegalArgumentException("삭제를 위해 사용자 ID가 필요합니다.");
		}

		try {
			// FavoritePropertyMapper를 사용하여 관심 매물 삭제
			// favorite_property 테이블에서 property_id와 user_id가 일치하는 레코드 삭제
			int deletedRows = propertyMapper.deleteFavoriteProperty(propertyId, userId);

			if (deletedRows == 0) {
				log.warn("removeFavoriteProperty: 매물 {}이 사용자 {}의 관심 매물에 없거나 이미 삭제되었습니다.", propertyId, userId);
				// 이미 삭제되었거나 존재하지 않는 경우, 클라이언트에게 성공으로 알리거나 특정 에러를 반환할 수 있습니다.
				// 여기서는 IllegalArgumentException을 던져 컨트롤러에서 400 Bad Request로 처리하도록 합니다.
				throw new IllegalArgumentException("관심 매물 목록에 없거나 이미 삭제된 매물입니다.");
			}
			log.info("removeFavoriteProperty: 매물 {}이 사용자 {}의 관심 매물에서 성공적으로 삭제되었습니다.", propertyId, userId);

		} catch (Exception e) {
			log.error("removeFavoriteProperty 서비스 에러: {}", e.getMessage(), e);
			throw new RuntimeException("관심 매물 삭제 실패", e);
		}
	}

	@Override
	@Transactional // 데이터 변경이므로 @Transactional 어노테이션 추가
	public void addFavoriteProperty(Long userId, Long propertyId) {
		log.info("관심 매물 추가 요청 - userId: {}, propertyId: {}", userId, propertyId);

		Integer count = propertyMapper.checkIfFavoriteExists(userId, propertyId);
		if (count != null && count > 0) {
		    log.warn("이미 등록된 관심 매물 - userId: {}, propertyId: {}", userId, propertyId);
		    // throw new IllegalArgumentException("이미 관심 매물로 등록되어 있습니다.");
		    return; // 또는 특정 예외를 발생시켜 클라이언트에 알림
		}

		// saved_at 필드는 현재 시간으로 설정
		int insertedRows = propertyMapper.addFavoriteProperty(userId, propertyId, LocalDateTime.now());

		if (insertedRows == 0) {
			log.error("관심 매물 추가 실패 - userId: {}, propertyId: {}", userId, propertyId);
			// throw new RuntimeException("관심 매물 추가에 실패했습니다.");
		} else {
			log.info("관심 매물 추가 성공 - userId: {}, propertyId: {}", userId, propertyId);
		}
	}

	public OwnerInfoDTO getRealEstateRegisters(String uniqueNumber) {
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
			.uniqueNo(uniqueNumber)
			.ePrepayNo(ePrepayNo)
			.ePrepayPass(ePrepayPass)
			.issueType("1")
			.build();

		return requestCodef(realEstateRegisterRequestDTO);
	}

	private OwnerInfoDTO requestCodef(RealEstateRegisterRequestDTO realEstateRegisterRequestDTO) {
		int retryCount = 0;
		while (true) {
			// 토큰이 없거나 만료되었을 때만 재발급
			if (codefAccessToken.isEmpty()) {
				HashMap<String, Object> map = CodefService.publishToken(clientId, clientSecret);
				if (map != null && map.containsKey("access_token")) {
					this.codefAccessToken = (String) map.get("access_token");
				} else {
					log.error("CodeF access token 발급 실패.");
					throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(codefAccessToken);
			HttpEntity<RealEstateRegisterRequestDTO> requestEntity = new HttpEntity<>(realEstateRegisterRequestDTO, headers);
			RestTemplate restTemplate = new RestTemplate();
			try {
				ResponseEntity<String> responseEntity = restTemplate.postForEntity(codefUrl, requestEntity, String.class);
				log.info("CodeF API 요청 성공. Status Code: {}", responseEntity.getStatusCode());
				String rawResponseBody = responseEntity.getBody();
				String decodedBody = URLDecoder.decode(rawResponseBody, StandardCharsets.UTF_8.name());
				RealEstateRegisterResponseDTO realEstateRegisterResponseDTO = objectMapper.readValue(decodedBody, RealEstateRegisterResponseDTO.class);

				return OwnerInfoDTO.fromRealEstateRegisterResponseDTO(realEstateRegisterResponseDTO);
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
}