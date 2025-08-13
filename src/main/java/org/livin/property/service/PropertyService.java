package org.livin.property.service;

import java.util.List;

import org.livin.global.codef.dto.realestateregister.request.OwnerInfoRequestDTO;
import org.livin.global.codef.dto.realestateregister.response.OwnerInfoResponseDTO;
import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.OptionDTO;
import org.livin.property.dto.PropertyDTO;
import org.livin.property.dto.PropertyDetailsDTO;
import org.livin.property.dto.PropertyRequestDTO;

public interface PropertyService {
	public List<PropertyDTO> getFavoritePropertiesForMain(FilteringDTO address);

	public List<PropertyDTO> getPropertiesByRegion(FilteringDTO address);

	public long countProperties(FilteringDTO address);

	PropertyDetailsDTO getPropertyDetails(Long propertyId, String providerId);

	// ✅ 필터링된 관심 매물 조회를 위한 메서드 추가
	public List<PropertyDTO> getFavoritePropertiesWithFilter(FilteringDTO filteringDTO);

	// ✅ 관심 매물 삭제를 위한 메서드 추가
	public void removeFavoriteProperty(Long propertyId, Long userId);

	OwnerInfoResponseDTO getRealEstateRegisters(OwnerInfoRequestDTO ownerInfoRequestDTO);

	public PropertyDTO addFavoriteProperty(Long userId, Long propertyId);

	void createProperty(PropertyRequestDTO propertyRequestDTO);

	List<OptionDTO> getOptionList();
}
