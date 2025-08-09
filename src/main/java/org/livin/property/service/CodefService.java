package org.livin.property.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Base64;

@Service
public class CodefService {

	// ObjectMapper는 한 번만 생성해서 재사용하는 것이 효율적입니다.
	private static final ObjectMapper mapper = new ObjectMapper();

	protected static HashMap<String, Object> publishToken(String clientId, String clientSecret) {
		HttpURLConnection con = null;
		BufferedReader br = null;

		try {
			// HTTP 요청을 위한 URL 오브젝트 생성
			URL url = new URL("https://oauth.codef.io/oauth/token");
			String params = "grant_type=client_credentials&scope=read";

			con = (HttpURLConnection) url.openConnection();
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
			return mapper.readValue(decodedResponse, new TypeReference<HashMap<String, Object>>(){});
		} catch (Exception e) {
			e.printStackTrace(); // 예외 로그 출력
			return null;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// 예외 로그
				}
			}
			if (con != null) {
				con.disconnect();
			}
		}
	}
}