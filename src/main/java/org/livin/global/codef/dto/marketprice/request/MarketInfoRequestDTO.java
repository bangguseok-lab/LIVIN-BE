package org.livin.global.codef.dto.marketprice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketInfoRequestDTO {
	private String organization;
	private String searchGbn;
	private String complexNo;
	private String dong;
	private String ho;
}
