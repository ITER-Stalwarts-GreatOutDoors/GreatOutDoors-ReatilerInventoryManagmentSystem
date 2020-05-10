package com.capgemini.go.bean;

import java.time.Period;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Inventory_Details_Bean" )
public class RetailerInventoryBean {
	private int retailerId;
	private String retailerName;
	private byte productCategoryNumber;
	private String productCategoryName;
	private String productName;
	private String productUniqueId;
	private Period deliveryTimePeriod;
	private Period shelfTimePeriod;
	
	public RetailerInventoryBean() {  }

	public RetailerInventoryBean(int retailerId, String retailerName, byte productCategoryNumber,
				String productCategoryName, String productName, String productUniqueId, Period deliveryTimePeriod,
				Period shelfTimePeriod) {
			super();
			this.retailerId = retailerId;
			this.retailerName = retailerName;
			this.productCategoryNumber = productCategoryNumber;
			this.productCategoryName = productCategoryName;
			this.productName = productName;
			this.productUniqueId = productUniqueId;
			this.deliveryTimePeriod = deliveryTimePeriod;
			this.shelfTimePeriod = shelfTimePeriod;
		}

	public int getRetailerId() {
		return retailerId;
	}
	public void setRetailerId(String string) {
		this.retailerId = string;
	}
	public String getRetailerName() {
		return retailerName;
	}
	public void setRetailerName(String retailerName) {
		this.retailerName = retailerName;
	}
	public byte getProductCategoryNumber() {
		return productCategoryNumber;
	}
	public void setProductCategoryNumber(byte productCategoryNumber) {
		this.productCategoryNumber = productCategoryNumber;
	}
	public String getProductCategoryName() {
		return productCategoryName;
	}
	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductUniqueId() {
		return productUniqueId;
	}
	public void setProductUniqueId(String productUniqueId) {
		this.productUniqueId = productUniqueId;
	}

	public void setShelfTimePeriod(Object object) {
		// TODO Auto-generated method stub
		
	}
	public Period getDeliveryTimePeriod() {
		return deliveryTimePeriod;
	}

	public void setDeliveryTimePeriod(Period deliveryTimePeriod) {
		this.deliveryTimePeriod = deliveryTimePeriod;
	}

	public Period getShelfTimePeriod() {
		return shelfTimePeriod;
	}

	public void setShelfTimePeriod(Period shelfTimePeriod) {
		this.shelfTimePeriod = shelfTimePeriod;
	}
	
	 

}
