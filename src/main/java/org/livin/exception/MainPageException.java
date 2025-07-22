package org.livin.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


// 메인 페이지 전용 예외 처리 클래스
// 추후 팀 프로젝트 통합시 GlobalExceptionHandler와 병합 예정
@RestControllerAdvice(basePackages = "org.livin.controller")
@Log4j2
public class MainPageException {

    // ===== 커스텀 예외 클래스들 (내부 클래스로 정의) =====

    // 인증되지 않은 사용자 예외
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException() {
            super("인증되지 않은 사용자입니다.");
        }

        public UnauthorizedException(String message) {
            super(message);
        }
    }

    // 리소스를 찾을 수 없는 예외
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    // 잘못된 요청 예외
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    // 서버 내부 오류 예외
    public static class InternalServerException extends RuntimeException {
        public InternalServerException(String message) {
            super(message);
        }

        public InternalServerException(String message, Throwable cause) {
            super(message, cause);
        }
    }


    // ===== 예외 핸들러 메소드들 =====

    // 인증 예외 처리 - 401 Unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException e) {
        log.error("메인페이지 인증 오류: {}", e.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // 리소스 없음 예외 처리 - 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error("메인페이지 리소스 없음: {}", e.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 잘못된 요청 예외 처리 - 400 Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException e) {
        log.error("메인페이지 잘못된 요청: {}", e.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 서버 내부 오류 예외 처리 - 500 Internal Server Error
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Map<String, Object>> handleInternalServerException(InternalServerException e) {
        log.error("메인페이지 서버 내부 오류: {}", e.getMessage(), e);

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException e) {
        log.error("메인페이지 NullPointerException 발생", e);

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "데이터 처리 중 오류가 발생했습니다."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("메인페이지 잘못된 인자: {}", e.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("메인페이지 예상치 못한 오류: {}", e.getMessage(), e);

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "서버 처리 중 오류가 발생했습니다."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }


    // ===== 헬퍼 메소드 =====

    // 에러 응답 생성 헬퍼 메소드
    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("path", "/api/main/*");  // 메인 페이지 API 경로

        return errorResponse;
    }


    // ===== 유틸리티 메소드 =====

    // 위도 검증
    public static void validateLatitude(Double lat) {
        if (lat == null) {
            throw new BadRequestException("위도 정보가 필요합니다.");
        }
        if (lat < -90 || lat > 90) {
            throw new BadRequestException("유효하지 않은 위도 값입니다. (-90 ~ 90)");
        }
    }

    // 경도 검증
    public static void validateLongitude(Double lng) {
        if (lng == null) {
            throw new BadRequestException("경도 정보가 필요합니다.");
        }
        if (lng < -180 || lng > 180) {
            throw new BadRequestException("유효하지 않은 경도 값입니다. (-180 ~ 180)");
        }
    }

    // 위치 정보 검증
    public static void validateLocation(Double lat, Double lng) {
        if (lat == null || lng == null) {
            throw new BadRequestException("위도와 경도 정보가 필요합니다.");
        }
        validateLatitude(lat);
        validateLongitude(lng);
    }
}