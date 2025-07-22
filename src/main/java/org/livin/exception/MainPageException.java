package org.livin.exception;

public class MainPageException extends RuntimeException {
    public MainPageException() {
        super("인증되지 않은 사용자입니다.");
    }

    public MainPageException(String message) {
        super(message);
    }
}

// 리소스를 찾을 수 없는 예외
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// 잘못된 요청 예외
class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

// 서버 내부 오류 예외
class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}