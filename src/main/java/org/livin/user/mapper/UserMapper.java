package org.livin.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.livin.user.dto.UserUpdateRequestDto;
import org.livin.user.entity.User;

import java.util.Optional;



public interface UserMapper {
    Optional<User> findById(Long userId);
    int updateUser(@Param("dto")UserUpdateRequestDto dto, @Param("userId") Long userId);
    int updateUserRole(@Param("userId") long userId, @Param("role") String role);
}
