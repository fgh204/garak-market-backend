package com.lawzone.market.notice.dao;

import org.springframework.stereotype.Component;

@Component
public class NoticeInfoJdbcDAO {
	public String getNoticeListPageInfo(String _pageCnt, String _maxPage
										, String _noticeCfcd, String _searchGb, String _postYn, String _dateGb) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				.append("\n     page.cnt as total_count ")
				.append("\n     , page.p_cnt as total_pages ")
				.append("\n     , if(page.p_cnt > " + _pageCnt + "  ,'Y','N') as has_next ")
				.append("\n     , if(1 < " + _pageCnt + "  ,'Y','N') as has_previous ")
				.append("\n     , 0 as page_number ")
				.append("\n from ( ")
				.append("\n 	select ") 
				.append("\n 		COUNT(1) as cnt ")
				.append("\n 	    , CEIL (COUNT(1) / " + _maxPage + ") as p_cnt ")
				.append("\n 	from lz_market.notice_info ni ");
				if("01".equals( _dateGb) ) {
					_query.append("\n 	where ni.begin_date >= DATE_FORMAT(now(), '%Y-%m-%d') ");
				} else {
					_query.append("\n 	where ni.begin_date between ? and ? ");
				}
				if(!"000".equals(_noticeCfcd)) {
					_query.append("\n 	and ni.notice_cfcd = ? ");
				}
				
				if(!"00".equals(_postYn)) {
					_query.append("\n 	and ni.post_yn = ? ");
				}
				
				if("001".equals(_searchGb)) {
					_query.append("\n 	and ni.notice_title like ? ");
					
				} else if("002".equals(_searchGb)) {
					_query.append("\n 	and ni.notice_desc like ? ");
					
				} else if("003".equals(_searchGb)) {
					_query.append("\n 	and (ni.notice_title  like ? OR ni.notice_desc  like ?) ");
				}
				_query.append("\n )page ");
		
		return _query.toString();
	}
	
	public String getNoticeListPageInfo2(String _pageCnt, String _maxPage
			, String _noticeCfcd, String _searchGb, String _postYn, String _dateGb) {
	StringBuffer _query = new StringBuffer();
	
		_query.append("\n select ")
		.append("\n     page.p_cnt as total_page_count  ")
		.append("\n     , page.cnt as total_count ")
		.append("\n from ( ")
		.append("\n 	select ") 
		.append("\n 		COUNT(1) as cnt ")
		.append("\n 	    , CEIL (COUNT(1) / " + _maxPage + ") as p_cnt ")
		.append("\n 	from lz_market.notice_info ni ");
		if("01".equals( _dateGb) ) {
			_query.append("\n 	where ni.begin_date >= DATE_FORMAT(now(), '%Y-%m-%d') ");
		} else {
			_query.append("\n 	where ni.begin_date between ? and ? ");
		}
		
		if(!"000".equals(_noticeCfcd)) {
			_query.append("\n 	and ni.notice_cfcd = ? ");
		}
		
		if(!"00".equals(_postYn)) {
			_query.append("\n 	and ni.post_yn = ? ");
		}
		
		if("001".equals(_searchGb)) {
			_query.append("\n 	and ni.notice_title like ? ");
		
		} else if("002".equals(_searchGb)) {
			_query.append("\n 	and ni.notice_desc like ? ");
		
		} else if("003".equals(_searchGb)) {
			_query.append("\n 	and (ni.notice_title  like ? OR ni.notice_desc  like ?) ");
		}
		_query.append("\n )page ");
		
		return _query.toString();
	}
	
	public String getNoticeList(String _pageCnt, String _maxPage
								, String _noticeCfcd, String _searchGb, String _postYn, String _dateGb) {
	StringBuffer _query = new StringBuffer();
	
	_query.append("\n select ")
			.append("\n 	ni.notice_number ")
			.append("\n 	, ni.notice_cfcd ")
			.append("\n 	, lz_market.FN_DTL_NM(16, ni.notice_cfcd) ")
			.append("\n 	, ni.notice_title ")
			.append("\n 	, ni.notice_desc ")
			.append("\n 	, ni.notice_images_path ")
			.append("\n 	, DATE_FORMAT(ni.begin_date, '%Y-%m-%d') ")
			.append("\n 	, DATE_FORMAT(ni.end_date, '%Y-%m-%d') ")
			.append("\n 	, ni.post_yn ")
			.append("\n from lz_market.notice_info ni ");
			if("01".equals( _dateGb) ) {
				_query.append("\n 	where ni.begin_date >= DATE_FORMAT(now(), '%Y-%m-%d') ");
			} else {
				_query.append("\n 	where ni.begin_date between ? and ? ");
			}
			
			if(!"000".equals(_noticeCfcd)) {
				_query.append("\n 	and ni.notice_cfcd = ? ");
			}
	
			if(!"00".equals(_postYn)) {
				_query.append("\n 	and ni.post_yn = ? ");
			}
	
			if("001".equals(_searchGb)) {
				_query.append("\n 	and ni.notice_title like ? ");
			
			} else if("002".equals(_searchGb)) {
				_query.append("\n 	and ni.notice_desc like ? ");
			
			} else if("003".equals(_searchGb)) {
				_query.append("\n 	and (ni.notice_title  like ? OR ni.notice_desc  like ?) ");
			}
			_query.append("\n order by ni.notice_number  desc");
			_query.append("\n limit " + _pageCnt + ", " + _maxPage);
	
	return _query.toString();
	}
}
