package com.lawzone.market.notice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.common.service.DtlCodeDTO;
import com.lawzone.market.notice.dao.NoticeInfo;
import com.lawzone.market.notice.dao.NoticeInfoDAO;
import com.lawzone.market.notice.dao.NoticeInfoJdbcDAO;
import com.lawzone.market.order.service.ProductOrderInfo;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeService {
	private final NoticeInfoDAO noticeInfoDAO;
	private final NoticeInfoJdbcDAO noticeInfoJdbcDAO;
	private final UtilService utilService;
	private final ModelMapper modelMapper;
	
	public Long addNoticeInfo(NoticeInfoDTO noticeInfoDTO) {
		NoticeInfo noticeInfo = new NoticeInfo();
		noticeInfo = modelMapper.map(noticeInfoDTO, NoticeInfo.class);
		
		return this.noticeInfoDAO.save(noticeInfo).getNoticeNumber();
	}
	
	public List getNoticeListPageInfo(NoticeInfoCDTO noticeInfoCDTO) {
		String _pageCnt = noticeInfoCDTO.getPageCnt();
		String _maxPage = noticeInfoCDTO.getMaxPage();
		String _noticeCfcd = noticeInfoCDTO.getNoticeCfcd();
		String _searchGb = noticeInfoCDTO.getSearchGb();
		String _searchValue = noticeInfoCDTO.getSearchValue();
		String _postYn = noticeInfoCDTO.getPostYn();
		String _noticeBeginDate = noticeInfoCDTO.getNoticeBeginDate();
		String _noticeEndDate = noticeInfoCDTO.getNoticeEndDate();
		String _dateGb = noticeInfoCDTO.getDateGb();
		
		String _sql = this.noticeInfoJdbcDAO.getNoticeListPageInfo(_pageCnt, _maxPage, _noticeCfcd, _searchGb, _postYn, _dateGb);
				
		ArrayList<String> _queryValue = new ArrayList<>();
		int index = 0;
		if(!"01".equals(_dateGb)) {
			_queryValue.add(index, _noticeBeginDate + " 00:00:00");
			index++;
			_queryValue.add(index, _noticeEndDate + " 23:59:59");
			index++;
		}
		
		if(!"000".equals(_noticeCfcd)) {
			_queryValue.add(index, _noticeCfcd);
			index++;
		}
		
		if(!"00".equals(_postYn)) {
			_queryValue.add(index, _postYn);
			index++;
		}
		
		if("001".equals(_searchGb) || "002".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;			
		} else if("003".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;
			_queryValue.add(index, _searchValue);
			index++;
		}
		
		AdminPageInfoDTO adminPageInfoDTO = new AdminPageInfoDTO();
		
		List<AdminPageInfoDTO> adminPageInfo = this.utilService.getQueryString(_sql ,adminPageInfoDTO, _queryValue);
		return adminPageInfo;
		
	}
	
	public List getNoticeListPageInfo2(NoticeInfoCDTO noticeInfoCDTO) {
		String _pageCnt = noticeInfoCDTO.getPageCnt();
		String _maxPage = noticeInfoCDTO.getMaxPage();
		String _noticeCfcd = noticeInfoCDTO.getNoticeCfcd();
		String _searchGb = noticeInfoCDTO.getSearchGb();
		String _searchValue = noticeInfoCDTO.getSearchValue();
		String _postYn = noticeInfoCDTO.getPostYn();
		String _noticeBeginDate = noticeInfoCDTO.getNoticeBeginDate();
		String _noticeEndDate = noticeInfoCDTO.getNoticeEndDate();
		String _dateGb = noticeInfoCDTO.getDateGb();
		
		String _sql = this.noticeInfoJdbcDAO.getNoticeListPageInfo2(_pageCnt, _maxPage, _noticeCfcd, _searchGb, _postYn, _dateGb);
				
		ArrayList<String> _queryValue = new ArrayList<>();
		int index = 0;
		if(!"01".equals(_dateGb)) {
			_queryValue.add(index, _noticeBeginDate + " 00:00:00");
			index++;
			_queryValue.add(index, _noticeEndDate + " 23:59:59");
			index++;
		}
		
		if(!"000".equals(_noticeCfcd)) {
			_queryValue.add(index, _noticeCfcd);
			index++;
		}
		
		if(!"00".equals(_postYn)) {
			_queryValue.add(index, _postYn);
			index++;
		}
		
		if("001".equals(_searchGb) || "002".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;			
		} else if("003".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;
			_queryValue.add(index, _searchValue);
			index++;
		}
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		List<PageInfoDTO> pageInfo = this.utilService.getQueryString(_sql ,pageInfoDTO, _queryValue);
	
		return pageInfo;
		
	}
	
	public List getNoticeList(NoticeInfoCDTO noticeInfoCDTO) {
		String _pageCnt = noticeInfoCDTO.getPageCnt();
		String _maxPage = noticeInfoCDTO.getMaxPage();
		String _noticeCfcd = noticeInfoCDTO.getNoticeCfcd();
		String _searchGb = noticeInfoCDTO.getSearchGb();
		String _searchValue = noticeInfoCDTO.getSearchValue();
		String _postYn = noticeInfoCDTO.getPostYn();
		String _noticeBeginDate = noticeInfoCDTO.getNoticeBeginDate();
		String _noticeEndDate = noticeInfoCDTO.getNoticeEndDate();
		String _dateGb = noticeInfoCDTO.getDateGb();
		
		String _sql = this.noticeInfoJdbcDAO.getNoticeList(_pageCnt, _maxPage, _noticeCfcd, _searchGb, _postYn, _dateGb);
				
		ArrayList<String> _queryValue = new ArrayList<>();
		int index = 0;
		
		if(!"01".equals(_dateGb)) {
			_queryValue.add(index, _noticeBeginDate + " 00:00:00");
			index++;
			_queryValue.add(index, _noticeEndDate + " 23:59:59");
			index++;
		}
		
		if(!"000".equals(_noticeCfcd)) {
			_queryValue.add(index, _noticeCfcd);
			index++;
		}
		
		if(!"00".equals(_postYn)) {
			_queryValue.add(index, _postYn);
			index++;
		}
		
		if("001".equals(_searchGb) || "002".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;			
		} else if("003".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;
			_queryValue.add(index, _searchValue);
			index++;
		}
		
		NoticeInfoDTO noticeInfoDTO = new NoticeInfoDTO();
		
		List<NoticeInfoDTO> noticeInfo = this.utilService.getQueryString(_sql ,noticeInfoDTO, _queryValue);
	
		return noticeInfo;
	}
}
