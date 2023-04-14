package com.lawzone.market.notice.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lawzone.market.config.SessionBean;
import com.lawzone.market.notice.service.NoticeInfoCDTO;
import com.lawzone.market.notice.service.NoticeInfoDTO;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;
import com.lawzone.market.notice.service.NoticeService;

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
}
