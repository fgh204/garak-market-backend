package com.lawzone.market.externalLink.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.sf.json.JSONObject;

@Slf4j
@Component
public class BizTalkUtils {
	@Value("${biztalk.clientid}")
    private String clientid;
    
    @Value("${biztalk.secretkey}")
    private String secretkey;
    
    @Value("${biztalk.profile}")
    private String profile;
    
    @Value("${biztalk.endpoint}")
    private String endpoint;
    
    @Value("${biztalk.version}")
    private String version;
    
    @Value(value = "${lzmarket.service}")
	 private String svcGb;
    
    public Map getBiztalkToken() throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	
    	if("T".equals(svcGb) || "P".equals(svcGb) ) {
    		JSONObject json = new JSONObject();
        	json.put("bsid", this.clientid);
    		json.put("passwd", this.secretkey);
        	
    		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    		
    		HttpPost request = new HttpPost(this.endpoint + "/v2/auth/getToken");
    	    StringEntity params = new StringEntity(json.toString(), "UTF-8");
    	    request.addHeader("content-type", "application/json");
    	    request.setEntity(params);
    	    
    	    CloseableHttpResponse httpResponse = httpClient.execute(request);

    	    String response = EntityUtils.toString(httpResponse.getEntity());
        	
    	    ObjectMapper mapper = new ObjectMapper();
    	    
    	    res = mapper.readValue(response, Map.class);
    	}
    	return res;
    }
    
    public Map sendBiztalk(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	JSONObject json = new JSONObject();
    	
    	String token = (String) aMap.get("token");
    	String imageYn = (String) aMap.get("imageYn");
    	String buttonValue = (String) aMap.get("buttonValue");
    	
    	json.put("msgIdx", (String) aMap.get("msgIdx"));
    	json.put("countryCode", "82");
    	json.put("resMethod", "PUSH");
    	json.put("senderKey", this.profile);
    	json.put("tmpltCode", (String) aMap.get("tmpltCode"));
    	json.put("message", (String) aMap.get("message"));
    	json.put("recipient", (String) aMap.get("recipient"));
    	
    	if("Y".equals(imageYn)) {
    		json.put("messageType", "AI");
    	}
    	
    	if(!("".equals(buttonValue) || buttonValue == null)) {
    		json.put("attach", buttonValue);
    	}
    	
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPost request = new HttpPost(this.endpoint + "/v2/kko/sendAlimTalk");
	    StringEntity params = new StringEntity(json.toString(), "UTF-8");
	    request.addHeader("content-type", "application/json");
	    request.addHeader("bt-token", token);
	    request.setEntity(params);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());

	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
    	return res;
    }
    
    public Map getBiztalkSendResult(String token) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpGet request = new HttpGet(this.endpoint + "/v2/kko/getResultAll");
	    request.addHeader("content-type", "application/json");
	    request.addHeader("bt-token", token);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
	    
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
    	return res;
    }
    
    public Map getBiztalkPolling(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	String token = (String) aMap.get("token");
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpGet request = new HttpGet(this.endpoint + "/v2/kko/getResultPoll");
	    request.addHeader("content-type", "application/json");
	    request.addHeader("bt-token", token);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
	    
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
    	return res;
    }
    
    public Map getBiztalkAckResultPoll(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	String token = (String) aMap.get("token");
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpGet request = new HttpGet(this.endpoint + "/v2/kko/getResultPoll");
	    request.addHeader("content-type", "application/json");
	    request.addHeader("bt-token", token);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
	    
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
    	return res;
    }
}
