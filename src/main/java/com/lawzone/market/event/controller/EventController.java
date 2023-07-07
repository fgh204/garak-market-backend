package com.lawzone.market.event.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.event.service.EventInfoCDTO;
import com.lawzone.market.event.service.EventInfoDTO;
import com.lawzone.market.event.service.EventMstService;
import com.lawzone.market.event.service.EventProductCDTO;
import com.lawzone.market.event.service.EventProductInfoListDTO;
import com.lawzone.market.notice.service.NoticeInfoCDTO;
import com.lawzone.market.notice.service.NoticeInfoDTO;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.notice.service.NoticeService;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductCDTO;
import com.lawzone.market.product.service.ProductInfoListDTO;
import com.lawzone.market.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/event")
public class EventController {
	private final NoticeService noticeService;
	private final EventMstService eventMstService;
	private final ProductService productService;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addEventInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		NoticeInfoDTO noticeInfoDTO = new NoticeInfoDTO();
		noticeInfoDTO = (NoticeInfoDTO) ParameterUtils.setDto(map, noticeInfoDTO, "insert", sessionBean);
		
		Map noticeMap = new HashMap<>();
		
		this.noticeService.addNoticeInfo(noticeInfoDTO);
		
		return JsonUtils.returnValue("0000", "저장되었습니다.", noticeMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/noticeInfoList")
	public String getNoticeInfoList(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		NoticeInfoCDTO noticeInfoCDTO = new NoticeInfoCDTO();
		noticeInfoCDTO = (NoticeInfoCDTO) ParameterUtils.setDto(map, noticeInfoCDTO, "insert", sessionBean);
		
		if("".equals(noticeInfoCDTO.getMaxPageCount()) || noticeInfoCDTO.getMaxPageCount() == null) {
			noticeInfoCDTO.setMaxPage("10");
    	} else {
    		noticeInfoCDTO.setMaxPage(noticeInfoCDTO.getMaxPageCount());
    	}
		
		if("".equals(noticeInfoCDTO.getPageCount()) || noticeInfoCDTO.getPageCount() == null) {
			noticeInfoCDTO.setPageCnt("0");
    	} else {
    		noticeInfoCDTO.setPageCnt(noticeInfoCDTO.getPageCount());
    	}
		
		if("".equals(noticeInfoCDTO.getPageCnt()) || noticeInfoCDTO.getPageCnt() == null) {
			noticeInfoCDTO.setPageCnt("0");
    	}else {
    		int _currentCnt = Integer.parseInt(noticeInfoCDTO.getPageCnt());
    		int _limitCnt = Integer.parseInt(noticeInfoCDTO.getMaxPage());
    		
    		noticeInfoCDTO.setPageCnt(Integer.toString(_currentCnt * _limitCnt));
    	}
		
		noticeInfoCDTO.setDateGb("01");
		noticeInfoCDTO.setPostYn("Y");
		noticeInfoCDTO.setNoticeCfcd("000");
		noticeInfoCDTO.setSearchGb("000");
		
		List<PageInfoDTO> pageInfo = this.noticeService.getNoticeListPageInfo2(noticeInfoCDTO);
		List<NoticeInfoDTO> noticeInfoList = this.noticeService.getNoticeList(noticeInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("noticeInfoList", noticeInfoList);
		rtnMap.put("paging", pageInfo.get(0));
		
		return JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/eventInfoList")
	public String getEventInfoList(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		EventInfoCDTO eventInfoCDTO = new EventInfoCDTO();
		eventInfoCDTO = (EventInfoCDTO) ParameterUtils.setDto(map, eventInfoCDTO, "insert", sessionBean);
		
		if("".equals(eventInfoCDTO.getMaxPageCount()) || eventInfoCDTO.getMaxPageCount() == null) {
			eventInfoCDTO.setMaxPage("10");
    	} else {
    		eventInfoCDTO.setMaxPage(eventInfoCDTO.getMaxPageCount());
    	}
		
		if("".equals(eventInfoCDTO.getPageCount()) || eventInfoCDTO.getPageCount() == null) {
			eventInfoCDTO.setPageCnt("0");
    	} else {
    		eventInfoCDTO.setPageCnt(eventInfoCDTO.getPageCount());
    	}
		
		if("".equals(eventInfoCDTO.getPageCnt()) || eventInfoCDTO.getPageCnt() == null) {
			eventInfoCDTO.setPageCnt("0");
    	}else {
    		int _currentCnt = Integer.parseInt(eventInfoCDTO.getPageCnt());
    		int _limitCnt = Integer.parseInt(eventInfoCDTO.getMaxPage());
    		
    		eventInfoCDTO.setPageCnt(Integer.toString(_currentCnt * _limitCnt));
    	}
		
		eventInfoCDTO.setSearchGb("000");
		
		if("".equals(eventInfoCDTO.getEventCfcd()) || eventInfoCDTO.getEventCfcd() == null) {
			eventInfoCDTO.setEventCfcd("000");
		}
		
		List<AdminPageInfoDTO> paging = this.eventMstService.getEventListPageInfo2(eventInfoCDTO);
		List<EventInfoDTO> eventInfoList = this.eventMstService.getEventListInfo(eventInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("eventInfoList", eventInfoList);
		rtnMap.put("paging", paging.get(0));
		
		return JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/eventDetailInfo")
	public String getEventDetailInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException, IllegalAccessException, InvocationTargetException {
		EventInfoCDTO eventInfoCDTO = new EventInfoCDTO();
		eventInfoCDTO = (EventInfoCDTO) ParameterUtils.setDto(map, eventInfoCDTO, "insert", sessionBean);
		
		eventInfoCDTO.setSearchGb("000");
		eventInfoCDTO.setEventCfcd("000");
		eventInfoCDTO.setPageCnt("0");
		eventInfoCDTO.setMaxPage("5");
		//이벤트정보
		List<EventInfoDTO> eventInfoList = this.eventMstService.getEventListInfo(eventInfoCDTO);
		
		//상품목록정보
		ProductCDTO productCDTO = new ProductCDTO();
		productCDTO.setEventId(eventInfoCDTO.getEventId());
		productCDTO.setUserId(eventInfoCDTO.getUserId());
		productCDTO.setPageCount("0");
		productCDTO.setMaxPageCount("5");
		productCDTO.setIsSoldOutHidden(false);
		productCDTO.setProductSortCode("001");
		
		List<EventProductInfoListDTO> productList = this.productService.getEventProductList(productCDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("eventInfo", eventInfoList.get(0));
		
		if(productList.size() > 0) {
			rtnMap.put("eventProductInfoList", productList);
		}
		
		//rtnMap.put("paging", paging.get(0));
		
		return JsonUtils.returnValue("0000", "조회되었습니다.", rtnMap).toString();
	}
	
	@ResponseBody
	@PostMapping("/mainEventInfoList")
	public String getMainEventInfoList(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
		EventInfoCDTO eventInfoCDTO = new EventInfoCDTO();
		eventInfoCDTO = (EventInfoCDTO) ParameterUtils.setDto(map, eventInfoCDTO, "insert", sessionBean);
		
		if(eventInfoCDTO.getEventCfcd() == null || "".equals(eventInfoCDTO.getEventCfcd())) {
			eventInfoCDTO.setEventCfcd("001");
		}
		
		if(eventInfoCDTO.getEventStateCode() == null || "".equals(eventInfoCDTO.getEventStateCode())) {
			eventInfoCDTO.setEventStateCode("999");
		}
		
		if(eventInfoCDTO.getSearchDateGb() == null || "".equals(eventInfoCDTO.getSearchDateGb())) {
			eventInfoCDTO.setSearchDateGb("999");
		}
		
		eventInfoCDTO.setPageCnt("0");
		eventInfoCDTO.setMaxPage("50");
		
		//List<AdminPageInfoDTO> paging = this.eventMstService.getEventListPageInfo(eventInfoCDTO);
		List<EventInfoDTO> eventInfoList = this.eventMstService.getEventListInfo(eventInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("mainEventInfoList", eventInfoList);
		
		return JsonUtils.returnValue("0000", "조회되었습니다", rtnMap).toString();
	}
}
