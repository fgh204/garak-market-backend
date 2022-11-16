package com.lawzone.market.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AES256Util {

    private String password = "lzmarket";

    private String algorithm = "PBEWithMD5AndDES";

    public String encryptedText(String plainText) {
    	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor(); 
    	SimpleStringPBEConfig config = new SimpleStringPBEConfig(); 
    	config.setPassword(password); 
		config.setAlgorithm(algorithm); 
		config.setKeyObtentionIterations("1000"); 
		config.setPoolSize("1"); 
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); 
		config.setStringOutputType("base64"); 
		encryptor.setConfig(config);
		
		return encryptor.encrypt(plainText);
    }
    
    public String decryptedText(String encryptedText) {
    	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor(); 
    	SimpleStringPBEConfig config = new SimpleStringPBEConfig(); 
    	config.setPassword(password); 
		config.setAlgorithm(algorithm); 
		config.setKeyObtentionIterations("1000"); 
		config.setPoolSize("1"); 
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); 
		config.setStringOutputType("base64"); 
		encryptor.setConfig(config);
		
		return encryptor.decrypt(encryptedText);
    }
}
