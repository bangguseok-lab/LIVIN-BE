package org.livin.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    private Long userId;
    private String provider;
    private String providerId;
    private String name;
    private String role;                    // enum('LANDLORD','TENANT')
    private LocalDateTime createdAt;
    private LocalDate birthDate;
    private String phone;
    private String nickname;
    private Integer profileImage;
}
