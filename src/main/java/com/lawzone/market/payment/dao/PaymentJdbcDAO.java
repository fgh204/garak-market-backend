package com.lawzone.market.payment.dao;

import org.springframework.stereotype.Component;

@Component
public class PaymentJdbcDAO {
	public String modifyOrderInfoStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_info")
			  .append("\n set order_state_code = ?")
			  .append("\n , update_date = now()")
			  .append("\nwhere order_no = ?");	
		return _query.toString();
	}
	
	public String modifyOrderItemInfoStat() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n update lz_market.product_order_item_info") 
				.append("\n	set order_item_state_code = ?")
				.append("\n , update_date = now()")
				.append("\nwhere order_no = ?");
		return _query.toString();
	}
	
	public String getpaymentCancleAmt(String _productIdYn) {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ") 
				.append("\n	poii.order_no as order_no ")
				.append("\n , poii.product_id as product_id ")
				.append("\n , (poii.product_count * poii.product_price) as product_total_amt ")
				.append("\n , poi.product_total_price as order_total_amt ")
				.append("\n , pi2.receipt_id as receipt_id ")
				.append("\n , pi2.payment_amount as payment_amount ")
				.append("\n , pi2.cancelled_payment_amount as cancelled_payment_amount ")
				.append("\n , '' as message ")
				.append("\n from lz_market.product_order_info poi")
				.append("\n , lz_market.product_order_item_info poii ")
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
	
	public String orderPaymentInfo() {
		StringBuffer _query = new StringBuffer();
		
		_query.append("\n select ") 
				.append("\n	pi2.payment_gb as pg_pay_co_nm ")
				.append("\n , case when pi2.card_quota = '00'  then '일시불' ")
				.append("\n when ifnull(pi2.card_quota,'') <> ''  then concat(substring(pi2.card_quota,2,1),'개월') ")
				.append("\n else '' ")
				.append("\n end as card_quota ")
				.append("\n , ceil(payment_amount) as payment_amount ")
				.append("\n , ceil(cancelled_payment_amount) as cancelled_payment_amount ")
				.append("\n from lz_market.payment_info pi2 ")
				.append("\n where order_no = ? ");
		
		return _query.toString();
	}
}
