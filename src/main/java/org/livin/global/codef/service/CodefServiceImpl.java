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

import org.livin.global.codef.dto.buildingregister.request.BuildingRegisterRequestDTO;
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

	// ObjectMapper는 한 번만 생성해서 재사용하는 것이 효율적입니다.
	private static final ObjectMapper mapper = new ObjectMapper();
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
	@Value("${juso.api.url}")
	private String jusoApiUrl;
	@Value("${juso.api.confmKey}")
	private String jusoApiConfmKey;
	private String codefAccessToken = "";
	private final RsaEncryptionService rsaEncryptionService;

	public HashMap<String, Object> publishToken(String clientId, String clientSecret) {
		HttpURLConnection con = null;
		BufferedReader br = null;

		try {
			// HTTP 요청을 위한 URL 오브젝트 생성
			URL url = new URL("https://oauth.codef.io/oauth/token");
			String params = "grant_type=client_credentials&scope=read";

			con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// 클라이언트아이디, 시크릿코드 Base64 인코딩
			String auth = clientId + ":" + clientSecret;
			String authStringEnc = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
			String authHeader = "Basic " + authStringEnc;

			con.setRequestProperty("Authorization", authHeader);
			con.setDoInput(true);
			con.setDoOutput(true);

			// 리퀘스트 바디 전송
			try (OutputStream os = con.getOutputStream()) {
				os.write(params.getBytes(StandardCharsets.UTF_8));
				os.flush();
			}

			// 응답 코드 확인
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
			} else {
				// 에러 발생 시 에러 스트림을 읽어서 로그를 남기는 것이 좋습니다.
				br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));
				// 에러 로그 출력 (필요에 따라 로거 사용)
				System.err.println("Token request failed with response code: " + responseCode);
				return null;
			}

			// 응답 바디 read
			String inputLine;
			StringBuilder responseStr = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				responseStr.append(inputLine);
			}

			// 응답결과 URL Decoding(UTF-8)
			String decodedResponse = URLDecoder.decode(responseStr.toString(), StandardCharsets.UTF_8);
			return mapper.readValue(decodedResponse, new TypeReference<HashMap<String, Object>>() {
			});
		} catch (IOException e) {
			log.error("CodeF 토큰 발급 중 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("CodeF 토큰 발급 중 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	private String getEncryptWithExternalPublicKey(String pw) {
		String encryptionPassword = "";
		try {
			encryptionPassword = rsaEncryptionService.encryptWithExternalPublicKey(pw);
		} catch (Exception e) {
			log.error("암호화 실패");
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return encryptionPassword;
	}

	//등기부등본 열람
	public RealEstateRegisterResponseDTO requestRealEstateResister(
		OwnerInfoRequestDTO ownerInfoRequestDTO
	) {
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

	public void requestBuildingRegister(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		String address = requestJusoApi(riskAnalysisRequestDTO);
		BuildingRegisterRequestDTO buildingRegisterRequestDTO = BuildingRegisterRequestDTO.builder()
			.organization("0001")
			.loginType("1")
			.userId(userId)
			.userPassword(getEncryptWithExternalPublicKey(userPassword))
			.address(address)
			.dong(riskAnalysisRequestDTO.getDong())
			.ho(riskAnalysisRequestDTO.getHo())
			.type("0")
			.zipCode(riskAnalysisRequestDTO.getZipCode())
			.originDataYN("0")
			.build();
		if (riskAnalysisRequestDTO.isGeneral()) {
			executeCodefRequest(generalBuildingRegisterRequestUrl, buildingRegisterRequestDTO, Object.class);
		} else {
			executeCodefRequest(setBuildingRegisterRequestUrl, buildingRegisterRequestDTO, Object.class);
		}

	}

	private <T> HttpEntity<T> createRequestEntity(T body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(codefAccessToken);
		return new HttpEntity<>(body, headers);
	}

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

	private <T, R> R executeCodefRequest(String url, T requestBody, Class<R> responseType) {
		int retryCount = 0;
		while (true) {
			ensureAccessToken();
			HttpEntity<T> requestEntity = createRequestEntity(requestBody);
			RestTemplate restTemplate = new RestTemplate();

			try {
				ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
				log.info("건축물대장:{}", URLDecoder.decode(responseEntity.getBody()));
				// 성공 시 루프를 빠져나오고 결과 반환
				return mapper.readValue(URLDecoder.decode(responseEntity.getBody(), StandardCharsets.UTF_8.name()),
					responseType);
			} catch (Exception e) {
				// 401 에러 발생 시 재시도
				if (e.getMessage() != null && e.getMessage().contains("401") && retryCount < 1) {
					log.warn("401 Unauthorized 에러 발생. 토큰 재발급 후 재시도합니다.");
					this.codefAccessToken = "";
					retryCount++;
					continue; // 루프의 다음 시도로 이동
				}
				log.error("CodeF API 요청 중 오류 발생: {}", e.getMessage(), e);
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
		}
	}

	private String requestJusoApi(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		String keyword = riskAnalysisRequestDTO.getRoadNo();
		try {
			String url = String.format(
				"%s?confmKey=%s&currentPage=1&countPerPage=10&keyword=%s&resultType=json",
				jusoApiUrl,
				jusoApiConfmKey,
				keyword
			);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				String responseBody = responseEntity.getBody();
				log.info("도로명주소 API 요청 성공. 응답: {}", responseBody);

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

					// 6. 원하는 형식으로 주소 조합
					StringBuilder addressBuilder = new StringBuilder();
					addressBuilder.append(rn).append(buldMnnm);
					if (buldSlno != null && !"0".equals(buldSlno)) {
						addressBuilder.append("-").append(buldSlno);
					}
					return addressBuilder.toString();
				} else {
					log.warn("도로명주소 API 응답에 주소 정보가 없습니다.");
					throw new CustomException(ErrorCode.NOT_FOUND);
				}
			} else {
				log.error("도로명주소 API 요청 실패. Status Code: {}", responseEntity.getStatusCode());
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			log.error("도로명주소 API 요청 중 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}