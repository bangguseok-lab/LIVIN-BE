package org.livin.user.dto;

import lombok.Builder;
import lombok.Data;
import org.livin.user.entity.UserRole;

import java.time.LocalDate;

@Data
@Builder
public class UserRegisterRequest {
    private String name;
    private String phone;
    private String nickname;
    private LocalDate birthDate;
    private UserRole role;
}