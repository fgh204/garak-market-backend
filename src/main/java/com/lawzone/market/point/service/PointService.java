package com.lawzone.market.point.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.el.lang.ELArithmetic.BigDecimalDelegate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.event.dao.EventInfoDAO;
import com.lawzone.market.event.service.EventInfo;
import com.lawzone.market.point.dao.PointDetailInfoDAO;
import com.lawzone.market.point.dao.PointDetailInfoJdbcDAO;
import com.lawzone.market.point.dao.PointInfoDAO;
import com.lawzone.market.point.dao.PointInfoJdbcDAO;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.service.SellerInfo;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.util.DateUtils;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class PointService {
	private final PointInfoDAO pointInfoDAO;
	private final UserInfoDAO userInfoDAO;
	private final PointDetailInfoDAO pointDetailInfoDAO;
	private final PointDetailInfoJdbcDAO pointDetailInfoJdbcDAO;
	private final PointInfoJdbcDAO pointInfoJdbcDAO;
	private final ModelMapper modelMapper;
	private final UtilService utilService;
	private final DateUtils dateUtils;
	private final EventInfoDAO eventInfoDAO;
	
	@Transactional(rollbackFor = Exception.class)
	public Map addPoint(PointInfoCDTO pointInfoCDTO){
		Map _rtnMap = new HashMap<>();
		_rtnMap.put("rtnCode", "0000");
		_rtnMap.put("rtnMsg", "저장 되었습니다.");
		
		List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		
		if(_userInfo.size() > 0) {
			String userId = _userInfo.get(0).getUserId();
			String socialId = _userInfo.get(0).getSocialId();
			
			if(!("002".equals(pointInfoCDTO.getEventCode()) || "003".equals(pointInfoCDTO.getEventCode()))){
				_rtnMap.put("rtnCode", "9999");
				_rtnMap.put("rtnMsg", "사용하지 않는 포인트 입니다.");
				return _rtnMap;
			}
			
			if("002".equals(pointInfoCDTO.getEventCode())){
				if("".equals(pointInfoCDTO.getEventId()) || pointInfoCDTO.getEventId() == null) {
					_rtnMap.put("rtnCode", "9999");
					_rtnMap.put("rtnMsg", "이벤트를 확인해 주세요");
					return _rtnMap;
				}
				
				String pointChkSql = this.pointInfoJdbcDAO.pointAccumulationYn();
				
				ArrayList<String> _pointChkQueryValue = new ArrayList<>();
				_pointChkQueryValue.add(0, pointInfoCDTO.getEventId());
				_pointChkQueryValue.add(1, socialId);
				_pointChkQueryValue.add(2, pointInfoCDTO.getEventCode());
				_pointChkQueryValue.add(3, pointInfoCDTO.getEventId());
				
				PointChkDTO pointChkDTO = new PointChkDTO();
				
				List<PointChkDTO> pointChkList 
				= this.utilService.getQueryString(pointChkSql,pointChkDTO,_pointChkQueryValue);
				
				if("N".equals(pointChkList.get(0).getPointYn())) {
					_rtnMap.put("rtnCode", "9999");
					_rtnMap.put("rtnMsg", "이미 지급된 포인트 입니다");
					return _rtnMap;
				} else {
					List<EventInfo> eventInfo = this.eventInfoDAO.findByeventIdAndUseYn(pointInfoCDTO.getEventId(), "Y");
					BigDecimal amountZero = new BigDecimal(0);
					if(eventInfo.size() > 0) {
						if(eventInfo.get(0).getPointAmount().compareTo(amountZero) > 0){
							pointInfoCDTO.setPointValue(eventInfo.get(0).getPointAmount());
						}
						pointInfoCDTO.setExpirationDateGb(eventInfo.get(0).getExpirationDateGb());
						pointInfoCDTO.setExpirationDateValue(eventInfo.get(0).getExpirationDateValue());
						
						if(pointInfoCDTO.getPointValue() == null || pointInfoCDTO.getPointValue().compareTo(amountZero) == 0){
							_rtnMap.put("rtnCode", "9999");
							_rtnMap.put("rtnMsg", "등록된 포인트 금액이 없습니다!");
							return _rtnMap;
						}
					} else {
						_rtnMap.put("rtnCode", "9999");
						_rtnMap.put("rtnMsg", "등록된 이벤트가 없습니다!");
						return _rtnMap;
					}
				}
			}
			
			if("003".equals(pointInfoCDTO.getEventCode()) && !(pointInfoCDTO.getEventId() == null || "".equals(pointInfoCDTO.getEventId()))) {
				String pointInfoSql = this.pointInfoJdbcDAO.pointDetailInfoByOrderNo();
				
				ArrayList<String> _queryValue = new ArrayList<>();
				_queryValue.add(0, userId);
				_queryValue.add(1, pointInfoCDTO.getEventId());
				
				DifferencePointAmountDTO differencePointAmountDTO = new DifferencePointAmountDTO();
				
				List<DifferencePointAmountDTO> differencePointAmountList 
					= this.utilService.getQueryString(pointInfoSql,differencePointAmountDTO,_queryValue);
				
				BigDecimal differencePointAmount = new BigDecimal("0");
				BigDecimal pointValue = pointInfoCDTO.getPointValue();
				
				for(int i = 0; i < differencePointAmountList.size(); i++) {
					if(pointValue.compareTo(new BigDecimal("0")) == 0) {
						break;
					}
					differencePointAmount = differencePointAmountList.get(i).getPointValue();
					
					pointInfoCDTO.setPointExpirationDatetime(differencePointAmountList.get(i).getPointExpirationDatetime());
					
					if(differencePointAmount.compareTo(pointValue) >= 0) {
						pointInfoCDTO.setPointValue(pointValue);
						pointValue = new BigDecimal("0");
					} else {
						pointInfoCDTO.setPointValue(differencePointAmount);
						pointValue = pointValue.subtract(differencePointAmount);
					}
					this.savePoint(pointInfoCDTO, userId);
				}
			}else {
				this.savePoint(pointInfoCDTO, userId);
			}
		}else {
			_rtnMap.put("rtnCode", "9999");
			_rtnMap.put("rtnMsg", "포인트를 등록할 고객정보가 없습니다.");
		}
	return _rtnMap;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void savePoint(PointInfoCDTO pointInfoCDTO, String userId){
		if(pointInfoCDTO.getPointExpirationDatetime() == null || "".equals(pointInfoCDTO.getPointExpirationDatetime())) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String _date = sf.format(date);
			String dateGb = "M";
			int dateValue = 6;
			
			if(pointInfoCDTO.getExpirationDateGb() != null) {
				dateGb = pointInfoCDTO.getExpirationDateGb();
			}
			
			if(pointInfoCDTO.getExpirationDateValue() != null) {
				dateValue = pointInfoCDTO.getExpirationDateValue().intValue();
			}
			
			String _PointExpirationDatetime = dateUtils.addDate(_date, "yyyy-MM-dd", dateGb, dateValue, "yyyy-MM-dd");
			
			_PointExpirationDatetime = _PointExpirationDatetime + " 23:59:59";
			
			pointInfoCDTO.setPointExpirationDatetime(_PointExpirationDatetime);
		}
		
		pointInfoCDTO.setUserId(userId);
		pointInfoCDTO.setPointCode("001");
		pointInfoCDTO.setPointStateCode("001");
		pointInfoCDTO.setPointRegistDate("now()");
		
		PointInfo pointInfo = new PointInfo();
		
		pointInfo.setUserId(userId);
		pointInfo.setEventCode(pointInfoCDTO.getEventCode());
		pointInfo.setEventId(pointInfoCDTO.getEventId());
		pointInfo.setPointStateCode(pointInfoCDTO.getPointStateCode());
		pointInfo.setPointValue(pointInfoCDTO.getPointValue());
		pointInfo.setPointCode(pointInfoCDTO.getPointCode());
		pointInfo.setPointRegistDate("now()");
		pointInfo.setPointExpirationDatetime(pointInfoCDTO.getPointExpirationDatetime());
		
		Long pointId = this.pointInfoDAO.save(pointInfo).getPointId();
		
		pointInfoCDTO.setPointOriginalId(pointId);
		
		PointDetailInfo pointDetailInfo = new PointDetailInfo();
		pointDetailInfo = modelMapper.map(pointInfoCDTO, PointDetailInfo.class);
		
		Long pointDetailId = this.pointDetailInfoDAO.save(pointDetailInfo).getPointDetailId();
		
		Optional<PointDetailInfo> _pointDetailInfo = this.pointDetailInfoDAO.findByPointDetailId(pointDetailId);
		
		if(_pointDetailInfo.isPresent()) {
			_pointDetailInfo.get().setPointSaveId(pointDetailId);
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BigDecimal getPointAmount(PointInfoCDTO pointInfoCDTO){
		BigDecimal _rtnValue = new BigDecimal("0");
		
		//List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		
		//if(_userInfo.size() > 0) {
			String sql = this.pointInfoJdbcDAO.pointAmount();
			
			ArrayList<String> _queryValue = new ArrayList<>();
			_queryValue.add(0, pointInfoCDTO.getUserId());
			
			String point = this.utilService.getQueryStringChk(sql,_queryValue);
			
			if(!"".equals(point)) {
				_rtnValue = new BigDecimal(point);
			}
		//}
	return _rtnValue;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BigDecimal getExpirationpointAmount(PointInfoCDTO pointInfoCDTO){
		BigDecimal _rtnValue = new BigDecimal("0");
		
		//List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		
		//if(_userInfo.size() > 0) {
			String sql = this.pointInfoJdbcDAO.expirationpointAmount();
			
			ArrayList<String> _queryValue = new ArrayList<>();
			_queryValue.add(0, pointInfoCDTO.getUserId());
			
			String point = this.utilService.getQueryStringChk(sql,_queryValue);
			
			if(!"".equals(point)) {
				_rtnValue = new BigDecimal(point);
				
				if(_rtnValue.compareTo(new BigDecimal(0)) < 0) {
					_rtnValue = new BigDecimal(0);
				}
			}
		//}
	return _rtnValue;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BigDecimal setPointDifference(PointInfoCDTO pointInfoCDTO){
		BigDecimal _rtnValue = new BigDecimal("0");
		
		List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		
		if(_userInfo.size() > 0) {
			String userId = _userInfo.get(0).getUserId();
			
			BigDecimal usePoint = pointInfoCDTO.getPointValue();
			BigDecimal userPoint = this.getPointAmount(pointInfoCDTO);
			if(userPoint.compareTo(usePoint) >= 0) {
				//포인트금액
				BigDecimal pointAmount = pointInfoCDTO.getPointValue();
				
				//사용금액확인
				String sql = this.pointDetailInfoJdbcDAO.differencePointAmount();
				
				DifferencePointAmountDTO differencePointAmountDTO = new DifferencePointAmountDTO();
				
				ArrayList<String> _queryValue = new ArrayList<>();
				_queryValue.add(0, "002");
				_queryValue.add(1, userId);
				
				List<DifferencePointAmountDTO> differencePointAmountList = this.utilService.getQueryString(sql,differencePointAmountDTO,_queryValue);
				
				PointInfo pointInfo = new PointInfo();
				
				pointInfo.setUserId(userId);
				pointInfo.setEventCode(pointInfoCDTO.getEventCode());
				pointInfo.setEventId(pointInfoCDTO.getEventId());
				pointInfo.setPointStateCode("002");
				pointInfo.setPointValue(new BigDecimal("-" + usePoint.toString()));
				pointInfo.setPointCode("001");
				pointInfo.setPointRegistDate("now()");
				pointInfo.setPointExpirationDatetime("2999-12-31 23:59:59");
				
				Long pointId = this.pointInfoDAO.save(pointInfo).getPointId();
				
				if(differencePointAmountList.size() == 0) {
					sql = this.pointDetailInfoJdbcDAO.pointAmount();
					
					differencePointAmountDTO = new DifferencePointAmountDTO();
					
					_queryValue = new ArrayList<>();
					_queryValue.add(0, "001");
					_queryValue.add(1, userId);
					
					differencePointAmountList = this.utilService.getQueryString(sql,differencePointAmountDTO,_queryValue);
					
					int _cnt = differencePointAmountList.size();
					
					BigDecimal differencePointAmount = new BigDecimal("0");
					BigDecimal pointDetailAmount = new BigDecimal("0");
					BigDecimal _zero = new BigDecimal("0");
					
					for(int i = 0; i < _cnt; i++) {
						if(usePoint.compareTo(_zero) == 0) {
							break;
						}
						
						differencePointAmount = differencePointAmountList.get(i).getPointValue();
						
						if(usePoint.compareTo(differencePointAmount) >= 0) {
							pointDetailAmount = new BigDecimal("-" + differencePointAmount.toString());
							usePoint = usePoint.subtract(differencePointAmount);
						}else {
							pointDetailAmount = new BigDecimal("-" + usePoint.toString());
							usePoint = _zero;
						}
						
						PointDetailInfo pointDetailInfo = new PointDetailInfo();
						pointDetailInfo.setPointCode("001");
						pointDetailInfo.setPointOriginalId(pointId);
						pointDetailInfo.setPointStateCode("002");
						pointDetailInfo.setPointValue(pointDetailAmount);
						pointDetailInfo.setPointSaveId(differencePointAmountList.get(i).getPointSaveId().longValue());
						pointDetailInfo.setUserId(userId);
						pointDetailInfo.setPointExpirationDatetime(differencePointAmountList.get(i).getPointExpirationDatetime());
						
						Long pointDetailId = this.pointDetailInfoDAO.save(pointDetailInfo).getPointDetailId();
						
//						Optional<PointDetailInfo> _pointDetailInfo = this.pointDetailInfoDAO.findByPointDetailId(pointDetailId);
//						
//						if(_pointDetailInfo.isPresent()) {
//							_pointDetailInfo.get().setPointOriginalId(pointDetailId);
//						}
					}
				}else {
					sql = this.pointDetailInfoJdbcDAO.pointAmount3();
					
					differencePointAmountDTO = new DifferencePointAmountDTO();
					
					_queryValue = new ArrayList<>();
					//_queryValue.add(0 , "001");
					_queryValue.add(0 , userId);
					//_queryValue.add(1 , differencePointAmountList.get(0).getPointSaveId().toString());
					
					List<DifferencePointAmountDTO> differencePointAmountList2 = this.utilService.getQueryString(sql,differencePointAmountDTO,_queryValue);
					
					int _cnt = differencePointAmountList2.size();
					
					BigDecimal differencePointAmount = new BigDecimal("0");
					BigDecimal pointDetailAmount = new BigDecimal("0");
					BigDecimal _zero = new BigDecimal("0");
					
					for(int i = 0; i < _cnt; i++) {
						if(usePoint.compareTo(_zero) == 0) {
							break;
						}
						//if(differencePointAmountList2.get(i).getPointSaveId().equals(differencePointAmountList.get(0).getPointSaveId())) {
						//	differencePointAmount = differencePointAmountList2.get(i).getPointValue().add(differencePointAmountList.get(0).getPointValue());
						//}else {
							differencePointAmount = differencePointAmountList2.get(i).getPointValue();
						//}
						
						if(differencePointAmount.compareTo(_zero) <= 0) {
							continue;
						}
							
						if(usePoint.compareTo(differencePointAmount) >= 0) {
							pointDetailAmount = new BigDecimal("-" + differencePointAmount.toString());
							usePoint = usePoint.subtract(differencePointAmount);
						}else {
							pointDetailAmount = new BigDecimal("-" + usePoint.toString());
							usePoint = _zero;
						}
						
						if(pointDetailAmount.compareTo(_zero) == 0) {
							continue;
						}
						
						PointDetailInfo pointDetailInfo = new PointDetailInfo();
						pointDetailInfo.setPointCode("001");
						pointDetailInfo.setPointOriginalId(pointId);
						pointDetailInfo.setPointStateCode("002");
						pointDetailInfo.setPointValue(pointDetailAmount);
						pointDetailInfo.setPointSaveId(differencePointAmountList2.get(i).getPointSaveId().longValue());
						pointDetailInfo.setUserId(userId);
						pointDetailInfo.setPointExpirationDatetime(differencePointAmountList2.get(i).getPointExpirationDatetime());
						
						Long pointDetailId = this.pointDetailInfoDAO.save(pointDetailInfo).getPointDetailId();
						
//						Optional<PointDetailInfo> _pointDetailInfo = this.pointDetailInfoDAO.findByPointDetailId(pointDetailId);
//						
//						if(_pointDetailInfo.isPresent()) {
//							_pointDetailInfo.get().setPointOriginalId(pointDetailId);
//						}
					}
				}
			}
		}
	return _rtnValue;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getPointHistInfo(PointInfoCDTO pointInfoCDTO){
		//List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		String monthValue = pointInfoCDTO.getMonthValue();
		String pointStateCode = pointInfoCDTO.getPointStateCode();
		String pageCount = pointInfoCDTO.getPageCount();
		String maxPageCount = pointInfoCDTO.getMaxPageCount();
		
		String _sql = this.pointDetailInfoJdbcDAO.pointHistInfo(monthValue, pointStateCode, pageCount, maxPageCount);
		
		PointInfoHistInfoDTO pointInfoHistInfoDTO = new PointInfoHistInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, pointInfoCDTO.getUserId());
		
		if(!("".equals(pointStateCode) || pointStateCode == null)) {
			_queryValue.add(1, pointStateCode);
		}
		
		List<PointInfoHistInfoDTO> pointInfoHistInfoList = this.utilService.getQueryString(_sql, pointInfoHistInfoDTO, _queryValue);
		
		return pointInfoHistInfoList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public List getPointHistPageInfo(PointInfoCDTO pointInfoCDTO){
		//List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		String monthValue = pointInfoCDTO.getMonthValue();
		String pointStateCode = pointInfoCDTO.getPointStateCode();
		String maxPageCnt = pointInfoCDTO.getMaxPageCount();
		
		String _sql = this.pointDetailInfoJdbcDAO.pointHistPageInfo(monthValue, pointStateCode, maxPageCnt);
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, pointInfoCDTO.getUserId());
		
		if(!("".equals(pointStateCode) || pointStateCode == null)) {
			_queryValue.add(1, pointStateCode);
		}
		
		List<PageInfoDTO> pageInfo = this.utilService.getQueryString(_sql, pageInfoDTO, _queryValue);
		
		return pageInfo;
	}
}
