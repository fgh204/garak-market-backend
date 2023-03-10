package com.lawzone.market.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lawzone.market.payment.controller.PaymentController;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SlackWebhook {
	 	@Value(value = "${slack.token}")
	    String token;
	    
	 	@Value(value = "${slack.channel.payment}")
	    String payment;
	    
	 	@Value(value = "${slack.channel.paymentcancel}")
	    String paymentcancel;
	    
	 	@Value(value = "${slack.channel.errorlog}")
	    String errorLog;
	    
	 	@Value(value = "${lzmarket.service}")
	    String svcGb;

	    public void postSlackMessage(String message, String sendGb){
	    	if("P".equals(svcGb)) {
	    		String channel = "";
		    	if("01".equals(sendGb)) {
		    		channel = payment;
		    	} else if("99".equals(sendGb)) {
		    		channel = errorLog;
		    	} else {
		    		channel = paymentcancel;
		    	}
		    	
		    	 RestTemplate restTemplate = new RestTemplate();

		         Map<String, Object> request = new HashMap<>();
		         //request.put("username", "slack메세지 명"); //slack bot name
		         request.put("text", message); //전송할 메세지
		         //request.put("icon_emoji", ":slack_emoji:"); //slack bot image

		         HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(request);

		         String url = channel; //복사한 Webhook URL 입력

		         restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	    	}
	    }
}
