package com.lawzone.market.telmsgLog.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.config.SessionBean;
import com.lawzone.market.review.service.ProductReviewInfo;
import com.lawzone.market.review.service.ProductReviewInfoDTO;
import com.lawzone.market.telmsgLog.dao.TelmsgLogDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class TelmsgLogService {
	private final TelmsgLogDAO telmsgLogDAO;
	
	@Resource
	private SessionBean sessionBean;
	
	@Transactional
	public void addTelmsgLog(String ingrsPathCd, String dlngTpcd
							, String trnrcvTpcd, Map telMsgDta) {
		try {
			TelmsgLogInfo telmsgLogInfo = new TelmsgLogInfo();

			telmsgLogInfo.setController(sessionBean.getController());
			telmsgLogInfo.setDlngDttm("now()");
			telmsgLogInfo.setMethod(sessionBean.getMethod());
			telmsgLogInfo.setSessionId(sessionBean.getSessionId());
			telmsgLogInfo.setSvcUrl(sessionBean.getSvcUrl());
			telmsgLogInfo.setUserId(sessionBean.getUserId());
			telmsgLogInfo.setUserIp(sessionBean.getUserIp());
			//00 : 일반로그, 01 : 결제, 02 : 운송장  
			telmsgLogInfo.setIngrsPathCd(ingrsPathCd);
			//01_00 : 결제요청, 01_01 : 결제조회, 01_04 : 결제승인, 01_90 : 결제취소 
			//02_00 : 운송장채번, 02_01 : 운송장번호조회, 01_90 : 운송장취소
			telmsgLogInfo.setDlngTpcd(dlngTpcd);
			//1 : 송신, 2 : 수신
			telmsgLogInfo.setTrnrcvCfcd(trnrcvTpcd);
			telmsgLogInfo.setDevice(sessionBean.getAgent());
			//전문DATA
			if(telMsgDta != null) {
				telmsgLogInfo.setTelmsgDtaInfo(telMsgDta.toString());
			}
			
			this.telmsgLogDAO.save(telmsgLogInfo);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
