package com.lawzone.market.payment.dao;

import org.springframework.stereotype.Component;

@Component
public class PaymentJdbcDAO {
	public String modifyOrderInfoStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_info")
			  .append("\n set order_state_code = ?")
			  .append("\n , update_datetime = now()")
			  .append("\nwhere order_no = ?");	
		return _query.toString();
	}
	
	public String modifyOrderItemInfoStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set order_item_state_code = ?")
				.append("\n , update_datetime = now()")
				.append("\nwhere order_no = ?");
		return _query.toString();
	}
	
	public String getpaymentCancleAmt(String _productIdYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ") 
				.append("\n	poii.order_no as order_no ")
				.append("\n , poii.product_id as product_id ")
				.append("\n , pi3.product_name  as product_name ")
				.append("\n , poi.phone_number as phoneNumber ")
				.append("\n , (poii.product_count * poii.product_price) as product_total_amt ")
				.append("\n , poi.product_total_price as order_total_amt ")
				.append("\n , pi2.receipt_id as receipt_id ")
				.append("\n , pi2.payment_amount as payment_amount ")
				.append("\n , pi2.cancelled_payment_amount as cancelled_payment_amount ")
				.append("\n , pi2.point_amount as point_amount")
				.append("\n , pi2.cancelled_point_amount as cancelled_point_amount ")
				.append("\n , poi.delivery_amount as delivery_amount ")
				.append("\n , poi.user_id as user_id ")
				.append("\n , '' as message ")
				.append("\n from lz_market.product_order_info poi")
				.append("\n , lz_market.product_order_item_info poii ")
				.append("\n left outer join lz_market.product_info pi3 ")
				.append("\n on pi3.product_id = poii.product_id ")
				.append("\n , lz_market.payment_info pi2  ")
				.append("\n where poi.order_no = poii.order_no ")
				.append("\n and poi.order_no = pi2.order_no ")
				.append("\n and poii.order_item_state_code = '003' ")
				.append("\n and poii.order_no = ? ");
		if("Y".equals(_productIdYn)) {
			_query.append("\n and poii.product_id = ? ");
		}		
		_query.append("\n limit 1 ");
		
		return _query.toString();
	}
	
	public String orderPaymentInfo(String userId) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ") 
				.append("\n	pi2.payment_gb as pg_pay_co_nm ")
				.append("\n , case when pi2.card_quota = '00'  then '일시불' ")
				.append("\n when ifnull(pi2.card_quota,'') <> ''  then concat(substring(pi2.card_quota,2,1),'개월') ")
				.append("\n else '' ")
				.append("\n end as card_quota ")
				.append("\n , ceil(pi2.payment_amount) as payment_amount ")
				.append("\n , ceil(poi.delivery_amount) as delivery_amount ")
				.append("\n , ceil(pi2.cancelled_payment_amount) as cancelled_payment_amount ")
				.append("\n , pi2.payment_name as payment_name ")
				.append("\n , ceil(pi2.payment_amount - cancelled_payment_amount) as finalPaymentAmount  ")
				.append("\n , ceil(pi2.point_amount ) as pointAmount ")
				.append("\n , ceil(pi2.cancelled_point_amount ) as cancelledPointAmount ")
				.append("\n , ceil(pi2.point_amount - pi2.cancelled_point_amount) as finalPointAmount ")
				.append("\n , ceil((select ")
				.append("\n SUM(if(opi.order_amount = opi.cancelled_order_amount, 0 , opi.delivery_amount )) ")
				.append("\n from lz_market.order_payment_info opi ")
				.append("\n where opi.order_no = ?)) as finalDeliveryAmount ")
				.append("\n , ceil(poi.delivery_amount - (select ")
				.append("\n SUM(if(opi.order_amount = opi.cancelled_order_amount, 0 , opi.delivery_amount )) ")
				.append("\n from lz_market.order_payment_info opi ")
				.append("\n where opi.order_no = ?)) as cancelledDeliveryAmount ")
				.append("\n , pi2.receipt_url ")
				.append("\n , ui.corp_yn ")
				.append("\n from lz_market.payment_info pi2 ")
				.append("\n , lz_market.product_order_info poi ")
				.append("\n , lz_market.user_info ui ")
				.append("\n where pi2.order_no = poi.order_no ")
				.append("\n and poi.user_id = ui.user_id  ")
				.append("\n and  pi2.order_no = ? ");
		if(!"".equals(userId)) {
			_query.append("\n and poi.user_id = ? ");
		}
	
		return _query.toString();
	}
	
	public String getSellerPushId() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ") 
				.append("\n	DISTINCT ui.push_id ")
				.append("\n	, pi2.product_name ")
				.append("\n	, ui.user_id ")
				.append("\n from lz_market.product_order_item_info poii ")
				.append("\n , lz_market.product_info pi2 ")
				.append("\n , lz_market.user_info ui ")
				.append("\n where poii.product_id = pi2.product_id ")
				.append("\n and pi2.seller_id = ui.user_id ")
				.append("\n and poii.order_no = ? ")
				.append("\n and ui.push_id IS NOT NULL ");
		
		return _query.toString();
	}
}
