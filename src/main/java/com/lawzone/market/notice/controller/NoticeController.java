package com.lawzone.market.notice.controller;

import java.io.IOException;
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
import com.lawzone.market.notice.service.NoticeInfoCDTO;
import com.lawzone.market.notice.service.NoticeInfoDTO;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.notice.service.NoticeService;
import com.lawzone.market.product.service.PageInfoDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/notice")
public class NoticeController {
	private final NoticeService noticeService;
	
	@Resource
	private SessionBean sessionBean;
	
	@ResponseBody
	@PostMapping("/create")
	public String addNoticeInfo(HttpServletRequest request, @RequestBody(required = true) Map map) throws ClientProtocolException, IOException {
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
}
