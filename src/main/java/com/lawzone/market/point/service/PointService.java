package com.lawzone.market.point.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.el.lang.ELArithmetic.BigDecimalDelegate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.point.dao.PointDetailInfoDAO;
import com.lawzone.market.point.dao.PointDetailInfoJdbcDAO;
import com.lawzone.market.point.dao.PointInfoDAO;
import com.lawzone.market.point.dao.PointInfoJdbcDAO;
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
	
	@Transactional(rollbackFor = Exception.class)
	public String addPoint(PointInfoCDTO pointInfoCDTO){
		String _rtnMsg = "저장 되었습니다.";
		
		List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		
		if(_userInfo.size() > 0) {
			String socialId = _userInfo.get(0).getSocialId();
			if("003".equals(pointInfoCDTO.getEventCode()) && !(pointInfoCDTO.getEventId() == null || "".equals(pointInfoCDTO.getEventId()))) {
				String pointInfoSql = this.pointInfoJdbcDAO.pointDetailInfoByOrderNo();
				
				ArrayList<String> _queryValue = new ArrayList<>();
				_queryValue.add(0, socialId);
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
					this.savePoint(pointInfoCDTO, socialId);
				}
			}else {
				this.savePoint(pointInfoCDTO, socialId);
			}
		}else {
			_rtnMsg = "포인트를 등록할 고객정보가 없습니다.";
		}
	return _rtnMsg;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void savePoint(PointInfoCDTO pointInfoCDTO, String socialId){
		if(pointInfoCDTO.getPointExpirationDatetime() == null || "".equals(pointInfoCDTO.getPointExpirationDatetime())) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String _date = sf.format(date);
			String _PointExpirationDatetime = dateUtils.addDate(_date, "yyyy-MM-dd", "Y", 1, "yyyy-MM-dd");
			
			_PointExpirationDatetime = _PointExpirationDatetime + " 23:59:59";
			
			pointInfoCDTO.setPointExpirationDatetime(_PointExpirationDatetime);
		}
		
		pointInfoCDTO.setSocialId(socialId);
		pointInfoCDTO.setPointCode("001");
		pointInfoCDTO.setPointStateCode("001");
		pointInfoCDTO.setPointRegistDate("now()");
		
		PointInfo pointInfo = new PointInfo();
		
		pointInfo.setSocialId(socialId);
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
		
		List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		
		if(_userInfo.size() > 0) {
			String sql = this.pointInfoJdbcDAO.pointAmount();
			
			ArrayList<String> _queryValue = new ArrayList<>();
			_queryValue.add(0, _userInfo.get(0).getSocialId());
			
			String point = this.utilService.getQueryStringChk(sql,_queryValue);
			
			if(!"".equals(point)) {
				_rtnValue = new BigDecimal(point);
			}
		}
	return _rtnValue;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BigDecimal setPointDifference(PointInfoCDTO pointInfoCDTO){
		BigDecimal _rtnValue = new BigDecimal("0");
		
		List<UserInfo> _userInfo = this.userInfoDAO.findByUserId(pointInfoCDTO.getUserId());
		
		if(_userInfo.size() > 0) {
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
				_queryValue.add(1, _userInfo.get(0).getSocialId());
				
				List<DifferencePointAmountDTO> differencePointAmountList = this.utilService.getQueryString(sql,differencePointAmountDTO,_queryValue);
				
				PointInfo pointInfo = new PointInfo();
				
				pointInfo.setSocialId(_userInfo.get(0).getSocialId());
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
					_queryValue.add(1, _userInfo.get(0).getSocialId());
					
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
						pointDetailInfo.setSocialId(_userInfo.get(0).getSocialId());
						pointDetailInfo.setPointExpirationDatetime(differencePointAmountList.get(i).getPointExpirationDatetime());
						
						Long pointDetailId = this.pointDetailInfoDAO.save(pointDetailInfo).getPointDetailId();
						
//						Optional<PointDetailInfo> _pointDetailInfo = this.pointDetailInfoDAO.findByPointDetailId(pointDetailId);
//						
//						if(_pointDetailInfo.isPresent()) {
//							_pointDetailInfo.get().setPointOriginalId(pointDetailId);
//						}
					}
				}else {
					sql = this.pointDetailInfoJdbcDAO.pointAmount2();
					
					differencePointAmountDTO = new DifferencePointAmountDTO();
					
					_queryValue = new ArrayList<>();
					_queryValue.add(0 , "001");
					_queryValue.add(1 , _userInfo.get(0).getSocialId());
					_queryValue.add(2 , differencePointAmountList.get(0).getPointSaveId().toString());
					
					List<DifferencePointAmountDTO> differencePointAmountList2 = this.utilService.getQueryString(sql,differencePointAmountDTO,_queryValue);
					
					int _cnt = differencePointAmountList2.size();
					
					BigDecimal differencePointAmount = new BigDecimal("0");
					BigDecimal pointDetailAmount = new BigDecimal("0");
					BigDecimal _zero = new BigDecimal("0");
					
					for(int i = 0; i < _cnt; i++) {
						if(usePoint.compareTo(_zero) == 0) {
							break;
						}
						if(differencePointAmountList2.get(i).getPointSaveId().equals(differencePointAmountList.get(0).getPointSaveId())) {
							differencePointAmount = differencePointAmountList2.get(i).getPointValue().add(differencePointAmountList.get(0).getPointValue());
						}else {
							differencePointAmount = differencePointAmountList2.get(i).getPointValue();
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
						pointDetailInfo.setSocialId(_userInfo.get(0).getSocialId());
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
}
