package com.lawzone.demo;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

public class LawzoneApplicationTests {
	@Test
	public void main() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor(); 
		SimpleStringPBEConfig config = new SimpleStringPBEConfig(); 
		config.setPassword("lzmarket"); 
		config.setAlgorithm("PBEWithMD5AndDES"); 
		config.setKeyObtentionIterations("1000"); 
		config.setPoolSize("1"); 
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); 
		config.setStringOutputType("base64"); 
		encryptor.setConfig(config); 

		String plainText = "1111"; // 암호화 할 내용
		String encryptedText = encryptor.encrypt(plainText); // 암호화
		String decryptedText = encryptor.decrypt(encryptedText); // 복호화
		System.out.println("Enc:"
				+ ""+encryptedText+", Dec:"+decryptedText);
	}
}
