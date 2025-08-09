package org.livin.property.dto.realestateregister.request;

import java.util.List;
import java.util.Optional;

import org.livin.property.dto.realestateregister.response.RealEstateRegisterResponseDTO;
import org.livin.property.dto.realestateregister.response.ResContentsListDTO;
import org.livin.property.dto.realestateregister.response.ResDetailListDTO;
import org.livin.property.dto.realestateregister.response.ResRegistrationHisListDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class OwnerInfoDTO {
	private String commUniqueNo;	//고유 번호
	private String ownerName;	//소유자 명
}
