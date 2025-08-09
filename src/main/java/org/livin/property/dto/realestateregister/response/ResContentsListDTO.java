package org.livin.property.dto.realestateregister.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResContentsListDTO {
	private String resType2;
	private List<ResDetailListDTO> resDetailList;
	private String resNumber;
}