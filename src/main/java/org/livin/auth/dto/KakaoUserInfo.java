package org.livin.auth.dto;

import lombok.Data;

@Data
public class KakaoUserInfo {
    // 소셜로그인 제공자(ex.카카오톡)
    private String provider;
    // 소셜로그인 고유 ID
    private String providerId;
}
