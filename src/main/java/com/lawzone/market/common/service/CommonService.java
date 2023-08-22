package com.lawzone.market.common.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.client.ClientProtocolException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.admin.dao.AdminJdbcDAO;
import com.lawzone.market.admin.dao.SlsDateInfoDAO;
import com.lawzone.market.admin.dto.order.AdminOrderCDTO;
import com.lawzone.market.admin.dto.user.AdminUserCDTO;
import com.lawzone.market.admin.service.SlsDateInfo;
import com.lawzone.market.admin.service.SlsDateInfoDTO;
import com.lawzone.market.cart.service.CartInfo;
import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.dao.BoilerplateInfoDAO;
import com.lawzone.market.common.dao.CdDtlInfoDAO;
import com.lawzone.market.common.dao.CdDtlInfoJdbcDAO;
import com.lawzone.market.common.dao.CommonJdbcDAO;
import com.lawzone.market.externalLink.util.BizTalkUtils;
import com.lawzone.market.externalLink.util.TodayUtils;
import com.lawzone.market.product.service.TagInfo;
import com.lawzone.market.util.DateUtils;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommonService {
	private final BoilerplateInfoDAO boilerplateInfoDAO;
	private final CommonJdbcDAO commonJdbcDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	private final SlsDateInfoDAO slsDateInfoDAO;
	private final DateUtils dateUtils;
	private final CdDtlInfoDAO cdDtlInfoDAO;
	private final TodayUtils todayUtils;
	private final BizTalkUtils bizTalkUtils;
	private final AdminJdbcDAO adminJdbcDAO;
	private final CdDtlInfoJdbcDAO cdDtlInfoJdbcDAO;
	@Transactional(rollbackFor = Exception.class)
	public String addBoilerplateInfo(BoilerplateInfoDTO boilerplateInfoDTO) {
		//기존 상용구 검색
		String msgCd = "";
		Optional<BoilerplateInfo> _boilerplateInfo = this.boilerplateInfoDAO.findByUserIdAndBoilerplateName(boilerplateInfoDTO.getUserId(), boilerplateInfoDTO.getBoilerplateName());
		
		if(_boilerplateInfo.isPresent()) {
			boilerplateInfoDTO.setBoilerplateNumber( new BigDecimal(_boilerplateInfo.get().getBoilerplateNumber()));
		}
		
		if("".equals(boilerplateInfoDTO.getUseYn()) || boilerplateInfoDTO.getUseYn() == null) {
			boilerplateInfoDTO.setUseYn("Y");
		}
		
		List<BoilerplateInfo> _boilerplateUseInfo = this.boilerplateInfoDAO.findByUserIdAndUseYn(boilerplateInfoDTO.getUserId(), "Y");
		
		boolean flag = false;
		
		for(int i = 0; i < _boilerplateUseInfo.size(); i++) {
			if(		boilerplateInfoDTO.getBoilerplateNumber() != null &&
					_boilerplateUseInfo.get(i).getBoilerplateNumber() == boilerplateInfoDTO.getBoilerplateNumber().longValue()
					) {
				flag = true;
				break;
			}
		}
		
		if( 
				_boilerplateUseInfo.size() >= 5 && !flag
				) {
			msgCd = "9999";
		}else {
			BoilerplateInfo boilerplateInfo = new BoilerplateInfo();
			boilerplateInfo = modelMapper.map(boilerplateInfoDTO, BoilerplateInfo.class);
			
			this.boilerplateInfoDAO.save(boilerplateInfo);
			msgCd = "0000";
		}
		return msgCd;
	}
	
	public List getBoilerplateList(BoilerplateInfoDTO boilerplateInfoDTO) {
		return this.boilerplateInfoDAO.findByUserIdAndUseYn(boilerplateInfoDTO.getUserId(), "Y");
	}
	
	//메뉴정보
	public List getMenuInfo(String userId) {
		String sql = this.commonJdbcDAO.menuInfo();
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, userId);
		_queryValue.add(1, userId);
		_queryValue.add(2, userId);
		_queryValue.add(3, userId);
		
		MenuDTO menuDTO = new MenuDTO();
		
		return this.utilService.getQueryString(sql,menuDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void addDateInfoList(AdminOrderCDTO adminOrderCDTO) throws ParseException {
		String strYear = adminOrderCDTO.getYear();
		String strDate = strYear + "-01-01";
		String toDate = "";
		String slsDayYn = "Y";
		String fstdyYn = "N";
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        int intYear = Integer.parseInt(strYear);
        Date date = new Date();
		String _date = sf.format(date);
        
		SlsDateInfo _slsDateInfo = new SlsDateInfo();
		
        int allDay = 365;
        int dayWeek = 0;
        String yearWeek = "";
        
        if(intYear%4 == 0){
        	allDay = 366;
        }
        
        Optional<SlsDateInfo> slsDateInfo = this.slsDateInfoDAO.findBySlsDate(date);
        
        if(slsDateInfo.isEmpty()) {
        	Optional<CdDtlInfo> cdDtlInfo = this.cdDtlInfoDAO.findByIdCodeNoAndIdDtlCode("4", strYear);
        	
        	String _chkDate = cdDtlInfo.get().getDtlCodeText();
        	
	        for(int i = 0; i < allDay; i++) {
	        	_slsDateInfo = new SlsDateInfo();
	        	date = new Date(sf.parse(strDate).getTime());
	        	dayWeek = this.dateUtils.getDayOfWeek(date);
	        	yearWeek = this.dateUtils.getWeek(dateUtils.dateToString(date));
	        	toDate = this.dateUtils.dateToString(date);
	        	_slsDateInfo.setSlsDate(date);
	        	
	        	slsDayYn = "Y";
	        	
	        	if(dayWeek == 1 || dayWeek == 7) {
	        		slsDayYn = "N";
	        	}
	        	
	        	if("Y".equals(slsDayYn)) {
	        		if(_chkDate.indexOf(strDate) > -1) {
	        			slsDayYn = "N";
	        		}
	        	}
	        	
	        	_slsDateInfo.setSlsDayYn(slsDayYn);
	        	_slsDateInfo.setFstdyYn(fstdyYn);
	        	_slsDateInfo.setWdcd(new BigDecimal(dayWeek));
	        	_slsDateInfo.setWeekDgreYymm(toDate.substring(0,4) + toDate.substring(5,7));
	        	_slsDateInfo.setCalndWeekDgre(new BigDecimal(yearWeek));
	            
	        	this.slsDateInfoDAO.save(_slsDateInfo);
	            
	        	strDate = DateUtils.getNextDate(date);
	        }
        }
	}
	
	public List getDateInfoList(AdminOrderCDTO adminOrderCDTO) {
		String slsDayYn = adminOrderCDTO.getSlsDayYn();
		
		String sql = this.commonJdbcDAO.dateInfoList(slsDayYn);
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, adminOrderCDTO.getYear() + adminOrderCDTO.getMonth());
		if(slsDayYn != null) {
			_queryValue.add(1, slsDayYn);
		}

		SlsDateInfoDTO slsDateInfoDTO = new SlsDateInfoDTO();
		return this.utilService.getQueryString(sql,slsDateInfoDTO,_queryValue);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void modifyDateInfoList(SlsDateInfoDTO slsDateInfoDTO) throws ParseException {		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		
		Optional<SlsDateInfo> slsDateInfo = this.slsDateInfoDAO.findBySlsDate(new Date(sf.parse(slsDateInfoDTO.getSlsDate()).getTime()));
		
		slsDateInfo.get().setSlsDayYn(slsDateInfoDTO.getSlsDayYn().toString());
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String setTodayToken() throws ClientProtocolException, IOException {
		Map todayTokenMap = this.todayUtils.getTodayToken();
		
		String accessToken = (String) todayTokenMap.get("access_token");
		String refreshToken = (String) todayTokenMap.get("refresh_token");
		
		String _sql = this.adminJdbcDAO.addExternalLinkInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, "00001");
		_queryValue.add(1, accessToken);
		_queryValue.add(2, refreshToken);
		_queryValue.add(3, "99999999");
		_queryValue.add(4, "99999999");
		_queryValue.add(5, accessToken);
		_queryValue.add(6, refreshToken);
		
		this.utilService.getQueryStringUpdate(_sql, _queryValue);
		
		return accessToken;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String setBiztalkToken() throws ClientProtocolException, IOException {
		String _checkSql = this.commonJdbcDAO.externalLinkInfoByBiztalk();
		String useYn = "N";
		String accessToken = "";
		
		ArrayList<String> _checkQueryValue = new ArrayList<>();
		_checkQueryValue.add(0, "00002");
		
		ExternalLinkInfoDTO externalLinkInfoDTO = new ExternalLinkInfoDTO();
		List<ExternalLinkInfoDTO> externalLinkInfoList = this.utilService.getQueryString(_checkSql ,externalLinkInfoDTO, _checkQueryValue);
		
		if(externalLinkInfoList.size() > 0) {
			useYn = externalLinkInfoList.get(0).getUseYn();
			accessToken = externalLinkInfoList.get(0).getAccessToken();
		}
		
		if("N".equals(useYn)) {
			Map biztalkTokenMap = this.bizTalkUtils.getBiztalkToken();
			accessToken = (String) biztalkTokenMap.get("token");
			
			if(!("".equals(accessToken) || accessToken == null)) {
				String _sql = this.adminJdbcDAO.addExternalLinkInfoByBiztalk();
				
				ArrayList<String> _queryValue = new ArrayList<>();
				_queryValue.add(0, "00002");
				_queryValue.add(1, accessToken);
				_queryValue.add(2, "99999999");
				_queryValue.add(3, "99999999");
				_queryValue.add(4, accessToken);
				
				this.utilService.getQueryStringUpdate(_sql, _queryValue);
			}
		}
		
//		this.bizTalkUtils.sendBiztalk(null);
//		this.bizTalkUtils.getBiztalkSendResult(null);
//		this.bizTalkUtils.getBiztalkPolling(null);
		
		return accessToken;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String getTodayAccessToken() throws ParseException, ClientProtocolException, IOException {		
		String _sql = this.commonJdbcDAO.externalLinkInfo();
		String accessToken = ""; 
		String refreshToken = "";
		String useYn = "";
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, "00001");
		
		ExternalLinkInfoDTO externalLinkInfoDTO = new ExternalLinkInfoDTO();
		
		List<ExternalLinkInfoDTO> externalLinkInfoList = this.utilService.getQueryString(_sql ,externalLinkInfoDTO, _queryValue);
		
		if(externalLinkInfoList.size() == 0) {
			accessToken = setTodayToken();
		} else {
			accessToken = externalLinkInfoList.get(0).getAccessToken();
			refreshToken = externalLinkInfoList.get(0).getRefreshToken();
			useYn = externalLinkInfoList.get(0).getUseYn();
			
			if("N".equals(useYn)) {
				Map todayTokenMap = this.todayUtils.getTodayTokenByRefreshToken(refreshToken);
				
				accessToken = (String) todayTokenMap.get("access_token");
				
				String _modifySql = this.commonJdbcDAO.modiifyExternalLinkInfo();
				
				ArrayList<String> _modifyValue = new ArrayList<>();
				_modifyValue.add(0, accessToken);
				_modifyValue.add(1, "00001");
				
				this.utilService.getQueryStringUpdate(_modifySql, _modifyValue);
			}
		}
		
		return accessToken;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deliveryCancel(String bookId) throws ClientProtocolException, IOException, ParseException {
		String access_token = getTodayAccessToken();
		Map req = new HashMap<>();
		req.put("access_token",access_token);
		req.put("invoice_number",bookId);
		
		Map todayTokenMap = this.todayUtils.getDeliveryCancel(req);	
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getDtlCodeAll(){
		List<CdDtlInfo> cdDtlInfoList = this.cdDtlInfoDAO.findAll();
		
		return cdDtlInfoList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getDtlCodeInfo(String cdNo, String useYn){
		String _sql = this.cdDtlInfoJdbcDAO.selectCdDtlInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, cdNo);
		_queryValue.add(1, useYn);
		
		DtlCodeDTO dtlCodeDTO = new DtlCodeDTO();
		
		List<DtlCodeDTO> dtlCodeInfoList = this.utilService.getQueryString(_sql ,dtlCodeDTO, _queryValue);
		
		return dtlCodeInfoList;
	}
}
