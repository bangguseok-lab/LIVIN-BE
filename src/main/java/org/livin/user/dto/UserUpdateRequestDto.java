package org.livin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserUpdateRequestDto {
    private String nickname;
    private String profileImage;
    private String phoneNumber;
}
