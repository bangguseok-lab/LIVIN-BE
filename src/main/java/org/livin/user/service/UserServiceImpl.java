package org.livin.user.service;


import lombok.RequiredArgsConstructor;
import org.livin.user.dto.UserResponseDto;
import org.livin.user.dto.UserUpdateRequestDto;
import org.livin.user.entity.User;
import org.livin.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService{

   private final UserMapper userMapper;

   @Override
   public UserResponseDto getUserInfo(Long userId) {
      User user = userMapper.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
      return new UserResponseDto(user);
   }

   @Override
   public void updateUserInfo(Long userId, UserUpdateRequestDto dto) {
      int result = userMapper.updateUser(dto, userId);
      if (result == 0) {
         throw new RuntimeException("회원 정보 수정 실패");
      }
   }

   @Override
   public void changeUserRole(Long userId, String newRole) {
      int result = userMapper.updateUserRole(userId, newRole);
      if (result == 0) {
         throw new RuntimeException("전환 실패");
      }
   }

}
