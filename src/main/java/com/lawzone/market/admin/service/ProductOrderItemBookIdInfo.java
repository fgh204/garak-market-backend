package com.lawzone.market.admin.service;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import com.lawzone.market.common.CdDtlInfo;
import com.lawzone.market.common.CdDtlInfoId;
import com.lawzone.market.config.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ProductOrderItemBookIdInfo")
public class ProductOrderItemBookIdInfo extends BaseTimeEntity{
	@EmbeddedId
	private ProductOrderItemBookIdInfoId id;
	
	@Comment("orderIdFromCorp")
	@Column(columnDefinition = "varchar (100)")
	private String orderIdFromCorp;
	
	@Comment("receiverAddress")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddress;
	
	@Comment("receiverAddressBuilding")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddressBuilding;
	
	@Comment("receiverAddressCleaned")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddressCleaned;
	
	@Comment("receiverAddressRoadCleaned")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddressRoadCleaned;
	
	@Comment("receiverAddressRoadDetail")
	@Column(columnDefinition = "varchar (300)")
	private String receiverAddressRoadDetail;
	
	@Comment("dongGroup")
	@Column(columnDefinition = "varchar (300)")
	private String dongGroup;
	
	@Comment("placePageUrl")
	@Column(columnDefinition = "varchar (300)")
	private String placePageUrl;
}
