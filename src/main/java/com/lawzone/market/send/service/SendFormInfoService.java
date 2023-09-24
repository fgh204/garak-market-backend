package com.lawzone.market.send.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawzone.market.common.service.CommonService;
import com.lawzone.market.externalLink.util.BizTalkUtils;
import com.lawzone.market.send.dao.SendFormInfoDAO;
import com.lawzone.market.send.dao.SendInfoDAO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SendFormInfoService {
	private final SendFormInfoDAO sendFormInfoDAO;
	private final SendInfoDAO sendInfoDAO;
	private final BizTalkUtils bizTalkUtils;
	private final CommonService commonService;
	
	@Transactional(rollbackFor = Exception.class)
	public void sendBiztalkInfo(SendFormInfoCDTO sendFormInfoCDTO) throws ClientProtocolException, IOException {
		String sendFormCode = sendFormInfoCDTO.getSendFormCode();
		String phoneNumber = sendFormInfoCDTO.getRecipient();
		String recipient = sendFormInfoCDTO.getRecipient();
		
		if("".equals(phoneNumber) || phoneNumber == null) {
			return;
		}
		
		List<SendFormInfo> sendFormInfo = this.sendFormInfoDAO.findBySendFormCodeAndUseYn(sendFormCode, "Y");
		
		Map sendBiztalkMap = new HashMap<>();
		
		if(sendFormInfo.size() > 0) {
			String uuid = UUID.randomUUID().toString();
			String token = this.commonService.setBiztalkToken();
			String sendText = sendFormInfo.get(0).getSendText();
			SendInfo sendInfo = new SendInfo();

			Map sendMap = new HashMap<>();
			sendMap.put("productName", sendFormInfoCDTO.getProductName());
			sendMap.put("orderNo", sendFormInfoCDTO.getOrderNo());
			sendMap.put("totalAmount", sendFormInfoCDTO.getTotalAmount());
			sendMap.put("cancelledPaymentAmount", sendFormInfoCDTO.getCancelledPaymentAmount());
			sendMap.put("deliveryName", sendFormInfoCDTO.getDeliveryName());
			sendMap.put("bookId", sendFormInfoCDTO.getBookId());
			sendMap.put("subPoint", sendFormInfoCDTO.getSubPoint());
			sendMap.put("addPoint", sendFormInfoCDTO.getAddPoint());
			sendMap.put("subDate", sendFormInfoCDTO.getSubDate());
			sendMap.put("sendDate", sendFormInfoCDTO.getSendDate());
			
			sendText = getSendText(sendText, sendMap);
			
			if("".equals(sendText)) {
				return;
			}
			
			sendInfo.setSendId(uuid);
			sendInfo.setSendFormCode(sendFormInfo.get(0).getSendFormCode());
			sendInfo.setSendFormName(sendFormInfo.get(0).getSendFormName());
			sendInfo.setPushSvcFormCode(sendFormInfo.get(0).getPushSvcFormCode());
			sendInfo.setPushSvcFormName(sendFormInfo.get(0).getPushSvcFormName());
			sendInfo.setSendText(sendText);
			sendInfo.setPhone(recipient);
			sendInfo.setSendYn("N");
			sendInfo.setRegResponseCode("0000");			
			sendInfo.setSendResponseCode("0000");
			sendInfo.setReceiveUserId(sendFormInfoCDTO.getReceiveUserId());
			
			sendInfoDAO.save(sendInfo);
			
			sendBiztalkMap.put("token", token);
			sendBiztalkMap.put("msgIdx", uuid);
			sendBiztalkMap.put("tmpltCode", sendFormInfo.get(0).getPushSvcFormCode());
			sendBiztalkMap.put("message", sendText);
			sendBiztalkMap.put("recipient", recipient);
			sendBiztalkMap.put("imageYn", sendFormInfo.get(0).getImageYn());
			sendBiztalkMap.put("buttonValue", sendFormInfo.get(0).getFormItm1Value());
			
			Map sendBiztalk = this.bizTalkUtils.sendBiztalk(sendBiztalkMap);
			String sendBiztalkResponseCode = (String) sendBiztalk.get("responseCode");
			
			if( "1000".equals(sendBiztalkResponseCode)) {
				Map getBiztalkSendResult = this.bizTalkUtils.getBiztalkSendResult(token);
				
				String biztalkSendResultResponseCode = (String) getBiztalkSendResult.get("responseCode");
				String sendId = "";
				String resultCode = "";
				if("1000".equals(biztalkSendResultResponseCode)) {
					List sendList = (List) getBiztalkSendResult.get("response");
					Map sendListMap = new HashMap<>();
					for(int i = 0; i < sendList.size(); i++) {
						sendListMap = new HashMap<>();
						sendListMap = (Map) sendList.get(i);
						
						sendId = (String) sendListMap.get("msgIdx");
						resultCode = (String) sendListMap.get("resultCode");
						
						List<SendInfo> resltSendInfo = this.sendInfoDAO.findBySendIdAndSendYn(sendId, "N");
						
						resltSendInfo.get(0).setSendResponseCode(resultCode);
						resltSendInfo.get(0).setRegResponseCode(biztalkSendResultResponseCode);
						
						if("1000".equals(resultCode)) {
							resltSendInfo.get(0).setSendYn("Y");
						}
					}
				}
			} else {
				List<SendInfo> resltSendInfo = this.sendInfoDAO.findBySendId(uuid);
				resltSendInfo.get(0).setRegResponseCode(sendBiztalkResponseCode);
			}
		}
	}
	
	public String getSendText(String sendText, Map sendMap) {
		String msgVlaue = "";
		String replaceValue = "";
		String objcNm = "";
		String sendPsblYn = "Y";
		int stratLength = 0;
		int endLength = 0;
		int tcnt = 0;
		if(!("".equals(sendText) || sendText == null)){
			msgVlaue = sendText;
			stratLength = 0;
			tcnt = 0;
			while (stratLength > -1) {
				if(tcnt > 10) {
					break;
				}
				tcnt++;
				replaceValue = "";
				stratLength = msgVlaue.indexOf("#{");
				endLength = msgVlaue.indexOf("}");
				
				if(stratLength > -1) {
					objcNm = msgVlaue.substring(stratLength + 2, endLength);
//					Logger.info(this, "objcNm====" + objcNm);
					replaceValue = String.valueOf(sendMap.get(objcNm));
					
					if("".equals(replaceValue) || sendMap.get(objcNm) == null) {
						sendPsblYn = "N";
						msgVlaue = "";
						break;
					}else {
						msgVlaue = msgVlaue.replace("#{" + objcNm + "}", replaceValue);
					}
				}
			}
		}
		return msgVlaue;
	}
}
