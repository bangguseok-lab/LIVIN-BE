package org.livin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.livin.vo.Property;

import java.util.List;

@Mapper
public interface FavoritePropertyMapper {

    // 1) user_id로 관심으로 등록된 매물들 id를 조회
    List<Long> getFavoritePropertyIdsByUserId(Long userId);

    // 2) user_id를 통해 관심 매물들 id들을 getFavoritePropertyByUserId로 받아왔다.
    // 3) 이제 해당 매물 id List를 바탕으로 하나씩 Property 정보들을 받아오면 된다. (+ 최신순, 3개)
    List<Property> getPropertiesByIdsSorted(
            @Param("propertyIds") List<Long> propertyIds,
            @Param("limit") int limit
    );

}
