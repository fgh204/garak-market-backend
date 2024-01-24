package com.lawzone.market.event.dao;

import org.springframework.stereotype.Component;

@Component
public class EventMstJdbcDAO {
	public String eventListPageInfo(String eventCfcd, String searchDateGb
								, String eventStateCode, String searchGb, String eventId
								, String page, String maxPage
								) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n     page.cnt as total_count ")
		.append("\n     , page.p_cnt as total_pages ")
		.append("\n     , if(page.p_cnt > " + page + "  , 'Y', 'N') as has_next ")
		.append("\n     , if(1 < " + page + "  ,'Y','N') as has_previous ")
		.append("\n     , 0 as page_number ")
		.append("\n from (  ")
		.append("\n 	select ")
		.append("\n 		COUNT(1) as cnt ")
		.append("\n        	, CEIL (COUNT(1) / " + maxPage + ") as p_cnt ")
		.append("\n      from lz_market.event_mst em ")
		.append("\n      	left outer join lz_market.user_info ui on em.create_user = ui.user_id ")
		.append("\n 	where 1 = 1 ");
		
		if("001".equals(searchDateGb)) {
			_query.append("\n 	and em.event_begin_date between ? and ? ");
		} else if("002".equals(searchDateGb)) {
			_query.append("\n 	and em.create_datetime between ? and ? ");
		}
		
		if(!"000".equals(eventCfcd)) {
			_query.append("\n 	and em.event_cfcd = ? ");
		}
		
		if("001".equals(searchGb)) {
			_query.append("\n 	and em.event_title like ? ");
		} else if("002".equals(searchGb)) {
			_query.append("\n 	and ui.user_name = ? ");
		} 
		
		if("001".equals(eventStateCode)) {
			_query.append("\n 	and (concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) <= date_format(now(), '%Y%m%d%H%i') and concat(if(em.event_end_date is null, '99991231', date_format(em.event_end_date, '%Y%m%d')), IFNULL(em.event_end_time, '2400')) >= date_format(now(), '%Y%m%d%H%i'))");
		} else if("002".equals(eventStateCode)) {
			_query.append("\n 	and concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) > date_format(now(), '%Y%m%d%H%i')");
		} else if("003".equals(eventStateCode)) {
			_query.append("\n 	and em.event_state_code = '" + eventStateCode + "'");
		} else if("004".equals(eventStateCode)) {
			_query.append("\n 	and concat(if(em.event_end_date is null, '99991231', date_format(em.event_end_date, '%Y%m%d')), IFNULL(em.event_end_time, '2400')) < date_format(now(), '%Y%m%d%H%i')");
		} else if("005".equals(eventStateCode)) {
			_query.append("\n 	and concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) > date_format(now(), '%Y%m%d%H%i')");
		}
		
		if(!("".equals(eventId) || eventId == null)) {
			_query.append("\n 	and em.event_id = ? ");
		}
		
		_query.append("\n 	)page ");
				
		return _query.toString();
	}
	
	public String eventListPageInfo2(String eventCfcd, String searchDateGb
			, String eventStateCode, String searchGb, String eventId
			, String page, String maxPage
			) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n     page.p_cnt as total_page_count  ")
		.append("\n     , page.cnt as total_count ")
		.append("\n from (  ")
		.append("\n 	select ")
		.append("\n 		COUNT(1) as cnt ")
		.append("\n        	, CEIL (COUNT(1) / " + maxPage + ") as p_cnt ")
		.append("\n      from lz_market.event_mst em ")
		.append("\n      	left outer join lz_market.user_info ui on em.create_user = ui.user_id ")
		.append("\n 	where 1 = 1 ");
		
		if("001".equals(searchDateGb)) {
			_query.append("\n 	and em.event_begin_date between ? and ? ");
		} else if("002".equals(searchDateGb)) {
			_query.append("\n 	and em.create_datetime between ? and ? ");
		}
		
		if(!"000".equals(eventCfcd)) {
			_query.append("\n 	and em.event_cfcd = ? ");
		}
		
		if("001".equals(searchGb)) {
			_query.append("\n 	and em.event_title like ? ");
		} else if("002".equals(searchGb)) {
			_query.append("\n 	and ui.user_name = ? ");
		} 
		
		if("001".equals(eventStateCode)) {
			_query.append("\n 	and (concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) <= date_format(now(), '%Y%m%d%H%i') and concat(if(em.event_end_date is null, '99991231', date_format(em.event_end_date, '%Y%m%d')), IFNULL(em.event_end_time, '2400')) >= date_format(now(), '%Y%m%d%H%i'))");
		} else if("002".equals(eventStateCode)) {
			_query.append("\n 	and concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) > date_format(now(), '%Y%m%d%H%i')");
		} else if("003".equals(eventStateCode)) {
			_query.append("\n 	and em.event_state_code = '" + eventStateCode + "'");
		} else if("004".equals(eventStateCode)) {
			_query.append("\n 	and concat(if(em.event_end_date is null, '99991231', date_format(em.event_end_date, '%Y%m%d')), IFNULL(em.event_end_time, '2400')) < date_format(now(), '%Y%m%d%H%i')");
		} else if("005".equals(eventStateCode)) {
			_query.append("\n 	and concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) > date_format(now(), '%Y%m%d%H%i')");
		}
		
		if(!("".equals(eventId) || eventId == null)) {
			_query.append("\n 	and em.event_id = ? ");
		}
		_query.append("\n 	)page ");
		
		return _query.toString();
	}
	
	public String eventListInfo(String eventCfcd, String searchDateGb
			, String eventStateCode, String searchGb, String eventId
			, String page, String maxPage
			) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	A.eventMstSeq ")
		.append("\n 	, A.eventId ")
		.append("\n 	, A.eventCfcd ")
		.append("\n 	, A.eventCfnm ")
		.append("\n 	, A.eventTitle ")
		.append("\n 	, A.eventDate ")
		.append("\n 	, A.landingPageUrl ")
		.append("\n 	, A.popupImagePath ")
		.append("\n 	, A.bannerImagePath ")
		.append("\n 	, A.landingPageImagePath ")
		.append("\n 	, A.appPushImagePath ")
		.append("\n 	, A.eventRgstDate ")
		.append("\n 	, lz_market.FN_DTL_NM(21, case when concat(A.eventEndDate, A.eventEndTime) <= concat(A.nowDate, A.nowTime) then '004' ")
		.append("\n 		when (A.eventBeginDate <= A.nowDate and A.eventEndDate >= A.nowDate) and A.nowTime between A.eventBeginTime and A.eventEndTime then '001' ")
		.append("\n 		when A.eventBeginDate > A.nowDate then '002' ")
		.append("\n 		when (A.eventBeginDate <= A.nowDate and A.eventEndDate >= A.nowDate) and A.nowTime > A.eventEndTime then '003' ")
		.append("\n 		else '005' end) as eventStateCodeName ")
		.append("\n 	, case when concat(A.eventEndDate, A.eventEndTime) <= concat(A.nowDate, A.nowTime) then '004' ")
		.append("\n 		when (A.eventBeginDate <= A.nowDate and A.eventEndDate >= A.nowDate) and A.nowTime between A.eventBeginTime and A.eventEndTime then '001' ")
		.append("\n 		when A.eventBeginDate > A.nowDate then '002' ")
		.append("\n 		when (A.eventBeginDate <= A.nowDate and A.eventEndDate >= A.nowDate) and A.nowTime > A.eventEndTime then '003'  ")
		.append("\n 		else '005' end as eventStateCode ")
		.append("\n 	, ceil(A.dateInterval) ")
		.append("\n 	, A.eventBeginDate2 ")
		.append("\n 	, A.eventEndDate2 ")
		.append("\n 	, A.productPrice ")
		.append("\n 	, A.eventBeginTime2 ")
		.append("\n 	, A.eventEndTime2 ")
		.append("\n 	, A.eventCount ")
		.append("\n 	, A.eventProceedCode ")
		.append("\n 	, A.popupDisplayCfcd ")
		.append("\n 	, A.bannerDetailDisplayYn ")
		.append("\n 	, A.bannerMyPageDisplayYn ")
		.append("\n 	, A.bannerTodayDisplayYn ")
		.append("\n 	, A.bannerPointHistDisplayYn ")
		.append("\n 	, A.bannerCreateReviewDisplayYn ")
		.append("\n 	, A.bannerReviewInfoDisplayYn ")
		.append("\n 	, A.personBuyCount ")
		.append("\n 	, A.benefitCfcd ")
		.append("\n 	, A.benefitAmount ")
		.append("\n 	, A.benefitName ")
		.append("\n 	, A.benefitDateCfcd ")
		.append("\n 	, A.benefitDate ")
		.append("\n 	, A.benefitDuplicatedYn ")
		.append("\n from ( ")
		.append("\n select ")
		.append("\n 	em.event_mst_seq as eventMstSeq ")
		.append("\n 	, em.event_id as eventId ")
		.append("\n 	, em.event_cfcd as eventCfcd ")
		.append("\n 	, lz_market.FN_DTL_NM(17, em.event_cfcd) as eventCfnm ")
		.append("\n 	, em.event_title as eventTitle ")
		//.append("\n 	, concat(date_format(em.event_begin_date, '%Y.%m.%d'),' ~ ', date_format(em.event_end_date, '%Y.%m.%d')) as eventDate ")
		.append("\n 	, concat(date_format(em.event_begin_date,'%Y.%m.%d'),' ~ ',if(em.event_end_date is null, '마감시까지', date_format(em.event_end_date,'%Y.%m.%d'))) as eventDate ")
		.append("\n 	, em.landing_page_url as landingPageUrl ")
		.append("\n 	, em.popup_image_path as popupImagePath ")
		.append("\n 	, em.banner_image_path as bannerImagePath ")
		.append("\n 	, em.landing_page_image_path as landingPageImagePath ")
		.append("\n 	, em.app_push_image_path as appPushImagePath ")
		.append("\n 	, date_format(em.create_datetime , '%Y.%m.%d') as eventRgstDate ")
		//.append("\n 	, concat(date_format(em.event_begin_date, '%Y%m%d'), em.event_begin_time) as eventBeginDate ")
		//.append("\n 	, concat(date_format(em.event_end_date, '%Y%m%d'), em.event_end_time) as eventEndDate ")
		.append("\n 	, date_format(em.event_begin_date, '%Y%m%d') as eventBeginDate ")
		//.append("\n 	, date_format(em.event_end_date, '%Y%m%d') as eventEndDate ")
		.append("\n 	, ifnull(date_format(em.event_end_date,'%Y%m%d'),'99991231') as eventEndDate ")
		.append("\n 	, if(ifnull(em.event_begin_time ,'') = '', '0000', em.event_begin_time) as eventBeginTime ")
		.append("\n 	, if(ifnull(em.event_end_time ,'') = '', '2400', em.event_end_time) as eventEndTime ")
		.append("\n 	, em.event_state_code as eventStateCode ")
		//.append("\n 	, date_format(now(), '%Y%m%d%H%i') as nowDate ")
		.append("\n 	, date_format(now(),'%Y%m%d') as nowDate ")
		.append("\n 	, date_format(now(),'%H%i') as nowTime ")
		.append("\n 	, DATEDIFF(event_end_date, now()) as dateInterval ")
		.append("\n 	, date_format(em.event_begin_date,'%Y-%m-%d') as eventBeginDate2 ")
		.append("\n 	, date_format(em.event_end_date,'%Y-%m-%d') as eventEndDate2 ")		
		.append("\n 	, (select ")
		.append("\n 	ceil(ifnull(case when c.event_product_discount_cf_code = '01' then c.event_product_discount_cf_value ")
		.append("\n 	when c.event_product_discount_cf_code = '02' then  b.product_price - (b.product_price * (c.event_product_discount_cf_value /100)) end, b.product_price)) ")
		.append("\n 	from lz_market.event_mst a ")
		.append("\n 	left outer join lz_market.event_product_amount_info c ")
		.append("\n 		on a.event_id = c.event_id ")
		.append("\n 		and DATE_FORMAT(now(), '%H%i') between c.begin_time and c.end_time ")
		.append("\n 	, lz_market.product_info b ")
		.append("\n 	where a.event_id = b.event_id ")
		.append("\n 	and a.event_id = em.event_id ")
		.append("\n 	order by c.begin_time desc ")
		.append("\n 	limit 1 ) as productPrice ")
		.append("\n 	, em.event_begin_time as eventBeginTime2 ")
		.append("\n 	, em.event_end_time as eventEndTime2 ")
		.append("\n 	, em.event_count as eventCount ")
		.append("\n 	, em.event_proceed_code as eventProceedCode ")
		.append("\n 	, em.popup_display_cfcd as popupDisplayCfcd ")
		.append("\n 	, em.banner_detail_display_yn as bannerDetailDisplayYn ")
		.append("\n 	, em.banner_my_page_display_yn as bannerMyPageDisplayYn ")
		.append("\n 	, em.banner_today_display_yn as bannerTodayDisplayYn ")
		.append("\n 	, em.banner_point_hist_display_yn as bannerPointHistDisplayYn ")
		.append("\n 	, em.banner_create_review_display_yn as bannerCreateReviewDisplayYn ")
		.append("\n 	, em.banner_review_info_display_yn as bannerReviewInfoDisplayYn ")
		.append("\n 	, em.person_buy_count as personBuyCount ")
		.append("\n 	, em.benefit_cfcd as benefitCfcd ")
		.append("\n 	, ceil(em.benefit_amount) as benefitAmount ")
		.append("\n 	, em.benefit_name as benefitName ")
		.append("\n 	, em.benefit_date_cfcd as benefitDateCfcd ")
		.append("\n 	, date_format(em.benefit_date,'%Y-%m-%d') as benefitDate ")
		.append("\n 	, em.benefit_duplicated_yn as benefitDuplicatedYn ")
		.append("\n from lz_market.event_mst em ")
		.append("\n      left outer join lz_market.user_info ui on em.create_user = ui.user_id ")
		.append("\n where 1 = 1 ");
		if("001".equals(searchDateGb)) {
			_query.append("\n 	and em.event_begin_date between ? and ? ");
		} else if("002".equals(searchDateGb)) {
			_query.append("\n 	and em.create_datetime between ? and ? ");
		}
		
		if(!"000".equals(eventCfcd)) {
			_query.append("\n 	and em.event_cfcd = ? ");
		}
		
		if("001".equals(searchGb)) {
			_query.append("\n 	and em.event_title like ? ");
		} else if("002".equals(searchGb)) {
			_query.append("\n 	and ui.user_name = ? ");
		} 
		
		if("001".equals(eventStateCode)) {
			_query.append("\n 	and (concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) <= date_format(now(), '%Y%m%d%H%i') and concat(if(em.event_end_date is null, '99991231', date_format(em.event_end_date, '%Y%m%d')), IFNULL(em.event_end_time, '2400')) >= date_format(now(), '%Y%m%d%H%i'))");
		} else if("002".equals(eventStateCode)) {
			_query.append("\n 	and concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) > date_format(now(), '%Y%m%d%H%i')");
		} else if("003".equals(eventStateCode)) {
			_query.append("\n 	and em.event_state_code = '" + eventStateCode + "'");
		} else if("004".equals(eventStateCode)) {
			_query.append("\n 	and concat(if(em.event_end_date is null, '99991231', date_format(em.event_end_date, '%Y%m%d')), IFNULL(em.event_end_time, '2400')) < date_format(now(), '%Y%m%d%H%i')");
		} else if("005".equals(eventStateCode)) {
			_query.append("\n 	and concat(date_format(em.event_begin_date, '%Y%m%d'), IFNULL(em.event_begin_time, '0000')) > date_format(now(), '%Y%m%d%H%i')");
		} else if("999".equals(eventStateCode)) {
			_query.append("\n 	and DATE_FORMAT(now(),'%Y%m%d%H%i')  between concat(DATE_FORMAT(em.event_begin_date,'%Y%m%d'), '0000') and concat(if(em.event_end_date is null, '99991231', date_format(em.event_end_date, '%Y%m%d')), if(ifnull(em.event_end_time ,'') = '','2400',em.event_end_time)) ");
		}
		
		if(!("".equals(eventId) || eventId == null)) {
			_query.append("\n 	and em.event_id = ? ");
		}
		
		_query.append("\n order by em.event_mst_seq desc ")
		.append("\n )A ")
		.append("\n LIMIT " + page + "  , " + maxPage);
		
		return _query.toString();
	}
	
	public String eventProductInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
		.append("\n 	ifnull(em.person_buy_count, 0) as personBuyCount ")
		.append("\n 	, ifnull(em.event_count, 0) as  eventCount ")
		.append("\n 	, ifnull(em.event_begin_time, '0000') as eventBeginTime ")
		.append("\n 	, ifnull(em.event_end_time, '2359') as eventEndTime ")
		.append("\n 	, ifnull(DATE_FORMAT(em.event_begin_date, '%Y-%m-%d') ,'2022-12-15') as eventBeginDate ")
		.append("\n 	, ifnull(DATE_FORMAT(em.event_end_date, '%Y-%m-%d'), '9999-12-31') as eventEndDate ")
		.append("\n 	, (select ")
		.append("\n   	 ifnull(sum(poii.product_count), 0)  ")
		.append("\n   from lz_market.product_order_info poi ")
		.append("\n   	, lz_market.product_order_item_info poii ")
		.append("\n   where poi.order_no = poii.order_no ")
		.append("\n   and poi.user_id = ? ")
		.append("\n   and poii.order_item_state_code = ? ")
		.append("\n   and poii.product_id = ?) as personPaymentCount ")
		.append("\n   ,(select ")
		.append("\n   	ifnull(sum(A.product_count), 0)  ")
		.append("\n   from lz_market.product_order_item_info A ")
		.append("\n   	, lz_market.payment_info B ")
		.append("\n   where A.order_no = B.order_no ") 
		.append("\n   and A.product_id = ? ")
		.append("\n   and A.order_item_state_code = ? ")
		.append("\n   and DATE_FORMAT(B.payment_dttm, '%Y%m%d') = DATE_FORMAT(now(), '%Y%m%d')) as eventPaymentCount ")
		.append("\n from lz_market.event_mst em ")
		.append("\n where em.event_id = ? ");
		
		return _query.toString();
	}
	
	public String eventIdInfoList() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			.append("\n 	ei.event_id as eventId ")
			.append("\n 	, ei.event_name as eventName ")
			.append("\n 	, ei.expiration_date_gb as expirationDateGb ")
			.append("\n 	, ceil(ei.expiration_date_value) as expirationDateValue ")
			.append("\n 	, ceil(ei.point_amount) as pointAmount ")
			.append("\n 	, ei.point_amount_modification_yn as pointAmountModificationYn ")
			.append("\n from lz_market.event_info ei ")
			//.append("\n where event_id in('99999', '00004') ")
			.append("\n where ei.duplicate_limit_yn = 'N' ")
			.append("\n order by ei.event_id asc ");
		
		return _query.toString();
	}
}
