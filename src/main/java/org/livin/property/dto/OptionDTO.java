package org.livin.property.dto;

import org.livin.property.entity.OptionVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionDTO {
	private Long optionId;
	private String optionType;

	public static OptionDTO fromOptionVO(OptionVO optionVO) {
		return OptionDTO.builder()
			.optionId(optionVO.getOptionId())
			.optionType(optionVO.getOptionType())
			.build();
	}
}
