package org.livin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.livin.dto.UserInfoDTO;

@Mapper
public interface UserMapper {

    @Select("SELECT user_id as userId, nickname, email, profile_image as profileImage, " +
            "phone_number as phoneNumber FROM users WHERE user_id = #{username}")
    UserInfoDTO findUserByUsername(@Param("username") String username);
}