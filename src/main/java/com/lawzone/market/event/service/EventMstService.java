package com.lawzone.market.event.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.lawzone.market.admin.dto.common.AdminPageInfoDTO;
import com.lawzone.market.config.SessionBean;
import com.lawzone.market.event.dao.EventMstDAO;
import com.lawzone.market.event.dao.EventMstJdbcDAO;
import com.lawzone.market.notice.dao.NoticeInfo;
import com.lawzone.market.notice.service.NoticeInfoDTO;
import com.lawzone.market.product.dao.ProductDAO;
import com.lawzone.market.product.service.PageInfoDTO;
import com.lawzone.market.product.service.ProductInfo;
import com.lawzone.market.user.service.SellerInfo;
import com.lawzone.market.util.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventMstService {
	private final EventMstDAO eventMstDAO;
	private final EventMstJdbcDAO eventMstJdbcDAO;
	private final UtilService utilService;
	private final ModelMapper modelMapper;
	private final ProductDAO productDAO;
	
	@Resource
	private SessionBean sessionBean;
	
	public List getEventListPageInfo(EventInfoCDTO eventInfoCDTO) {
		String _pageCnt = eventInfoCDTO.getPageCnt();
		String _maxPage = eventInfoCDTO.getMaxPage();
		String _eventCfcd = eventInfoCDTO.getEventCfcd();
		String _searchDateGb = eventInfoCDTO.getSearchDateGb();
		String _eventBeginDate = eventInfoCDTO.getEventBeginDate();
		String _eventEndDate = eventInfoCDTO.getEventEndDate();
		String _eventStateCode = eventInfoCDTO.getEventStateCode();
		String _searchGb = eventInfoCDTO.getSearchGb();
		String _searchValue = eventInfoCDTO.getSearchValue();
		String _eventId = eventInfoCDTO.getEventId();
		
		String _sql = this.eventMstJdbcDAO.eventListPageInfo(_eventCfcd, _searchDateGb, _eventStateCode, _searchGb, _eventId, _pageCnt, _maxPage);
				
		ArrayList<String> _queryValue = new ArrayList<>();
		int index = 0;
		_queryValue.add(index, _eventBeginDate + " 00:00:00");
		index++;
		_queryValue.add(index, _eventEndDate + " 23:59:59");
		index++;
		
		if(!"000".equals(_eventCfcd)) {
			_queryValue.add(index, _eventCfcd);
		}
		
		if("001".equals(_searchGb)) {
			_queryValue.add(index, "%" + _searchValue + "%");
			index++;
		} else if("002".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;
		} 
		
		if(!("".equals(_eventId) || _eventId == null)) {
			_queryValue.add(index, _eventId);
			index++;
		}
		
		AdminPageInfoDTO adminPageInfoDTO = new AdminPageInfoDTO();
		
		List<AdminPageInfoDTO> adminPageInfo = this.utilService.getQueryString(_sql ,adminPageInfoDTO, _queryValue);
		
		return adminPageInfo;
	}
	
	public List getEventListPageInfo2(EventInfoCDTO eventInfoCDTO) {
		String _pageCnt = eventInfoCDTO.getPageCnt();
		String _maxPage = eventInfoCDTO.getMaxPage();
		String _eventCfcd = eventInfoCDTO.getEventCfcd();
		String _searchDateGb = eventInfoCDTO.getSearchDateGb();
		String _eventBeginDate = eventInfoCDTO.getEventBeginDate();
		String _eventEndDate = eventInfoCDTO.getEventEndDate();
		String _eventStateCode = eventInfoCDTO.getEventStateCode();
		String _searchGb = eventInfoCDTO.getSearchGb();
		String _searchValue = eventInfoCDTO.getSearchValue();
		String _eventId = eventInfoCDTO.getEventId();
		
		String _sql = this.eventMstJdbcDAO.eventListPageInfo2(_eventCfcd, _searchDateGb, _eventStateCode, _searchGb, _eventId, _pageCnt, _maxPage);
				
		ArrayList<String> _queryValue = new ArrayList<>();
		int index = 0;
		
		if("001".equals(_searchDateGb) || "002".equals(_searchDateGb)) {
			_queryValue.add(index, _eventBeginDate + " 00:00:00");
			index++;
			_queryValue.add(index, _eventEndDate + " 23:59:59");
			index++;
		}
		if(!"000".equals(_eventCfcd)) {
			_queryValue.add(index, _eventCfcd);
		}
		
		if("001".equals(_searchGb)) {
			_queryValue.add(index, "%" + _searchValue + "%");
			index++;
		} else if("002".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;
		}
		
		if(!("".equals(_eventId) || _eventId == null)) {
			_queryValue.add(index, _eventId);
			index++;
		}
		
		PageInfoDTO pageInfoDTO = new PageInfoDTO();
		
		List<PageInfoDTO> pageInfo = this.utilService.getQueryString(_sql ,pageInfoDTO, _queryValue);
		
		return pageInfo;
	}
	
	public List getEventListInfo(EventInfoCDTO eventInfoCDTO) {
		String _pageCnt = eventInfoCDTO.getPageCnt();
		String _maxPage = eventInfoCDTO.getMaxPage();
		String _eventCfcd = eventInfoCDTO.getEventCfcd();
		String _searchDateGb = eventInfoCDTO.getSearchDateGb();
		String _eventBeginDate = eventInfoCDTO.getEventBeginDate();
		String _eventEndDate = eventInfoCDTO.getEventEndDate();
		String _eventStateCode = eventInfoCDTO.getEventStateCode();
		String _searchGb = eventInfoCDTO.getSearchGb();
		String _searchValue = eventInfoCDTO.getSearchValue();
		String _eventId = eventInfoCDTO.getEventId();
		
		String _sql = this.eventMstJdbcDAO.eventListInfo(_eventCfcd, _searchDateGb, _eventStateCode, _searchGb, _eventId, _pageCnt, _maxPage);
				
		ArrayList<String> _queryValue = new ArrayList<>();
		int index = 0;
		if("001".equals(_searchDateGb) || "002".equals(_searchDateGb)) {
			_queryValue.add(index, _eventBeginDate + " 00:00:00");
			index++;
			_queryValue.add(index, _eventEndDate + " 23:59:59");
			index++;
		}
		
		if(!"000".equals(_eventCfcd)) {
			_queryValue.add(index, _eventCfcd);
		}
		
		if("001".equals(_searchGb)) {
			_queryValue.add(index, "%" + _searchValue + "%");
			index++;
		} else if("002".equals(_searchGb)) {
			_queryValue.add(index, _searchValue);
			index++;
		} 
		
		if(!("".equals(_eventId) || _eventId == null)) {
			_queryValue.add(index, _eventId);
			index++;
		}
		
		EventInfoDTO eventInfoDTO = new EventInfoDTO();
		
		List<EventInfoDTO> eventInfoList = this.utilService.getQueryString(_sql ,eventInfoDTO, _queryValue);
		
		return eventInfoList;
	}
	
	public Long addEventInfo(EventMstDTO eventMstDTO) {
		if("".equals(eventMstDTO.getEventId()) || eventMstDTO.getEventId() == null) {
			
			String eventId = utilService.getNextVal("EVENT_ID");
			
			eventMstDTO.setEventId(eventId);
		}
		
		EventMst eventMst = new EventMst();
		eventMst = modelMapper.map(eventMstDTO, EventMst.class);
		
		return this.eventMstDAO.save(eventMst).getEventMstSeq();
	}
	
	public List getEventInfo(EventMstDTO eventMstDTO) {
		return this.eventMstDAO.findByeventMstSeq(eventMstDTO.getEventMstSeq().longValue());
	}
	
	public void modifyEventClosed(EventMstDTO eventMstDTO) {
		List<EventMst> eventMst = this.eventMstDAO.findByeventMstSeq(eventMstDTO.getEventMstSeq().longValue());
		
		if(eventMst.size() > 0) {
			eventMst.get(0).setEventStateCode("003");
			
			this.eventMstDAO.save(eventMst.get(0));
		}
	}
	
	public List getEventProductInfo(String productId, String eventId) {
		String sql = this.eventMstJdbcDAO.eventProductInfo();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		_queryValue.add(0, sessionBean.getUserId());
		_queryValue.add(1, "003");
		_queryValue.add(2, productId);
		_queryValue.add(3, productId);
		_queryValue.add(4, "003");
		_queryValue.add(5, eventId);
		
		EventProductInfoDTO eventProductInfoDTO = new EventProductInfoDTO();
		
		List<EventProductInfoDTO> eventProductInfo = this.utilService.getQueryString(sql,eventProductInfoDTO,_queryValue);
		
		return eventProductInfo;
	}
	
	public List getEventIdInfoList() {
		String sql = this.eventMstJdbcDAO.eventIdInfoList();
		
		ArrayList<String> _queryValue = new ArrayList<>();
		
		EventIdInfoDTO eventIdInfoDTO = new EventIdInfoDTO();
		
		List<EventIdInfoDTO> eventIdInfoList = this.utilService.getQueryString(sql,eventIdInfoDTO,_queryValue);
		
		return eventIdInfoList;
	}
}
