package org.livin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.livin.dto.PropertyWithImageDTO;

import java.util.List;

@Mapper
public interface FavoritePropertyMapper {

    // 1) user_id로 관심으로 등록된 매물들 id를 조회
    List<PropertyWithImageDTO> getFavoritePropertiesWithImageByUserId(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );

}
