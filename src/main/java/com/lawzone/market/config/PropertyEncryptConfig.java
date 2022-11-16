package com.lawzone.market.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;


@Configuration
@EnableEncryptableProperties
public class PropertyEncryptConfig {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

		
	@Value("${jasypt.encryptor.password}") 
	private String encryptKey;

	@Value("${jasypt.encryptor.algorithm}") 
	private String algorithm;
	
	@Bean("jasyptStringEncryptor") 
	public StringEncryptor stringEncryptor() { 
		logger.info("encryptKey: "+encryptKey);
		logger.info("algorithm: "+algorithm);
		
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor(); 
		SimpleStringPBEConfig config = new SimpleStringPBEConfig(); 
		config.setPassword(encryptKey); 
		config.setAlgorithm(algorithm); 
		config.setKeyObtentionIterations("1000"); 
		config.setPoolSize("1"); 
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); 
		config.setStringOutputType("base64"); 
		encryptor.setConfig(config); 
		
		logger.info("end");
		
		return encryptor;
	}
}
