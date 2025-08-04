package org.livin.property.service;

import java.util.List;

import org.livin.property.dto.FilteringDTO;
import org.livin.property.dto.PropertyDTO;

public interface PropertyService {
	public List<PropertyDTO> getFavoritePropertiesForMain(FilteringDTO address);

	public List<PropertyDTO> getPropertiesByRegion(FilteringDTO address);
}
