package org.livin.auth.service;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.livin.auth.dto.AdditionalUserInfo;
import org.livin.auth.dto.KakaoTokenResponse;
import org.livin.auth.dto.KakaoUserInfo;
import org.livin.global.jwt.service.TokenService;
import org.livin.global.jwt.util.JwtUtil;
import org.livin.user.entity.UserVO;
import org.livin.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

	private final JwtUtil jwtUtil;
	private final UserMapper userMapper;
	private final TokenService tokenService;

	@Value("${kakao.client-id}")
	private String clientId;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	public KakaoTokenResponse getKakaoAccessToken(String code) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
			"https://kauth.kakao.com/oauth/token", request, KakaoTokenResponse.class);
		System.out.println(response.getBody());
		return response.getBody();
	}

	public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(kakaoAccessToken);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
			"https://kapi.kakao.com/v2/user/me",
			HttpMethod.GET,
			request,
			String.class
		);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response.getBody());

			KakaoUserInfo userInfo = new KakaoUserInfo();
			userInfo.setProvider("kakao");
			userInfo.setProviderId(String.valueOf(root.get("id").asLong()));

			return userInfo;

		} catch (Exception e) {
			throw new RuntimeException("카카오 사용자 정보 파싱 실패", e);
		}
	}

	public String loginOrRegisterUser(KakaoUserInfo userInfo, AdditionalUserInfo additional) {
		UserVO existingUser = userMapper.findByProviderAndProviderId("kakao", userInfo.getProviderId());
		if (existingUser == null) {
			UserVO newUser = UserVO.builder()
				.provider("kakao")
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
		}

		UserVO user = (existingUser != null) ? existingUser :
			userMapper.findByProviderAndProviderId("kakao", userInfo.getProviderId());
		String refreshToken = jwtUtil.generateRefreshToken(user.getProvider(), user.getProviderId(), user.getRole());
		tokenService.saveRefreshToken(user.getProviderId(), refreshToken);
		return jwtUtil.generateAccessToken(user.getProvider(), user.getProviderId(), user.getRole());
	}

	public boolean existsUserByProviderId(String provider, String providerId) {
		return userMapper.findByProviderAndProviderId(provider, providerId) != null;
	}
}
