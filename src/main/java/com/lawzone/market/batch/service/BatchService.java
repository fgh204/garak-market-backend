package com.lawzone.market.batch.service;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Service;

import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.dao.CdDtlInfoDAO;
import com.lawzone.market.externalLink.util.AppPush;
import com.lawzone.market.externalLink.util.AppPushDTO;
import com.lawzone.market.order.service.ProductOrderService;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class BatchService {
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final AppPush appPush;
	private final UtilService utilService;
	private final CdDtlInfoDAO cdDtlInfoDAO;
	
	public void getPush() throws ClientProtocolException, IOException {
		String _date = sf.format(new Date());
		String _dateArray[] =  _date.split(" ");
		String _pushArray[];
		String _toDate = _dateArray[0];
		String title = "";
		String text = "";
		String slsDayYn = this.utilService.getSlsDayYn(_toDate);
		
		if("N".equals(slsDayYn)) {
			return;
		}
		
		List<CdDtlInfo> cdDtlInfo = cdDtlInfoDAO.findByIdCodeNoAndIdDtlCodeAndUseYn("5","001","Y");
		
		if(cdDtlInfo.size() == 0) {
			return;
		}else{
			_pushArray = cdDtlInfo.get(0).getDtlCodeText().split(";;");
			title = _pushArray[0];
			text = _pushArray[1];
		}
		
		AppPushDTO appPushDTO = new AppPushDTO();
		
		appPushDTO.setAllYn("Y");
		appPushDTO.setTitle(title);
		appPushDTO.setUrl("");
		ArrayList<String> push = new ArrayList<>();
		//push.add(0,"9bed6df2-9cc1-4c9e-925a-140ffebd0d44");
		//push.add(0,"9bed6df2-9cc1-4c9e-925a-140ffebd0d44, c91ea628-3e36-4d73-9f13-e82d609e5bca");
		//push.add(1,"c91ea628-3e36-4d73-9f13-e82d609e5bca");
		appPushDTO.setContent(text);
		appPushDTO.setPushList(push);
		
		this.appPush.getAppPush(appPushDTO);
	}
}
