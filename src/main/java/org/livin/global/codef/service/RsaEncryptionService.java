package org.livin.global.codef.service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RsaEncryptionService {
	private final String PUBLIC_KEY_STRING;
	private final PublicKey PUBLIC_KEY_OBJECT;

	public RsaEncryptionService(@Value("${codef.external_key}") String password) {
		this.PUBLIC_KEY_STRING = password;
		try {
			this.PUBLIC_KEY_OBJECT = getPublicKeyFromString(this.PUBLIC_KEY_STRING);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private PublicKey getPublicKeyFromString(String publicKeyStr) throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(spec);
	}

	public String encryptWithExternalPublicKey(String plainText) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, PUBLIC_KEY_OBJECT);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}
}
