package org.livin.config;

import lombok.RequiredArgsConstructor;
import org.livin.global.jwt.filter.JwtAuthenticationFilter;
import org.livin.global.jwt.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtUtil jwtUtil;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors() // ✅ cors 설정 활성화
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/api-docs/**",
                        "/api/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
    }

    // ✅ CORS 설정 Bean 등록
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 클라이언트에서 쿠키/헤더 허용
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // 모든 헤더 허용
        config.setAllowedHeaders(Arrays.asList("*"));
        // 메서드 허용
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // ✅ 프론트에서 Authorization 헤더 접근 허용
        config.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

