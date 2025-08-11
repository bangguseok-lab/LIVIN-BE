package org.livin.property.dto.realestateregister.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class OwnerInfoRequestDTO {
	private String commUniqueNo;	//고유 번호
	private String ownerName;	//소유자 명
}
