package com.lawzone.market.externalLink.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
@Component
public class DoobalHeroUtils {
	@Value("${dhero.token}")
    private String token;

    @Value("${dhero.spotcode}")
    private String spotcode;
    
    @Value("${dhero.endpoint}")
    private String endpoint;
    
    public Map getDeliveryReception(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	
    	JSONObject json = new JSONObject();
		json.put("spotCode", this.spotcode);
		json.put("receiverName", "api2복수건우편포함1");
		json.put("receiverMobile", "01012341234");
		json.put("receiverAddress", "서울 영등포구 도신로 31 (현대3차아파트) 123-1234");
		json.put("receiverAddressPostalCode", "07376");
		json.put("productName", "스타벅스 커피");
		json.put("memoFromCustomer", "경비실");
		json.put("productPrice", "15000");
		json.put("orderIdFromCorp", "21051202");
		json.put("print", "r");
    	
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPost request = new HttpPost(this.endpoint + "/deliveries");
	    StringEntity params = new StringEntity(json.toString(), "UTF-8");
	    request.addHeader("content-type", "application/json");
	    request.addHeader("Authorization", "Bearer " + this.token);
	    request.setEntity(params);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
    	
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
	    
    	return res;
    }
}
