package com.lawzone.market.externalLink.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
@Component
public class AppPush {
	 private String appId = "ffd2c22d-f049-4c43-84bd-2528f5e1f61a";
	 private String restapiKey = "ZTljYzA2YzUtMDkxYS00ZTcwLWExZjgtZjY2NmI4ZDYxOWEx";
	 @Value(value = "${lzmarket.service}")
	 private String svcGb;
	 
	 public void getAppPush(AppPushDTO appPushDTO) throws ClientProtocolException, IOException{
		 if("P".equals(svcGb)) {
	    	JSONObject json = new JSONObject();
	    	JSONObject dataJson = new JSONObject();
	    	JSONObject titleJson = new JSONObject();
	    	JSONObject contentsJson = new JSONObject();
	    	JSONObject urlJson = new JSONObject();
	    	JSONObject iosAttachmentsJson = new JSONObject();
			json.put("app_id", this.appId);
			
			if("Y".equals(appPushDTO.getAllYn())) {
				json.put("included_segments", "All");//전체발송여부
			}else {
				json.put("included_segments", "");//전체발송여부
			}
			
			ArrayList<String> player_id_array = new ArrayList<>();
			
			List pushList = appPushDTO.getPushList();
			
			for(int i = 0; i < pushList.size(); i ++) {
				player_id_array.add(i, pushList.get(i).toString());
			}
			
			json.put("include_player_ids", player_id_array);//수신자
			
			//푸시 타이틀
			titleJson.put("en", appPushDTO.getTitle());
			json.put("headings", titleJson.toString());
			//푸시 내용
			contentsJson.put("en", appPushDTO.getContent());
			json.put("contents", contentsJson.toString());
			//url
			if(!"".equals(appPushDTO.getUrl())) {
				urlJson.put("custom_url", appPushDTO.getUrl());
			}
			json.put("data", urlJson);
			//상태바 표시 icon
			json.put("small_icon", "icon_48");
			//안드로이드 푸시 이미지
			json.put("big_picture", "");
			//iOS 푸시 이미지
			json.put("ios_attachments", iosAttachmentsJson.put("id1", ""));
			//ios badge counter
			json.put("ios_badgeType", "Increase");
			//ios badge counter by 1
			json.put("ios_badgeCount", 1);
	    	
			dataJson.put("data", json);
			
	    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			
			HttpPost request = new HttpPost("https://onesignal.com/api/v1/notifications");
		    StringEntity params = new StringEntity(json.toString(), "UTF-8");
		    request.addHeader("content-type", "application/json");
		    request.addHeader("Authorization", "Basic " + this.restapiKey);
		    request.setEntity(params);
		    
		    CloseableHttpResponse httpResponse = httpClient.execute(request);

		    String response = EntityUtils.toString(httpResponse.getEntity());
		 }
	    }
}
