package com.lawzone.market.externalLink.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BootpayUtils {
	@Value("${bootpay.apikey}")
    private String apikey;

    @Value("${bootpay.privatekey}")
    private String privateKey;
    
    public Map getAccessToken() {      
        Map res = new HashMap<>();
    	
        //log.info("apikey=============" + apikey);
        //log.info("privateKey=========" + privateKey);
        
    	try {
        	Bootpay bootpay = new Bootpay(apikey, privateKey);
            res = bootpay.getAccessToken();
            
            //log.info("res=========" + res);
            
            if(res.get("error_code") == null) {
                System.out.println("goGetToken success: " + res);
            } else {
                System.out.println("goGetToken false: " + res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return res;
    }
    
    public Map getBootpayReceipt(String _receiptId){
    	Map res = new HashMap<>();
    	try {
    		Bootpay bootpay = new Bootpay(apikey, privateKey);
    	    HashMap token = bootpay.getAccessToken();
    	    if(token.get("error_code") != null) { //failed
    	        return token;
    	    }
    	    String receiptId = _receiptId; 
    	    res = bootpay.getReceipt(receiptId);
    	    
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	return res;
    }
    
    public Map getBootpayConfirm(String _receiptId){
    	Map res = new HashMap<>();
    	try {
    		Bootpay bootpay = new Bootpay(apikey, privateKey);
    	    HashMap token = bootpay.getAccessToken();
    	    if(token.get("error_code") != null) { //failed
    	        return token;
    	    }
    	    String receiptId = _receiptId; 
    	    res = bootpay.confirm(receiptId);
    	    
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	return res;
    }
    
    public Map getBootpayReceiptCancel(String _receiptId, String _userName
    								, String _message, Double _price){
    	Map res = new HashMap<>();
    	try {
    		Bootpay bootpay = new Bootpay(apikey, privateKey);
    	    HashMap token = bootpay.getAccessToken();
    	    if(token.get("error_code") != null) { //failed
    	        return token;
    	    }
    	
    	    String receiptId = _receiptId;
    	    
    	    Cancel cancel = new Cancel();
    	    cancel.receiptId = receiptId;
    	    cancel.cancelUsername = _userName;
    	    cancel.cancelMessage = _message;
    	    if(_price != null) {
    	    	cancel.cancelPrice = _price;
    	    }
    	    
    	    res = bootpay.receiptCancel(cancel);
    	    
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	return res;
    }
}
