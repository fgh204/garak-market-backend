package com.lawzone.market.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.lawzone.market.util.UtilService;

@Configuration
public class AppConfig {
	@Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
