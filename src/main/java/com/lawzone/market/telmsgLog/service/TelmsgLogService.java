package com.lawzone.market.telmsgLog.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.dao.CdDtlInfoDAO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.review.service.ProductReviewInfo;
import com.lawzone.market.review.service.ProductReviewInfoDTO;
import com.lawzone.market.telmsgLog.dao.TelmsgLogDAO;
import com.lawzone.market.user.dao.UserTrackingInfoDAO;
import com.lawzone.market.user.service.UserTrackingInfo;
import com.lawzone.market.user.service.UserTrackingInfoId;
import com.lawzone.market.util.SlackWebhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class TelmsgLogService {
	private final TelmsgLogDAO telmsgLogDAO;
	private final SlackWebhook slackWebhook;
	private final UserTrackingInfoDAO userTrackingInfoDAO;
	private final CdDtlInfoDAO cdDtlInfoDAO;
	
	@Resource
	private SessionBean sessionBean;
	
	@Transactional(rollbackFor = Exception.class)
	public void addTelmsgLog(String ingrsPathCd, String dlngTpcd
							, String trnrcvTpcd, Map telMsgDta, String exMsg) {
		try {
			TelmsgLogInfo telmsgLogInfo = new TelmsgLogInfo();

			telmsgLogInfo.setController(sessionBean.getController());
			telmsgLogInfo.setDlngDttm("now()");
			telmsgLogInfo.setMethod(sessionBean.getMethod());
			telmsgLogInfo.setSessionId(sessionBean.getSessionId());
			telmsgLogInfo.setSvcUrl(sessionBean.getSvcUrl());
			telmsgLogInfo.setUserId(sessionBean.getUserId());
			telmsgLogInfo.setUserIp(sessionBean.getUserIp());
			//00 : 일반로그, 01 : 결제, 02 : 운송장, 50:화면로그  
			telmsgLogInfo.setIngrsPathCd(ingrsPathCd);
			//01_00 : 결제요청, 01_01 : 결제조회, 01_04 : 결제승인, 01_90 : 결제취소 
			//02_00 : 운송장채번, 02_01 : 운송장번호조회, 02_90 : 운송장취소
			//00_00 : 서버로그
			//99_00 : 에러로그
			//50_00 : 화면로그
			telmsgLogInfo.setDlngTpcd(dlngTpcd);
			//1 : 송신, 2 : 수신
			telmsgLogInfo.setTrnrcvCfcd(trnrcvTpcd);
			telmsgLogInfo.setDevice(sessionBean.getAgent());
			//전문DATA
			if(telMsgDta != null) {
				telmsgLogInfo.setTelmsgDtaInfo(telMsgDta.toString());
			}
			
			this.telmsgLogDAO.save(telmsgLogInfo);
			
			if("99".equals(ingrsPathCd)) {
				if(!"".equals(exMsg)) {
					StringBuilder slackMsg = new StringBuilder();
					slackMsg.append("ScvUrl : " + sessionBean.getSvcUrl())
							.append("\nMethod : " + sessionBean.getMethod())
							.append("\nerror : " + exMsg);
					this.slackWebhook.postSlackMessage(slackMsg.toString(), "99");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void addTelmsgLog1(String ingrsPathCd, String dlngTpcd
							, String trnrcvTpcd, Map telMsgDta) {
		try {
			String userId = sessionBean.getUserId();
			String userIp = sessionBean.getUserIp();
			String sessionId = sessionBean.getSessionId();
			String svcUrl = (String) telMsgDta.get("svcUrl");
			
			TelmsgLogInfo telmsgLogInfo = new TelmsgLogInfo();
			
			telmsgLogInfo.setController((String) telMsgDta.get("controller"));
			telmsgLogInfo.setDlngDttm("now()");
			telmsgLogInfo.setMethod((String) telMsgDta.get("method"));
			telmsgLogInfo.setSessionId(sessionId);
			telmsgLogInfo.setSvcUrl(svcUrl);
			telmsgLogInfo.setUserId(userId);
			telmsgLogInfo.setUserIp(userIp);
			//00 : 일반로그, 01 : 결제, 02 : 운송장, 50:화면로그  
			telmsgLogInfo.setIngrsPathCd(ingrsPathCd);
			//01_00 : 결제요청, 01_01 : 결제조회, 01_04 : 결제승인, 01_90 : 결제취소 
			//02_00 : 운송장채번, 02_01 : 운송장번호조회, 02_90 : 운송장취소
			//00_00 : 서버로그
			//99_00 : 에러로그
			//50_00 : 화면로그
			telmsgLogInfo.setDlngTpcd(dlngTpcd);
			//1 : 송신, 2 : 수신
			telmsgLogInfo.setTrnrcvCfcd(trnrcvTpcd);
			telmsgLogInfo.setDevice(sessionBean.getAgent());
			//전문DATA
			if(telMsgDta != null) {
				if(telMsgDta.get("data") != null) {
					telmsgLogInfo.setTelmsgDtaInfo(telMsgDta.get("data").toString());
				}
			}
			
			this.telmsgLogDAO.save(telmsgLogInfo);
			
			if(userId != null) {
				List<CdDtlInfo> cdDtlInfoList = this.cdDtlInfoDAO.findByIdCodeNoAndDtlCodeNameAndUseYn("15",svcUrl , "Y");
				
				if(cdDtlInfoList.size() > 0) {
					UserTrackingInfo userTrackingInfo = new UserTrackingInfo();
					
					UserTrackingInfoId userTrackingInfoId = new UserTrackingInfoId();
					
					Optional<UserTrackingInfo> userTrackingSearchInfo = this.userTrackingInfoDAO.findByIdUserIdAndIdSvcUrl(userId, svcUrl);
					
					if(userTrackingSearchInfo.isEmpty()) {
						userTrackingInfoId = new UserTrackingInfoId();
						userTrackingInfoId.setUserId(userId);
						userTrackingInfoId.setSvcUrl(svcUrl);
						userTrackingInfo.setId(userTrackingInfoId);
						
						this.userTrackingInfoDAO.save(userTrackingInfo);
					} else {
						userTrackingInfoId = new UserTrackingInfoId();
						userTrackingInfoId.setUserId(userTrackingSearchInfo.get().getId().getUserId());
						userTrackingInfoId.setSvcUrl(userTrackingSearchInfo.get().getId().getSvcUrl());
						userTrackingInfo.setId(userTrackingInfoId);
						this.userTrackingInfoDAO.save(userTrackingInfo);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
