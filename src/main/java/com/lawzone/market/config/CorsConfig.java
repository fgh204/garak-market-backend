package com.lawzone.market.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CorsConfig {
	@Value("${lzmarket.service}")
    private String service;
	
   @Bean
   public CorsFilter corsFilter() {      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      //config.addAllowedOrigin("*"); // java.lang.IllegalArgumentException: When allowCredentials is true, allowedOrigins cannot contain the special value "*" since that cannot be set on the "Access-Control-Allow-Origin" response header. To allow credentials to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.
      //config.addAllowedOrigin("http://localhost:4200");
      if("D".equals(service)) {
          config.addAllowedOriginPattern("*");
      }else if("T".equals(service)) {
    	  config.addAllowedOriginPattern("https://test.domaado.me");
      } else {
    	  config.addAllowedOriginPattern("https://domaado.me");
          config.addAllowedOriginPattern("https://admin.domaado.me");
      }
      config.addAllowedHeader("*");
      config.addAllowedMethod("*");

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
   }

}
