package com.lawzone.market.point.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lawzone.market.admin.dto.login.LoginDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.point.service.PointInfoCDTO;
import com.lawzone.market.point.service.PointService;
import com.lawzone.market.util.JsonUtils;
import com.lawzone.market.util.ParameterUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/point")
public class PointController {
	@Resource
	private SessionBean sessionBean;
	
	private final PointService pointService;
	
	//포인트적립
	@ResponseBody
	@PostMapping("/save")
	public String addPoint(HttpServletRequest request, @RequestBody(required = true) Map map) {
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO = (PointInfoCDTO) ParameterUtils.setDto(map, pointInfoCDTO, "insert", sessionBean);
		String _rtnMsg = this.pointService.addPoint(pointInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		return JsonUtils.returnValue("0000", _rtnMsg, rtnMap).toString();
	}
	
	//포인트조회
	@ResponseBody
	@PostMapping("/pointAmount")
	public String getPointAmount(HttpServletRequest request, @RequestBody(required = true) Map map) {
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO = (PointInfoCDTO) ParameterUtils.setDto(map, pointInfoCDTO, "insert", sessionBean);
		
		BigDecimal point = this.pointService.getPointAmount(pointInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("pointAmount",point);
		return JsonUtils.returnValue("0000", "조회 되었습니다.", rtnMap).toString();
	}
	
	//포인트사용
	@ResponseBody
	@PostMapping("/pointDifference")
	public String setPointDifference(HttpServletRequest request, @RequestBody(required = true) Map map) {
		PointInfoCDTO pointInfoCDTO = new PointInfoCDTO();
		pointInfoCDTO = (PointInfoCDTO) ParameterUtils.setDto(map, pointInfoCDTO, "insert", sessionBean);
		
		BigDecimal point = this.pointService.setPointDifference(pointInfoCDTO);
		
		Map rtnMap = new HashMap<>();
		rtnMap.put("pointAmount",point);
		return JsonUtils.returnValue("0000", "조회 되었습니다.", rtnMap).toString();
	}
}
