package com.lawzone.market.order.dao;

import org.springframework.stereotype.Component;

@Component
public class ProductOrderJdbcDAO {
	public String custProductItemByOrderId(String statCd, String userId, String payYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  //.append("\n	b.order_no as order_no")
			  .append("\n   lz_market.FN_PRDT_NM(b.product_id) as product_name")
			  .append("\n	, b.order_item_state_code as state_code")
			  .append("\n	, lz_market.FN_DTL_NM(2,b.order_item_state_code) as state_name");
		if("Y".equals(payYn)) {
			_query.append("\n	, if(b.point_amount = 0 and poi.delivery_amount = 0, b.product_count, 1) as product_count")
			.append("\n	, round(if(b.point_amount = 0 and poi.delivery_amount = 0, b.product_price, b.total_product_price),0)");
		}else {
			_query.append("\n	, b.product_count as product_count")
			.append("\n	, round(b.product_price,0)");
		}
		_query.append("\n	, round(b.point_amount,0)")
			  .append("\n	, round(b.total_product_price,0)")
			  .append("\n	, pii.thumbnail_image_path ")
			  .append("\n	, b.product_id ")
			  //.append("\n	, b.order_item_dlng_state_code  as order_item_dlng_state_code ")
			  //.append("\n	, lz_market.FN_DTL_NM(3,b.order_item_dlng_state_code) as order_item_dlng_state_name ")
			  .append("\n	, case when b.delivery_state_code = '400' then b.delivery_state_code else  b.order_item_dlng_state_code end as order_item_dlng_state_code")
			  .append("\n	, case when b.delivery_state_code = '400' then lz_market.FN_DTL_NM(7,b.delivery_state_code) else lz_market.FN_DTL_NM(3,b.order_item_dlng_state_code) end as order_item_dlng_state_name")
			  .append("\n	, if(ifnull(pri.review_number,'N') = 'N', 'N', 'Y') as isReviewRegistered")
			  .append("\n	, case when b.delivery_state_code <> '400' or b.order_item_state_code <> '003' then 'N' ")
			  .append("\n	when b.delivery_state_code = '400' and DATEDIFF(now(), b.update_datetime) <= 90 then 'Y' else 'N' end as isReviewWritable ")
			  .append("\n 	, si.shop_name as shopName ")
			  .append("\n 	, si.seller_id as sellerId ")
			  .append("\n 	, si.today_delivery_standard_time as todayDeliveryStandardTime ")
			  .append("\n 	, ifnull(DATE_FORMAT(pi2.create_datetime, '%Y.%m.%d'), '') as orderCompletionDate ")
			  .append("\n 	, ifnull(DATE_FORMAT(pi2.create_datetime, '%Y.%m.%d'), '') as deliveryEstimatedDate ")
			  .append("\n 	, ifnull(DATE_FORMAT(pi2.create_datetime, '%H%i'), '') as orderCompletionTime ")
			  .append("\n 	, case when b.order_item_state_code = '002' then  DATE_FORMAT(b.update_datetime, '%Y.%m.%d')  else '' end as orderCancellationDate ")
			  .append("\n 	, case when (b.delivery_state_code = '400' OR b.order_item_dlng_state_code = '400') AND b.order_item_state_code <> '002' then  DATE_FORMAT(b.update_datetime, '%Y.%m.%d')  else '' end as deliveryCompletionDate ")
			  .append("\n 	, ifnull((select cdi.dtl_code_text  from lz_market.cd_dtl_info cdi where cdi.code_no = 8 and cdi.dtl_code = si.seller_id),'') as slsDateText ")
			  .append("\n 	, b.order_no as orderNo ")
			  .append("\n from lz_market.product_order_item_info b")
			  //.append("\n left outer join lz_market.product_order_item_book_id_info poibii")
			  //.append("\n on b.order_no = poibii.order_no ")
			  //.append("\n and b.product_id = poibii.product_id")
			  .append("\n 	left outer join lz_market.product_review_info pri")
			  .append("\n 	on pri.order_no = b.order_no ")
			  .append("\n 	and pri.product_id = b.product_id")
			  .append("\n 	left outer join lz_market.seller_info si ")
			  .append("\n 	on si.seller_id = b.seller_id ")
			  .append("\n 	left outer join lz_market.payment_info pi2 ")
			  .append("\n 	on b.order_no = pi2.order_no ")
			  .append("\n	, lz_market.product_image_info pii ")
			  .append("\n	, lz_market.product_order_info poi ")
			  .append("\n where b.product_id = pii.product_id")
			  .append("\n and b.order_no = poi.order_no ")
			  .append("\n and b.order_no = ?")
			  .append("\n and pii.delegate_thumbnail_yn = 'Y'")
			  .append("\n and pii.image_cfcd = '01'");
		if("".equals(statCd)) {
			_query.append("\nand b.order_item_state_code <> ?");
		}else {
			_query.append("\nand b.order_item_state_code = ?");
		}
		
		if(!"".equals(userId)) {
			_query.append("\nand poi.user_id = ?");
		}
			  		
		return _query.toString();
	}
	
	public String adminProductItemInfo(String statCd) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  //.append("\n	b.order_no as order_no")
			  .append("\n   lz_market.FN_PRDT_NM(b.product_id) as product_name")
			  .append("\n	, b.order_item_state_code as state_code")
			  .append("\n	, lz_market.FN_DTL_NM(2,b.order_item_state_code) as state_name")
			  .append("\n	, b.product_count as product_count")
			  .append("\n	, round(b.product_price,0)")
			  .append("\n	, round(b.point_amount,0)")
			  .append("\n	, round(b.total_product_price,0)")
			  .append("\n	, pii.thumbnail_image_path ")
			  .append("\n	, b.product_id ")
			  //.append("\n	, b.order_item_dlng_state_code  as order_item_dlng_state_code ")
			  //.append("\n	, lz_market.FN_DTL_NM(3,b.order_item_dlng_state_code) as order_item_dlng_state_name ")
			  .append("\n	, case when poibii.delivery_state_code = '400' then poibii.delivery_state_code else  b.order_item_dlng_state_code end as order_item_dlng_state_code")
			  .append("\n	, case when poibii.delivery_state_code = '400' then lz_market.FN_DTL_NM(7,poibii.delivery_state_code) else lz_market.FN_DTL_NM(3,b.order_item_dlng_state_code) end as order_item_dlng_state_name")
			  .append("\n	, si.shop_name ")
			  .append("\n from lz_market.product_order_item_info b")
			  .append("\n left outer join lz_market.product_order_item_book_id_info poibii")
			  .append("\n on b.order_no = poibii.order_no ")
			  .append("\n and b.product_id = poibii.product_id")
			  .append("\n	, lz_market.product_image_info pii")
			  .append("\n	, lz_market.product_info a")
			  .append("\n	, lz_market.seller_info si")
			  .append("\n where b.product_id = pii.product_id")
			  .append("\n and b.product_id = a.product_id")
			  .append("\n and a.seller_id = si.seller_id")
			  .append("\n and b.order_no = ?")
			  .append("\n and b.product_id = ?")
			  .append("\n and pii.delegate_thumbnail_yn = 'Y' ")
			  .append("\n and pii.image_cfcd = '01' ");
		if("".equals(statCd)) {
			_query.append("\nand b.order_item_state_code <> ?");
		}else {
			_query.append("\nand b.order_item_state_code = ?");
		}
		
			  		
		return _query.toString();
	}
	
	public String custProductInfoByOrderId(String statCd, String userId, String payYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\nselect") 
				.append("\n	poi.order_no")  
				.append("\n	, ui.user_name")   
				.append("\n	, poi.zonecode");
		
				if("Y".equals(payYn)) {
					_query.append("\n	, round((poi.product_total_price + poi.delivery_amount) - poi.point_amount,0) ");
				}else {
					_query.append("\n	, round(poi.product_total_price,0) ");
				}
				_query.append("\n	, round(poi.point_amount,0) ") 
				.append("\n	, case when ifnull(poi.building_name,'') = '' then poi.address\r\n"
						+ "        	else concat(poi.address , ' (' , poi.building_name ,')') end as address ") 
				.append("\n	, poi.detail_address ")
				.append("\n	, poi.order_name ")
				.append("\n	, poi.recipient_name ")
				.append("\n	, DATE_FORMAT(poi.order_date, '%Y.%m.%d %H:%i') as order_date ")
				.append("\n	, poi.phone_number as phone_number ")
				.append("\n	, poi.sub_phone_number as sub_phone_number ")
				.append("\n	, poi.delivery_message as delivery_message ")
				.append("\n	, poi.delivery_location_cfcd as deliveryLocationCfcd ")
				.append("\n	, lz_market.FN_DTL_NM(10,poi.delivery_location_cfcd) as deliveryLocationCfcdNm ")
				.append("\n	, poi.access_method_cfcd as accessMethodCfcd")
				.append("\n	, lz_market.FN_DTL_NM(11,poi.access_method_cfcd) as accessMethodCfcdNm ")
				.append("\n	, REGEXP_REPLACE(access_method_text , '[0-9]' ,'*') as accessMethodText ")
				//.append("\n	, poi.order_dlng_state_code as order_dlng_state_code ")
				//.append("\n	, lz_market.FN_DTL_NM(3,poi.order_dlng_state_code) as order_dlng_state_name ")
				.append("\nfrom lz_market.product_order_info poi ") 
				.append("\n	, lz_market.user_info ui ") 
				.append("\nwhere poi.user_id = ui.user_id ")
				.append("\nand order_no = ?");
		
				if("".equals(statCd)) {
					_query.append("\nand poi.order_state_code <> ?");
				}else {
					_query.append("\nand poi.order_state_code = ?");
				}
				
				if(!"".equals(userId)) {
					_query.append("\nand poi.user_id = ?");
				}
				
		return _query.toString();
	}
	
	public String modifyOrderInfoStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_info")
			  .append("\n set order_state_code = ?")
			  //.append("\n , order_dlng_state_code = ?")
			  .append("\n , update_datetime = now()")
			  .append("\n where order_no = ?")
			  .append("\n and order_state_code = ?");
		return _query.toString();
	}
	
	public String modifyOrderItemInfoStat(String _productIdYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set order_item_state_code = ? ")
				.append("\n , order_item_dlng_state_code = ? ")
				.append("\n , update_datetime = now() ")
				.append("\n where order_no = ? ")
				.append("\n and order_item_state_code = ? ");
				if("Y".equals(_productIdYn)) {
					_query.append("\nand product_id = ? ");
				}
		
		return _query.toString();
	}
	
	public String modifyOrderItemInfoDlngStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set order_item_dlng_state_code = ? ")
				.append("\n , update_datetime = now() ")
				.append("\n where order_no = ? ")
				.append("\nand product_id = ? ");
		return _query.toString();
	}
	
	public String adminOrderList(String statCd, String dlngStatCd, String deliveryStateCode, String sellerIdYn
								, String searchGb, String page, String orderDateGb, String deliveryComplYn, String maxPage) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
				//.append("\n 	DATE_FORMAT(poi.order_date,'%Y-%m-%d') as orderDate ")
				.append("\n 	DATE_FORMAT(pi3.create_datetime ,'%Y-%m-%d %H:%i:%s') as orderDate ")
				.append("\n 	, poi.order_no ") 
				//.append("\n 	, concat(substr(poi.order_name,1,20), '...') as orderName ")
				.append("\n 	, poii.product_id as productId ")
				.append("\n 	, pi2.product_name  as productName ")
				.append("\n 	, concat(poii.product_count  ,'') as orderCount ")
				.append("\n 	, format(poii.product_price,0) as productPrice ")
				.append("\n 	, format(poii.product_price * poii.product_count ,0) as orderPrice ")
				.append("\n 	, poii.order_item_state_code as orderItemStateCode")
				.append("\n 	, lz_market.FN_DTL_NM(2, poii.order_item_state_code) as orderItemStateName")
				.append("\n 	, poii.order_item_dlng_state_code  as orderItemDlngStateCode")
				.append("\n 	, lz_market.FN_DTL_NM(3, poii.order_item_dlng_state_code) as orderItemDlngStateName")
				.append("\n 	, ui.user_name ")
				.append("\n 	, si.shop_name ")
				.append("\n 	, poii.delivery_state_code  as deliveryStateCode ")
				.append("\n 	, lz_market.FN_DTL_NM(7, poii.delivery_state_code) as deliveryStateCodeName ")
				.append("\n from lz_market.product_order_info poi ") 
				.append("\n 	, lz_market.product_order_item_info poii ")
//				.append("\n 	left outer join lz_market.product_order_item_book_id_info poibii ")
//				.append("\n 	on poii.order_no = poibii.order_no  ")
//				.append("\n 	and poii.product_id = poibii.product_id ")
				.append("\n 	, lz_market.product_info pi2 ") 
				.append("\n 	, lz_market.user_info ui ")
				.append("\n 	, lz_market.seller_info si ")
				.append("\n 	, lz_market.payment_info pi3 ")
				.append("\n where poi.order_no = poii.order_no ") 
				.append("\n and poii.product_id = pi2.product_id ")
				.append("\n and poi.user_id = ui.user_id ")
				.append("\n and pi2.seller_id =  si.seller_id ")
				.append("\n and poi.order_no = pi3.order_no ");
					
				if("01".equals(orderDateGb)) {
					_query.append("\n and poi.order_date BETWEEN concat(?, ' 00:00:00') and concat(?, ' 23:59:59') ");
				}
		
				if("000".equals(statCd)) {
					//_query.append("\n and poii.order_item_state_code in('003','002') ");
				}else {
					_query.append("\n and poii.order_item_state_code = ? ");
				}
				
				if("Y".equals(deliveryComplYn)) {
					_query.append("\n and poii.order_item_dlng_state_code <> '900' ");
				} else {
					if(!"000".equals(dlngStatCd)) {
						//if("300".equals(dlngStatCd) || "400".equals(dlngStatCd)) {
						//	_query.append("\n and poibii.delivery_state_code = ? ");
						//}else {
							_query.append("\n and poii.order_item_dlng_state_code = ? ");
						//}
					}
				}
				
				if(!"000".equals(deliveryStateCode)) {
					_query.append("\n and poii.delivery_state_code = ? ");
				}
				
				if("Y".equals(sellerIdYn)) {
					_query.append("\n and pi2.seller_id = ?");
				}
				if("01".equals(searchGb)) {
					_query.append("\n and ui.user_name like concat('%',?,'%')");
				} else if("02".equals(searchGb)) {
					_query.append("\n and si.shop_name like concat('%',?,'%')");
				} else if("03".equals(searchGb)) {//주문번호
					_query.append("\n and poi.order_no = ?");
				} else if("04".equals(searchGb)) {//구매자 연락처
					_query.append("\n and ui.phone_number = ?");
				} else if("05".equals(searchGb)) {//수취인명
					_query.append("\n and poi.recipient_name like concat('%',?,'%')");
				} else if("06".equals(searchGb)) {//수취자 연락처
					_query.append("\n and poi.phone_number = ?");
				} 
				
				_query.append("\n  group by poii.order_no , poii.product_id ")
				.append("\n order by poi.order_no desc ")
				.append("\n limit " + page + " , " + maxPage);
		return _query.toString();
	}
	
	public String adminOrderItemPageInfo(String statCd, String dlngStatCd, String deliveryStateCode,  String sellerIdYn
										, String searchGb, String page, String orderDateGb, String deliveryComplYn) {
		StringBuffer _query = new StringBuffer();
		_query.append("\n select ")
				.append("\n page.cnt as total_count ")
				.append("\n , page.p_cnt as total_pages ")
				.append("\n , if(page.p_cnt > " + page + " , 'Y', 'N') as has_next ")
				.append("\n , if(1 < " + page + " , 'Y', 'N') as has_previous ")
				.append("\n , " + page + " as page_number ")
				.append("\n from ( ")
				.append("\n select ") 
				.append("\n 	COUNT(1) as cnt ")
				.append("\n 	, CEIL (COUNT(1)/10) as p_cnt ")
				.append("\n from lz_market.product_order_info poi ") 
				.append("\n 	, lz_market.product_order_item_info poii ")
//				.append("\n 	left outer join lz_market.product_order_item_book_id_info poibii ")
//				.append("\n 	on poii.order_no = poibii.order_no  ")
//				.append("\n 	and poii.product_id = poibii.product_id ")
				.append("\n 	, lz_market.product_info pi2 ") 
				.append("\n 	, lz_market.user_info ui ")
				.append("\n 	, lz_market.seller_info si ")
				.append("\n 	, lz_market.payment_info pi3 ")
				.append("\n where poi.order_no = poii.order_no ")
				.append("\n and poii.product_id = pi2.product_id ")
				.append("\n and poi.user_id = ui.user_id ")
				.append("\n and pi2.seller_id =  si.seller_id ")
				.append("\n and poi.order_no = pi3.order_no ");
				if("01".equals(orderDateGb)) {
					_query.append("\n and poi.order_date BETWEEN concat(?, ' 00:00:00') and concat(?, ' 23:59:59') ");
				}
				
				if("000".equals(statCd)) {
					//_query.append("\n and poii.order_item_state_code in('003','002') ");
				}else {
					_query.append("\n and poii.order_item_state_code = ? ");
				}
				
				if("Y".equals(deliveryComplYn)) {
					_query.append("\n and poii.order_item_dlng_state_code <> '900' ");
				} else {
					if(!"000".equals(dlngStatCd)) {
						//if("300".equals(dlngStatCd) || "400".equals(dlngStatCd)) {
						//	_query.append("\n and poibii.delivery_state_code = ? ");
						//}else {
							_query.append("\n and poii.order_item_dlng_state_code = ? ");
						//}
					}
				}
				if(!"000".equals(deliveryStateCode)) {
					_query.append("\n and poii.delivery_state_code = ? ");
				}
				
				if("Y".equals(sellerIdYn)) {
					_query.append("\n and pi2.seller_id = ?");
				}
				if("01".equals(searchGb)) {//구매자명
					_query.append("\n and ui.user_name like concat('%',?,'%')");
				} else if("02".equals(searchGb)) {//판매자명
					_query.append("\n and si.shop_name like concat('%',?,'%')");
				} else if("03".equals(searchGb)) {//주문번호
					_query.append("\n and poi.order_no = ?");
				} else if("04".equals(searchGb)) {//구매자 연락처
					_query.append("\n and ui.phone_number = ?");
				} else if("05".equals(searchGb)) {//수취인명
					_query.append("\n and poi.recipient_name like concat('%',?,'%')");
				} else if("06".equals(searchGb)) {//수취자 연락처
					_query.append("\n and poi.phone_number = ?");
				} 
				_query.append("\n )page ");
		return _query.toString();
	}
	
	public String custOrderList(String pageCnt, String maxPageCnt) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
		  .append("\n poi.order_no as order_no")
		  .append("\n , DATE_FORMAT(poi.order_date ,'%Y-%m-%d') as order_date")
		  .append("\n , poi.order_state_code as order_state_code ")
		  .append("\n , lz_market.FN_DTL_NM(1,poi.order_state_code) as order_state_name ")
		  //.append("\n , poi.order_dlng_state_code as order_dlng_state_code ")
		  //.append("\n , lz_market.FN_DTL_NM(3,poi.order_dlng_state_code) as order_dlng_state_name ")
		  .append("\n from lz_market.product_order_info poi ")
		  .append("\n where order_state_code <> '001'")
		  .append("\n and user_id = ?")
		  .append("\n order by create_datetime desc ")
		  .append("\n limit " + pageCnt + ", " + maxPageCnt);
		
	return _query.toString();
	}
	
	public String custOrderListPageInfo(String maxPageCnt) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ceil(count(1) / " + maxPageCnt + ") AS total_page_cnt")
			  .append("\n   , count(1) AS total_count")
			  .append("\n from lz_market.product_order_info poi")
			  .append("\n where order_state_code <> '001'")
			  .append("\n and user_id = ?");
		
		return _query.toString();
	}
	
	public String adminOrderCountInfo(String sellerIdYn, String orderDateGb, String searchGb) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ")
			.append("\n 		SUM(IF(a.orderItemDlngStateCode = '000', a.product_count, 0)) AS orderStayCnt ")
			//.append("\n 		, SUM(IF(a.orderItemDlngStateCode = '100', a.product_count, 0)) AS orderReceivedCnt ")
			//.append("\n 		, SUM(IF(a.orderItemDlngStateCode = '200', a.product_count, 0)) AS orderConfirmCnt ")
			.append("\n 		, SUM(IF(a.orderItemDlngStateCode = '100' AND a.deliveryStateCode = '000', a.product_count, 0)) AS orderReceivedCnt ")
			.append("\n 		, SUM(IF(a.orderItemDlngStateCode = '200' AND a.deliveryStateCode = '100', a.product_count, 0)) AS orderConfirmCnt ")
			.append("\n 		, SUM(IF(a.deliveryStateCode = '300' AND a.orderItemDlngStateCode <> '900' , a.product_count, 0)) AS deliveryRequestCnt ")
			.append("\n 		, SUM(IF(a.orderItemDlngStateCode = '800', a.product_count, 0)) AS orderCancellationRequestCnt ")
			.append("\n 		, SUM(IF(a.orderItemDlngStateCode = '900', a.product_count, 0)) AS orderCancellationCnt ")
			.append("\n 		, SUM(IF(a.deliveryStateCode = '400' AND a.orderItemDlngStateCode <> '900' ,a.product_count,0)) AS deliveryCompleteCnt ")
			//.append("\n 		, SUM(IF(a.deliveryStateCode = '400' ,a.product_count,0)) AS deliveryCompleteCnt ")
			.append("\n 	from ( ")
			.append("\n    	select ")
			.append("\n     	poii.order_item_dlng_state_code  as orderItemDlngStateCode , ")
			.append("\n         lz_market.FN_DTL_NM(3,poii.order_item_dlng_state_code) as orderItemDlngStateName   , ")
			.append("\n         poii.product_count  ,")
			.append("\n         poii.delivery_state_code as deliveryStateCode ")
			.append("\n     from ")
			.append("\n         lz_market.product_order_info poi    , ")
			.append("\n         lz_market.product_order_item_info poii ,")
//			.append("\n         left outer join lz_market.product_order_item_book_id_info poibii ")
//			.append("\n         on poii.order_no = poibii.order_no ")
//			.append("\n         and poii.product_id = poibii.product_id , ")
			.append("\n         lz_market.product_info pi2    , ")
			.append("\n         lz_market.user_info ui ,")
			.append("\n         lz_market.seller_info si ,")
			.append("\n         lz_market.payment_info pi3 ")
			.append("\n     where ")
			.append("\n         poi.order_no = poii.order_no    ")
			.append("\n         and poii.product_id = pi2.product_id    ")
			.append("\n         and poi.user_id = ui.user_id ")
			.append("\n         and pi2.seller_id =  si.seller_id ")
			.append("\n         and poi.order_no = pi3.order_no ");
			if("01".equals(orderDateGb)) {
				_query.append("\n 		and poi.order_date BETWEEN concat(?, ' 00:00:00') and concat(?, ' 23:59:59') ");
			}
			if("Y".equals(sellerIdYn)) {
				_query.append("\n and pi2.seller_id = ?");
			}
			
			if("01".equals(searchGb)) {
				_query.append("\n and ui.user_name like concat('%',?,'%')");
			} else if("02".equals(searchGb)) {
				_query.append("\n and si.shop_name like concat('%',?,'%')");
			} 
			
		_query.append("\n  group by poii.order_no , poii.product_id  ")	
		.append("\n     )a ");
		
		return _query.toString();
	}
	
	public String getProductOrderUserInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	ui.user_name ")
			  .append("\n from lz_market.product_order_info poi ")
			  .append("\n 		,lz_market.user_info ui ")
			  .append("\n where poi.user_id = ui.user_id ")
			  .append("\n and poi.order_no = ? ");
		
		return _query.toString();
	}
	
	public String modifyOrderItemInfoStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set order_item_dlng_state_code = ?")
				.append("\n , update_datetime = now()")
				.append("\n , update_user = '99999999'")
				.append("\nwhere order_no = ?")
				.append("\nand product_id = ?")
				.append("\nand order_item_dlng_state_code = ?");
		return _query.toString();
	}
	
	public String modifyOrderItemDeliveryInfoStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_book_id_info") 
				.append("\n	set delivery_state_code = ?")
				.append("\n , update_datetime = now()")
				.append("\n , update_user = '99999999'")
				.append("\nwhere order_id_from_corp = ?")
				.append("\nand order_no = ?")
				.append("\nand product_id = ?")
				.append("\nand delivery_state_code = ?");
		return _query.toString();
	}
	
	public String modifyOrderItemInfoDlngStatDeliveryBySellerId() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set order_item_dlng_state_code = ? ")
				.append("\n , delivery_state_code = ? ")
				.append("\n , update_datetime = now() ")
				.append("\n where order_no = ? ")
				.append("\n and seller_id = ? ");
		return _query.toString();
	}
	
	public String modifyOrderItemInfoDlngStatDeliveryByProduct() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set order_item_dlng_state_code = ? ")
				.append("\n , delivery_state_code = ? ")
				.append("\n , update_datetime = now() ")
				.append("\n where order_no = ? ")
				.append("\nand product_id = ? ");
		return _query.toString();
	}
	
	public String modifyOrderItemInfoDeliveryBySellerId() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set delivery_state_code = ? ")
				.append("\n , update_datetime = now() ")
				.append("\n where order_no = ? ")
				.append("\n and seller_id = ? ");
		return _query.toString();
	}
	
	public String modifyOrderItemInfoDeliveryByProduct() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set delivery_state_code = ? ")
				.append("\n , update_datetime = now() ")
				.append("\n where order_no = ? ")
				.append("\nand product_id = ? ");
		return _query.toString();
	}
	
	public String getOrderPaymentInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select")
			  .append("\n	poii.order_no ")
			  //.append("\n 	, sum(poii.total_product_price) ")
			  .append("\n 	, sum(poii.product_price * poii.product_count) ")
			  .append("\n 	, poii.seller_id ")
			  //.append("\n 	, si.delivery_amount ")
			  .append("\n 	, case when si.combined_delivery_yn = 'N' then 0 ")
			  .append("\n 		when si.combined_delivery_yn = 'Y' AND sum(poii.product_price * poii.product_count) > si.combined_delivery_standard_amount then 0 ")
			  .append("\n 		else si.delivery_amount END as deliveryAmount ")
			  .append("\n from lz_market.product_order_item_info poii ")
			  .append("\n 	, lz_market.seller_info si ")
			  .append("\n where poii.seller_id = si.seller_id ")
			  .append("\n and poii.order_no = ? ")
			  .append("\n group by poii.order_no , poii.seller_id ");
		
		return _query.toString();
	}
	
	public String modifyOrderPaymentInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.order_payment_info opi ") 
				.append("\n	set opi.cancelled_order_amount = ? ")
				.append("\n 	, opi.update_datetime = now() ")
				.append("\n where order_no = ? ")
				.append("\n and seller_id = ? ");
		return _query.toString();
	}
}
