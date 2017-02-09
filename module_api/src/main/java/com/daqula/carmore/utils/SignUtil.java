package com.daqula.carmore.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class SignUtil {

    public static boolean verifyData(byte[] data, byte[] sigBytes, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(publicKey);
		signature.update(data);
		return signature.verify(sigBytes);
	}

    public static PublicKey getPubKey(ResourceLoader resource) throws Exception {
		InputStream in = resource.getResource("classpath:garageman.pub").getInputStream();
		byte[] keyBytes = new byte[in.available()];
		in.read(keyBytes);
		in.close();
		String pubKey = new String(keyBytes, "UTF-8");
		pubKey = pubKey.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
		keyBytes = Base64.decodeBase64(pubKey);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(spec);
		return publicKey;
	}
}
