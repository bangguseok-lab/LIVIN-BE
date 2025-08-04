package org.livin.global.jwt.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.livin.global.jwt.util.JwtUtil;
import org.livin.user.entity.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;

@Log4j2
// @RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {     // OncePerRequestFilter: 요청당 한 번만 실행되는 필터

    // JWT 생성/검증 유틸 클래스
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        System.out.println("🟡 JwtAuthenticationFilter 인스턴스 생성됨: " + this);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        try {
            return false; // 무조건 실행
        } catch (Exception e) {
            log.error("❌ shouldNotFilter 예외 발생", e);
            return true;
        }
    }

    // 요청이 들어올 때마다 실행, 핵심 인증 로직이 들어 있는 부분
    // request: 현재 요청, response: 응답 객체, filterChain: 다음 필터로 요청을 넘기기 위한 객체
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        System.out.println("🟢 doFilterInternal() 호출됨: " + this);

        // 요청 로그 출력
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Authorization 헤더 추출 및 검증
        String authHeader = request.getHeader("Authorization");     // HTTP 헤더에서 "Authorization" 값을 가져와서
        log.info("✅ Authorization Header: {}", authHeader);

        // Bearer {token} 형식으로 전달된 경우에만 처리
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // "Bearer " 문자열을 잘라내고 실제 토큰 추출
            String token = authHeader.substring(7);

            log.info("70, token: {}", token);

            try {
                // JWT 토큰 유효성 검증
                Claims claims = jwtUtil.validateToken(token);  // 토큰 검증, 안에 있는 claims(JWT Claims) 꺼냄
                log.info("RefreshToken claims: {}", claims);

                String username = claims.getSubject(); // 사용자 식별 값, provider:providerId 형태
                String roleName = (String)claims.get("role"); // LANDLORD 또는 TENANT

                log.info("📌 토큰에서 추출된 사용자: {}, 역할: {}", username, roleName);

                // provider와 providerId 분리
                String[] parts = username.split(":", 2);
                String provider = parts.length > 1 ? parts[0] : "unknown";
                String providerId = parts.length > 1 ? parts[1] : username;

                // UserRole enum 매칭
                UserRole userRole = UserRole.valueOf(roleName);

                // Spring Security 에서 요구하는 ROLE_ 접두사 붙인 권한 객체 생성
                List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + userRole.name())
                );

                // 인증 객체 생성 및 SecurityContext 등록
                CustomUserDetails userDetails = new CustomUserDetails(provider, providerId, userRole);

                // UsernamePasswordAuthenticationToken: 인증 객체 생성 (비밀번호는 null로 둠)
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                    authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContextHolder: 현재 요청의 SecurityContext에 인증 객체를 저장
                // → 이후 컨트롤러에서 @AuthenticationPrincipal 사용 가능
                SecurityContextHolder.getContext().setAuthentication(auth);

            }catch (ExpiredJwtException e) {
                // 토큰 만료 예외 처리
                log.error("❌ 토큰이 만료되었습니다: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 상태 코드 401 설정
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"토큰이 만료되었습니다.\"}");
                return; // 필터 체인 중단
            } catch (Exception e) {
                log.error("❌ 토큰 검증 중 예기치 못한 에러: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"토큰 검증 중 예기치 못한 에러\"}");
                return; // 필터 체인 중단
            }
        }

        filterChain.doFilter(request, response);
    }
}