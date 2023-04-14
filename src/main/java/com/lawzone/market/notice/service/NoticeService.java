package com.lawzone.market.notice.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.lawzone.market.notice.dao.NoticeInfo;
import com.lawzone.market.notice.dao.NoticeInfoDAO;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeService {
	private final NoticeInfoDAO noticeInfoDAO;
	private final UtilService utilService;
	private final ModelMapper modelMapper;
	
	public String addNoticeInfo(NoticeInfoDTO noticeInfoDTO) {
		if("".equals(noticeInfoDTO.getNoticeId()) || noticeInfoDTO.getNoticeId() == null) {
			
			String noticeId = utilService.getNextVal("NOTICE_ID");
			
			noticeInfoDTO.setNoticeId(noticeId);
		}
		NoticeInfo noticeInfo = new NoticeInfo();
		noticeInfo = modelMapper.map(noticeInfoDTO, NoticeInfo.class);
		
		return this.noticeInfoDAO.save(noticeInfo).getNoticeId();
	}
		
}
