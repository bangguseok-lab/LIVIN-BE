package org.livin.global.codef.service;

import java.util.HashMap;

import org.livin.property.dto.realestateregister.request.OwnerInfoRequestDTO;
import org.livin.property.dto.realestateregister.response.RealEstateRegisterResponseDTO;

public interface CodefService {
	HashMap<String, Object> publishToken(String clientId, String clientSecret);    //accessToken 발급

	public String getEncryptWithExternalPublicKey();    //비밀번호 암호화

	RealEstateRegisterResponseDTO requestRealEstateResister(
		String encryptionPassword,
		OwnerInfoRequestDTO ownerInfoRequestDTO
	);    //등기부등본 요청
}
