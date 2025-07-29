package org.livin.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.livin.auth.dto.AdditionalUserInfo;
import org.livin.auth.dto.NaverTokenResponse;
import org.livin.auth.dto.NaverUserInfo;
import org.livin.global.jwt.service.TokenService;
import org.livin.global.jwt.util.JwtUtil;
import org.livin.user.entity.User;
import org.livin.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NaverAuthService {

	private final UserMapper userMapper;
	private final JwtUtil jwtUtil;
	private final TokenService tokenService;

	@Value("${naver.client-id}")
	private String clientId;

	@Value("${naver.client-secret}")
	private String clientSecret;

	@Value("${naver.redirect-uri}")
	private String redirectUri;

	private final RestTemplate restTemplate = new RestTemplate();

	public NaverTokenResponse getNaverAccessToken(String code, String state) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("code", code);
		params.add("state", state);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		ResponseEntity<NaverTokenResponse> response = restTemplate.postForEntity(
			"https://nid.naver.com/oauth2.0/token", request, NaverTokenResponse.class);

		return response.getBody();
	}

	public NaverUserInfo getUserInfo(String naverAccessToken) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(naverAccessToken);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
			"https://openapi.naver.com/v1/nid/me",
			HttpMethod.GET,
			request,
			String.class
		);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response.getBody());
			JsonNode userNode = root.get("response");

			NaverUserInfo userInfo = new NaverUserInfo();
			userInfo.setProvider("naver");
			userInfo.setProviderId(userNode.get("id").asText());
			//            userInfo.setProviderId(String.valueOf(userNode.get("id").asLong()));

			return userInfo;

		} catch (Exception e) {
			throw new RuntimeException("네이버 사용자 정보 파싱 실패", e);
		}
	}

	public String loginOrRegisterUser(NaverUserInfo userInfo, AdditionalUserInfo additional) {
		// 기존 회원 조회
		User existingUser = userMapper.findByProviderAndProviderId("naver", userInfo.getProviderId());

		// 신규 회원일 경우 DB에 저장
		if (existingUser == null) {
			User newUser = User.builder()
				.provider("naver")
				.providerId(userInfo.getProviderId())
				.name(additional.getName())
				.phone(additional.getPhone())
				.nickname(additional.getNickname())
				.birthDate(additional.getBirthDate())
				.role(additional.getRole())
				.profileImage(additional.getProfileImage())
				.createdAt(LocalDateTime.now())
				.build();

			userMapper.insertUser(newUser);
			existingUser = newUser;
		}

		User user = existingUser;

		String refreshToken = jwtUtil.generateRefreshToken(user.getProvider(), user.getProviderId());
		tokenService.saveRefreshToken(user.getProviderId(), refreshToken);
		return jwtUtil.generateAccessToken(user.getProvider(), user.getProviderId(), user.getRole());
	}

	public boolean existsUserByProviderId(String provider, String providerId) {
		return userMapper.findByProviderAndProviderId(provider, providerId) != null;
	}
}
