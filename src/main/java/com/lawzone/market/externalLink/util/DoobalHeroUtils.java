package com.lawzone.market.externalLink.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawzone.market.admin.service.OrderBookIdInfoDTO;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
@Component
public class DoobalHeroUtils {
	@Value("${dhero.token}")
    private String token;
    
    @Value("${dhero.endpoint}")
    private String endpoint;
    
    @Value("${dhero.spotId}")
    private String spotId;
    
    @Value(value = "${lzmarket.service}")
    private String svcGb;
    
    public Map getDeliveryReception(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	
    	JSONObject json = new JSONObject();
    	if("P".equals(svcGb)) {
    		json.put("spotCode", aMap.get("spotCode"));
    	}else {
    		json.put("spotCode", spotId);
    	}
		
		json.put("receiverName", aMap.get("receiverName"));
		json.put("receiverMobile", aMap.get("receiverMobile"));
		json.put("receiverAddress", aMap.get("receiverAddress"));
		json.put("receiverAddressPostalCode", aMap.get("receiverAddressPostalCode"));
		json.put("productName", aMap.get("productName"));
		json.put("memoFromCustomer", aMap.get("memoFromCustomer"));
		json.put("productPrice", aMap.get("productPrice"));
		json.put("orderIdFromCorp", aMap.get("orderIdFromCorp"));
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
    
    public Map getDeliveryStaus(OrderBookIdInfoDTO orderBookIdInfoDTO) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	List _res = new ArrayList<>();
    	
    	String bookId = orderBookIdInfoDTO.getBookId();
    	String dateFrom = orderBookIdInfoDTO.getOrderDate();
    	
    	String queryString = "?page=1&pageSize=10&dateFrom=" + dateFrom + "&canceled=0";
    	
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpGet request = new HttpGet(this.endpoint + "/deliveries/" + bookId + queryString);
	    request.addHeader("content-type", "application/json");
	    request.addHeader("Authorization", "Bearer " + this.token);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
	    
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
//	    try {
//	    	_res = mapper.readValue(response, List.class);
//	    }catch (Exception e) {
//	    	res = mapper.readValue(response, Map.class);
//	    	
//    	  _res = (List<Entry<String, Integer>>) res.entrySet()
// 	            .stream()
// 	            .collect(Collectors.toList());
//		}
	    return res;
    }
}
