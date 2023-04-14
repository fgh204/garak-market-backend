package com.lawzone.market.externalLink.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawzone.market.admin.service.OrderBookIdInfoDTO;
import com.lawzone.market.admin.service.TodayProductDTO;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.sf.json.JSONObject;

@Slf4j
@Component
public class TodayUtils {
	@Value("${today.clientid}")
    private String clientid;
    
    @Value("${today.secretkey}")
    private String secretkey;
    
    @Value("${today.endpoint}")
    private String endpoint;
    
    @Value("${today.version}")
    private String version;
    
    @Value(value = "${lzmarket.service}")
    private String svcGb;
    
    @Value(value = "${today.service}")
    private String service;
    
    public Map getTodayToken() throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	
    	//JSONObject json = new JSONObject();
    	
    	Map requestMap = new HashMap<>();
		
    	requestMap.put("grant_type", "password");
    	requestMap.put("username", this.clientid);
    	requestMap.put("password", this.secretkey);
    	requestMap.put("scope", "openid email profile");
    	requestMap.put("client_id", this.service);
    	
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPost request = new HttpPost("https://auth.vtov.studio/auth/realms/vtov/protocol/openid-connect/token");
	    //StringEntity params = new StringEntity(json.toString(), "UTF-8");
	    request.addHeader("content-type", "application/x-www-form-urlencoded");
	    List paramList = convertParam(requestMap);
	    //request.addHeader("Authorization", "Bearer " + this.token);
	    request.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
    	
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
	    
    	//return (String) res.get("access_token");
	    return res;
    }
    
    public Map getTodayTokenByRefreshToken(String refreshToken) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	
    	//JSONObject json = new JSONObject();
    	
    	Map requestMap = new HashMap<>();
		
    	requestMap.put("grant_type", "refresh_token");
    	requestMap.put("refresh_token", refreshToken);
    	requestMap.put("scope", "openid email profile");
    	requestMap.put("client_id", this.service);
    	
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPost request = new HttpPost("https://auth.vtov.studio/auth/realms/vtov/protocol/openid-connect/token");
	    //StringEntity params = new StringEntity(json.toString(), "UTF-8");
	    request.addHeader("content-type", "application/x-www-form-urlencoded");
	    List paramList = convertParam(requestMap);
	    //request.addHeader("Authorization", "Bearer " + this.token);
	    request.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
    	
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
	    
    	//return (String) res.get("access_token");
	    return res;
    }
    
    private static List convertParam(Map<String, Object> params) {
        List paramList = new ArrayList<>();

        if(params != null) {
            for(Object item : params.keySet()) {
                String key = String.valueOf(item);
                paramList.add(new BasicNameValuePair(key, params.get(key).toString()));
            }
        }

        return paramList;
    }
    
    public Map addDeliveryReception(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	
    	JSONObject json = new JSONObject();
    	JSONArray jsonArray = new JSONArray();
    	JSONObject jsonReceiver = new JSONObject();
    	JSONObject jsonSender = new JSONObject();
    	JSONObject jsonProducts = new JSONObject();
    	JSONObject jsonReturn = new JSONObject();
    	
    	jsonSender.put("name", "도매도_" + aMap.get("sendName"));
    	jsonSender.put("phone", aMap.get("sendPhone"));
    	jsonSender.put("address", aMap.get("sendAddress"));
    	jsonSender.put("postal_code", aMap.get("sendPostalCode"));
    	
    	jsonReceiver.put("name", aMap.get("receiverName"));
    	jsonReceiver.put("phone", aMap.get("receiverPhone"));
    	jsonReceiver.put("address", aMap.get("receiverAddress"));
    	jsonReceiver.put("postal_code", aMap.get("receiverPostalCode"));
    	jsonReceiver.put("access_method", aMap.get("accessMethod"));
    	jsonReceiver.put("preference", aMap.get("receiverMemo"));
    	
    	List<TodayProductDTO> productList = (List<TodayProductDTO>) aMap.get("productList");
    	int _productCnt = productList.size();
    	
    	for(int i = 0; i < _productCnt; i++) {
    		jsonProducts = new JSONObject();
    		
    		jsonProducts.put("name", productList.get(i).getProductName());
        	jsonProducts.put("category", productList.get(i).getProductCategory());
        	jsonProducts.put("fragile", false);
        	jsonProducts.put("price", productList.get(i).getProductPrice());
        	jsonProducts.put("seller_name", productList.get(i).getProductSellerName());
        	jsonProducts.put("client_product_id", productList.get(i).getProductId());
        	jsonProducts.put("count", productList.get(i).getProductCount());
        	jsonProducts.put("customer_request_time", productList.get(i).getOrderDttm());
        	
        	jsonArray.add(jsonProducts);
    	}
    	
    	//jsonProducts.put("picking_location", "B구역 3번");
    	//jsonProducts.put("count", 4);
    	//jsonProducts.put("customer_request_time", "2021-04-14T12:56:00+09:00");
    	
    	
    	
    	jsonReturn.put("invoice_number", "899034025615");
    	
		json.put("broker_client_id", "");
		json.put("skip_take_out", true);
		json.put("shipping_place_id", aMap.get("shippingPlaceId"));
		json.put("client_order_id", aMap.get("orderNo"));
		//json.put("client_shipping_id", "970611JY");
		//json.put("developer_payload", "");
		//json.put("note", "비고");
		//json.put("returning_info", jsonReturn);
		json.put("products", jsonArray);
		json.put("sender", jsonSender);
		json.put("receiver", jsonReceiver);
    	
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPost request = new HttpPost(this.endpoint + "/order/" + this.version + "/clients/" + this.clientid + "/orders");
	    StringEntity params = new StringEntity(json.toString(), "UTF-8");
	    request.addHeader("content-type", "application/json");
	    request.addHeader("Authorization", "Bearer " + aMap.get("access_token"));
	    request.setEntity(params);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
    	
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
	    
    	return res;
    }
    
    public Map getDeliveryShippingList(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	List _res = new ArrayList<>();

    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpGet request = new HttpGet(this.endpoint + "/client/" + this.version + "/clients/" + this.clientid + "/shipping-places");
	    request.addHeader("Authorization", "Bearer " + aMap.get("access_token"));
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
	    
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
	    
	    return res;
    }
    
    public Map getDeliveryStatus(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	List _res = new ArrayList<>();

    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpGet request = new HttpGet(this.endpoint + "/order/" + this.version + "/clients/" + this.clientid + "/orders/" + aMap.get("invoice_number") + "?type=invoice_number");
	    request.addHeader("Authorization", "Bearer " + aMap.get("access_token"));
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
	    
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);

	    return res;
    }
    
    public String getDeliveryInvoiceImeag(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	List _res = new ArrayList<>();

    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpGet request = new HttpGet(this.endpoint + "/invoice/v2/clients/" + this.clientid + "/orders/" + aMap.get("order_id") + "/invoice");
	    request.addHeader("Authorization", "Bearer " + aMap.get("access_token"));
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);
	    
	    HttpEntity response2 = httpResponse.getEntity();
	    InputStream is  = response2.getContent();
	    
	    byte[] sourceBytes = IOUtils.toByteArray(is);
	    
	    String imgEncodedString = Base64.encodeBase64String(sourceBytes);

	    return imgEncodedString;
    }
    
    public Map getDeliveryCancel(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	
    	JSONObject json = new JSONObject();
    	
		json.put("cancel_reason", "고객 취소");

    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPost request = new HttpPost(this.endpoint + "/order/" + this.version + "/clients/" + this.clientid + "/orders/" + aMap.get("invoice_number") + "/cancel");
	    StringEntity params = new StringEntity(json.toString(), "UTF-8");
	    request.addHeader("content-type", "application/json");
	    request.addHeader("Authorization", "Bearer " + aMap.get("access_token"));
	    request.setEntity(params);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
    	
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
	    
    	return res;
    }
    
    public Map getvalidateAddress(Map aMap) throws ClientProtocolException, IOException{
    	Map res = new HashMap<>();
    	
    	JSONObject json = new JSONObject();
    	
		json.put("address", aMap.get("address"));
		json.put("postal_code", aMap.get("postal_code"));

    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPost request = new HttpPost(this.endpoint + "/order/" + this.version + "/clients/" + this.clientid + "/validate-address");
	    StringEntity params = new StringEntity(json.toString(), "UTF-8");
	    request.addHeader("content-type", "application/json");
	    request.addHeader("Authorization", "Bearer " + aMap.get("access_token"));
	    request.setEntity(params);
	    
	    CloseableHttpResponse httpResponse = httpClient.execute(request);

	    String response = EntityUtils.toString(httpResponse.getEntity());
    	
	    ObjectMapper mapper = new ObjectMapper();
	    
	    res = mapper.readValue(response, Map.class);
	    
    	return res;
    }
}
