package org.livin.user.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class User {
    private Long userId;
    private String name;
    private String nickname;
    private String phone;
    private LocalDate birthDate;
    private String role;
    private String profileImage;
}
