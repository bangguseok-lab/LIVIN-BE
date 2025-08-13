package org.livin.global.codef.service;

import java.util.HashMap;

import org.livin.global.codef.dto.buildingregister.BuildingInfoDTO;
import org.livin.global.codef.dto.marketprice.response.BuildingCodeResponseDTO;
import org.livin.global.codef.dto.marketprice.response.MarketInfoResponseDTO;
import org.livin.global.codef.dto.realestateregister.request.OwnerInfoRequestDTO;
import org.livin.global.codef.dto.realestateregister.response.RealEstateRegisterResponseDTO;
import org.livin.risk.dto.RiskAnalysisRequestDTO;

public interface CodefService {
	HashMap<String, Object> publishToken(String clientId, String clientSecret);    //accessToken 발급

	// public String getEncryptWithExternalPublicKey();    //비밀번호 암호화

	RealEstateRegisterResponseDTO requestRealEstateResister(OwnerInfoRequestDTO ownerInfoRequestDTO);    //등기부등본 요청

	<R> R requestBuildingRegister(RiskAnalysisRequestDTO riskAnalysisRequestDTO, Class<R> responseType);

	BuildingCodeResponseDTO requestBuildingCode(BuildingInfoDTO buildingInfoDTO);

	MarketInfoResponseDTO requestMarketInfo(String complexNo, RiskAnalysisRequestDTO riskAnalysisRequestDTO);
}
