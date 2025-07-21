package org.livin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.livin.dto.FavoritePropertyDTO;
import org.livin.dto.PropertyDTO;

import java.util.List;

@Mapper
public interface PropertyMapper {

    @Select("SELECT p.property_id as propertyId, p.property_name as propertyName, " +
            "p.address, p.property_type as propertyType, p.monthly_rent as monthlyRent, " +
            "p.deposit, p.area, p.floor, p.image_url as imageUrl, " +
            "p.latitude, p.longitude, f.created_at as favoriteDate " +
            "FROM properties p " +
            "INNER JOIN favorites f ON p.property_id = f.property_id " +
            "WHERE f.user_id = #{userId} " +
            "ORDER BY f.created_at DESC " +
            "LIMIT #{limit}")
    List<FavoritePropertyDTO> findFavoritePropertiesByUserId(@Param("userId") String userId,
                                                             @Param("limit") int limit);

    @Select("SELECT property_id as propertyId, property_name as propertyName, " +
            "address, property_type as propertyType, monthly_rent as monthlyRent, " +
            "deposit, area, floor, total_floors as totalFloors, image_url as imageUrl, " +
            "latitude, longitude, description, has_elevator as hasElevator, " +
            "has_parking as hasParking, available_date as availableDate " +
            "FROM properties " +
            "WHERE status = 'AVAILABLE'")
    List<PropertyDTO> findAllProperties();

    List<PropertyDTO> findNearbyLatestProperties(@Param("lat") double lat,
                                                 @Param("lng") double lng,
                                                 @Param("radius") double radius,
                                                 @Param("limit") int limit);
}