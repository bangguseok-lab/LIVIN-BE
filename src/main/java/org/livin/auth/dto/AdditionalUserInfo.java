package org.livin.auth.dto;

import lombok.Data;
import org.livin.user.entity.UserRole;
import java.time.LocalDate;

@Data
public class AdditionalUserInfo {
    private String name;
    private String phone;
    private String nickname;
    private LocalDate birthDate;
    private String profileImage;
    private UserRole role;
}
