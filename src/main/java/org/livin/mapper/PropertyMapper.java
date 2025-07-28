package org.livin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.livin.dto.PropertyNearLocationDTO;

import java.util.List;

@Mapper
public interface PropertyMapper {
    List<PropertyNearLocationDTO> selectPropertyNearLocationByUserId(
            @Param("sido") String sido,
            @Param("sigungu") String sigungu,
            @Param("eupmyendong") String eupmyendong
    );
}
