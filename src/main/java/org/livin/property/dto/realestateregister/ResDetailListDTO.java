package org.livin.property.dto.realestateregister;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResDetailListDTO {
	private String resNumber;
	private String resContents;
}
