package org.livin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String userId;
    private String nickname;
    private String email;
    private String profileImage;
    private String phoneNumber;
}
