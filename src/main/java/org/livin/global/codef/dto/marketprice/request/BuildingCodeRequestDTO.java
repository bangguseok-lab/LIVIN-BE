package org.livin.global.codef.dto.marketprice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildingCodeRequestDTO {
	private String organization;
	private String addrSido;
	private String addrSigun;
	private String addrDong;
}
