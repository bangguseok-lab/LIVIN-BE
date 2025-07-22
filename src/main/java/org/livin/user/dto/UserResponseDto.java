package org.livin.user.dto;

import lombok.Getter;
import org.livin.user.entity.User;
import java.time.LocalDate;

@Getter

public class UserResponseDto {
    private String name;
    private String nickname;
    private String phone;
    private LocalDate birthDate;
    private String role;
    private String profileImage;

    public UserResponseDto(User user) {
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
        this.birthDate = user.getBirthDate();
        this.role = user.getRole();
        this.profileImage = user.getProfileImage();
    }
}
