package org.livin.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.livin.user.entity.User;


public interface UserMapper {

    User findByProviderAndProviderId(@Param("provider") String provider,
                                     @Param("providerId") String providerId);

    void insertUser(User user);

    void deleteByProviderAndProviderId(@Param("providerId") String providerId);
}
