package org.livin.property.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.ManagementDTO;
import org.livin.property.entity.BuildingVO;
import org.livin.property.entity.PropertyDetailsVO;
import org.livin.property.entity.PropertyImageVO;
import org.livin.property.entity.PropertyVO;

public interface PropertyMapper {
	List<PropertyVO> selectPropertyListByRegion(FilteringDTO address);

	List<PropertyVO> selectFavoritePropertiesWithFilter(FilteringDTO address);

	List<PropertyImageVO> selectThumbnailImageByPropertyId(Long propertyId);

	List<PropertyImageVO> selectImagesByPropertyId(Long propertyId);

	LocalDateTime findCreatedAtByPropertyId(Long propertyId);

	long countProperties(FilteringDTO filter);

	Optional<PropertyDetailsVO> getPropertyDetailsById(@Param("propertyId") Long propertyId,
		@Param("userId") Long userId);

	int deleteFavoriteProperty(@Param("propertyId") Long propertyId, @Param("userId") Long userId);

	int addFavoriteProperty(@Param("userId") Long userId, @Param("propertyId") Long propertyId,
		@Param("savedAt") LocalDateTime savedAt);

	int checkIfFavoriteExists(@Param("userId") Long userId, @Param("propertyId") Long propertyId);

	Optional<PropertyVO> selectPropertyById(@Param("propertyId") Long propertyId, @Param("userId") Long userId);

	long createBuilding(BuildingVO buildingVO);

	long createProperty(PropertyVO propertyVO);

	Boolean existsBuilding(@Param("roadAddress") String roadAddress);

	BuildingVO getBuilding(@Param("roadAddress") String roadAddress);

	void createPropertyOptions(@Param("propertyId") Long propertyId, @Param("optionIdList") List<Long> optionIdList);

	void createManagement(@Param("propertyId") Long propertyId,
		@Param("managementDTOList") List<ManagementDTO> managementDTOList);

	void createPropertyImages(@Param("propertyId") Long propertyId,
		@Param("imgUrls") List<PropertyImageVO> propertyImageVOList);
}

