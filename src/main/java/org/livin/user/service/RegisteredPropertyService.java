package org.livin.user.service;

import org.livin.property.dto.PropertyDTO;

import java.util.List;

public interface RegisteredPropertyService {
	List<PropertyDTO> getMyProperties(Long userId);
	long countMyProperties(Long userId);
	void deleteMyProperty(Long propertyId, Long userId);
}
