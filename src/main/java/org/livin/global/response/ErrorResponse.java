package org.livin.global.response;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private boolean success;    // API 호출 성공/실패 여부
    private String message;     // 에러 메시지
    private int status;         // HTTP 상태 코드 (숫자)

    public ErrorResponse(boolean success, String message, int status) {
        this.success = success;
        this.message = message;
        this.status = status;
    }
}
