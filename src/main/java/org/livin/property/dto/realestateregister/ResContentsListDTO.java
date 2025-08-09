package org.livin.property.dto.realestateregister;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResContentsListDTO {
	private String resType2;
	private List<ResDetailListDTO> resDetailListDTO;
	private String resNumber;
}