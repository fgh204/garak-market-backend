package com.lawzone.market.product.service;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.lawzone.market.config.BaseTimeEntity;

import lombok.Data;

@Entity
@Data
public class ProductTagInfo extends BaseTimeEntity{
	@EmbeddedId
	private ProductTagInfoId productTagInfoId;
}
