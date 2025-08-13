package org.livin.global.codef.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.livin.global.codef.dto.buildingregister.BuildingInfoDTO;
import org.livin.global.codef.dto.buildingregister.request.BuildingRegisterCollgationRequestDTO;
import org.livin.global.codef.dto.buildingregister.request.BuildingRegisterRequestDTO;
import org.livin.global.codef.dto.marketprice.request.BuildingCodeRequestDTO;
import org.livin.global.codef.dto.marketprice.request.MarketInfoRequestDTO;
import org.livin.global.codef.dto.marketprice.response.BuildingCodeResponseDTO;
import org.livin.global.codef.dto.marketprice.response.MarketInfoResponseDTO;
import org.livin.global.codef.dto.realestateregister.request.OwnerInfoRequestDTO;
import org.livin.global.codef.dto.realestateregister.request.RealEstateRegisterRequestDTO;
import org.livin.global.codef.dto.realestateregister.response.RealEstateRegisterResponseDTO;
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.risk.dto.RiskAnalysisRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class CodefServiceImpl implements CodefService {

	private static final ObjectMapper mapper = new ObjectMapper();
	private final RsaEncryptionService rsaEncryptionService;
	private String codefAccessToken = "";

	// Codef API 키 및 설정 정보
	@Value("${codef.password}")
	private String password;
	@Value("${codef.ePrepayNo}")
	private String ePrepayNo;
	@Value("${codef.ePrepayPass}")
	private String ePrepayPass;
	@Value("${codef.real-estate-registry}")
	private String realEstateRegisterRequestUrl;
	@Value("${codef.building-registry-general}")
	private String generalBuildingRegisterRequestUrl;
	@Value("${codef.building-registry-set}")
	private String setBuildingRegisterRequestUrl;
	@Value("${codef.client-id}")
	private String clientId;
	@Value("${codef.client-secret}")
	private String clientSecret;
	@Value("${codef.user-id}")
	private String userId;
	@Value("${codef.user-password}")
	private String userPassword;
	@Value("${codef.building-info}")
	private String buildingCodeRequestUrl;
	@Value("${codef.market-price-info}")
	private String marketInfoRequestUrl;
	@Value("${codef.building-registry-colligation}")
	private String colligationBuildingRegisterRequestUrl;

	// Juso API 키 및 설정 정보
	@Value("${juso.api.url}")
	private String jusoApiUrl;
	@Value("${juso.api.confmKey}")
	private String jusoApiConfmKey;

	//codef accessToken 발급
	public HashMap<String, Object> publishToken(String clientId, String clientSecret) {
		HttpURLConnection con = null;
		BufferedReader br = null;

		try {
			URL url = new URL("https://oauth.codef.io/oauth/token");
			String params = "grant_type=client_credentials&scope=read";

			con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			String auth = clientId + ":" + clientSecret;
			String authStringEnc = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
			con.setRequestProperty("Authorization", "Basic " + authStringEnc);

			con.setDoInput(true);
			con.setDoOutput(true);

			try (OutputStream os = con.getOutputStream()) {
				os.write(params.getBytes(StandardCharsets.UTF_8));
				os.flush();
			}

			int responseCode = con.getResponseCode();
			InputStreamReader isr = (responseCode == HttpURLConnection.HTTP_OK) ?
				new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8) :
				new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8);

			try (BufferedReader reader = new BufferedReader(isr)) {
				String responseStr = reader.lines().collect(Collectors.joining());
				String decodedResponse = URLDecoder.decode(responseStr, StandardCharsets.UTF_8);
				return mapper.readValue(decodedResponse, new TypeReference<HashMap<String, Object>>() {
				});
			}
		} catch (IOException e) {
			log.error("CodeF 토큰 발급 중 I/O 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	//비밀번호 암호화
	private String getEncryptWithExternalPublicKey(String pw) {
		try {
			return rsaEncryptionService.encryptWithExternalPublicKey(pw);
		} catch (Exception e) {
			log.error("암호화 실패", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	//등기부등본 요청
	@Override
	public RealEstateRegisterResponseDTO requestRealEstateResister(OwnerInfoRequestDTO ownerInfoRequestDTO) {
		RealEstateRegisterRequestDTO realEstateRegisterRequestDTO = RealEstateRegisterRequestDTO.builder()
			.organization("0002")
			.phoneNo("01083376023")
			.password(getEncryptWithExternalPublicKey(password))
			.inquiryType("0")
			.uniqueNo(ownerInfoRequestDTO.getCommUniqueNo())
			.ePrepayNo(ePrepayNo)
			.ePrepayPass(ePrepayPass)
			.issueType("1")
			.build();
		return executeCodefRequest(realEstateRegisterRequestUrl, realEstateRegisterRequestDTO,
			RealEstateRegisterResponseDTO.class);
	}

	//건축물 대장 요청
	@Override
	public <R> R requestBuildingRegister(RiskAnalysisRequestDTO riskAnalysisRequestDTO, Class<R> responseType) {
		String address = requestJusoApi(riskAnalysisRequestDTO);

		if (riskAnalysisRequestDTO.isGeneral()) {
			return requestGeneralBuildingRegister(address, riskAnalysisRequestDTO, responseType);
		} else {
			try {
				return requestColligationBuildingRegister(address, riskAnalysisRequestDTO, responseType);
			} catch (CustomException e) {
				// 포괄(colligation) 요청 실패 시 집합 건물(set)로 재시도
				log.warn("건물 등기부등본(포괄) 요청 실패, 집합 건물(set)로 재시도합니다.", e);
				return requestSetBuildingRegister(address, riskAnalysisRequestDTO, responseType);
			}
		}
	}

	//단지코드 정보 조회
	@Override
	public BuildingCodeResponseDTO requestBuildingCode(BuildingInfoDTO buildingInfoDTO) {
		BuildingCodeRequestDTO buildingCodeRequestDTO = BuildingCodeRequestDTO.builder()
			.organization("0011")
			.addrSido(buildingInfoDTO.getSido())
			.addrSigun(buildingInfoDTO.getSigungu())
			.addrDong(buildingInfoDTO.getEupmyeondong())
			.build();
		return executeCodefRequest(buildingCodeRequestUrl, buildingCodeRequestDTO, BuildingCodeResponseDTO.class);
	}

	//매물 시세 조회
	@Override
	public MarketInfoResponseDTO requestMarketInfo(String complexNo, RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		MarketInfoRequestDTO marketInfoRequestDTO = MarketInfoRequestDTO.builder()
			.organization("0011")
			.searchGbn("2")
			.complexNo(complexNo)
			.dong(riskAnalysisRequestDTO.getDong().replaceAll("동", ""))
			.ho(riskAnalysisRequestDTO.getHo())
			.build();
		return executeCodefRequest(marketInfoRequestUrl, marketInfoRequestDTO, MarketInfoResponseDTO.class);
	}

	//일반 건축물대장 요청
	private <R> R requestGeneralBuildingRegister(String address, RiskAnalysisRequestDTO riskAnalysisRequestDTO,
		Class<R> responseType) {
		BuildingRegisterRequestDTO buildingRegisterRequestDTO = BuildingRegisterRequestDTO.builder()
			.organization("0001")
			.loginType("1")
			.userId(userId)
			.userPassword(getEncryptWithExternalPublicKey(userPassword))
			.address(address)
			.dong(riskAnalysisRequestDTO.getDong())
			.type("0")
			.zipCode(riskAnalysisRequestDTO.getZipCode())
			.originDataYN("0")
			.build();
		return executeCodefRequest(generalBuildingRegisterRequestUrl, buildingRegisterRequestDTO, responseType);
	}

	//집합건축물대장 요청
	private <R> R requestSetBuildingRegister(String address, RiskAnalysisRequestDTO riskAnalysisRequestDTO,
		Class<R> responseType) {
		BuildingRegisterRequestDTO buildingRegisterRequestDTO = BuildingRegisterRequestDTO.builder()
			.organization("0001")
			.loginType("1")
			.userId(userId)
			.userPassword(getEncryptWithExternalPublicKey(userPassword))
			.address(address)
			.dong(riskAnalysisRequestDTO.getDong())
			.type("0")
			.zipCode(riskAnalysisRequestDTO.getZipCode())
			.originDataYN("0")
			.build();
		return executeCodefRequest(setBuildingRegisterRequestUrl, buildingRegisterRequestDTO, responseType);
	}

	//집합 건축물대장 총괄 요청
	private <R> R requestColligationBuildingRegister(String address, RiskAnalysisRequestDTO riskAnalysisRequestDTO,
		Class<R> responseType) {
		BuildingRegisterCollgationRequestDTO buildingRegisterCollgationRequestDTO = BuildingRegisterCollgationRequestDTO.builder()
			.organization("0001")
			.loginType("1")
			.userId(userId)
			.userPassword(getEncryptWithExternalPublicKey(userPassword))
			.address(address)
			.inquiryType("1")
			.type("0")
			.zipCode(riskAnalysisRequestDTO.getZipCode())
			.originDataYN("0")
			.build();
		return executeCodefRequest(colligationBuildingRegisterRequestUrl, buildingRegisterCollgationRequestDTO,
			responseType);
	}

	//엑세스 토큰 확인 및 재발급 요청
	private void ensureAccessToken() {
		if (codefAccessToken.isEmpty()) {
			HashMap<String, Object> map = publishToken(clientId, clientSecret);
			if (map != null && map.containsKey("access_token")) {
				this.codefAccessToken = (String)map.get("access_token");
			} else {
				log.error("CodeF access token 발급 실패.");
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
		}
	}

	//401에러 시 토큰 재발급
	private <T, R> R executeCodefRequest(String url, T requestBody, Class<R> responseType) {
		int retryCount = 0;
		while (true) {
			ensureAccessToken();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(codefAccessToken);
			HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, headers);
			RestTemplate restTemplate = new RestTemplate();

			try {
				ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
				String decodedBody = URLDecoder.decode(responseEntity.getBody(), StandardCharsets.UTF_8.name());
				log.info("CodeF API 요청 성공. 응답: {}", decodedBody);
				return mapper.readValue(decodedBody, responseType);
			} catch (Exception e) {
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

	//도로명 주소 호출
	private String requestJusoApi(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		String keyword = riskAnalysisRequestDTO.getRoadNo();
		try {
			String url = String.format(
				"%s?confmKey=%s&currentPage=1&countPerPage=10&keyword=%s&resultType=json",
				jusoApiUrl, jusoApiConfmKey, keyword
			);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

			if (!responseEntity.getStatusCode().is2xxSuccessful()) {
				log.error("도로명주소 API 요청 실패. Status Code: {}", responseEntity.getStatusCode());
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}

			String responseBody = responseEntity.getBody();
			HashMap<String, Object> responseMap = mapper.readValue(responseBody,
				new TypeReference<HashMap<String, Object>>() {
				});

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> jusoList = (List<Map<String, Object>>)((Map<String, Object>)responseMap.get(
				"results")).get("juso");

			if (jusoList != null && !jusoList.isEmpty()) {
				Map<String, Object> juso = jusoList.get(0);
				String rn = (String)juso.get("rn");
				String buldMnnm = (String)juso.get("buldMnnm");
				String buldSlno = (String)juso.get("buldSlno");

				// 주소 조합 로직을 스트림과 join을 활용하여 간결하게 개선
				return Stream.of(rn, buldMnnm, (buldSlno != null && !"0".equals(buldSlno)) ? "-" + buldSlno : "")
					.filter(s -> s != null && !s.isEmpty())
					.collect(Collectors.joining());
			} else {
				log.warn("도로명주소 API 응답에 주소 정보가 없습니다.");
				throw new CustomException(ErrorCode.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("도로명주소 API 요청 중 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}