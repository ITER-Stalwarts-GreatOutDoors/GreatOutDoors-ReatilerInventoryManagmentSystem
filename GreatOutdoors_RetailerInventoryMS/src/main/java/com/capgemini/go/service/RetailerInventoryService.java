package com.capgemini.go.service;

import java.util.List;

import com.capgemini.go.bean.RetailerInventoryBean;
import com.capgemini.go.dto.RetailerInventoryDTO;
import com.capgemini.go.exception.RetailerInventoryException;


 
public interface RetailerInventoryService {
	public List<RetailerInventoryBean> getListOfRetailers() throws RetailerInventoryException;
	public boolean addItemToInventory(String retailerId, byte productCategory, String productId);
	public boolean deleteItemFromInventory(String retailerId, String productId );
	public List<RetailerInventoryDTO> getInventoryById(String retailerId );
	
	
	
	

}
