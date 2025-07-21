package org.livin.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long userId;
    private String provider;
    private String providerId;
    private String name;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDate birthDate;
    private String phone;
    private String nickname;
    private String profileImage;
}
