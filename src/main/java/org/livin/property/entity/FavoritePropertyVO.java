package org.livin.property.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritePropertyVO {
	private Long favoritePropertyId;
	private Long propertyId;
	private Long userId;
	private LocalDateTime savedAt;
}
