package org.livin.property.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livin.user.entity.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO {
    private Long userId;
    private String provider;
    private String providerId;
    private String name;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDate birthDate;
    private String phone;
    private String nickname;
    private int profileImage;
}
