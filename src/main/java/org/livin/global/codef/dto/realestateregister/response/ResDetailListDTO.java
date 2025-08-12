package org.livin.global.codef.dto.realestateregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResDetailListDTO {
	private String resNumber;
	private String resContents;
}
