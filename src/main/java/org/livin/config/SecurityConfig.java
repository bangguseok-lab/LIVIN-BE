package org.livin.config;

import lombok.RequiredArgsConstructor;

import org.livin.global.jwt.filter.JwtAuthenticationFilter;
import org.livin.global.jwt.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity		// SpringSecurity 설정 활성화, WebSecurityConfigurerAdapter 설정을 적용할 수 있게 함
@ComponentScan(basePackages = {"org.livin.global.jwt", "org.livin.config"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtUtil jwtUtil;	// JWT 토큰 생성, 검증 등의 유틸 클래스
	private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 인증 필터, 사용자의 토큰을 확인 및 인증 객체를 SecurityContextHolder에 저장하는 역할

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors().and()
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/api/auth/**", "/swagger-ui/**").permitAll()	// 인증 없이 접근 허용
			.antMatchers("/api/kakao/**", "/api/naver/**").permitAll()
			.antMatchers("/api/**").authenticated()	// 반드시 인증(로그인)된 사용자만 접근 가능
			.anyRequest().permitAll()	// 위에서 지정한 경로 외에는 모두 인증 없이 접근 허용
			.and()
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}


	/**
	 * CORS 설정 (전역 허용 or 프론트만 허용)
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173")); // 또는 프론트 주소 http://localhost:5173
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}

